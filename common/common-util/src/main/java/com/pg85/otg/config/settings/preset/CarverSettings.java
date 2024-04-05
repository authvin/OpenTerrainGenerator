package com.pg85.otg.config.settings.preset;

import com.pg85.otg.config.io.SettingsMap;
import com.pg85.otg.config.standard.PresetStandardValues;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CarverSettings {
    private final boolean cavesEnabled;
    private final int caveFrequency;
    private final int caveRarity;
    private final boolean evenCaveDistribution;
    private final int caveMinAltitude;
    private final int caveMaxAltitude;
    private final int caveSystemFrequency;
    private final int individualCaveRarity;
    private final int caveSystemPocketMinSize;
    private final int caveSystemPocketChance;
    private final int caveSystemPocketMaxSize;
    private final boolean ravinesEnabled;
    private final int ravineRarity;
    private final int ravineMinLength;
    private final int ravineMaxLength;
    private final double ravineDepth;
    private final int ravineMinAltitude;
    private final int ravineMaxAltitude;

    public static CarverSettings getCarverSettings(SettingsMap reader) {
        var carverSettingsBuilder = builder();

        carverSettingsBuilder.cavesEnabled(reader.getSetting(PresetStandardValues.CAVES_ENABLED));
        carverSettingsBuilder.caveFrequency(reader.getSetting(PresetStandardValues.CAVE_FREQUENCY));
        carverSettingsBuilder.caveRarity(reader.getSetting(PresetStandardValues.CAVE_RARITY));
        carverSettingsBuilder.evenCaveDistribution(reader.getSetting(PresetStandardValues.EVEN_CAVE_DISTRIBUTION));
        carverSettingsBuilder.caveMinAltitude(reader.getSetting(PresetStandardValues.CAVE_MIN_ALTITUDE));
        carverSettingsBuilder.caveMaxAltitude(reader.getSetting(PresetStandardValues.CAVE_MAX_ALTITUDE));
        carverSettingsBuilder.caveSystemFrequency(reader.getSetting(PresetStandardValues.CAVE_SYSTEM_FREQUENCY));
        carverSettingsBuilder.individualCaveRarity(reader.getSetting(PresetStandardValues.INDIVIDUAL_CAVE_RARITY));
        carverSettingsBuilder.caveSystemPocketChance(reader.getSetting(PresetStandardValues.CAVE_SYSTEM_POCKET_CHANCE));
        carverSettingsBuilder.caveSystemPocketMinSize(reader.getSetting(PresetStandardValues.CAVE_SYSTEM_POCKET_MIN_SIZE));
        carverSettingsBuilder.caveSystemPocketMaxSize(reader.getSetting(PresetStandardValues.CAVE_SYSTEM_POCKET_MAX_SIZE));

        carverSettingsBuilder.ravinesEnabled(reader.getSetting(PresetStandardValues.RAVINES_ENABLED));
        carverSettingsBuilder.ravineRarity(reader.getSetting(PresetStandardValues.RAVINE_RARITY));
        carverSettingsBuilder.ravineMinLength(reader.getSetting(PresetStandardValues.RAVINE_MIN_LENGTH));
        carverSettingsBuilder.ravineMaxLength(reader.getSetting(PresetStandardValues.RAVINE_MAX_LENGTH));
        carverSettingsBuilder.ravineDepth(reader.getSetting(PresetStandardValues.RAVINE_DEPTH));
        carverSettingsBuilder.ravineMinAltitude(reader.getSetting(PresetStandardValues.RAVINE_MIN_ALTITUDE));
        carverSettingsBuilder.ravineMaxAltitude(reader.getSetting(PresetStandardValues.RAVINE_MAX_ALTITUDE));
        var carverSettings = carverSettingsBuilder.fixSettings().build();
        return carverSettings;
    }

    public static class CarverSettingsBuilder {
        public CarverSettingsBuilder fixSettings() {
            checkCaveMaxAltitude();
            checkCaveSystemPocketMaxSize();
            checkRavineMaxAltitude();
            checkRavineMaxLength();
            return this;
        }
        private void checkCaveMaxAltitude() {
            caveMaxAltitude = Math.max(caveMaxAltitude, caveMinAltitude);
        }
        private void checkCaveSystemPocketMaxSize() {
            caveSystemPocketMaxSize = Math.max(caveSystemPocketMaxSize, caveSystemPocketMinSize);
        }
        private void checkRavineMaxAltitude() {
            ravineMaxAltitude = Math.max(ravineMaxAltitude, ravineMinAltitude);
        }
        private void checkRavineMaxLength() {
            ravineMaxLength = Math.max(ravineMaxLength, ravineMinLength);
        }
    }
}