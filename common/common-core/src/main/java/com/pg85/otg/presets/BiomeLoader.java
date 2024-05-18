package com.pg85.otg.presets;

import com.pg85.otg.config.settings.biome.BiomeSettings;

import java.util.List;

public abstract class BiomeLoader {
    // load a biome config
    public abstract BiomeSettings getBiome(String name);
    // list of loaded biomes
    public abstract List<BiomeSettings> listBiomes();
    // load all biomes for a preset
    public abstract List<BiomeSettings> loadBiomes(Preset preset);
}
