package com.pg85.otg.fabric.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.pg85.otg.OTG;
import com.pg85.otg.gen.biome.layers.BiomeLayers;
import com.pg85.otg.gen.biome.layers.util.CachingLayerSampler;
import com.pg85.otg.interfaces.ILayerSampler;
import com.pg85.otg.interfaces.ILayerSource;
import lombok.Getter;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;

import java.util.stream.Stream;

@Getter
public class OTGFabricBiomeProvider extends BiomeSource implements ILayerSource, BiomeManager.NoiseBiomeSource {
    public static final Codec<OTGFabricBiomeProvider> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.STRING.fieldOf("preset_name").stable().forGetter(OTGFabricBiomeProvider::getPresetFolderName),
                    Codec.LONG.fieldOf("seed").stable().forGetter(OTGFabricBiomeProvider::getSeed)
            ).apply(instance, instance.stable(OTGFabricBiomeProvider::new)));
    private final String presetFolderName;
    private final Long seed;
    private final ThreadLocal<CachingLayerSampler> layer;

    public OTGFabricBiomeProvider(String presetFolderName, Long seed) {
        this.presetFolderName = presetFolderName;
        this.seed = seed;
        this.layer = ThreadLocal.withInitial(() -> BiomeLayers.create(seed, OTG.getEngine().getPresetLoader().getPresetGenerationData().get(presetFolderName), OTG.getEngine().getLogger()));
    }

    @Override
    public ILayerSampler getSampler() {
        return layer.get();
    }

    @Override
    protected Codec<? extends BiomeSource> codec() {
        return codec();
    }

    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        return Stream.empty();
    }

    @Override
    public Holder<Biome> getNoiseBiome(int i, int j, int k, Climate.Sampler sampler) {
        return null;
    }

    @Override
    public Holder<Biome> getNoiseBiome(int i, int j, int k) {
        return null;
    }
}
