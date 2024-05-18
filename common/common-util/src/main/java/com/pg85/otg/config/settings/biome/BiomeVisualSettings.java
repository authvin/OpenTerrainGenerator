package com.pg85.otg.config.settings.biome;

import com.pg85.otg.config.io.SettingsMap;
import com.pg85.otg.config.settingType.Setting;
import com.pg85.otg.config.settings.preset.VisualSettings;
import com.pg85.otg.config.standard.BiomeStandardValues;
import com.pg85.otg.constants.settings.GrassColorModifier;
import com.pg85.otg.exceptions.InvalidConfigException;
import com.pg85.otg.interfaces.IMaterialReader;
import com.pg85.otg.util.OTGLog;
import com.pg85.otg.util.biome.ColorSet;
import com.pg85.otg.util.biome.SimpleColorSet;
import com.pg85.otg.util.logging.LogCategory;
import com.pg85.otg.util.logging.LogLevel;
import lombok.Builder;
import lombok.Getter;

import java.text.MessageFormat;
import java.util.Optional;

@Builder
@Getter
public class BiomeVisualSettings {
    private final VisualSettings parent;
    private final int skyColor;
    private final int waterColor;
    private final ColorSet waterColorControl;
    private final int grassColor;
    private final ColorSet grassColorControl;
    private final GrassColorModifier grassColorModifier;
    private final int foliageColor;
    private final ColorSet foliageColorControl;
    private final int fogColor;
    private final float fogDensity;
    private final int waterFogColor;
    private final String particleType;
    private final String music;
    private final int musicMinDelay;
    private final int musicMaxDelay;
    private final boolean replaceCurrentMusic;
    private final String ambientSound;
    private final String moodSound;
    private final int moodSoundDelay;
    private final int moodSearchRange;
    private final double moodOffset;
    private final String additionsSound;
    private final double additionsTickChance;
    private final double particleProbability;
    private final float biomeTemperature;
    private final float biomeWetness;

    public static BiomeVisualSettings getBiomeVisualSettings(SettingsMap reader, VisualSettings parent) {
        BiomeVisualSettingsBuilder builder = BiomeVisualSettings.builder();

        builder.parent(parent);
        builder.skyColor(reader.getSetting(BiomeStandardValues.SKY_COLOR));
        builder.waterColor(reader.getSetting(BiomeStandardValues.WATER_COLOR));
        builder.waterColorControl(reader.getSetting(BiomeStandardValues.WATER_COLOR_CONTROL));
        builder.grassColor(reader.getSetting(BiomeStandardValues.GRASS_COLOR));
        builder.grassColorControl(reader.getSetting(BiomeStandardValues.GRASS_COLOR_CONTROL));
        builder.grassColorModifier(reader.getSetting(BiomeStandardValues.GRASS_COLOR_MODIFIER));
        builder.foliageColor(reader.getSetting(BiomeStandardValues.FOLIAGE_COLOR));
        builder.foliageColorControl(reader.getSetting(BiomeStandardValues.FOLIAGE_COLOR_CONTROL));
        builder.fogColor(reader.getSetting(BiomeStandardValues.FOG_COLOR));
        builder.fogDensity(reader.getSetting(BiomeStandardValues.FOG_DENSITY));
        builder.waterFogColor(reader.getSetting(BiomeStandardValues.WATER_FOG_COLOR));
        builder.particleType(reader.getSetting(BiomeStandardValues.PARTICLE_TYPE));
        builder.music(reader.getSetting(BiomeStandardValues.MUSIC));
        builder.musicMinDelay(reader.getSetting(BiomeStandardValues.MUSIC_MIN_DELAY));
        builder.musicMaxDelay(reader.getSetting(BiomeStandardValues.MUSIC_MAX_DELAY));
        builder.replaceCurrentMusic(reader.getSetting(BiomeStandardValues.REPLACE_CURRENT_MUSIC));
        builder.ambientSound(reader.getSetting(BiomeStandardValues.AMBIENT_SOUND));
        builder.moodSound(reader.getSetting(BiomeStandardValues.MOOD_SOUND));
        builder.moodSoundDelay(reader.getSetting(BiomeStandardValues.MOOD_SOUND_DELAY));
        builder.moodSearchRange(reader.getSetting(BiomeStandardValues.MOOD_SEARCH_RANGE));
        builder.moodOffset(reader.getSetting(BiomeStandardValues.MOOD_OFFSET));
        builder.additionsSound(reader.getSetting(BiomeStandardValues.ADDITIONS_SOUND));
        builder.additionsTickChance(reader.getSetting(BiomeStandardValues.ADDITIONS_TICK_CHANCE));
        builder.particleProbability(reader.getSetting(BiomeStandardValues.PARTICLE_PROBABILITY));
        builder.biomeTemperature(reader.getSetting(BiomeStandardValues.BIOME_TEMPERATURE));
        builder.biomeWetness(reader.getSetting(BiomeStandardValues.BIOME_WETNESS));
        if (builder.fogDensity == 0.5 && builder.fogColor == BiomeStandardValues.FOG_COLOR.getDefaultValue())
        {
            builder.fogDensity = 0.0f;
        }
        readLegacyColorSetting(reader, BiomeStandardValues.LEGACY_GRASS_COLOR2).ifPresent(set -> builder.grassColorControl = set);
        readLegacyColorSetting(reader, BiomeStandardValues.LEGACY_FOLIAGE_COLOR2).ifPresent(set -> builder.foliageColorControl = set);

        return builder.build();
    }

    private static Optional<ColorSet> readLegacyColorSetting(SettingsMap reader, Setting<String> oldSetting) {
        ColorSet colorSet = null;
        if (reader.hasSetting(oldSetting)) {
            String color = reader.getSetting(oldSetting);
            if (!color.equals(oldSetting.getDefaultValue())) {
                try {
                    colorSet = new SimpleColorSet(new String[]
                            { color, "-0.1" });
                } catch (InvalidConfigException e) {
                    if (OTGLog.getLogger().getLogCategoryEnabled(LogCategory.CONFIGS)) {
                        OTGLog.getLogger().log(LogLevel.ERROR, LogCategory.CONFIGS, MessageFormat.format(
                                "Encountered an invalid value while reading legacy setting {0} with value {1}: {2}",
                                oldSetting.getName(), color, e.getMessage()
                        ));
                    }
                }
            }
        }
        return Optional.ofNullable(colorSet);
    }
}