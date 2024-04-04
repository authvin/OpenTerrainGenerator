package com.pg85.otg.settings.preset;

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
}
