package com.pg85.otg.fabric.presets;

import com.pg85.otg.config.biome.BiomeConfigFinder;
import com.pg85.otg.fabric.materials.FabricMaterialReader;
import com.pg85.otg.interfaces.IMaterialReader;
import com.pg85.otg.presets.LocalPresetLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;

import java.nio.file.Path;

public class FabricPresetLoader extends LocalPresetLoader {
    public FabricPresetLoader(Path otgRootFolder) {
        super(otgRootFolder);
    }

    @Override
    protected IMaterialReader createMaterialReader() {
        return new FabricMaterialReader();
    }

    @Override
    protected void mergeVanillaBiomeMobSpawnSettings(BiomeConfigFinder.BiomeConfigStub biomeConfigStub, String inheritMobsBiomeName) {

    }
}
