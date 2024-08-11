package com.pg85.otg.config.biome;

import com.pg85.otg.config.io.FileSettingsReader;
import com.pg85.otg.config.io.SettingsMap;
import com.pg85.otg.constants.Constants;
import com.pg85.otg.util.OTGLog;
import com.pg85.otg.util.logging.LogCategory;
import com.pg85.otg.util.logging.LogLevel;

import java.io.File;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.*;

/**
 * This class searches for the appropriate file for each biome.
 * You give it a list of folders to search in, and it will find the location of
 * all BiomeConfigs. Files will be created for non-existing BiomeConfigs.
 * 
 */
public final class BiomeConfigFinder
{
	// >> Biome Extensions & Related
	public static final Collection<String> BiomeConfigExtensions = Arrays.asList(
		"BiomeConfig.ini",
		".biome",
		".bc",
		".bc.ini",
		".biome.ini"
	);

	/**
	 * Constructs a new biome loader.
	 *
	 */
	public BiomeConfigFinder() { }

	/**
	 * Finds the biomes in the given directories.
	 * 
	 * @param directories The directories to search in.
	 *
	 * @return A map of biome name --> location on disk.
	 */
	public Map<String, SettingsMap> findBiomes(Collection<Path> directories)
	{
		Map<String, SettingsMap> biomeConfigsStore = new HashMap<>();

		// Search all directories
		for (Path directoryPath  : directories)
		{
			File directory = directoryPath.toFile();
			// Account for the possibility that folder creation failed
			if (directory.exists())
			{
				loadBiomesFromDirectory(biomeConfigsStore, directory);
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
	private void loadBiomesFromDirectory(Map<String, SettingsMap> biomeConfigsStore, File directory)
	{
		for (File file : Objects.requireNonNull(directory.listFiles()))
		{
			// Search recursively
			if (file.isDirectory())
			{
				loadBiomesFromDirectory(biomeConfigsStore, file);
				continue;
			}

			// Extract name from filename
			String biomeName = toBiomeName(file);
			if (biomeName == null)
			{
				// Not a valid biome file
				continue;
			}
			
			// Load biomeconfig
			File renamedFile = renameBiomeFile(file, biomeName);
			SettingsMap settings = FileSettingsReader.read(biomeName, renamedFile);
			biomeConfigsStore.put(biomeName, settings);
		}
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
	private File renameBiomeFile(File toRename, String biomeName)
	{
		String preferredFileName = toFileName(biomeName);
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
	private String toBiomeName(File file)
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
	private String toFileName(String biomeName)
	{
		return biomeName + Constants.BiomeConfigFileExtension;
	}
}
