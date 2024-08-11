package com.pg85.otg.fabric.presets;

import com.pg85.otg.config.biome.BiomeConfigFinder;
import com.pg85.otg.gen.biome.layers.BiomeLayerData;
import com.pg85.otg.interfaces.IBiome;
import com.pg85.otg.presets.LocalPresetLoader;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class FabricPresetLoader extends LocalPresetLoader {
    public FabricPresetLoader(Path otgRootFolder) {
        super(otgRootFolder);
    }

    @Override
    public List<ResourceKey<Biome>> getBiomeResourceKeys(String presetFolderName) {
        return List.of();
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
