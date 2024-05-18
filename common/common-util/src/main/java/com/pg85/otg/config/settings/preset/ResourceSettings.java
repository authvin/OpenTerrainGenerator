package com.pg85.otg.config.settings.preset;

import com.pg85.otg.config.io.SettingsMap;
import com.pg85.otg.config.standard.PresetStandardValues;
import com.pg85.otg.constants.settings.structure.CustomStructureType;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ResourceSettings {
    private final boolean disableOreGen;
    private final String BO3AtSpawn;
    private final CustomStructureType customStructureType;
    private final boolean useOldBO3StructureRarity;
    private final boolean decorationBoundsCheck;
    private final int maximumCustomStructureRadius;

    public static ResourceSettings getResourceSettings(SettingsMap reader) {
        var resourceSettingsBuilder = builder();
        resourceSettingsBuilder.disableOreGen(reader.getSetting(PresetStandardValues.DISABLE_OREGEN));
        resourceSettingsBuilder.customStructureType(reader.getSetting(PresetStandardValues.CUSTOM_STRUCTURE_TYPE));
        resourceSettingsBuilder.useOldBO3StructureRarity(reader.getSetting(PresetStandardValues.USE_OLD_BO3_STRUCTURE_RARITY));
        resourceSettingsBuilder.decorationBoundsCheck(reader.getSetting(PresetStandardValues.DECORATION_BOUNDS_CHECK));
        resourceSettingsBuilder.maximumCustomStructureRadius(reader.getSetting(PresetStandardValues.MAXIMUM_CUSTOM_STRUCTURE_RADIUS));
        resourceSettingsBuilder.BO3AtSpawn(reader.getSetting(PresetStandardValues.BO3_AT_SPAWN));
        return resourceSettingsBuilder.build();
    }
}