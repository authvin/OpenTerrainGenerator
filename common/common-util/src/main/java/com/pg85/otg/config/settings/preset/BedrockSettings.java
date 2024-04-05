package com.pg85.otg.config.settings.preset;

import com.pg85.otg.config.io.SettingsMap;
import com.pg85.otg.config.standard.PresetStandardValues;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BedrockSettings {
    private final boolean ceilingBedrock;
    private final boolean flatBedrock;
    private final boolean bedrockDisabled;

    public static BedrockSettings getBedrockSettings(SettingsMap reader) {
        var bedrockSettingsBuilder = builder();

        bedrockSettingsBuilder.bedrockDisabled(reader.getSetting(PresetStandardValues.DISABLE_BEDROCK));
        bedrockSettingsBuilder.ceilingBedrock(reader.getSetting(PresetStandardValues.CEILING_BEDROCK));
        bedrockSettingsBuilder.flatBedrock(reader.getSetting(PresetStandardValues.FLAT_BEDROCK));
        return bedrockSettingsBuilder.build();
    }
}