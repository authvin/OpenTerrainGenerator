package com.pg85.otg.config.preset;

import com.pg85.otg.config.ConfigFunction;
import com.pg85.otg.config.biome.BiomeGroupFunction;
import com.pg85.otg.config.biome.TemplateBiome;
import com.pg85.otg.config.io.IConfigFunctionProvider;
import com.pg85.otg.config.io.SettingsMap;
import com.pg85.otg.config.settings.preset.*;
import com.pg85.otg.config.standard.PresetStandardValues;
import com.pg85.otg.interfaces.ILogger;
import com.pg85.otg.interfaces.IMaterialReader;
import com.pg85.otg.util.biome.ReplaceBlockMatrix;
import com.pg85.otg.util.materials.LocalMaterialData;
import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;
import java.util.*;

/**
 * PresetConfig.ini classes
 * <p>
 * PresetSettings defines anything that's used/exposed between projects.
 * PresetConfig contains only fields/methods used for io/serialisation/instantiation.
 * <p>
 * PresetConfig should be used only in common-core and platform-specific layers, when reading/writing settings on app start.
 * PresetSettings should be used wherever settings are used in code.
 */
@Getter
@Setter
public class PresetConfig extends PresetSettings {
    public static final HashMap<String, Class<? extends ConfigFunction<?>>> CONFIG_FUNCTIONS = new HashMap<>();

    static {
        CONFIG_FUNCTIONS.put("BiomeGroup", BiomeGroupFunction.class);
        CONFIG_FUNCTIONS.put("TemplateBiome", TemplateBiome.class);
    }

    protected boolean biomeConfigsHaveReplacement = false;
    protected int maxSmoothRadius = 2;

    public PresetConfig(Path settingsDir, SettingsMap settingsReader, ArrayList<String> biomes, IConfigFunctionProvider biomeResourcesManager, ILogger logger, IMaterialReader materialReader) {
        super(settingsReader.getName());
        this.renameOldSettings(settingsReader, logger, materialReader);
        presetInfo = PresetInfo.buildPresetInfo(settingsReader);
        visualSettings = VisualSettings.builder().fogColor(settingsReader.getSetting(PresetStandardValues.PRESET_FOG_COLOR)).build();
        resourceSettings = ResourceSettings.getResourceSettings(settingsReader);
        blockSettings = BlockSettings.getBlockSettings(settingsReader, materialReader);
        generationSettings = GenerationSettings.getBiomeSettings(this, settingsReader, biomeResourcesManager, biomes, materialReader, settingsDir);
        terrainSettings = TerrainSettings.getTerrainSettings(settingsReader);
        imageSettings = ImageSettings.getImageSettings(settingsReader, biomes);
        structureSettings = StructureSettings.getStructureSettings(settingsReader);
        carverSettings = CarverSettings.getCarverSettings(settingsReader);
        spawnSettings = SpawnSettings.getSpawnSettings(settingsReader, materialReader);
        portalSettings = PortalSettings.getPortalSettings(settingsReader, materialReader);
        dimensionSettings = DimensionSettings.getDimensionSettings(settingsReader);
        gameRuleSettings = GameRuleSettings.getGameRuleSettings(settingsReader);
    }

    public LocalMaterialData getBedrockBlockReplaced(ReplaceBlockMatrix replaceBlocks, int y) {
        if (replaceBlocks.replacesBedrock) {
            return this.getBlockSettings().getBedrockBlock().parseWithBiomeAndHeight(this.biomeConfigsHaveReplacement, replaceBlocks, y);
        }
        return this.getBlockSettings().getBedrockBlock();
    }

    @Override
    public void renameOldSettings(SettingsMap reader, ILogger logger, IMaterialReader materialReader) {
        // Put BiomeMode in compatibility mode when NormalBiomes is found and create default groups
        if (reader.hasSetting(PresetStandardValues.NORMAL_BIOMES)) {
            int landSize = reader.getSetting(PresetStandardValues.LAND_SIZE);
            int landRarity = reader.getSetting(PresetStandardValues.LAND_RARITY);
            List<String> normalBiomes = reader.getSetting(PresetStandardValues.NORMAL_BIOMES);

            BiomeGroupFunction normalGroup = new BiomeGroupFunction(this, PresetStandardValues.BiomeGroupNames.NORMAL, landSize, landRarity, normalBiomes);

            int iceSize = reader.getSetting(PresetStandardValues.ICE_SIZE);
            int iceRarity = reader.getSetting(PresetStandardValues.ICE_RARITY);
            List<String> iceBiomes = reader.getSetting(PresetStandardValues.ICE_BIOMES);
            BiomeGroupFunction iceGroup = new BiomeGroupFunction(this, PresetStandardValues.BiomeGroupNames.ICE, iceSize, iceRarity, iceBiomes);

            reader.addConfigFunctions(Arrays.asList(normalGroup, iceGroup));
        }

        // Rename old settings

        reader.renameOldSetting("SpawnPointSet", PresetStandardValues.FIXED_SPAWN_POINT);
        reader.renameOldSetting("PopulationBoundsCheck", PresetStandardValues.DECORATION_BOUNDS_CHECK);
        reader.renameOldSetting("EvenCaveDistrubution", PresetStandardValues.EVEN_CAVE_DISTRIBUTION);
        reader.renameOldSetting("WorldFog", PresetStandardValues.PRESET_FOG_COLOR);
        reader.renameOldSetting("BedrockobBlock", PresetStandardValues.BEDROCK_BLOCK);
        reader.renameOldSetting("DimensionPortalMaterials", PresetStandardValues.PORTAL_BLOCKS);
    }
    @Override
    public void writeConfigSettings(SettingsMap writer) {
        PresetWriter.writePresetConfig(this, writer);
    }
}
