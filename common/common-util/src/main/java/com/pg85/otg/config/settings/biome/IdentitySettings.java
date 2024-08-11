package com.pg85.otg.config.settings.biome;

import com.pg85.otg.config.io.SettingsMap;
import com.pg85.otg.config.settingType.Setting;
import com.pg85.otg.config.settingType.Settings;
import com.pg85.otg.config.settings.ConfigSection;
import com.pg85.otg.constants.settings.TemplateBiomeType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class IdentitySettings extends ConfigSection {
    private final String biomeCategory;
    private final String biomeName;
    private final boolean isTemplateForBiome;
    private final TemplateBiomeType templateBiomeType;
    private final List<String> biomeDictTags;

    @Override
    public String getSectionName() {
        return "Biome Identity Settings";
    }

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
    public static final Setting<TemplateBiomeType> TEMPLATE_BIOME_TYPE = Settings.enumSetting(
            "TemplateBiomeType",
            TemplateBiomeType.Overworld,
            t -> ((IdentitySettings)t).getTemplateBiomeType(),
            "If this is a template biome config for an overworld biome, set this to Overworld. STONE is used for base terrain generation.",
            "If this is a template biome config for a nether biome, set this to Nether. NETHERRACK is used for base terrain generation.",
            "If this is a template biome config for an end biome, set this to End. END_STONE is used for base terrain generation."
    );
    public static final Setting<String> BIOME_CATEGORY = Settings.stringSetting(
            "BiomeCategory",
            "plains",
            t -> ((IdentitySettings)t).getBiomeCategory(),
            "Set a category for this biome, used by vanilla for... something",
            "Accepts one of the following values:",
            "none, taiga, extreme_hills, jungle, mesa, plains, savanna, icy, the_end, beach, forest, ocean, desert, river, swamp, mushroom, nether",
            "TemplateForBiome biomes inherit this from the targeted biomes."
    );
    public static final Setting<List<String>> BIOME_DICT_TAGS = Settings.stringListSetting(
            "BiomeDictTags",
            new String[]{""},
            t -> ((IdentitySettings)t).getBiomeDictTags(),
            "Forge Biome Dictionary tags used by other mods to identify a biome and",
            "place modded blocks, items and mobs in it.", "Example: HOT, DRY, SANDY, OVERWORLD",
            "TemplateForBiome biomes inherit these from the targeted biomes."
    );

    public static IdentitySettings buildIdentitySettings(SettingsMap reader) {
        IdentitySettingsBuilder builder = IdentitySettings.builder();

        builder.biomeCategory(reader.getSetting(BIOME_CATEGORY));
        builder.biomeName(reader.getName());
        builder.isTemplateForBiome(reader.getSetting(IS_TEMPLATE_FOR_BIOME));
        builder.templateBiomeType(reader.getSetting(TEMPLATE_BIOME_TYPE));
        builder.biomeDictTags(reader.getSetting(BIOME_DICT_TAGS));

        return builder.build();
    }
}
