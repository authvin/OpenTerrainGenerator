package com.pg85.otg.presets;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.pg85.otg.config.biome.BiomeConfig;
import com.pg85.otg.config.biome.BiomeConfigFinder;
import com.pg85.otg.config.biome.BiomeConfigFinder.BiomeConfigStub;
import com.pg85.otg.config.io.FileSettingsReader;
import com.pg85.otg.config.io.FileSettingsWriter;
import com.pg85.otg.config.io.IConfigFunctionProvider;
import com.pg85.otg.config.io.SettingsMap;
import com.pg85.otg.config.standard.BiomeStandardValues;
import com.pg85.otg.config.world.PresetConfig;
import com.pg85.otg.constants.Constants;
import com.pg85.otg.interfaces.ILogger;
import com.pg85.otg.interfaces.IMaterialReader;
import com.pg85.otg.interfaces.IPresetConfig;
import com.pg85.otg.util.logging.LogCategory;
import com.pg85.otg.util.logging.LogLevel;
import com.pg85.otg.util.minecraft.BiomeRegistryNames;

/**
 * A base class for a platform-specific preset loader, which loads 
 * all presets from disk when the OTG Engine is started at app start, 
 * and registers all biomes with their worldconfig/biomeconfig settings.
 */
public abstract class LocalPresetLoader
{	
	private static final int MAX_INHERITANCE_DEPTH = 15;
	protected final Object materialReaderLock = new Object();
	protected final File presetsDir;
	protected final HashMap<String, Preset> presets = new HashMap<>();
	protected final HashMap<String, String> aliasMap = new HashMap<>();
	protected HashMap<String, IMaterialReader> materialReaderByPresetFolderName = new HashMap<>();

	public LocalPresetLoader(Path otgRootFolder)
	{
		this.presetsDir = Paths.get(otgRootFolder.toString(), File.separator + Constants.PRESETS_FOLDER).toFile();
	}

	public IMaterialReader getMaterialReader(String presetFolderName)
	{
		IMaterialReader materialReader;
		synchronized(this.materialReaderLock)
		{
			materialReader = this.materialReaderByPresetFolderName.get(presetFolderName);
			if(materialReader == null)
			{
				materialReader = createMaterialReader();
				this.materialReaderByPresetFolderName.put(presetFolderName, materialReader);
			}
		}
		return materialReader;
	}

	// Creates a preset-specific materialreader, have to do this
	// only when loading each preset since each preset may have
	// its own block fallbacks / block dictionaries.
	protected abstract IMaterialReader createMaterialReader();

	public abstract void registerBiomes();
	
	protected abstract void mergeVanillaBiomeMobSpawnSettings(BiomeConfigStub biomeConfigStub, String inheritMobsBiomeName);
	
	public Preset getPresetByShortNameOrFolderName(String name)
	{
		// Example: preset is stored as "Biome Bundle v7", but also accepts "Biome Bundle"
		if (aliasMap.containsKey(name))
		{
			return this.presets.get(aliasMap.get(name));
		}
		return this.presets.get(name);
	}
	
	public Preset getPresetByFolderName(String name)
	{
		return this.presets.get(name);
	}

	public ArrayList<Preset> getAllPresets()
	{
		return new ArrayList<Preset>(presets.values());
	}

	public Set<String> getAllPresetFolderNames()
	{
		return presets.keySet();
	}
	
	public String getDefaultPresetFolderName()
	{
		return this.presets.keySet().size() == 0 ? Constants.DEFAULT_PRESET_NAME : this.presets.keySet().contains(Constants.DEFAULT_PRESET_NAME) ? Constants.DEFAULT_PRESET_NAME : (String) this.presets.keySet().toArray()[0];
	}
		
	public void loadPresetsFromDisk(IConfigFunctionProvider biomeResourcesManager, ILogger logger)
	{
		if(this.presetsDir.exists() && this.presetsDir.isDirectory())
		{
			for(File presetDir : this.presetsDir.listFiles())
			{
				if(presetDir.isDirectory())
				{
					for(File file : presetDir.listFiles())
					{
						if(file.getName().equals(Constants.PRESET_CONFIG_FILE))
						{
							Preset preset = loadPreset(presetDir.toPath(), biomeResourcesManager, logger);
							this.presets.put(preset.getFolderName(), preset);
							this.aliasMap.put(preset.getShortPresetName(), preset.getFolderName());
							break;
						}
					}
				}
			}
		}
	}
	
	protected Preset loadPreset(Path presetDir, IConfigFunctionProvider biomeResourcesManager, ILogger logger)
	{
		File presetConfigFile = new File(presetDir.toString(), Constants.PRESET_CONFIG_FILE);
		File biomesDirectory = new File(presetDir.toString(), Constants.BIOMES_FOLDER);
		if(!biomesDirectory.exists())
		{
			biomesDirectory = new File(presetDir.toString(), Constants.LEGACY_WORLD_BIOMES_FOLDER);
		}
		String presetFolderName = presetDir.toFile().getName();
		
		SettingsMap presetConfigSettings = FileSettingsReader.read(presetFolderName, presetConfigFile, logger);
		PresetConfig presetConfig = new PresetConfig(presetDir, presetConfigSettings, addBiomesFromDirRecursive(biomesDirectory), biomeResourcesManager, logger, getMaterialReader(presetFolderName), presetFolderName);
		FileSettingsWriter.writeToFile(presetConfig.getSettingsAsMap(), presetConfigFile, presetConfig.getPresetInfo().getSettingsMode(), logger);

		// use shortPresetName to register the biomes, instead of presetName
		ArrayList<BiomeConfig> biomeConfigs = loadBiomeConfigs(presetConfig.getPresetInfo().getShortPresetName(), presetConfig.getPresetInfo().getMajorVersion(), presetDir, biomesDirectory.toPath(), presetConfig, biomeResourcesManager, logger, getMaterialReader(presetFolderName));

		return new Preset(presetDir, presetConfig.getPresetInfo().getShortPresetName(), presetConfig, biomeConfigs);
	}
	
	private ArrayList<String> addBiomesFromDirRecursive(File biomesDirectory)
	{
		ArrayList<String> biomes = new ArrayList<String>();
		if(biomesDirectory.exists())
		{
			for(File biomeConfig : biomesDirectory.listFiles())
			{
				if(biomeConfig.isFile() && biomeConfig.getName().endsWith(Constants.BiomeConfigFileExtension))
				{
					biomes.add(biomeConfig.getName().replace(Constants.BiomeConfigFileExtension, ""));
				}
				else if(biomeConfig.isDirectory())
				{
					biomes.addAll(addBiomesFromDirRecursive(biomeConfig));
				}
			}
		}
		return biomes;
	}

	private ArrayList<BiomeConfig> loadBiomeConfigs(String presetShortName, int presetMajorVersion, Path presetDir, Path presetBiomesDir, IPresetConfig presetConfig, IConfigFunctionProvider biomeResourcesManager, ILogger logger, IMaterialReader materialReader)
	{
		// Establish folders
		List<Path> biomeDirs = new ArrayList<Path>(2);
		biomeDirs.add(presetBiomesDir);
		
		// Load all files
		BiomeConfigFinder biomeConfigFinder = new BiomeConfigFinder();
		Map<String, BiomeConfigStub> biomeConfigStubs = biomeConfigFinder.findBiomes(presetConfig.getBiomeSettings().getWorldBiomes(), presetConfig.getTerrainSettings().getWorldHeightScale(), biomeDirs, logger, materialReader);

		// Read all settings
		ArrayList<BiomeConfig> biomeConfigs = readAndWriteSettings(presetConfig, biomeConfigStubs, presetDir, presetShortName, presetMajorVersion, true, biomeResourcesManager, logger, materialReader);

		// Update settings dynamically, these changes don't get written back to the file
		processSettings(presetConfig, biomeConfigs);

		if(logger.getLogCategoryEnabled(LogCategory.CONFIGS) && logger.canLogForPreset(presetDir.getFileName().toString()))
		{
			logger.log(
				LogLevel.INFO,
				LogCategory.CONFIGS,
				MessageFormat.format(
					"{0} biomes Loaded", 
					biomeConfigs.size()
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

	private ArrayList<BiomeConfig> readAndWriteSettings(IPresetConfig presetConfig, Map<String, BiomeConfigStub> biomeConfigStubs, Path presetDir, String presetShortName, int presetMajorVersion, boolean write, IConfigFunctionProvider biomeResourcesManager, ILogger logger, IMaterialReader materialReader)
	{
		ArrayList<BiomeConfig> biomeConfigs = new ArrayList<BiomeConfig>();

		for (BiomeConfigStub biomeConfigStub : biomeConfigStubs.values())
		{
			// Inheritance
			processMobInheritance(biomeConfigStubs, biomeConfigStub, 0, logger);

			// Settings reading
			BiomeConfig biomeConfig = new BiomeConfig(biomeConfigStub.getBiomeName(), biomeConfigStub, presetDir, biomeConfigStub.getSettings(), presetConfig, presetShortName, presetMajorVersion, biomeResourcesManager, logger, materialReader);
			biomeConfigs.add(biomeConfig);

			// Settings writing
			if(write)
			{
				Path writeFile = biomeConfigStub.getPath();
				FileSettingsWriter.writeToFile(biomeConfig.getSettingsAsMap(), writeFile.toFile(), presetConfig.getPresetInfo().getSettingsMode(), logger);
			}
		}

		return biomeConfigs;
	}

	private void processSettings(IPresetConfig presetConfig, ArrayList<BiomeConfig> biomeConfigs)
	{
		for(BiomeConfig biomeConfig : biomeConfigs)
		{
			// Index ReplacedBlocks
			if (!presetConfig.getBiomeSettings().isBiomeConfigsHaveReplacement())
			{
				presetConfig.setBiomeConfigsHaveReplacement(biomeConfig.hasReplaceBlocksSettings());
			}

			// Index maxSmoothRadius
			if (presetConfig.getTerrainSettings().getMaxSmoothRadius() < biomeConfig.getTerrainSettings().getSmoothRadius())
			{
				presetConfig.setMaxSmoothRadius(biomeConfig.getTerrainSettings().getSmoothRadius());
			}
			if (presetConfig.getTerrainSettings().getMaxSmoothRadius() < biomeConfig.getTerrainSettings().getCHCSmoothRadius())
			{
				presetConfig.setMaxSmoothRadius(biomeConfig.getTerrainSettings().getCHCSmoothRadius());
			}
		}
	}

	private void processMobInheritance(Map<String, BiomeConfigStub> biomeConfigStubs, BiomeConfigStub biomeConfigStub, int currentDepth, ILogger logger)
	{
		if (biomeConfigStub.inheritMobsBiomeNameProcessed)
		{
			// Already processed
			return;
		}

		String stubInheritMobsBiomeName = biomeConfigStub.getSettings().getSetting(BiomeStandardValues.INHERIT_MOBS_BIOME_NAME, BiomeStandardValues.INHERIT_MOBS_BIOME_NAME.getDefaultValue(), logger, null);

		if(stubInheritMobsBiomeName != null && stubInheritMobsBiomeName.length() > 0)
		{
			String[] inheritMobsBiomeNames = stubInheritMobsBiomeName.split(",");
			for(String inheritMobsBiomeName : inheritMobsBiomeNames)
			{
				if (inheritMobsBiomeName.isEmpty())
				{
					// Not extending anything
					continue;
				}

				// This biome inherits mobs from another biome
				BiomeConfigStub inheritMobsBiomeConfig = biomeConfigStubs.get(inheritMobsBiomeName);

				if (inheritMobsBiomeConfig == null || inheritMobsBiomeConfig == biomeConfigStub) // Most likely a legacy config that is not using resourcelocation yet, for instance: Plains instead of minecraft:plains. Try to convert.
				{
					String vanillaBiomeName = BiomeRegistryNames.getRegistryNameForDefaultBiome(inheritMobsBiomeName);
					if(vanillaBiomeName != null)
					{
						inheritMobsBiomeConfig = null;
						inheritMobsBiomeName = vanillaBiomeName;
					}
					else if(inheritMobsBiomeConfig == biomeConfigStub)
					{
						if(logger.getLogCategoryEnabled(LogCategory.MOBS))
						{
							logger.log(
								LogLevel.ERROR,
								LogCategory.MOBS,
								MessageFormat.format("The biome {0} tried to inherit mobs from itself.", biomeConfigStub.getBiomeName())
							);
						}
						continue;
					}
				}

				// Check for too much recursion
				if (currentDepth > MAX_INHERITANCE_DEPTH)
				{
					if(logger.getLogCategoryEnabled(LogCategory.MOBS))
					{
						logger.log(
							LogLevel.ERROR,
							LogCategory.MOBS,
							MessageFormat.format(
								"The biome {0} cannot inherit mobs from biome {1} - too many configs processed already! Cyclical inheritance?", 
								biomeConfigStub.getPath().toFile().getName(), 
								inheritMobsBiomeConfig.getPath().toFile().getName()
							)
						);
					}
				}

				if(inheritMobsBiomeConfig != null)
				{
					if (!inheritMobsBiomeConfig.inheritMobsBiomeNameProcessed)
					{
						// This biome has not been processed yet, do that first
						processMobInheritance(biomeConfigStubs, inheritMobsBiomeConfig, currentDepth + 1, logger);
					}

					// Merge the two
					biomeConfigStub.mergeMobs(inheritMobsBiomeConfig);
				} else {

					// This is a vanilla biome or a biome added by another mod.
					mergeVanillaBiomeMobSpawnSettings(biomeConfigStub, inheritMobsBiomeName);
					continue;
				}
			}

			// Done
			biomeConfigStub.inheritMobsBiomeNameProcessed = true;
		}
	}
}
