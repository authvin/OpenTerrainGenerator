package com.pg85.otg.config.settings.preset;

import com.pg85.otg.config.io.SettingsMap;
import com.pg85.otg.config.standard.PresetStandardValues;
import com.pg85.otg.constants.settings.ConfigMode;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PresetInfo {
    private final ConfigMode settingsMode;
    private final String shortPresetName;
    private final int majorVersion;
    private final int minorVersion;
    private final String author;
    private final String description;

    public static PresetInfo buildPresetInfo(SettingsMap reader) {
        var presetInfoBuilder = builder();

        presetInfoBuilder.settingsMode(reader.getSetting(PresetStandardValues.SETTINGS_MODE));
        presetInfoBuilder.author(reader.getSetting(PresetStandardValues.AUTHOR));
        presetInfoBuilder.description(reader.getSetting(PresetStandardValues.DESCRIPTION));
        presetInfoBuilder.shortPresetName(reader.getSetting(PresetStandardValues.SHORT_PRESET_NAME));
        presetInfoBuilder.majorVersion(reader.getSetting(PresetStandardValues.MAJOR_VERSION));
        presetInfoBuilder.minorVersion(reader.getSetting(PresetStandardValues.MINOR_VERSION));

        return presetInfoBuilder.build();
    }
}
