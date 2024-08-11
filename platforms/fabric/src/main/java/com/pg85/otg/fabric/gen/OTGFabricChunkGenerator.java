package com.pg85.otg.fabric.gen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.pg85.otg.OTG;
import com.pg85.otg.fabric.biome.FabricBiome;
import com.pg85.otg.fabric.biome.OTGFabricBiomeProvider;
import com.pg85.otg.gen.OTGChunkGenerator;
import com.pg85.otg.presets.Preset;
import com.pg85.otg.util.ChunkCoordinate;
import com.pg85.otg.util.gen.ChunkBuffer;
import com.pg85.otg.util.gen.JigsawStructureData;
import com.pg85.otg.util.gen.WorldHeight;
import com.pg85.otg.util.helpers.MathHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.structure.*;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;

@Getter
public class OTGFabricChunkGenerator extends ChunkGenerator {
    public static final Codec<OTGFabricChunkGenerator> CODEC =
            RecordCodecBuilder.create(instance ->
                    instance.group(
                            NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter(OTGFabricChunkGenerator::getSettings),
                            OTGFabricBiomeProvider.CODEC.fieldOf("biome_source").forGetter(OTGFabricChunkGenerator::getBiomeSource)
                    ).apply(instance, instance.stable(OTGFabricChunkGenerator::new)));

    private final Holder<NoiseGeneratorSettings> settings;
    private final OTGFabricBiomeProvider biomeSource;
    private final OTGChunkGenerator internalGenerator;
    private final Preset preset;
    private final NoiseBasedChunkGenerator horribleDelegateForCarvers;
    private final Aquifer.FluidPicker globalFluidPicker;

    public OTGFabricChunkGenerator(Holder<NoiseGeneratorSettings> settings, OTGFabricBiomeProvider biomeSource) {
        super(biomeSource);
        this.settings = settings;
        this.biomeSource = biomeSource;
        this.internalGenerator = new OTGChunkGenerator(
                OTG.getEngine().getPresetLoader().getPresetByFolderName(biomeSource.getPresetFolderName()), 
                biomeSource.getSeed(), 
                biomeSource, 
                OTG.getEngine().getPresetLoader().getGlobalIdMapping(biomeSource.getPresetFolderName()));
        preset = OTG.getEngine().getPresetLoader().getPresetByFolderName(biomeSource.getPresetFolderName());
        horribleDelegateForCarvers = new NoiseBasedChunkGenerator(biomeSource, settings);
        globalFluidPicker = createFluidPicker(settings.value());
    }

    private static Aquifer.FluidPicker createFluidPicker(NoiseGeneratorSettings noiseGeneratorSettings) {
        Aquifer.FluidStatus fluidStatus = new Aquifer.FluidStatus(-54, Blocks.LAVA.defaultBlockState());
        int i = noiseGeneratorSettings.seaLevel();
        Aquifer.FluidStatus fluidStatus2 = new Aquifer.FluidStatus(i, noiseGeneratorSettings.defaultFluid());
        return (j, k, l) -> {
            if (k < Math.min(-54, i)) {
                return fluidStatus;
            }
            return fluidStatus2;
        };
    }

    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public void applyCarvers(WorldGenRegion worldGenRegion, long seed, RandomState randomState, BiomeManager biomeManager, StructureManager structureManager, ChunkAccess chunkAccess, GenerationStep.Carving carving) {

        handleOTGCarvers(seed, chunkAccess, carving);

        //applyNonOTGCarvers(seed, biomeManager, chunkAccess, carving);

        List<String> defaultCavesAndRavines = Arrays.asList("minecraft:cave", "minecraft:underwater_cave", "minecraft:nether_cave", "minecraft:canyon", "minecraft:underwater_canyon");

        BiomeManager biomeManager2 = biomeManager.withDifferentSource((i, j, k) -> this.biomeSource.getNoiseBiome(i, j, k, randomState.sampler()));
        WorldgenRandom worldgenRandom = new WorldgenRandom(new LegacyRandomSource(RandomSupport.generateUniqueSeed()));
        int i2 = 8;
        ChunkPos chunkPos = chunkAccess.getPos();
        NoiseChunk noiseChunk = chunkAccess.getOrCreateNoiseChunk(chunkAccess2 -> this.createNoiseChunk(chunkAccess2, structureManager, Blender.of(worldGenRegion), randomState));
        CarvingMask carvingMask = ((ProtoChunk) chunkAccess).getOrCreateCarvingMask(carving);;
        Aquifer aquifer = noiseChunk.aquifer();
        CarvingContext carvingContext = new CarvingContext(this.horribleDelegateForCarvers, worldGenRegion.registryAccess(), chunkAccess.getHeightAccessorForGeneration(), noiseChunk, randomState, this.settings.value().surfaceRule());
        for (int j2 = -8; j2 <= 8; ++j2) {
            for (int k2 = -8; k2 <= 8; ++k2) {
                ChunkPos chunkPos2 = new ChunkPos(chunkPos.x + j2, chunkPos.z + k2);
                ChunkAccess chunkAccess22 = worldGenRegion.getChunk(chunkPos2.x, chunkPos2.z);
                BiomeGenerationSettings biomeGenerationSettings = chunkAccess22.carverBiome(() -> this.getBiomeGenerationSettings(this.biomeSource.getNoiseBiome(QuartPos.fromBlock(chunkPos2.getMinBlockX()), 0, QuartPos.fromBlock(chunkPos2.getMinBlockZ()), randomState.sampler())));
                Iterable<Holder<ConfiguredWorldCarver<?>>> iterable = biomeGenerationSettings.getCarvers(carving);
                int m = 0;
                for (Holder<ConfiguredWorldCarver<?>> holder : iterable) {
                    ConfiguredWorldCarver<?> configuredWorldCarver = holder.value();
                    if (defaultCavesAndRavines.stream().noneMatch(
                            b -> b.equalsIgnoreCase(
                                    Objects.requireNonNull(BuiltInRegistries.CARVER.getKey(configuredWorldCarver.worldCarver())).toString()
                            )
                    )) {
                        worldgenRandom.setLargeFeatureSeed(seed + (long)m, chunkPos2.x, chunkPos2.z);
                        if (configuredWorldCarver.isStartChunk(worldgenRandom)) {
                            configuredWorldCarver.carve(carvingContext, chunkAccess, biomeManager2::getBiome, worldgenRandom, aquifer, chunkPos2, carvingMask);
                        }
                        ++m;
                    }
                }
            }
        }
    }

    private void handleOTGCarvers(long seed, ChunkAccess chunkAccess, GenerationStep.Carving carving) {
        FabricBiome biome = (FabricBiome) this.internalGenerator.getCachedBiomeProvider().getNoiseBiome(chunkAccess.getPos().x << 2, chunkAccess.getPos().z << 2);
        BiomeGenerationSettings biomegenerationsettings = biome.getBiome().getGenerationSettings();
        Iterable<Holder<ConfiguredWorldCarver<?>>> iterable = biomegenerationsettings.getCarvers(carving);

        // Only use OTG carvers when default mc carvers are found
        List<String> defaultCaves = Arrays.asList("minecraft:cave", "minecraft:underwater_cave", "minecraft:nether_cave");
        boolean cavesEnabled = this.preset.getPresetConfig().getCarverSettings().isCavesEnabled();
        if (cavesEnabled)
        {
            for (Holder<ConfiguredWorldCarver<?>> carver : iterable)
            {
                if (defaultCaves.stream().noneMatch(
                        b -> b.equalsIgnoreCase(
                                Objects.requireNonNull(BuiltInRegistries.CARVER.getKey(carver.value().worldCarver())).toString()
                        )
                ))
                {
                    cavesEnabled = false;
                    break;
                }
            }
        }

        List<String> defaultRavines = Arrays.asList("minecraft:canyon", "minecraft:underwater_canyon");
        boolean ravinesEnabled = this.preset.getPresetConfig().getCarverSettings().isRavinesEnabled();
        if (ravinesEnabled)
        {
            for (Holder<ConfiguredWorldCarver<?>> carver : iterable)
            {
                if (defaultRavines.stream().noneMatch(
                        b -> b.equalsIgnoreCase(
                                Objects.requireNonNull(BuiltInRegistries.CARVER.getKey(carver.value().worldCarver())).toString()
                        )
                ))
                {
                    ravinesEnabled = false;
                    break;
                }
            }
        }

        ChunkBuffer chunkBuffer = new FabricChunkBuffer(chunkAccess);
        CarvingMask carvingMask = ((ProtoChunk) chunkAccess).getOrCreateCarvingMask(carving);
        BitSet bitSet = BitSet.valueOf(carvingMask.toArray());
        internalGenerator.carve(
                chunkBuffer,
                seed,
                bitSet,
                cavesEnabled,
                ravinesEnabled
        );
    }

    private NoiseChunk createNoiseChunk(ChunkAccess chunkAccess, StructureManager structureManager, Blender blender, RandomState randomState) {
        return NoiseChunk.forChunk(chunkAccess, randomState, Beardifier.forStructuresInChunk(structureManager, chunkAccess.getPos()), this.settings.value(), this.globalFluidPicker, blender);
    }

    @Override
    public void buildSurface(WorldGenRegion worldGenRegion, StructureManager structureManager, RandomState randomState, ChunkAccess chunkAccess) {
        // surface is handled in fillFromNoise
    }

    @Override
    public void spawnOriginalMobs(WorldGenRegion worldGenRegion) {
        // TODO: Implement this
    }

    @Override
    public int getGenDepth() {
        return this.settings.value().noiseSettings().height();
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor executor, Blender blender, RandomState randomState, StructureManager structureManager, ChunkAccess chunkAccess) {
        ChunkCoordinate chunkCoord = ChunkCoordinate.fromChunkCoords(chunkAccess.getPos().x, chunkAccess.getPos().z);

        // Fetch any chunks that are cached in the WorldGenRegion, so we can
        // pre-emptively generate and cache base terrain for them asynchronously.

        // If we've already (shadow-)generated and cached this
        // chunk while it was unloaded, use cached data.
        ChunkBuffer buffer = new FabricChunkBuffer(chunkAccess);
        // Setup jigsaw data
        ObjectList<JigsawStructureData> structures = new ObjectArrayList<>(10);
        ChunkPos pos = chunkAccess.getPos();
        int chunkX = pos.x;
        int chunkZ = pos.z;
        int startX = chunkX << 4;
        int startZ = chunkZ << 4;

        // Iterate through all of the jigsaw structures (villages, pillager outposts, nether fossils)
        for (var n : chunkAccess.getAllStarts().entrySet()) {
            if ( n.getKey().terrainAdaptation() != TerrainAdjustment.NONE && n.getValue().isValid()) {
                StructureStart start = n.getValue();
                for (StructurePiece piece : start.getPieces()) {
                    if (piece.isCloseToChunk(pos, 0)) {
                        BoundingBox box = piece.getBoundingBox();
                        structures.add(new JigsawStructureData(box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ(), false, 0, 0, 0));
                    }
                }
            }
        }
        WorldHeight worldHeight = new WorldHeight(0, 255);
        // we have no more world random, so this is a bit of a stopgap for now. Seems to be mainly used for bedrock and surface
        Random random = new Random(chunkCoord.getChunkX()*341873128712L + chunkCoord.getChunkZ()*132897987541L);
        this.internalGenerator.populateNoise(worldHeight, buffer, buffer.getChunkCoordinate(), structures, random);
        return CompletableFuture.completedFuture(chunkAccess);
    }

    @Override
    public int getSeaLevel() {
        return settings.value().seaLevel();
    }

    @Override
    public int getMinY() {
        return settings.value().noiseSettings().minY();
    }

    // For later lookup - will return the height of the terrain at the given x and z coordinates, but not save anything to the column
    @Override
    public int getBaseHeight(int i, int j, Heightmap.Types types, LevelHeightAccessor levelHeightAccessor, RandomState randomState) {
        return this.sampleHeightmap(i, j, null, types.isOpaque());
    }

    // For generation - will generate the chunk and save the data to the column
    @Override
    public NoiseColumn getBaseColumn(int i, int j, LevelHeightAccessor levelHeightAccessor, RandomState randomState) {
        BlockState[] blockStates = new BlockState[levelHeightAccessor.getHeight()];
        this.sampleHeightmap(i, j, blockStates, null);
        return new NoiseColumn(levelHeightAccessor.getMinBuildHeight(), blockStates);
    }

    @Override
    public void addDebugScreenInfo(List<String> list, RandomState randomState, BlockPos blockPos) {

    }

    private int sampleHeightmap(int x, int z, @Nullable BlockState[] blockStates, @Nullable Predicate<BlockState> predicate)
    {
        // Get all of the coordinate starts and positions
        int xStart = Math.floorDiv(x, 4);
        int zStart = Math.floorDiv(z, 4);
        int xProgress = Math.floorMod(x, 4);
        int zProgress = Math.floorMod(z, 4);
        double xLerp = (double) xProgress / 4.0;
        double zLerp = (double) zProgress / 4.0;
        // Create the noise data in a 2 * 2 * 32 grid for interpolation.
        double[][] noiseData = new double[4][this.internalGenerator.getNoiseSizeY() + 1];

        // Initialize noise array.
        for (int i = 0; i < noiseData.length; i++)
        {
            noiseData[i] = new double[this.internalGenerator.getNoiseSizeY() + 1];
        }

        // Sample all 4 nearby columns.
        this.internalGenerator.getNoiseColumn(noiseData[0], xStart, zStart);
        this.internalGenerator.getNoiseColumn(noiseData[1], xStart, zStart + 1);
        this.internalGenerator.getNoiseColumn(noiseData[2], xStart + 1, zStart);
        this.internalGenerator.getNoiseColumn(noiseData[3], xStart + 1, zStart + 1);

        // [0, 32] -> noise chunks
        for (int noiseY = this.internalGenerator.getNoiseSizeY() - 1; noiseY >= 0; --noiseY)
        {
            // Gets all the noise in a 2x2x2 cube and interpolates it together.
            // Lower pieces
            double val000 = noiseData[0][noiseY];
            double val010 = noiseData[1][noiseY];
            double val100 = noiseData[2][noiseY];
            double val110 = noiseData[3][noiseY];
            // Upper pieces
            double val001 = noiseData[0][noiseY + 1];
            double val011 = noiseData[1][noiseY + 1];
            double val101 = noiseData[2][noiseY + 1];
            double val111 = noiseData[3][noiseY + 1];

            // [0, 8] -> noise pieces
            for (int pieceY = 7; pieceY >= 0; --pieceY)
            {
                double yLerp = (double) pieceY / 8.0;
                // Density at this position given the current y interpolation
                //double density = MathHelper.lerp3(yLerp, xLerp, zLerp, val000, val001, val100, val101, val010, val011, val110, val111);
                double density = MathHelper.lerp3(xLerp, yLerp, zLerp, val000, val100, val010, val110, val001, val101, val011, val111);

                // Get the real y position (translate noise chunk and noise piece)
                int y = (noiseY * 8) + pieceY;

                BlockState state = this.getBlockState(density, y);
                if (blockStates != null)
                {
                    blockStates[y] = state;
                }

                // return y if it fails the check
                if (predicate != null && predicate.test(state))
                {
                    return y + 1;
                }
            }
        }

        return 0;
    }

    // MC's NoiseChunkGenerator returns defaultBlock and defaultFluid here, so callers
    // apparently don't rely on any blocks (re)placed after base terrain gen, only on
    // the default block/liquid set for the dimension (stone/water for overworld,
    // netherrack/lava for nether), that MC uses for base terrain gen.
    // We can do the same, no need to pass biome config and fetch replaced blocks etc.
    // OTG does place blocks other than defaultBlock/defaultLiquid during base terrain gen
    // (for replaceblocks/sagc), but that shouldn't matter for the callers of this method.
    // Actually, it's probably better if they don't see OTG's replaced blocks, and just see
    // the default blocks instead, as vanilla MC would do.
    private BlockState getBlockState(double density, int y)
    {
        if (density > 0.0D)
        {
            return this.settings.value().defaultBlock();
        }
        else if (y < this.getSeaLevel())
        {
            return this.settings.value().defaultFluid();
        } else {
            return Blocks.AIR.defaultBlockState();
        }
    }
}
