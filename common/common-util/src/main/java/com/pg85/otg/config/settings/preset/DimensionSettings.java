package com.pg85.otg.config.settings.preset;

import com.pg85.otg.config.io.SettingsMap;
import com.pg85.otg.config.standard.PresetStandardValues;
import lombok.Builder;
import lombok.Getter;

import java.util.OptionalLong;

@Builder
@Getter
public class DimensionSettings {
    private final OptionalLong fixedTime;
    private final boolean hasSkyLight;
    private final boolean hasCeiling;
    private final boolean ultraWarm;
    private final boolean natural;
    private final double coordinateScale;
    private final boolean createDragonFight;
    private final boolean piglinSafe;
    private final boolean bedWorks;
    private final boolean respawnAnchorWorks;
    private final boolean hasRaids;
    private final int logicalHeight;
    private final String infiniburn;
    private final String effectsLocation;
    private final double ambientLight;

    public static DimensionSettings getDimensionSettings(SettingsMap reader) {
        var dimensionSettingsBuilder = builder();

        long fixedTime = reader.getSetting(PresetStandardValues.FIXED_TIME);
        dimensionSettingsBuilder.fixedTime(fixedTime == -1L ? OptionalLong.empty() : OptionalLong.of(fixedTime));
        dimensionSettingsBuilder.hasSkyLight(reader.getSetting(PresetStandardValues.HAS_SKYLIGHT));
        dimensionSettingsBuilder.hasCeiling(reader.getSetting(PresetStandardValues.HAS_CEILING));
        dimensionSettingsBuilder.ultraWarm(reader.getSetting(PresetStandardValues.ULTRA_WARM));
        dimensionSettingsBuilder.natural(reader.getSetting(PresetStandardValues.NATURAL));
        dimensionSettingsBuilder.coordinateScale(reader.getSetting(PresetStandardValues.COORDINATE_SCALE));
        dimensionSettingsBuilder.createDragonFight(reader.getSetting(PresetStandardValues.CREATE_DRAGON_FLIGHT));
        dimensionSettingsBuilder.piglinSafe(reader.getSetting(PresetStandardValues.PIGLIN_SAFE));
        dimensionSettingsBuilder.bedWorks(reader.getSetting(PresetStandardValues.BED_WORKS));
        dimensionSettingsBuilder.respawnAnchorWorks(reader.getSetting(PresetStandardValues.RESPAWN_ANCHOR_WORKS));
        dimensionSettingsBuilder.hasRaids(reader.getSetting(PresetStandardValues.HAS_RAIDS));
        dimensionSettingsBuilder.logicalHeight(reader.getSetting(PresetStandardValues.LOGICAL_HEIGHT));
        dimensionSettingsBuilder.infiniburn(reader.getSetting(PresetStandardValues.INFINIBURN));
        dimensionSettingsBuilder.effectsLocation(reader.getSetting(PresetStandardValues.EFFECTS_LOCATION));
        dimensionSettingsBuilder.ambientLight(reader.getSetting(PresetStandardValues.AMBIENT_LIGHT).floatValue());

        var dimensionSettings = dimensionSettingsBuilder.build();
        return dimensionSettings;
    }
}