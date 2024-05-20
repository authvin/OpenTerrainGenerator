package com.pg85.otg.config;

import com.pg85.otg.config.io.SettingsMap;
import com.pg85.otg.config.io.SimpleSettingsMap;
import com.pg85.otg.constants.Constants;
import com.pg85.otg.interfaces.ILogger;
import com.pg85.otg.interfaces.IMaterialReader;
import com.pg85.otg.util.minecraft.BiomeRegistryNames;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for all configuration files. Configuration files read
 * the desired settings from a {@link SettingsMap}, and can then write the
 * settings back to such a map.
 *
 */
public interface ConfigFile
{
	String getConfigName();
	/**
	 * Gets all settings of this config file.
	 * @return All settings.
	 */
	default SettingsMap getSettingsAsMap()
	{
		SettingsMap settingsMap = new SimpleSettingsMap(getConfigName());
		writeConfigSettings(settingsMap);
		return settingsMap;
	}

	/**
	 * Methods that subclasses must override to write the actual settings.
	 * @param settingsMap The map to write the settings to.
	 */
	void writeConfigSettings(SettingsMap settingsMap);

	/**
	 * rewrite configs in old formats to the modern format, so that they can be read.
	 * @param reader The settings reader.
	 */
	void renameOldSettings(SettingsMap reader, ILogger logger, IMaterialReader materialReader);

	static List<String> filterBiomes(List<String> biomes, List<String> customBiomes)
	{
		ArrayList<String> output = new ArrayList<String>();

		for (String key : biomes)
		{
			key = key.trim();
			if (customBiomes.contains(key))
			{
				output.add(key);
				continue;
			}

			if (BiomeRegistryNames.Contain(key))
			{
				output.add(key);
			}
		}
		return output;
	}
}
