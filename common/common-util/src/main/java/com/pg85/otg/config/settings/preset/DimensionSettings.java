package com.pg85.otg.config.settings.preset;

import com.pg85.otg.config.io.SettingsMap;
import com.pg85.otg.config.settingType.Setting;
import com.pg85.otg.config.settingType.Settings;
import com.pg85.otg.config.settings.ConfigSection;
import lombok.Builder;
import lombok.Getter;

import java.util.OptionalLong;

@Builder
@Getter
public class DimensionSettings extends ConfigSection {
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

    public static final Setting<String> INFINIBURN = Settings.stringSetting(
            "InfiniBurn", "minecraft:infiniburn_overworld",
            t -> ((DimensionSettings) t).getInfiniburn(),
            "Infiniburn block tag registry key, minecraft:infiniburn_overworld by default.",
            "Can be either overworld/nether/end (or potentially modded)."
    );
    public static final Setting<String> EFFECTS_LOCATION = Settings.stringSetting(
            "EffectsLocation", "minecraft:overworld",
            t -> ((DimensionSettings) t).getEffectsLocation(),
            "Effects registry key, minecraft:overworld by default.",
            "Can be either overworld/nether/end (or potentially modded)."
    );
    public static final Setting<Integer> LOGICAL_HEIGHT = Settings.intSetting(
            "LogicalHeight", 256, 0, Integer.MAX_VALUE,
            t -> ((DimensionSettings) t).getLogicalHeight(),
            "World height, 256 by default. Affects portals and chorus fruits."
    );
    public static final Setting<Long> FIXED_TIME = Settings.longSetting(
            "FixedTime", -1L, -1L, 24000L,
            t -> {
                DimensionSettings ds = (DimensionSettings) t;
                if (ds.getFixedTime().isEmpty()) {
                    return -1L;
                }
                return ds.getFixedTime().getAsLong();
            },
            "The time this dimension is fixed at, from 0 to 24000.",
            "-1 by default, meaning disabled, so time passes normally.",
            "Vanilla Nether uses 18000, End uses 6000."
    );
    public static final Setting<Boolean> HAS_SKYLIGHT = Settings.booleanSetting(
            "HasSkylight", true,
            t -> ((DimensionSettings) t).isHasSkyLight(),
            "Whether this dimension uses a skylight, defaults to true.",
            "Vanilla nether and end use false, nether combines this with AmbientLight:0.1."
    );
    public static final Setting<Boolean> HAS_CEILING = Settings.booleanSetting(
            "HasCeiling", false,
            t -> ((DimensionSettings) t).isHasCeiling(),
            "Whether this dimension has a ceiling, affects mob spawning, weather (thunder), maps.",
            "Defaults to false, vanilla nether uses true."
    );
    public static final Setting<Boolean> ULTRA_WARM = Settings.booleanSetting(
            "UltraWarm", false,
            t -> ((DimensionSettings) t).isUltraWarm(),
            "Whether water evaporates in this dimension. Also appears to affect lava/lava flow.",
            "Defaults to false. Vanilla nether uses true."
    );
    public static final Setting<Boolean> NATURAL = Settings.booleanSetting(
            "Natural", true,
            t -> ((DimensionSettings) t).isNatural(),
            "When set to false, mobs do not spawn from portals and players cannot use beds in this dimension.",
            "Defaults to true."
    );
    public static final Setting<Boolean> CREATE_DRAGON_FLIGHT = Settings.booleanSetting(
            "CreateDragonFight", false,
            t -> ((DimensionSettings) t).isCreateDragonFight(),
            "Probably starts a dragon fight, we think. Try it, what could possibly go wrong?"
    );
    public static final Setting<Boolean> PIGLIN_SAFE = Settings.booleanSetting(
            "PiglinSafe", false,
            t -> ((DimensionSettings) t).isPiglinSafe(),
            "Whether this dimension can spawn piglins, false by default."
    );
    public static final Setting<Boolean> BED_WORKS = Settings.booleanSetting(
            "BedWorks", true,
            t -> ((DimensionSettings) t).isBedWorks(),
            "Whether beds can be used to sleep and skip time in this dimension, true by default."
    );
    public static final Setting<Boolean> RESPAWN_ANCHOR_WORKS = Settings.booleanSetting(
            "RespawnAnchorWorks", true,
            t -> ((DimensionSettings) t).isRespawnAnchorWorks(),
            "Whether RespawnAnchorBlocks can be used, false by default."
    );
    public static final Setting<Boolean> HAS_RAIDS = Settings.booleanSetting(
            "HasRaids", true,
            t -> ((DimensionSettings) t).isHasRaids(),
            "Whether the dimension has raids, true by default."
    );
    public static final Setting<Double> COORDINATE_SCALE = Settings.doubleSetting(
            "CoordinateScale", 1.0D, 0.0D, Integer.MAX_VALUE,
            t -> ((DimensionSettings) t).getCoordinateScale(),
            "The amount of blocks traveled compared to other dimensions.",
            "1 by default, same as vanilla overworld, nether uses 8."
    );
    public static final Setting<Double> AMBIENT_LIGHT = Settings.doubleSetting(
            "AmbientLight", 0.0D, 0.0D, Integer.MAX_VALUE,
            t -> ((DimensionSettings) t).getAmbientLight(),
            "The base ambient light level for the world, 0.0 for overworld/end, 0.1 for nether."
    );

    public static DimensionSettings getDimensionSettings(SettingsMap reader) {
        var dimensionSettingsBuilder = builder();

        long fixedTime = reader.getSetting(FIXED_TIME);
        dimensionSettingsBuilder.fixedTime(fixedTime == -1L ? OptionalLong.empty() : OptionalLong.of(fixedTime));
        dimensionSettingsBuilder.hasSkyLight(reader.getSetting(HAS_SKYLIGHT));
        dimensionSettingsBuilder.hasCeiling(reader.getSetting(HAS_CEILING));
        dimensionSettingsBuilder.ultraWarm(reader.getSetting(ULTRA_WARM));
        dimensionSettingsBuilder.natural(reader.getSetting(NATURAL));
        dimensionSettingsBuilder.coordinateScale(reader.getSetting(COORDINATE_SCALE));
        dimensionSettingsBuilder.createDragonFight(reader.getSetting(CREATE_DRAGON_FLIGHT));
        dimensionSettingsBuilder.piglinSafe(reader.getSetting(PIGLIN_SAFE));
        dimensionSettingsBuilder.bedWorks(reader.getSetting(BED_WORKS));
        dimensionSettingsBuilder.respawnAnchorWorks(reader.getSetting(RESPAWN_ANCHOR_WORKS));
        dimensionSettingsBuilder.hasRaids(reader.getSetting(HAS_RAIDS));
        dimensionSettingsBuilder.logicalHeight(reader.getSetting(LOGICAL_HEIGHT));
        dimensionSettingsBuilder.infiniburn(reader.getSetting(INFINIBURN));
        dimensionSettingsBuilder.effectsLocation(reader.getSetting(EFFECTS_LOCATION));
        dimensionSettingsBuilder.ambientLight(reader.getSetting(AMBIENT_LIGHT).floatValue());

        return dimensionSettingsBuilder.build();
    }

    @Override
    public String getSectionName() {
        return "Dimension Settings";
    }
}