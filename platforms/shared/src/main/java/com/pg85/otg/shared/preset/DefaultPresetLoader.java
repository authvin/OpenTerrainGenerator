package com.pg85.otg.shared.preset;

import com.pg85.otg.gen.biome.layers.BiomeLayerData;
import com.pg85.otg.interfaces.IBiome;
import com.pg85.otg.presets.LocalPresetLoader;

import java.nio.file.Path;
import java.util.Map;

public class DefaultPresetLoader extends LocalPresetLoader {
    public DefaultPresetLoader(Path otgRootFolder) {
        super(otgRootFolder);
    }

    @Override
    public IBiome[] getGlobalIdMapping(String presetFolderName) {
        return new IBiome[0];
    }

    @Override
    public Map<String, BiomeLayerData> getPresetGenerationData() {
        return Map.of();
    }
}
