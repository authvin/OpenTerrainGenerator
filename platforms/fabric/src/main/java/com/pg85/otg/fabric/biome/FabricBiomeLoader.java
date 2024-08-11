package com.pg85.otg.fabric.biome;

import com.pg85.otg.config.settings.biome.BiomeSettings;
import com.pg85.otg.config.settings.preset.PresetSettings;
import com.pg85.otg.presets.Preset;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.MobSpawnSettings;

public class FabricBiomeLoader {

    public FabricBiome getBiome(Preset preset, String biomeName) {
        // load the BiomeSettings from file


        // let's divide this up into individual component loaders. Visual loads visual settings, terrain loads terrain settings, etc.
        // Wherever we need to interface with MC, we do it like this.
        // -> This might let us abstract a lot of the logic upwards, and keep only the visual etc part in the platform-specific layer.

        // This function should only create a single biome. Let's keep it like that.

        Biome.BiomeBuilder builder = new Biome.BiomeBuilder();

        // generation settings
        // has two builders.
        // One is plain, and takes features or carvers.
        // with carvers, it requires a HolderGetter (a registry)
        // with features, it just requires a decoration step

        // The other builder is constructed with a registry for features and carvers
        // it then adds decoration step as an argument to its function

        // with the standard builder, we can still use the methods from the plain builder.
        // this means we can add features that aren't registered

        // visual settings

        // mob settings

        // temperature settings

        // special effect settings

        // create a biome using BiomeBuilder
        // create a FabricBiome with the two
        // return the new FabricBiome
        return null;
    }
}
