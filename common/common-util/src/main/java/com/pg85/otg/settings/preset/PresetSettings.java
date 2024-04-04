package com.pg85.otg.settings.preset;

import com.pg85.otg.util.biome.ReplaceBlockMatrix;
import com.pg85.otg.util.materials.LocalMaterialData;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * PresetConfig.ini classes
 * <p>
 * PresetSettings defines anything that's used/exposed between projects.
 * PresetConfig contains only fields/methods used for io/serialisation/instantiation.
 * <p>
 * PresetConfig should be used only in common-core and platform-specific layers, when reading/writing settings on app start.
 * PresetSettings should be used wherever settings are used in code.
 */
@Builder
@Getter
public abstract class PresetSettings {
	protected GameRuleSettings gameRuleSettings;
	protected StructureSettings structureSettings;
	protected CarverSettings carverSettings;
	protected SpawnSettings spawnSettings;
	protected PortalSettings portalSettings;
	protected DimensionSettings dimensionSettings;
	protected BiomeSettings biomeSettings;
	protected TerrainSettings terrainSettings;
	protected ImageSettings imageSettings;
	protected CustomStructureSettings customStructureSettings;
	protected VisualSettings visualSettings;
	protected PresetInfo presetInfo;
	protected ResourceSettings resourceSettings;
	protected BlockSettings blockSettings;
	protected BedrockSettings bedrockSettings;
    protected PresetSettings() {}
	public abstract LocalMaterialData getBedrockBlockReplaced(ReplaceBlockMatrix replaceBlocks, int y);
}

