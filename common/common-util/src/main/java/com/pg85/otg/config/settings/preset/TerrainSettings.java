package com.pg85.otg.config.settings.preset;

import com.pg85.otg.config.io.SettingsMap;
import com.pg85.otg.config.settings.biome.OutdatedSettings;
import com.pg85.otg.config.settingtype.IntSetting;
import com.pg85.otg.config.settingtype.Setting;
import com.pg85.otg.config.settingtype.Settings;
import com.pg85.otg.config.settings.ConfigSection;
import com.pg85.otg.constants.Constants;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TerrainSettings extends ConfigSection {
    private final double fractureHorizontal;
    private final double fractureVertical;
    private final int worldHeightCap;
    private final int worldHeightScale;
    private final int minY;
    private final int height;
    private final boolean betterSnowFall;
    private final int waterLevelMax;
    private final int waterLevelMin;
    private final int carverLavaBlockHeight;
    private final int chcStart;
    private final double continentalScale;
    private final double continentalBias;
    private final double baseHeightFraction;
    private final double biomeHeightWeight;
    private final double continentalHeightWeight;
    private final double falloffSteepness;
    private final double noiseAmplitude;

    public static final Setting<Boolean> BETTER_SNOW_FALL = Settings.booleanSetting(
            "BetterSnowFall", false,
            t -> ((TerrainSettings) t).isBetterSnowFall(),
            "When set to false, 1 layer of snow falls on the highest block only.",
            "When set to true, the number of layers (1-8) is dependent on biome temperature.",
            "Higher altitudes have lower temperatures, so snow becomes deeper higher up.",
            "Also causes snow to fall through leaves, leaves can carry 3 layers while the rest falls through."
    );

    public static final Setting<Integer> WORLD_HEIGHT_SCALE = Settings.intSetting(
            "WorldHeightScale", 128, 16, 2032,
            t -> ((TerrainSettings) t).getWorldHeightScale(),
            "Surface Height in the world"
    );

    public static final Setting<Integer> MIN_Y = Settings.intSetting(
            "MinY", 0, Constants.WORLD_START_MIN_Y, Constants.WORLD_END_MAX_Y-15,
            t -> ((TerrainSettings) t).getMinY(),
            "Minimum Y value for this dimension, 0 by default.",
            "Must be a multiple of 16."
    );
    public static final Setting<Integer> HEIGHT = Settings.intSetting(
            "Height", 256, 16, Constants.WORLD_MAX_HEIGHT,
            t -> ((TerrainSettings) t).getHeight(),
            "Total height of this dimension, 256 by default (0 to 255).",
            "Must be a multiple of 16."
    );

    public static final Setting<Integer> WATER_LEVEL_MAX = Settings.intSetting(
            "WaterLevelMax", 63, Constants.WORLD_START_MIN_Y, Constants.WORLD_END_MAX_Y,
            t -> ((TerrainSettings) t).getWaterLevelMax(),
            "Set water level. Every empty block under this level down to min will be fill water or another block from WaterBlock."
    );
    public static final Setting<Integer> WATER_LEVEL_MIN = Settings.intSetting(
            "WaterLevelMin", 0, Constants.WORLD_START_MIN_Y, Constants.WORLD_END_MAX_Y,
            t -> ((TerrainSettings) t).getWaterLevelMin(),
            "Set water level. Every empty block over this level up to max will be fill water or another block from WaterBlock."
    );
    public static final Setting<Integer> CARVER_LAVA_BLOCK_HEIGHT = Settings.intSetting(
            "CarverLavaBlockHeight", 10, Constants.WORLD_START_MIN_Y, Constants.WORLD_END_MAX_Y,
            t -> ((TerrainSettings) t).getCarverLavaBlockHeight(),
            "All air blocks are replaced to CarverLavaBlock from world bottom up to CarverLavaBlockHeight.",
            "For example, vanilla replaces air in caves with lava up to Y10.",
            "Defaults to: 10"
    );
    public static final Setting<Double> FRACTURE_HORIZONTAL = Settings.doubleSetting(
            "FractureHorizontal", 1.0, -500, 500,
            t -> ((TerrainSettings) t).getFractureHorizontal(),
            "Multiplier for horizontal terrain noise frequency. 1.0 = default feature size.",
            "Above 1.0 fractures the landscape more (smaller, busier features).",
            "Below 1.0 (but above 0) relaxes it, leading to more gradual and smoother height transitions.",
            "Legacy (version 1) configs store 0-centered values; these are converted on load."
    );
    public static final Setting<Double> FRACTURE_VERTICAL = Settings.doubleSetting(
            "FractureVertical", 1.0, -500, 500,
            t -> ((TerrainSettings) t).getFractureVertical(),
            "Multiplier for vertical terrain noise frequency. 1.0 = default feature size.",
            "Above 1.0 leads to large cliffs/overhangs, floating islands, and/or a cavern world depending on other settings.",
            "Below 1.0 (but above 0) makes terrain volatility more 'spiky' but lessens overhangs and floating terrain.",
            "Legacy (version 1) configs store 0-centered values; these are converted on load."
    );

    public static final Setting<Integer> CHC_START = new IntSetting(
        "CHCStart", 0, Constants.WORLD_START_MIN_Y, Constants.WORLD_END_MAX_Y - 15,
        t -> ((TerrainSettings) t).getChcStart(),
        "Start Y for custom height control array. Default is 0. Must be divisible by 8."
    );

    public static final Setting<Double> CONTINENTAL_SCALE = Settings.doubleSetting(
            "ContinentalScale", 0.2, 0.0, 10.0,
            t -> ((TerrainSettings) t).getContinentalScale(),
            "Overall amplitude of continental height variation.",
            "Controls how much the large-scale terrain undulates vertically.",
            "0 = no continental variation (flat baseline), 0.2 = default, higher = more dramatic."
    );
    public static final Setting<Double> CONTINENTAL_BIAS = Settings.doubleSetting(
            "ContinentalBias", -0.05, -1.0, 1.0,
            t -> ((TerrainSettings) t).getContinentalBias(),
            "Shifts the balance between valleys and peaks in continental noise.",
            "Positive values = more peaks than valleys. Negative = more valleys than peaks.",
            "Measured splits: -0.05 = ~55% valleys, -0.15 = ~65% valleys, -0.30 = ~77% valleys."
    );
    public static final Setting<Double> BASE_HEIGHT_FRACTION = Settings.doubleSetting(
            "BaseHeightFraction", 0.46875, 0.0, 1.0,
            t -> ((TerrainSettings) t).getBaseHeightFraction(),
            "Where the terrain surface sits as a fraction of world height when biome height is 0.",
            "0.47 = surface roughly at half world height (default).",
            "0.25 = low surface with lots of sky, 0.75 = high surface with deep underground."
    );
    public static final Setting<Double> BIOME_HEIGHT_WEIGHT = Settings.doubleSetting(
            "BiomeHeightWeight", 0.125, 0.0, 1.0,
            t -> ((TerrainSettings) t).getBiomeHeightWeight(),
            "How much biome height config shifts the terrain surface.",
            "0 = all biomes at same baseline, 0.125 = default.",
            "Higher values create more dramatic height differences between biomes."
    );
    public static final Setting<Double> CONTINENTAL_HEIGHT_WEIGHT = Settings.doubleSetting(
            "ContinentalHeightWeight", 0.25, 0.0, 1.0,
            t -> ((TerrainSettings) t).getContinentalHeightWeight(),
            "How much continental noise shifts the terrain surface.",
            "0 = continental noise has no effect on surface position, 0.25 = default.",
            "Higher values create larger-scale terrain undulation."
    );
    public static final Setting<Double> FALLOFF_STEEPNESS = Settings.doubleSetting(
            "FalloffSteepness", 6.0, 0.1, 100.0,
            t -> ((TerrainSettings) t).getFalloffSteepness(),
            "Controls how sharply terrain transitions from solid to air.",
            "Higher values = thinner transition zone = sharper terrain edges.",
            "Lower values = thicker transition zone = smoother, more blobby terrain.",
            "Default 6.0 produces ~80 block transition at default biome volatility (0.3)."
    );
    public static final Setting<Double> NOISE_AMPLITUDE = Settings.doubleSetting(
            "NoiseAmplitude", 1.0, 0.0, 1000.0,
            t -> ((TerrainSettings) t).getNoiseAmplitude(),
            "Global multiplier for terrain noise contribution.",
            "Scales the effect of Volatility1/Volatility2 from all biomes uniformly.",
            "1.0 = noise at face value, higher = more chaotic terrain, 0 = falloff-only terrain."
    );

    public static TerrainSettings getTerrainSettings(SettingsMap reader) {
        var builder = builder();

        builder.fractureHorizontal(reader.getSetting(FRACTURE_HORIZONTAL));
        builder.fractureVertical(reader.getSetting(FRACTURE_VERTICAL));

        // Handle world height
        if (reader.hasSetting(OutdatedSettings.WORLD_HEIGHT_CAP_BITS)) {
            builder.worldHeightCap(1 << reader.getSetting(OutdatedSettings.WORLD_HEIGHT_CAP_BITS));
        } else {
            builder.worldHeightCap(reader.getSetting(OutdatedSettings.WORLD_HEIGHT_CAP));
        }
        if (reader.hasSetting(OutdatedSettings.WORLD_HEIGHT_SCALE_BITS)) {
             builder.worldHeightScale(1 << reader.getSetting(OutdatedSettings.WORLD_HEIGHT_SCALE_BITS));
        } else {
            builder.worldHeightScale(reader.getSetting(WORLD_HEIGHT_SCALE));
        }

        builder.minY(reader.getSetting(MIN_Y));
        if (reader.hasSetting(HEIGHT)) {
            builder.height(reader.getSetting(HEIGHT));
        } else {
            builder.height(builder.worldHeightCap - builder.minY);
        }

        builder.betterSnowFall(reader.getSetting(BETTER_SNOW_FALL));
        builder.waterLevelMax(reader.getSetting(WATER_LEVEL_MAX));
        builder.waterLevelMin(reader.getSetting(WATER_LEVEL_MIN));
        builder.carverLavaBlockHeight(reader.getSetting(CARVER_LAVA_BLOCK_HEIGHT));
        builder.chcStart(reader.getSetting(CHC_START));
        builder.continentalScale(reader.getSetting(CONTINENTAL_SCALE));
        builder.continentalBias(reader.getSetting(CONTINENTAL_BIAS));
        builder.baseHeightFraction(reader.getSetting(BASE_HEIGHT_FRACTION));
        builder.biomeHeightWeight(reader.getSetting(BIOME_HEIGHT_WEIGHT));
        builder.continentalHeightWeight(reader.getSetting(CONTINENTAL_HEIGHT_WEIGHT));
        builder.falloffSteepness(reader.getSetting(FALLOFF_STEEPNESS));
        builder.noiseAmplitude(reader.getSetting(NOISE_AMPLITUDE));

        int configVersion = reader.getVersion();
        if (configVersion < 2) {
            // Version 1 stored user-facing 0-centered values; internal values are multipliers
            // (1 = neutral). Only convert values actually present in the file — absent settings
            // already hold the internal default.
            if (reader.hasSetting(FRACTURE_HORIZONTAL)) {
                builder.fractureHorizontal(legacyToMultiplier(builder.fractureHorizontal));
            }
            if (reader.hasSetting(FRACTURE_VERTICAL)) {
                builder.fractureVertical(legacyToMultiplier(builder.fractureVertical));
            }
        }

        return builder.fixSettings().build();
    }

    // Maps a legacy 0-centered config value onto the internal multiplier scale:
    // 0 -> 1 (neutral), positive -> value+1, negative -> shrinking fraction of 1.
    public static double legacyToMultiplier(double value) {
        return value < 0.0D ? 1.0D / (Math.abs(value) + 1.0D) : value + 1.0D;
    }

    @Override
    public String getSectionName() {
        return "Terrain Settings";
    }

    public static class TerrainSettingsBuilder {
        public TerrainSettingsBuilder fixSettings() {
            checkWaterLevelMax();
            checkFractionHorizontal();
            checkFractionVertical();
            return this;
        }
        private void checkWaterLevelMax() {
            waterLevelMax = Math.max(waterLevelMax, waterLevelMin);
        }
        // Negative values in version 2+ configs are legacy-style input; convert with the same
        // formula as the version 1 migration rather than rejecting them.
        private void checkFractionHorizontal() {
            if (fractureHorizontal < 0) {
                fractureHorizontal = legacyToMultiplier(fractureHorizontal);
            }
        }
        private void checkFractionVertical() {
            if (fractureVertical < 0) {
                fractureVertical = legacyToMultiplier(fractureVertical);
            }
        }
    }
}