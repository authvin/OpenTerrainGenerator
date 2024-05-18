package com.pg85.otg.config.settings.biome;

import com.pg85.otg.config.io.SettingsMap;
import com.pg85.otg.config.settings.preset.TerrainSettings;
import com.pg85.otg.config.standard.BiomeStandardValues;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BiomeTerrainSettings {
    private final TerrainSettings parent;
    private final float biomeHeight;
    private final float biomeVolatility;
    private final int smoothRadius;
    private final int CHCSmoothRadius;
    private final double maxAverageHeight;
    private final double maxAverageDepth;
    private final double volatility1;
    private final double volatility2;
    private final double volatilityWeight1;
    private final double volatilityWeight2;
    private final boolean disableBiomeHeight;
    private final double[] customHeightControl;

    public static BiomeTerrainSettings getBiomeTerrainSettings(SettingsMap reader, TerrainSettings parent) {
        BiomeTerrainSettingsBuilder builder = BiomeTerrainSettings.builder();

        builder.parent(parent);
        builder.biomeHeight(reader.getSetting(BiomeStandardValues.BIOME_HEIGHT));
        builder.biomeVolatility(reader.getSetting(BiomeStandardValues.BIOME_VOLATILITY));
        builder.smoothRadius(reader.getSetting(BiomeStandardValues.SMOOTH_RADIUS));
        builder.CHCSmoothRadius(reader.getSetting(BiomeStandardValues.CUSTOM_HEIGHT_CONTROL_SMOOTH_RADIUS));
        builder.maxAverageHeight(reader.getSetting(BiomeStandardValues.MAX_AVERAGE_HEIGHT));
        builder.maxAverageDepth(reader.getSetting(BiomeStandardValues.MAX_AVERAGE_DEPTH));
        builder.volatility1(reader.getSetting(BiomeStandardValues.VOLATILITY_1));
        builder.volatility2(reader.getSetting(BiomeStandardValues.VOLATILITY_2));
        builder.volatilityWeight1(reader.getSetting(BiomeStandardValues.VOLATILITY_WEIGHT_1));
        builder.volatilityWeight2(reader.getSetting(BiomeStandardValues.VOLATILITY_WEIGHT_2));
        int configVersion = reader.getVersion();
        if (configVersion < 2) {
            // In older configs, the values were stored as negative values and then converted
            builder.volatility1(builder.volatility1 < 0.00D ? 1.0D / Math.abs(builder.volatility1) : builder.volatility1 + 1.0D);
            builder.volatility2(builder.volatility2 < 0.00D ? 1.0D / Math.abs(builder.volatility2) : builder.volatility2 + 1.0D);
            builder.volatilityWeight1((builder.volatilityWeight1 - 0.5D) * 24.0D);
            builder.volatilityWeight2((builder.volatilityWeight2 - 0.5D) * 24.0D);
        }

        builder.disableBiomeHeight(reader.getSetting(BiomeStandardValues.DISABLE_BIOME_HEIGHT));
        builder.customHeightControl(builder.readHeightSettings(reader));

        return builder.fixSettings().build();
    }

    public static class BiomeTerrainSettingsBuilder {
        protected double[] readHeightSettings(SettingsMap settings)
        {
            double[] heightMatrix = new double[this.parent.getWorldHeightCap() / this.parent.getWorldHeightScale() + 1];
            double[] keys = settings.getSetting(BiomeStandardValues.CUSTOM_HEIGHT_CONTROL);
            for (int i = 0; i < heightMatrix.length && i < keys.length; i++)
            {
                heightMatrix[i] = keys[i];
            }
            return heightMatrix;
        }
        public BiomeTerrainSettingsBuilder fixSettings() {
            return this;
        }
    }

}