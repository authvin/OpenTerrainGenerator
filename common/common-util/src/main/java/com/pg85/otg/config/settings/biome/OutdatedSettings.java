package com.pg85.otg.config.settings.biome;

import com.pg85.otg.config.settings.preset.TerrainSettings;
import com.pg85.otg.config.settingtype.Setting;
import com.pg85.otg.config.settingtype.Settings;

public class OutdatedSettings {
    // Set a category for this biome, used by vanilla for... something
    // Accepts one of the following values:
    //   none, taiga, extreme_hills, jungle, mesa, plains, savanna, icy, the_end,
    //   beach, forest, ocean, desert, river, swamp, mushroom, nether.
    // TemplateForBiome biomes inherit this from the targeted biomes.
    public static final Setting<String> BIOME_CATEGORY = Settings.stringSetting(
            "BiomeCategory",
            "plains"
    );
    public static final Setting<Boolean> IS_TEMPLATE_FOR_BIOME = Settings.booleanSetting(
            "TemplateForBiome",
            false,
            t -> ((IdentitySettings)t).isTemplateForBiome(),
            "Set this to true if this biome config is used with non-OTG biomes, configured in the PresetConfig via TemplateBiome()",
            "OTG generates the terrain for the biome as configured in this file and spawns resources, but also allows the biome to spawn ",
            "its own resources and mobs and apply its settings. Because of this, the following OTG settings cannot be used:",
            "- Colors, Mob spawning, particles, sounds, vanilla structures, wetness, temperature.",
            "What can be configured: ",
            " - Biome generator settings.",
            " - Terrain settings.",
            " - Resources. Non-OTG biome resources are currently spawned after all OTG resources in the resourcequeue.",
            " - OTG settings not mentioned above that are handled by OTG and don't rely on MC logic."
    );
    public static final Setting<Integer> WORLD_HEIGHT_SCALE_BITS = Settings.intSetting(
            "WorldHeightScaleBits", 7, 5, 8,
            t -> ((TerrainSettings) t).getWorldHeightScale(),
            "The height scale of the world. Increasing this by one doubles the terrain height of the world,",
            "substracting one halves the terrain height. Values must be between 5 and 8, inclusive."
    );
    public static final Setting<Integer> WORLD_HEIGHT_CAP_BITS = Settings.intSetting(
            "WorldHeightCapBits", 8, 5, 8,
            t -> ((TerrainSettings) t).getWorldHeightCap(),
            "The height cap of the world. A cap of 7 will make sure that there is no terrain above 128 (y=2^7). Near this cap less and less terrain generates with no terrain above this cap.",
            "Values must be between 5 and 8 (inclusive), and may not be lower that WorldHeightScaleBits."
    );
    public static final Setting<Integer> WORLD_HEIGHT_CAP = Settings.intSetting(
            "WorldHeightCap", 256, 16, 2032,
            t -> ((TerrainSettings) t).getWorldHeightCap(),
            "The height cap of the world"
    );
}
