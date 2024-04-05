package com.pg85.otg.config;

import com.pg85.otg.config.io.IConfigFunctionProvider;
import com.pg85.otg.config.io.SettingsMap;
import com.pg85.otg.config.io.SimpleSettingsMap;
import com.pg85.otg.interfaces.ILogger;
import com.pg85.otg.interfaces.IMaterialReader;
import com.pg85.otg.util.minecraft.BiomeRegistryNames;

import java.nio.file.Path;
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
	 * Called before {@link #readConfigSettings(SettingsMap)} to rewrite
	 * configs in old formats to the modern format, so that they can be read.
	 * @param reader The settings reader.
	 */
	void renameOldSettings(SettingsMap reader, ILogger logger, IMaterialReader materialReader);

	/**
	 * Silently corrects the given number so that it is higher than or equal to
	 * the minimum value.
	 * @param currentValue The current value, will be corrected if needed.
	 * @param minimumValue The minimum value.
	 * @return The corrected value.
	 */
	default int higherThanOrEqualTo(int currentValue, int minimumValue)
	{
        return Math.max(currentValue, minimumValue);
    }

	/**
	 * Silently corrects the given number so that it is lower than or equal
	 * to the maximum value.
	 * @param currentValue The current value, will be corrected if needed.
	 * @param maximumValue The maximum value.
	 * @return The corrected value.
	 */
	default int lowerThanOrEqualTo(int currentValue, int maximumValue)
	{
		if (currentValue > maximumValue)
		{
			return maximumValue;
		}
		return currentValue;
	}

	default List<String> filterBiomes(List<String> biomes, List<String> customBiomes)
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
