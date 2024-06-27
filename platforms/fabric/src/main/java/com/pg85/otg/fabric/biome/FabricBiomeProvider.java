package com.pg85.otg.fabric.biome;

import com.mojang.serialization.Codec;
import com.pg85.otg.interfaces.ILayerSampler;
import com.pg85.otg.interfaces.ILayerSource;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;

import java.util.stream.Stream;

public class FabricBiomeProvider extends BiomeSource implements ILayerSource {
    @Override
    public ILayerSampler getSampler() {
        return null;
    }

    @Override
    protected Codec<? extends BiomeSource> codec() {
        return null;
    }

    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        return Stream.empty();
    }

    @Override
    public Holder<Biome> getNoiseBiome(int i, int j, int k, Climate.Sampler sampler) {
        return null;
    }
}
