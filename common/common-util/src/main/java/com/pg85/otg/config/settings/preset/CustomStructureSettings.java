package com.pg85.otg.config.settings.preset;

import com.pg85.otg.config.io.SettingsMap;
import com.pg85.otg.config.standard.PresetStandardValues;
import com.pg85.otg.constants.settings.structure.CustomStructureType;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CustomStructureSettings {
    private final String BO3AtSpawn;
    private final CustomStructureType customStructureType;
    private final boolean useOldBO3StructureRarity;
    private final boolean decorationBoundsCheck;
    private final int maximumCustomStructureRadius;

    public static CustomStructureSettings getCustomStructureSettings(SettingsMap reader) {
        var customStructureSettingsBuilder = builder();

        customStructureSettingsBuilder.customStructureType(reader.getSetting(PresetStandardValues.CUSTOM_STRUCTURE_TYPE));
        customStructureSettingsBuilder.useOldBO3StructureRarity(reader.getSetting(PresetStandardValues.USE_OLD_BO3_STRUCTURE_RARITY));
        customStructureSettingsBuilder.decorationBoundsCheck(reader.getSetting(PresetStandardValues.DECORATION_BOUNDS_CHECK));
        customStructureSettingsBuilder.maximumCustomStructureRadius(reader.getSetting(PresetStandardValues.MAXIMUM_CUSTOM_STRUCTURE_RADIUS));
        customStructureSettingsBuilder.BO3AtSpawn(reader.getSetting(PresetStandardValues.BO3_AT_SPAWN));
        var customStructureSettings = customStructureSettingsBuilder.build();
        return customStructureSettings;
    }
}