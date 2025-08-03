package com.pg85.otg.config.settings.biome;

import com.pg85.otg.config.io.SettingsMap;
import com.pg85.otg.config.settings.preset.PresetSettings;
import com.pg85.otg.config.settingtype.Setting;
import com.pg85.otg.config.settingtype.Settings;
import com.pg85.otg.config.settings.ConfigSection;
import com.pg85.otg.constants.settings.BiomeType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Locale;

@Builder
@Getter
public class IdentitySettings extends ConfigSection {
    private final String biomeName;
    private final String displayName;
    private final String registryPath;
    private final boolean isTemplateForBiome;

    @Override
    public String getSectionName() {
        return "Biome Identity Settings";
    }

    public static final Setting<String> DISPLAY_NAME = Settings.stringSetting(
            "DisplayName",
            "",
            t -> ((IdentitySettings)t).getDisplayName(),
            "Used for generating language files, which determines what will show up in f3 and similar info screens"
    );

    public static IdentitySettings buildIdentitySettings(SettingsMap reader, PresetSettings presetSettings) {
        var builder = builder();

        builder.biomeName(reader.getName());
        builder.displayName(reader.getSetting(DISPLAY_NAME));
        builder.isTemplateForBiome(reader.getSetting(OutdatedSettings.IS_TEMPLATE_FOR_BIOME));


        String namespace = presetSettings.getPresetInfo().getRegistryName();
        String path = reader.getName()
                .replaceAll("([a-z])([A-Z])", "$1_$2") // snake_case from CamelCase
                .toLowerCase(Locale.ROOT) // All registry keys must be lower case
                .replaceAll(" ", "_") // replace spaces with underscores
                .replaceAll("[^a-z0-9_\\-/.]", ""); // Remove any illegal characters

        builder.registryPath(String.format("%s:%s", namespace, path));

        return builder.build();
    }
}
