package com.pg85.otg.config.biome;

import com.pg85.otg.config.io.SettingsMap;
import com.pg85.otg.config.settings.biome.*;
import com.pg85.otg.config.settings.preset.PresetSettings;
import com.pg85.otg.constants.Constants;
import com.pg85.otg.interfaces.IBiomeResourceLocation;
import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;

public class BiomeTemplate extends BiomeSettings {

    private final TemplateConfig templateConfig;

    public BiomeTemplate(SettingsMap reader, PresetSettings parent) {
        super(reader.getName());
        this.configPath = reader.getPath();
        this.parent = parent;
        renameOldSettings(reader);
        templateConfig = TemplateConfig.buildTemplateSettings(reader);
        switch (templateConfig.getTemplateType()) {
            case FULL -> {
                readDefaultSettings(reader, parent, BiomeResourcesManager.get());
            }
            case VISUAL -> {
                visualSettings = BiomeVisualSettings.getBiomeVisualSettings(reader, parent.getVisualSettings());
            }
            case TERRAIN -> {
                terrainSettings = BiomeTerrainSettings.getBiomeTerrainSettings(reader, parent.getTerrainSettings());
            }
            case GENERATION -> {
                generationSettings = BiomePlacementConfig.getGenerationSettings(reader, parent.getGenerationSettings());
            }
        }
    }

    @Getter
    @Setter
    private IBiomeResourceLocation registryKey;

    @Getter
    private final Path configPath;
    private final PresetSettings parent;

    @Override
    public boolean biomeConfigsHaveReplacement() {
        throw new UnsupportedOperationException(
                "Cannot query a template for whether biome configs have replacements"
        );
    }

    @Override
    public void writeConfigSettings(SettingsMap writer) {
        writer.putSetting(Constants.ConfigVersionSetting, Constants.ConfigVersion);
        writeConfigSection(writer, templateConfig);
    }
}
