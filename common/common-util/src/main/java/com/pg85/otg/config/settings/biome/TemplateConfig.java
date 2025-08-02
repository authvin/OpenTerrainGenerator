package com.pg85.otg.config.settings.biome;

import com.pg85.otg.config.annotation.Config;
import com.pg85.otg.config.annotation.EnumSetting;
import com.pg85.otg.config.annotation.LongDescription;
import com.pg85.otg.config.io.SettingsMap;
import com.pg85.otg.config.settings.ConfigSection;
import com.pg85.otg.config.settings.TemplateTypes;
import lombok.Builder;
import lombok.Getter;

import static com.pg85.otg.config.settings.biome.generated.TemplateSettings.*;

@Builder
@Getter
@Config
public class TemplateConfig extends ConfigSection {

    @EnumSetting("RESOURCES")
    @LongDescription({
            "TemplateType declares which settings will be read from this template",
            "Options are: ",
            "- FULL (all options)",
            "- SURFACE",
            "- STRUCTURES",
            "- RESOURCES",
            "- TERRAIN",
            "- GENERATION",
            "- VISUAL",
            "- TAGS",
            ""
    })
    TemplateTypes templateType;

    public static TemplateConfig buildTemplateSettings(SettingsMap settingsMap) {
        var builder = builder();

        builder.templateType(settingsMap.getSetting(TEMPLATE_TYPE));

        return builder.build();
    }

    @Override
    public String getSectionName() {
        return "Biome Template Settings";
    }
}
