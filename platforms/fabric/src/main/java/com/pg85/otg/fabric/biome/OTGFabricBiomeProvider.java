package com.pg85.otg.fabric.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.pg85.otg.OTG;
import com.pg85.otg.gen.biome.layers.BiomeLayers;
import com.pg85.otg.gen.biome.layers.util.CachingLayerSampler;
import com.pg85.otg.interfaces.ILayerSampler;
import com.pg85.otg.interfaces.ILayerSource;
import com.pg85.otg.util.logging.LogCategory;
import com.pg85.otg.util.logging.LogLevel;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

@Getter
public class OTGFabricBiomeProvider extends BiomeSource implements ILayerSource, BiomeManager.NoiseBiomeSource {
    public static final Codec<OTGFabricBiomeProvider> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.STRING.fieldOf("preset_name").stable().forGetter(OTGFabricBiomeProvider::getPresetFolderName)
            ).apply(instance, instance.stable(OTGFabricBiomeProvider::new)));
    private final String presetFolderName;
    //this.layer = ThreadLocal.withInitial(() -> BiomeLayers.create(seed, OTG.getEngine().getPresetLoader().getPresetGenerationData().get(presetFolderName), OTG.getEngine().getLogger()));

    private long seed;
    private ThreadLocal<CachingLayerSampler> layer;
    private final Int2ObjectOpenHashMap<Holder<Biome>> keyLookup = new Int2ObjectOpenHashMap<>();

    public OTGFabricBiomeProvider(String presetFolderName) {
        this.presetFolderName = presetFolderName;
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
    public Holder<Biome> getNoiseBiome(int i, int j, int k, Climate.Sampler sampler) {
        return keyLookup.get(this.getLayer().get().sample(i, k));
    }

    @Override
    public Holder<Biome> getNoiseBiome(int i, int j, int k) {
        return keyLookup.get(this.getLayer().get().sample(i, k));
    }

    public void setSeed(long seed) {
        this.seed = seed;
        layer = ThreadLocal.withInitial(() -> BiomeLayers.create(seed, OTG.getEngine().getPresetLoader().getPresetGenerationData().get(presetFolderName), OTG.getEngine().getLogger()));
    }
}
