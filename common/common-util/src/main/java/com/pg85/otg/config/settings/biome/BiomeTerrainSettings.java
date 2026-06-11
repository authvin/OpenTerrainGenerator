package com.pg85.otg.config.settings.biome;

import com.pg85.otg.config.io.SettingsMap;
import com.pg85.otg.config.settingtype.DoubleArraySetting;
import com.pg85.otg.config.settingtype.Setting;
import com.pg85.otg.config.settingtype.Settings;
import com.pg85.otg.config.settings.ConfigSection;
import com.pg85.otg.config.settings.preset.TerrainSettings;
import com.pg85.otg.constants.Constants;
import it.unimi.dsi.fastutil.ints.Int2DoubleAVLTreeMap;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BiomeTerrainSettings extends ConfigSection {
    private final TerrainSettings parent;
    private final float biomeHeight;
    private final float biomeVolatility;
    private final int smoothRadius;
    private final int CHCSmoothRadius;
    private final double peakFactor;
    private final double valleyFactor;
    private final double volatility1;
    private final double volatility2;
    private final double volatilityWeight1;
    private final double volatilityWeight2;
    private final boolean disableBiomeHeight;
    private final Int2DoubleAVLTreeMap customHeightControl;
    @Override
    public String getSectionName() {
        return "Biome Terrain Settings";
    }

    public static final Setting<Integer> SMOOTH_RADIUS = Settings.intSetting(
            "SmoothRadius", 2, 0, 32,
            t -> ((BiomeTerrainSettings) t).getSmoothRadius(),
            "Smooth radius between biomes. Must be between 0 and 32, inclusive. The resulting",
            "smooth radius seems to be  (thisSmoothRadius + 1 + smoothRadiusOfBiomeOnOtherSide) * 4 .",
            "So if two biomes next to each other have both a smooth radius of 2, the",
            "resulting smooth area will be (2 + 1 + 2) * 4 = 20 blocks wide."
    );
    public static final Setting<Integer> CUSTOM_HEIGHT_CONTROL_SMOOTH_RADIUS = Settings.intSetting(
            "CustomHeightControlSmoothRadius", 2, 0, 32,
            t -> ((BiomeTerrainSettings) t).getCHCSmoothRadius(),
            "Works the same way as SmoothRadius but only acts on CustomHeightControl.",
            "Must be between 0 and 32, inclusive.",
            "Does nothing if Custom Height Control smoothing is not enabled in the world config."
    );
    public static final Setting<Double> VOLATILITY_1 = Settings.doubleSetting(
            "Volatility1", 1.0, -10000, 1000,
            t -> ((BiomeTerrainSettings) t).getVolatility1(),
            "Multiplier for the first independent terrain noise layer. 1.0 = noise at face value.",
            "Higher values make landscape generation more chaotic/volatile.",
            "Values between 0 and 1 make it calmer/gentler; 0 disables this noise layer.",
            "Legacy (version 1) configs store 0-centered values; these are converted on load."
    );
    public static final Setting<Double> VOLATILITY_2 = Settings.doubleSetting(
            "Volatility2", 1.0, -10000, 1000,
            t -> ((BiomeTerrainSettings) t).getVolatility2(),
            "Same as Volatility1, for the second noise layer.",
            "VolatilityWeight1/VolatilityWeight2 control where each layer applies."
    );
    public static final Setting<Double> VOLATILITY_WEIGHT_1 = Settings.doubleSetting(
            "VolatilityWeight1", 0.45, -1000, 1000,
            t -> ((BiomeTerrainSettings) t).getVolatilityWeight1(),
            "Adjust the weight of the corresponding volatility settings.",
            "This allows you to change how prevalent you want either of the volatility settings to be in the terrain."
    );
    public static final Setting<Double> VOLATILITY_WEIGHT_2 = Settings.doubleSetting(
            "VolatilityWeight2", 0.5, -1000, 1000,
            t -> ((BiomeTerrainSettings) t).getVolatilityWeight2()
    );
    public static final Setting<Double> PEAK_FACTOR = Settings.doubleSetting(
            "PeakFactor", 1.0, -1000, 1000,
            t -> ((BiomeTerrainSettings) t).getPeakFactor(),
            "How strongly this biome responds to continental peaks. Multiplier: 1.0 = full",
            "response, between 0 and 1 = weaker peaks, 0 = no continental peaks in this biome.",
            "Negative values invert the response (continental peaks lower the terrain instead).",
            "Legacy (version 1) configs store this as MaxAverageHeight (0-centered); converted on load."
    );
    public static final Setting<Double> VALLEY_FACTOR = Settings.doubleSetting(
            "ValleyFactor", 1.0, -1000, 1000,
            t -> ((BiomeTerrainSettings) t).getValleyFactor(),
            "How strongly this biome responds to continental valleys (usually ocean depth).",
            "Multiplier: 1.0 = full response, between 0 and 1 = shallower valleys, 0 = none.",
            "Negative values invert the response (continental valleys raise the terrain instead).",
            "Legacy (version 1) configs store this as MaxAverageDepth (0-centered); converted on load."
    );
    public static final Setting<Float> BIOME_HEIGHT = Settings.floatSetting(
            "BiomeHeight", 0.1f, -10, 10,
            t -> ((BiomeTerrainSettings) t).getBiomeHeight(),
            "BiomeHeight defines how much height will be added during terrain generation",
            "Must be between -10.0 and 10.0",
            "Value 0.0 is equivalent to half of map height with all other settings at defaults."
    );
    public static final Setting<Float> BIOME_VOLATILITY = Settings.floatSetting(
            "BiomeVolatility", 0.3f, -1000, 1000,
            t -> ((BiomeTerrainSettings) t).getBiomeVolatility(),
            "Controls the width of the transition zone where noise sculpts terrain.",
            "Higher values = wider transition = softer, more varied terrain.",
            "Lower values = narrow transition = sharper, flatter terrain."
    );
    public static final Setting<Boolean> DISABLE_BIOME_HEIGHT = Settings.booleanSetting(
            "DisableBiomeHeight", false,
            t -> ((BiomeTerrainSettings) t).isDisableBiomeHeight(),
            "Disable all noises except Volatility1 and Volatility2. Also disable default block chance from height."
    );
    public static final Setting<double[]> CUSTOM_HEIGHT_CONTROL = new DoubleArraySetting(
            "CustomHeightControl",
            t -> getEntries(((BiomeTerrainSettings) t).getCustomHeightControl()),
            "List of custom height factors, 17 double entries, each controls about 7",
            "blocks height, starting at the bottom of the world. Positive entry - larger chance of spawn blocks, negative - smaller",
            "Values which affect your configuration may be found only experimentally. Values may be very big, like ~3000.0 depends from height",
            "Example:",
            "  CustomHeightControl:0.0,-2500.0,0.0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0",
            "Makes empty layer above bedrock layer. "
    );



    public static double[] getEntries(Int2DoubleAVLTreeMap map) {
        double[] arr = new double[map.size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = map.get(i);
        }
        return arr;
    }

    public static BiomeTerrainSettings getBiomeTerrainSettings(SettingsMap reader, TerrainSettings parent) {
        BiomeTerrainSettingsBuilder builder = BiomeTerrainSettings.builder();

        builder.parent(parent);
        builder.biomeHeight(reader.getSetting(BIOME_HEIGHT));
        builder.biomeVolatility(reader.getSetting(BIOME_VOLATILITY));
        builder.smoothRadius(reader.getSetting(SMOOTH_RADIUS));
        builder.CHCSmoothRadius(reader.getSetting(CUSTOM_HEIGHT_CONTROL_SMOOTH_RADIUS));
        builder.peakFactor(reader.getSetting(PEAK_FACTOR));
        builder.valleyFactor(reader.getSetting(VALLEY_FACTOR));
        builder.volatility1(reader.getSetting(VOLATILITY_1));
        builder.volatility2(reader.getSetting(VOLATILITY_2));
        builder.volatilityWeight1(reader.getSetting(VOLATILITY_WEIGHT_1));
        builder.volatilityWeight2(reader.getSetting(VOLATILITY_WEIGHT_2));
        int configVersion = reader.getVersion();
        if (configVersion < 2) {
            // Version 1 stored user-facing 0-centered values; internal values are multipliers
            // (1 = neutral). Only convert values actually present in the file — absent settings
            // already hold the internal default. PeakFactor/ValleyFactor arrive via the
            // MaxAverageHeight/MaxAverageDepth renames, whose additive scale just shifts by 1.
            if (reader.hasSetting(VOLATILITY_1)) {
                builder.volatility1(TerrainSettings.legacyToMultiplier(builder.volatility1));
            }
            if (reader.hasSetting(VOLATILITY_2)) {
                builder.volatility2(TerrainSettings.legacyToMultiplier(builder.volatility2));
            }
            if (reader.hasSetting(PEAK_FACTOR)) {
                builder.peakFactor(builder.peakFactor + 1);
            }
            if (reader.hasSetting(VALLEY_FACTOR)) {
                builder.valleyFactor(builder.valleyFactor + 1);
            }
        }

        builder.disableBiomeHeight(reader.getSetting(DISABLE_BIOME_HEIGHT));
        builder.customHeightControl(builder.readHeightSettings(reader, parent));

        return builder.fixSettings().build();
    }

    public static class BiomeTerrainSettingsBuilder {
        protected Int2DoubleAVLTreeMap readHeightSettings(SettingsMap settings, TerrainSettings parent)
        {
            Int2DoubleAVLTreeMap heightMatrix = new Int2DoubleAVLTreeMap();
            double[] keys = settings.getSetting(CUSTOM_HEIGHT_CONTROL);

            int chcStart = parent.getChcStart();
            // TODO: will OTGChunkGenerator fetch negative Y values here? I suspect not...
            int offset = chcStart / Constants.PIECE_Y_SIZE;

            for (int i = 0; i < keys.length; i++)
            {
                heightMatrix.put(i + offset, keys[i]);
            }
            return heightMatrix;
        }
        public BiomeTerrainSettingsBuilder fixSettings() {
            checkVolatility1();
            checkVolatility2();
            return this;
        }
        // Negative values in version 2+ configs are legacy-style input; convert with the same
        // formula as the version 1 migration rather than rejecting them.
        private void checkVolatility1() {
            if (this.volatility1 < 0.0D) {
                this.volatility1 = TerrainSettings.legacyToMultiplier(this.volatility1);
            }
        }
        private void checkVolatility2() {
            if (this.volatility2 < 0.0D) {
                this.volatility2 = TerrainSettings.legacyToMultiplier(this.volatility2);
            }
        }
    }

}