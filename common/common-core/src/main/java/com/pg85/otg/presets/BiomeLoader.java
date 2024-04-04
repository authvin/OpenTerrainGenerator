package com.pg85.otg.presets;

import com.pg85.otg.interfaces.IBiomeConfig;

import java.util.List;

public abstract class BiomeLoader {
    // load a biome config
    public abstract IBiomeConfig getBiome(String name);
    // list of loaded biomes
    public abstract List<IBiomeConfig> listBiomes();
    // load all biomes for a preset
    public abstract List<IBiomeConfig> loadBiomes(Preset preset);
}
