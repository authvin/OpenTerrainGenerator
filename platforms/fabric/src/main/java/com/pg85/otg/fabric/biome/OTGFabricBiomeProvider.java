package com.pg85.otg.fabric.biome;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.pg85.otg.OTG;
import com.pg85.otg.config.settings.preset.GenerationSettings;
import com.pg85.otg.gen.OTGChunkGenerator;
import com.pg85.otg.gen.biome.layers.BiomeLayers;
import com.pg85.otg.gen.biome.layers.util.CachingLayerSampler;
import com.pg85.otg.gen.noise.CaveBiomeSelector;
import com.pg85.otg.interfaces.IBiome;
import com.pg85.otg.interfaces.ILayerSampler;
import com.pg85.otg.interfaces.ILayerSource;
import com.pg85.otg.util.logging.LogCategory;
import com.pg85.otg.util.logging.LogLevel;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Getter
public class
OTGFabricBiomeProvider extends BiomeSource implements ILayerSource, BiomeManager.NoiseBiomeSource {
    public static final Codec<OTGFabricBiomeProvider> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.STRING.fieldOf("preset_name").stable().forGetter(OTGFabricBiomeProvider::getPresetFolderName),
                    Codec.LONG.fieldOf("seed").stable().forGetter(OTGFabricBiomeProvider::getSeed)
            ).apply(instance, instance.stable(OTGFabricBiomeProvider::new)));
    private final String presetFolderName;
    //this.layer = ThreadLocal.withInitial(() -> BiomeLayers.create(seed, OTG.getEngine().getPresetLoader().getPresetGenerationData().get(presetFolderName), OTG.getEngine().getLogger()));
    private final CountDownLatch latch = new CountDownLatch(1);
    private long seed;
    private ThreadLocal<CachingLayerSampler> layer;
    private final Int2ObjectOpenHashMap<Holder<Biome>> keyLookup = new Int2ObjectOpenHashMap<>();

    // Cave biome support. The 2D layer stack stays the only source for surface
    // biomes; below the depth boundary the Minecraft-facing lookups return a
    // cave biome instead. OTG's internal column pipeline never sees these.
    private volatile OTGChunkGenerator terrainHeightSource;
    private volatile boolean caveBiomesResolved = false;
    private Holder<Biome>[] caveBiomeHolders;
    private CaveBiomeSelector caveBiomeSelector;
    private int caveBiomeDepthBelowSurface;
    private final ThreadLocal<CaveColumnMemo> caveColumnMemo = ThreadLocal.withInitial(CaveColumnMemo::new);

    public OTGFabricBiomeProvider(String presetFolderName, long seed) {
        this.presetFolderName = presetFolderName;
        this.seed = seed;
    }

    /**
     * Wired by the chunk generator's constructor; until set, biome lookup is
     * pure 2D (no cave biomes).
     */
    public void setTerrainHeightSource(OTGChunkGenerator terrainHeightSource) {
        this.terrainHeightSource = terrainHeightSource;
    }

    @Override
    public ILayerSampler getSampler() {
        return layer.get();
    }

    @Override
    protected Codec<? extends BiomeSource> codec() {
        return CODEC;
    }

    @Override
    protected @NotNull Stream<Holder<Biome>> collectPossibleBiomes() {
        var iBiomes = OTG.getEngine().getPresetLoader().getGlobalIdMapping(presetFolderName);
        if (iBiomes == null) {
            OTG.getEngine().getLogger().log(LogLevel.ERROR, LogCategory.BIOME_REGISTRY,
                    "Biome mapping for preset " + presetFolderName + " is null.");
            return Stream.empty();
        }
        for (int otgBiomeID = 0; otgBiomeID < iBiomes.length; otgBiomeID++) {
            keyLookup.put(otgBiomeID, ((FabricBiome) iBiomes[otgBiomeID]).getBiomeHolder());
        }
        return Stream.of(iBiomes).map(iBiome -> ((FabricBiome) iBiome).getBiomeHolder());
    }

    @Override
    public @Nullable Pair<BlockPos, Holder<Biome>> findBiomeHorizontal(int i, int j, int k, int l, int m, Predicate<Holder<Biome>> predicate, RandomSource randomSource, boolean bl, Climate.Sampler sampler) {
        if (this.getLayer() == null) {
            throw new IllegalStateException("Layer is null. Seed was not set properly.");
        }
        return super.findBiomeHorizontal(i, j, k, l, m, predicate, randomSource, bl, sampler);
    }

    @Override
    public @Nullable Pair<BlockPos, Holder<Biome>> findBiomeHorizontal(int i, int j, int k, int l, Predicate<Holder<Biome>> predicate, RandomSource randomSource, Climate.Sampler sampler) {
        if (this.getLayer() == null) {
            throw new IllegalStateException("Layer is null. Seed was not set properly.");
        }
        return super.findBiomeHorizontal(i, j, k, l, predicate, randomSource, sampler);
    }

    @Override
    public Holder<Biome> getNoiseBiome(int i, int j, int k, Climate.Sampler sampler) {
        // The climate sampler is ignored; OTG drives biomes itself.
        return getNoiseBiome(i, j, k);
    }

    @Override
    public Holder<Biome> getNoiseBiome(int i, int j, int k) {
        Holder<Biome> caveBiome = getCaveBiome(i, j, k);
        if (caveBiome != null) {
            return caveBiome;
        }
        return keyLookup.get(this.getLayer().get().sample(i, k));
    }

    /**
     * The pure 2D lookup, for callers that must keep keying off the surface
     * biome regardless of depth (e.g. per-chunk carver selection).
     */
    public Holder<Biome> getSurfaceNoiseBiome(int i, int k) {
        return keyLookup.get(this.getLayer().get().sample(i, k));
    }

    private Holder<Biome> getCaveBiome(int quartX, int quartY, int quartZ) {
        OTGChunkGenerator heightSource = this.terrainHeightSource;
        // Also wait for setSeed: resolving earlier would bake a stale seed
        // into the cave biome selector.
        if (heightSource == null || this.layer == null) {
            return null;
        }
        resolveCaveBiomes();
        Holder<Biome>[] holders = this.caveBiomeHolders;
        if (holders.length == 0) {
            return null;
        }
        double centerHeight = this.caveColumnMemo.get().fetch(heightSource, quartX, quartZ);
        if ((quartY << 2) >= centerHeight - this.caveBiomeDepthBelowSurface) {
            return null;
        }
        return holders[this.caveBiomeSelector.sample(quartX, quartY, quartZ)];
    }

    @SuppressWarnings("unchecked")
    private void resolveCaveBiomes() {
        if (this.caveBiomesResolved) {
            return;
        }
        synchronized (this) {
            if (this.caveBiomesResolved) {
                return;
            }
            GenerationSettings generationSettings = OTG.getEngine().getPresetLoader()
                    .getPresetByFolderName(this.presetFolderName).getPresetConfig().getGenerationSettings();
            List<Holder<Biome>> holders = new ArrayList<>();
            IBiome[] iBiomes = OTG.getEngine().getPresetLoader().getGlobalIdMapping(this.presetFolderName);
            if (iBiomes != null) {
                for (String caveBiomeName : generationSettings.getCaveBiomes()) {
                    Holder<Biome> match = null;
                    for (IBiome iBiome : iBiomes) {
                        if (caveBiomeName.equals(iBiome.getBiomeSettings().getIdentitySettings().getBiomeName())) {
                            match = ((FabricBiome) iBiome).getBiomeHolder();
                            break;
                        }
                    }
                    if (match == null) {
                        OTG.getEngine().getLogger().log(LogLevel.ERROR, LogCategory.BIOME_REGISTRY,
                                "CaveBiomes entry \"" + caveBiomeName + "\" not found in preset "
                                        + this.presetFolderName + ", skipping it.");
                    } else {
                        holders.add(match);
                    }
                }
            }
            this.caveBiomeDepthBelowSurface = generationSettings.getCaveBiomeDepthBelowSurface();
            this.caveBiomeSelector = CaveBiomeSelector.fromBlockSizes(
                    this.seed,
                    generationSettings.getCaveBiomeRegionSize(),
                    generationSettings.getCaveBiomeRegionHeight(),
                    Math.max(1, holders.size())
            );
            this.caveBiomeHolders = holders.toArray(new Holder[0]);
            this.caveBiomesResolved = true;
        }
    }

    private static final class CaveColumnMemo {
        private double centerHeight;
        private int quartX = Integer.MAX_VALUE;
        private int quartZ = Integer.MAX_VALUE;

        private double fetch(OTGChunkGenerator generator, int quartX, int quartZ) {
            if (this.quartX != quartX || this.quartZ != quartZ) {
                this.centerHeight = generator.getColumnCenterHeightInBlocks(quartX, quartZ);
                this.quartX = quartX;
                this.quartZ = quartZ;
            }
            return this.centerHeight;
        }
    }

    public void setSeed(long seed) {
        synchronized (this) {
            if (this.seed == seed && this.layer != null) {
                return;
            }
            if (this.seed != seed && this.layer != null) {
                OTG.getEngine().getLogger().log(LogLevel.ERROR, LogCategory.MAIN,
                        "Biome provider seed changed from " + this.seed + " to " + seed +
                                ". This is unexpected and may lead to inconsistent biome generation.");
            }
            this.seed = seed;
            layer = ThreadLocal.withInitial(() -> BiomeLayers.create(seed, OTG.getEngine().getPresetLoader().getPresetGenerationData().get(presetFolderName), OTG.getEngine().getLogger()));
            latch.countDown();
        }
    }
}
