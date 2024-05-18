package com.pg85.otg.config.settings.biome;

import com.pg85.otg.config.io.SettingsMap;
import com.pg85.otg.config.standard.BiomeStandardValues;
import com.pg85.otg.constants.settings.TemplateBiomeType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class IdentitySettings {
    private final String biomeCategory;
    private final String biomeName;
    private final boolean isTemplateForBiome;
    private final TemplateBiomeType templateBiomeType;
    private final List<String> biomeDictTags;

    public static IdentitySettings buildIdentitySettings(SettingsMap reader, String biomeName) {
        IdentitySettingsBuilder builder = IdentitySettings.builder();

        builder.biomeCategory(reader.getSetting(BiomeStandardValues.BIOME_CATEGORY));
        builder.biomeName(biomeName);
        builder.isTemplateForBiome(reader.getSetting(BiomeStandardValues.IS_TEMPLATE_FOR_BIOME));
        builder.templateBiomeType(reader.getSetting(BiomeStandardValues.TEMPLATE_BIOME_TYPE));
        builder.biomeDictTags(reader.getSetting(BiomeStandardValues.BIOME_DICT_TAGS));

        return builder.build();
    }
}
