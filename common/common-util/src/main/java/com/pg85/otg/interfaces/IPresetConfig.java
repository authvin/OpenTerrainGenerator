package com.pg85.otg.interfaces;

import com.pg85.otg.settings.preset.*;
import com.pg85.otg.util.biome.ReplaceBlockMatrix;
import com.pg85.otg.util.materials.LocalMaterialData;

/**
 * PresetConfig.ini classes
 * 
 * IPresetConfig defines anything that's used/exposed between projects.
 * PresetConfigBase implements anything needed for IPresetConfig. 
 * PresetConfig contains only fields/methods used for io/serialisation/instantiation.
 * 
 * PresetConfig should be used only in common-core and platform-specific layers, when reading/writing settings on app start.
 * IPresetConfig should be used wherever settings are used in code. 
 */
public interface IPresetConfig {

	// all settings wrappers from com.pg85.otg.records.preset should have a corresponding getter here
	BedrockSettings getBedrockSettings();

	BiomeSettings getBiomeSettings();

	BlockSettings getBlockSettings();

	CarverSettings getCarverSettings();

	CustomStructureSettings getCustomStructureSettings();

	DimensionSettings getDimensionSettings();

	GameRuleSettings getGameRuleSettings();

	ImageSettings getImageSettings();

	PortalSettings getPortalSettings();

	PresetInfo getPresetInfo();

	ResourceSettings getResourceSettings();

	SpawnSettings getSpawnSettings();

	StructureSettings getStructureSettings();

	TerrainSettings getTerrainSettings();

	VisualSettings getVisualSettings();

	LocalMaterialData getBedrockBlockReplaced(ReplaceBlockMatrix replacedBlocks, int y);

	// Biome settings

	boolean setBiomeConfigsHaveReplacement(boolean biomeConfigsHaveReplacement);

	void setMaxSmoothRadius(int smoothRadius);
}

