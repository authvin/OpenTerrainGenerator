package com.pg85.otg.config.settings.preset;

import com.pg85.otg.config.io.SettingsMap;
import com.pg85.otg.config.standard.PresetStandardValues;
import com.pg85.otg.interfaces.IMaterialReader;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SpawnSettings {
    private final boolean spawnPointSet;
    private final int spawnPointX;
    private final int spawnPointY;
    private final int spawnPointZ;
    private final float spawnPointAngle;

    public static SpawnSettings getSpawnSettings(SettingsMap reader, IMaterialReader materialReader) {
        var spawnSettingsBuilder = builder();

        spawnSettingsBuilder.spawnPointSet(reader.getSetting(PresetStandardValues.FIXED_SPAWN_POINT, materialReader));
        spawnSettingsBuilder.spawnPointX(reader.getSetting(PresetStandardValues.SPAWN_POINT_X, materialReader));
        spawnSettingsBuilder.spawnPointY(reader.getSetting(PresetStandardValues.SPAWN_POINT_Y, materialReader));
        spawnSettingsBuilder.spawnPointZ(reader.getSetting(PresetStandardValues.SPAWN_POINT_Z, materialReader));
        spawnSettingsBuilder.spawnPointAngle(reader.getSetting(PresetStandardValues.SPAWN_POINT_ANGLE, materialReader));
        var spawnSettings = spawnSettingsBuilder.build();
        return spawnSettings;
    }
}