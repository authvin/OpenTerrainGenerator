package com.pg85.otg.config.settings.preset;

import com.pg85.otg.config.io.SettingsMap;
import com.pg85.otg.config.standard.PresetStandardValues;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TerrainSettings {
    private final double fractureHorizontal;
    private final double fractureVertical;
    private final int worldHeightCap;
    private final int worldHeightScale;
    private final boolean betterSnowFall;
    private final int waterLevelMax;
    private final int waterLevelMin;
    private final int carverLavaBlockHeight;

    public static TerrainSettings getTerrainSettings(SettingsMap reader) {
        var terrainSettingsBuilder = builder();

        terrainSettingsBuilder.fractureHorizontal(reader.getSetting(PresetStandardValues.FRACTURE_HORIZONTAL));
        terrainSettingsBuilder.fractureVertical(reader.getSetting(PresetStandardValues.FRACTURE_VERTICAL));
        terrainSettingsBuilder.worldHeightCap(1 << reader.getSetting(PresetStandardValues.WORLD_HEIGHT_CAP_BITS));
        terrainSettingsBuilder.worldHeightScale(1 << reader.getSetting(PresetStandardValues.WORLD_HEIGHT_SCALE_BITS));
        terrainSettingsBuilder.betterSnowFall(reader.getSetting(PresetStandardValues.BETTER_SNOW_FALL));
        terrainSettingsBuilder.waterLevelMax(reader.getSetting(PresetStandardValues.WATER_LEVEL_MAX));
        terrainSettingsBuilder.waterLevelMin(reader.getSetting(PresetStandardValues.WATER_LEVEL_MIN));
        terrainSettingsBuilder.carverLavaBlockHeight(reader.getSetting(PresetStandardValues.CARVER_LAVA_BLOCK_HEIGHT));

        var terrainSettings = terrainSettingsBuilder.fixSettings().build();
        return terrainSettings;
    }

    public static class TerrainSettingsBuilder {
        public TerrainSettingsBuilder fixSettings() {
            checkWaterLevelMax();
            return this;
        }
        private void checkWaterLevelMax() {
            waterLevelMax = Math.max(waterLevelMax, waterLevelMin);
        }
    }
}