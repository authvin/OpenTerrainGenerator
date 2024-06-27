package com.pg85.otg.fabric.biome;

import com.pg85.otg.config.settings.biome.BiomeSettings;
import com.pg85.otg.interfaces.IBiome;
import net.minecraft.world.level.biome.Biome;

public class FabricBiome implements IBiome {
    private final BiomeSettings biomeSettings;
    private final Biome biome;

    public FabricBiome(BiomeSettings biomeSettings, Biome biome) {
        this.biomeSettings = biomeSettings;
        this.biome = biome;
    }

    @Override
    public BiomeSettings getBiomeConfig() {
        return biomeSettings;
    }

    @Override
    public float getTemperatureAt(int x, int y, int z) {
        return biome.getBaseTemperature();
    }
}
