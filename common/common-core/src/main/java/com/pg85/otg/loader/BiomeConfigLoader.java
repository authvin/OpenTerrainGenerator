package com.pg85.otg.loader;

import com.pg85.otg.config.biome.BiomeConfig;
import com.pg85.otg.config.io.FileSettingsReader;
import com.pg85.otg.config.io.FileSettingsWriter;
import com.pg85.otg.config.io.IConfigFunctionProvider;
import com.pg85.otg.config.io.SettingsMap;
import com.pg85.otg.config.preset.PresetConfig;
import com.pg85.otg.constants.Constants;
import com.pg85.otg.interfaces.ILogger;
import com.pg85.otg.util.OTGLog;
import com.pg85.otg.util.logging.LogCategory;
import com.pg85.otg.util.logging.LogLevel;

import java.io.File;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class searches for the appropriate file for each biome.
 * You give it a list of folders to search in, and it will find the location of
 * all BiomeConfigs. Files will be created for non-existing BiomeConfigs.
 * 
 */
public final class BiomeConfigLoader {
	// >> Biome Extensions & Related
	public static final Collection<String> BiomeConfigExtensions = Arrays.asList(
		"BiomeConfig.ini",
		".biome",
		".bc",
		".bc.ini",
		".biome.ini"
	);

	private BiomeConfigLoader() { }

	/**
	 * Finds the biomes in the given directories.
	 *
	 * @param directory The directory to search for biomes in.
	 * @return A map of biome name --> location on disk.
	 */
	public static Map<String, SettingsMap> readAllBiomeFiles(File directory)
	{
		Map<String, SettingsMap> biomeConfigsStore = new HashMap<>();

		// Account for the possibility that folder creation failed
		if (directory.exists())
		{
			readBiomeFilesRecursive(biomeConfigsStore, directory);
		} else {
			if(OTGLog.getLogger().getLogCategoryEnabled(LogCategory.CONFIGS))
			{
				OTGLog.getLogger().log(
					LogLevel.ERROR,
					LogCategory.CONFIGS,
					MessageFormat.format("Biome directory {0} does not exist.", directory)
				);
			}
		}

		return biomeConfigsStore;
	}

	/**
	 * Loads the biomes from the given directory.
	 * 
	 * @param biomeConfigsStore Map to store all the found biome configs in.
	 * @param directory		 The directory to load from.
	 */
	private static void readBiomeFilesRecursive(Map<String, SettingsMap> biomeConfigsStore, File directory)
	{
		for (File file : Objects.requireNonNull(directory.listFiles()))
		{
			// Search recursively
			if (file.isDirectory())
			{
				readBiomeFilesRecursive(biomeConfigsStore, file);
				continue;
			}

			// Extract name from filename
			String biomeName = toBiomeName(file);
			if (biomeName == null)
			{
				// Not a valid biome file
				continue;
			}

			SettingsMap settings = readBiomeFile(file, biomeName);
			biomeConfigsStore.put(biomeName, settings);
		}
	}

	private static SettingsMap readBiomeFile(File file, String biomeName) {
		// Load biomeconfig
		File renamedFile = renameBiomeFile(file, biomeName);
        return FileSettingsReader.read(biomeName, renamedFile);
	}

	/**
	 * Tries to rename the config file so that it has the correct extension.
	 * Does nothing if the config file already has the correct extension. If
	 * the rename fails, a message is printed.
	 *
	 * @param toRename  The file that should be renamed.
	 * @param biomeName The biome that the file has settings for.
	 * @return The renamed file.
	 */
	private static File renameBiomeFile(File toRename, String biomeName)
	{
		String preferredFileName = BiomeConfigLoader.toFileName(biomeName);
		if (toRename.getName().equalsIgnoreCase(preferredFileName))
		{
			// No need to rename
			return toRename;
		}

		// Wrong extension, rename
		File newFile = new File(toRename.getParentFile(), preferredFileName);
		if (toRename.renameTo(newFile))
		{
			return newFile;
		} else {
			if(OTGLog.getLogger().getLogCategoryEnabled(LogCategory.CONFIGS))
			{
				OTGLog.getLogger().log(
					LogLevel.ERROR,
					LogCategory.CONFIGS,
					MessageFormat.format(
						"Failed to rename biome file {0} to {1}",
						toRename.getAbsolutePath(), 
						newFile.getAbsolutePath()
					)
				);
			}
			return toRename;
		}
	}

	/**
	 * Extracts the biome name out of the file name.
	 * 
	 * @param file The file to extract the biome name out of.
	 * @return The biome name, or null if the file is not a biome config file.
	 */
	private static String toBiomeName(File file)
	{
		String fileName = file.getName();
		for (String extension : BiomeConfigExtensions)
		{
			if (fileName.endsWith(extension))
			{
                return fileName.substring(0, fileName.lastIndexOf(extension));
			}
		}

		// Invalid file name
		return null;
	}

	/**
	 * Gets the name of the file the biome should be saved in. This will use
	 * the extension as defined in the PluginConfig.ini file.
	 * 
	 * @param biomeName The biome.
	 * @return The name of the file the biome should be saved in.
	 */
	private static String toFileName(String biomeName)
	{
		return biomeName + Constants.BiomeConfigFileExtension;
	}

	public static File getBiomeDirectory(Path presetDir) {
		File biomesDirectory = new File(presetDir.toString(), Constants.BIOMES_FOLDER);
        if (biomesDirectory.exists()) {
            return biomesDirectory;
        } else {
			return new File(presetDir.toString(), Constants.LEGACY_WORLD_BIOMES_FOLDER);
		}
	}

	public static ArrayList<BiomeConfig> loadBiomeConfigs(Path presetDir, PresetConfig presetConfig, IConfigFunctionProvider biomeResourcesManager)
	{
		File biomesDirectory = getBiomeDirectory(presetDir);
		// Load all files
        Map<String, SettingsMap> biomeConfigStore = readAllBiomeFiles(biomesDirectory);

		// Read all settings
		ArrayList<BiomeConfig> biomeConfigs = readAndWriteSettings(presetConfig, biomeConfigStore, biomeResourcesManager);

		// Update settings dynamically, these changes don't get written back to the file
		processSettings(presetConfig, biomeConfigs);

		ILogger logger = OTGLog.getLogger();
		if(logger.getLogCategoryEnabled(LogCategory.CONFIGS) && logger.canLogForPreset(presetDir.getFileName().toString()))
		{
			logger.log(
				LogLevel.INFO,
				LogCategory.CONFIGS,
				MessageFormat.format(
					"{0} biomes loaded for preset {1}",
					biomeConfigs.size(),
					presetConfig.getConfigName()
				)
			);
			logger.log(
				LogLevel.INFO,
				LogCategory.CONFIGS,
				biomeConfigs.stream().map(
					item -> item.getIdentitySettings().getBiomeName()
				).collect(
					Collectors.joining(", ")
				)
			);
		}
		return biomeConfigs;
	}

	private static ArrayList<BiomeConfig> readAndWriteSettings(PresetConfig presetConfig, Map<String, SettingsMap> biomeSettingsMaps, IConfigFunctionProvider biomeResourcesManager)
	{
		ArrayList<BiomeConfig> biomeConfigs = new ArrayList<BiomeConfig>();

		for (SettingsMap settingsMap : biomeSettingsMaps.values())
		{
			// Settings reading
			BiomeConfig biomeConfig = new BiomeConfig(settingsMap, presetConfig, biomeResourcesManager);
			biomeConfigs.add(biomeConfig);

			// Settings writing
            Path writeFile = settingsMap.getPath();
            FileSettingsWriter.writeToFile(biomeConfig.getSettingsAsMap(), writeFile.toFile(), presetConfig.getPresetInfo().getSettingsMode());
        }

		return biomeConfigs;
	}

	private static void processSettings(PresetConfig presetConfig, ArrayList<BiomeConfig> biomeConfigs)
	{
		for(BiomeConfig biomeConfig : biomeConfigs)
		{
			// Index ReplacedBlocks
			if (!presetConfig.isBiomeConfigsHaveReplacement())
			{
				presetConfig.setBiomeConfigsHaveReplacement(biomeConfig.getSurfaceSettings().getReplacedBlocks().hasReplaceSettings());
			}

			// Index maxSmoothRadius
			if (presetConfig.getMaxSmoothRadius() < biomeConfig.getTerrainSettings().getSmoothRadius())
			{
				presetConfig.setMaxSmoothRadius(biomeConfig.getTerrainSettings().getSmoothRadius());
			}
			if (presetConfig.getMaxSmoothRadius() < biomeConfig.getTerrainSettings().getCHCSmoothRadius())
			{
				presetConfig.setMaxSmoothRadius(biomeConfig.getTerrainSettings().getCHCSmoothRadius());
			}
		}
	}
}
