package com.pg85.otg.config.settings.biome;

import com.pg85.otg.config.ConfigFile;
import com.pg85.otg.config.ConfigFunction;
import com.pg85.otg.config.io.IConfigFunctionProvider;
import com.pg85.otg.config.io.SettingsMap;
import com.pg85.otg.config.settings.ConfigSection;
import com.pg85.otg.config.settings.preset.PresetSettings;
import com.pg85.otg.config.settingtype.Setting;
import com.pg85.otg.interfaces.ICustomStructureGen;
import com.pg85.otg.interfaces.ISaplingSpawner;
import com.pg85.otg.util.materials.LocalMaterialData;
import com.pg85.otg.util.minecraft.SaplingType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * BiomeConfig (*.bc) classes
 * <p>
 * IBiomeConfig defines anything that's used/exposed between projects.
 * BiomeConfigBase implements anything needed for IBiomeConfig.
 * BiomeConfig contains only fields/methods used for io/serialisation/instantiation.
 * <p>
 * BiomeConfig should be used only in common-core and platform-specific layers, when reading/writing settings on app start.
 * IBiomeConfig should be used wherever settings are used in code.
 */
@Getter
public abstract class BiomeSettings implements ConfigFile {
    protected IdentitySettings identitySettings = null;
    protected MobSettings mobSettings = null;
    protected BiomePlacementConfig generationSettings = null;
    protected BiomeStructureSettings structureSettings = null;
    protected BiomeTerrainSettings terrainSettings = null;
    protected BiomeVisualSettings visualSettings = null;
    protected SurfaceSettings surfaceSettings = null;
    protected BiomeResourceSettings resourceSettings = null;

    protected BiomeTagConfig biomeTagConfig = null;
    protected BiomeStructureTagConfig biomeStructureTagConfig = null;

    private final String configName;
    @Getter
    @Setter
    protected ICustomStructureGen structureGen;

    protected BiomeSettings(String configName) {
        this.configName = configName;
    }

    @Override
    public String getConfigName() {
        return configName;
    }

    // Misc

    // PresetConfig getters
    // TODO: Ideally, don't contain presetConfig within biomeconfig,
    // use a parent object that holds both, like a worldgenregion.

    abstract public boolean biomeConfigsHaveReplacement();

    // Inheritance

    // Height / volatility
    public double getCHCData(int controlLayer) {
        return this.getTerrainSettings().getCustomHeightControl()[controlLayer];
    }

    // OTG Custom structures (BO's)

    public List<List<String>> getCustomStructureNames() {
        List<List<String>> customStructureNamesByGen = new ArrayList<>();
        for (ICustomStructureGen structureGens : this.getResourceSettings().getCustomStructures()) {
            List<String> customStructureNames = Arrays.asList(structureGens.getObjectNames());
            customStructureNamesByGen.add(customStructureNames);
        }
        return customStructureNamesByGen;
    }

    // Saplings

    public ISaplingSpawner getSaplingGen(SaplingType type) {
        ISaplingSpawner gen = this.resourceSettings.getSaplingGrowers().get(type);
        if (gen == null && type.growsTree()) {
            gen = this.resourceSettings.getSaplingGrowers().get(SaplingType.All);
        }
        return gen;
    }

    public ISaplingSpawner getCustomSaplingGen(LocalMaterialData materialData, boolean wideTrunk) {
        if (wideTrunk) {
            ISaplingSpawner spawner = this.resourceSettings.getCustomBigSaplingGrowers().get(materialData);
            if (spawner != null) {
                return spawner;
            }
        }
        return this.resourceSettings.getCustomSaplingGrowers().get(materialData);
    }
    // Misc
    public List<ConfigFunction<BiomeSettings>> getResourceQueue() {
        return this.getResourceSettings().getResourceQueue();
    }
    @Override
    public void renameOldSettings(SettingsMap settings)
    {
        settings.renameOldSetting("DisableNotchHeightControl", BiomeTerrainSettings.DISABLE_BIOME_HEIGHT);
        settings.renameOldSetting("BiomeDictId", IdentitySettings.BIOME_DICT_TAGS);
        settings.renameOldSetting("IsleInBiome", BiomePlacementConfig.ISLE_IN_BIOMES);
        settings.renameOldSetting("BiomeIsBorder", BiomePlacementConfig.BORDER_IN_BIOMES);
        settings.renameOldSetting("BiomeColor", BiomePlacementConfig.BIOME_MAP_COLOR);
    }

    protected void readDefaultSettings(SettingsMap settingsMap, PresetSettings presetSettings, IConfigFunctionProvider provider) {
        identitySettings = IdentitySettings.buildIdentitySettings(settingsMap, presetSettings);
        mobSettings = MobSettings.getMobSettings(settingsMap);
        generationSettings = BiomePlacementConfig.getGenerationSettings(settingsMap, presetSettings.getGenerationSettings());
        structureSettings = BiomeStructureSettings.getBiomeStructureSettings(settingsMap, presetSettings.getStructureSettings());
        terrainSettings = BiomeTerrainSettings.getBiomeTerrainSettings(settingsMap, presetSettings.getTerrainSettings());
        visualSettings = BiomeVisualSettings.getBiomeVisualSettings(settingsMap, presetSettings.getVisualSettings());

        biomeTagConfig = BiomeTagConfig.getBiomeTagConfig(settingsMap, identitySettings);
        biomeStructureTagConfig = BiomeStructureTagConfig.getBiomeStructureTagConfig(settingsMap, structureSettings);

        surfaceSettings = SurfaceSettings.getSurfaceSettings(
                settingsMap,
                presetSettings.getBlockSettings(),
                presetSettings.getTerrainSettings()
                );

        resourceSettings = BiomeResourceSettings.getResourceSettings(
                presetSettings.getResourceSettings(),
                new ArrayList<>(
                        settingsMap.getConfigFunctions(
                                this,
                                provider,
                                presetSettings.getConfigName()
                        )));
    }

    @Override
    public void writeConfigSettings(SettingsMap writer) {
        if (identitySettings != null)
            writeConfigSection(writer, identitySettings);
        if (generationSettings != null)
            writeConfigSection(writer, generationSettings);
        if (terrainSettings != null)
            writeConfigSection(writer, terrainSettings);
        if (surfaceSettings != null)
            writeConfigSection(writer, surfaceSettings);
        if (visualSettings != null)
            writeConfigSection(writer, visualSettings);
        if (resourceSettings != null)
            writeConfigSection(writer, resourceSettings);
        if (structureSettings != null)
            writeConfigSection(writer, structureSettings);
        if (mobSettings != null)
            writeConfigSection(writer, mobSettings);
        if (biomeTagConfig != null) {
            if (biomeTagConfig.isDisplayAllBiomeTags()) {
                writeConfigSection(writer, biomeTagConfig);
            } else {
                writeAlteredConfigSection(writer, biomeTagConfig);
            }
        }
        if (biomeStructureTagConfig != null) {
            if (biomeStructureTagConfig.isDisplayAllStructureTags()) {
                writeConfigSection(writer, biomeStructureTagConfig);
            } else {
                writeAlteredConfigSection(writer, biomeStructureTagConfig);
            }
        }
    }

    public void writeConfigSection(SettingsMap writer, ConfigSection section) {
        writer.header1(section.getSectionName());

        for (Setting<?> setting : section.getSettingsList()) {
            writer.putSetting(setting, section);
        }
    }

    public void writeAlteredConfigSection(SettingsMap writer, ConfigSection section) {
        writer.header2(section.getSectionName());

        for (Setting<?> setting : section.getAlteredSettings()) {
            writer.putSetting(setting, section);
        }
    }
}
