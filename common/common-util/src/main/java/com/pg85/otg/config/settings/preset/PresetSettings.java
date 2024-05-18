package com.pg85.otg.config.settings.preset;

import com.pg85.otg.config.ConfigFile;
import lombok.Getter;

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
public abstract class PresetSettings implements ConfigFile {
	protected GameRuleSettings gameRuleSettings;
	protected StructureSettings structureSettings;
	protected CarverSettings carverSettings;
	protected SpawnSettings spawnSettings;
	protected PortalSettings portalSettings;
	protected DimensionSettings dimensionSettings;
	protected GenerationSettings generationSettings;
	protected TerrainSettings terrainSettings;
	protected ImageSettings imageSettings;
	protected VisualSettings visualSettings;
	protected PresetInfo presetInfo;
	protected ResourceSettings resourceSettings;
	protected BlockSettings blockSettings;
	private final String configName;

    protected PresetSettings(String configName) {
        this.configName = configName;
    }

	@Override
	public String getConfigName() {
		return configName;
	}
}

