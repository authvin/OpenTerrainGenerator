package com.pg85.otg.config.world;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.OptionalLong;

import com.pg85.otg.config.ConfigFunction;
import com.pg85.otg.config.biome.BiomeGroupFunction;
import com.pg85.otg.config.biome.BiomeGroupManager;
import com.pg85.otg.config.biome.TemplateBiome;
import com.pg85.otg.config.io.IConfigFunctionProvider;
import com.pg85.otg.config.io.SettingsMap;
import com.pg85.otg.config.standard.PresetStandardValues;
import com.pg85.otg.constants.Constants;
import com.pg85.otg.constants.settings.BiomeMode;
import com.pg85.otg.interfaces.ILogger;
import com.pg85.otg.interfaces.IMaterialReader;
import com.pg85.otg.interfaces.IPresetConfig;
import com.pg85.otg.settings.preset.*;
import com.pg85.otg.util.logging.LogCategory;
import com.pg85.otg.util.logging.LogLevel;
import com.pg85.otg.util.minecraft.BiomeRegistryNames;

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
public class PresetConfig extends PresetConfigBase
{
	public static final HashMap<String, Class<? extends ConfigFunction<?>>> CONFIG_FUNCTIONS = new HashMap<>();
	static
	{
		CONFIG_FUNCTIONS.put("BiomeGroup", BiomeGroupFunction.class);
		CONFIG_FUNCTIONS.put("TemplateBiome", TemplateBiome.class);
	}

	// Fields used only in common-core or platform layers that aren't in IPresetConfig

	// TODO: Refactor BiomeGroups classes, since we have new biome groups now.
	// TODO: Refactor to IBiomeGroupManager and move to Base?	
	private BiomeGroupManager biomeGroupManager;
	// TODO: Refactor to ITemplateBiome and move to Base?	
	private List<TemplateBiome> templateBiomes;
	
	public PresetConfig(Path settingsDir, SettingsMap settingsReader, ArrayList<String> biomes, IConfigFunctionProvider biomeResourcesManager, ILogger logger, IMaterialReader materialReader, String presetFolderName)
	{
		super(settingsReader.getName());

		this.worldBiomes.addAll(biomes);
		this.renameOldSettings(settingsReader, logger, materialReader);
		this.readConfigSettings(settingsReader, biomeResourcesManager, logger, materialReader, presetFolderName);
		this.validateAndCorrectSettings(settingsDir, logger);		 
	}

	// TODO: Refactor to IBiomeGroupManager and move to Base?
	public BiomeGroupManager getBiomeGroupManager()
	{
		return this.biomeGroupManager;
	}

	// TODO: Refactor to ITemplateBiome and move to Base?
	public List<TemplateBiome> getTemplateBiomes()
	{
		return this.templateBiomes;
	}
	
	@Override
	protected void renameOldSettings(SettingsMap reader, ILogger logger, IMaterialReader materialReader)
	{
		// Put BiomeMode in compatibility mode when NormalBiomes is found and create default groups
		if (reader.hasSetting(PresetStandardValues.NORMAL_BIOMES))
		{
			int landSize = reader.getSetting(PresetStandardValues.LAND_SIZE, logger);
			int landRarity = reader.getSetting(PresetStandardValues.LAND_RARITY, logger);
			List<String> normalBiomes = reader.getSetting(PresetStandardValues.NORMAL_BIOMES, logger);

			BiomeGroupFunction normalGroup = new BiomeGroupFunction(this, PresetStandardValues.BiomeGroupNames.NORMAL, landSize, landRarity, normalBiomes);

			int iceSize = reader.getSetting(PresetStandardValues.ICE_SIZE, logger);
			int iceRarity = reader.getSetting(PresetStandardValues.ICE_RARITY, logger);
			List<String> iceBiomes = reader.getSetting(PresetStandardValues.ICE_BIOMES, logger);
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
	protected void validateAndCorrectSettings(Path settingsDir, ILogger logger)
	{
		this.landSize = lowerThanOrEqualTo(this.landSize, this.generationDepth);
		this.landFuzzy = lowerThanOrEqualTo(this.landFuzzy, this.generationDepth - this.landSize);
		this.riverRarity = lowerThanOrEqualTo(this.riverRarity, this.generationDepth);
		this.riverSize = lowerThanOrEqualTo(this.riverSize, this.generationDepth - this.riverRarity);

		this.biomeGroupManager.filterBiomes(this.worldBiomes, logger);
		this.isleBiomes = filterBiomes(this.isleBiomes, this.worldBiomes);
		this.borderBiomes = filterBiomes(this.borderBiomes, this.worldBiomes);

		if (this.biomeMode == BiomeMode.FromImage)
		{
			File mapFile = new File(settingsDir.toString(), this.imageFile);
			if (!mapFile.exists())
			{
				logger.log(LogLevel.ERROR, LogCategory.MAIN, "Biome map file not found. Switching BiomeMode to Normal");
				this.biomeMode = BiomeMode.Normal;
			}
		}
		this.imageFillBiome = (BiomeRegistryNames.Contain(this.imageFillBiome) || this.worldBiomes.contains(this.imageFillBiome)) ? this.imageFillBiome : PresetStandardValues.IMAGE_FILL_BIOME.getDefaultValue(null);

		this.caveMaxAltitude = higherThanOrEqualTo(this.caveMaxAltitude, this.caveMinAltitude);
		this.caveSystemPocketMaxSize = higherThanOrEqualTo(this.caveSystemPocketMaxSize, this.caveSystemPocketMinSize);
		this.ravineMaxAltitude = higherThanOrEqualTo(this.ravineMaxAltitude, this.ravineMinAltitude);
		this.ravineMaxLength = higherThanOrEqualTo(this.ravineMaxLength, this.ravineMinLength);
		this.waterLevelMax = higherThanOrEqualTo(this.waterLevelMax, this.waterLevelMin);
	}

	@Override
	protected void readConfigSettings(SettingsMap reader, IConfigFunctionProvider biomeResourcesManager, ILogger logger, IMaterialReader materialReader, String presetFolderName)
	{
		// Misc
		
		var presetInfoBuilder = PresetInfo.builder();
		
		presetInfoBuilder.settingsMode(reader.getSetting(PresetStandardValues.SETTINGS_MODE, logger));
		presetInfoBuilder.author(reader.getSetting(PresetStandardValues.AUTHOR, logger));
		presetInfoBuilder.description(reader.getSetting(PresetStandardValues.DESCRIPTION, logger));
		presetInfoBuilder.shortPresetName(reader.getSetting(PresetStandardValues.SHORT_PRESET_NAME, logger));
		presetInfoBuilder.majorVersion(reader.getSetting(PresetStandardValues.MAJOR_VERSION, logger));
		presetInfoBuilder.minorVersion(reader.getSetting(PresetStandardValues.MINOR_VERSION, logger));

		this.presetInfo = presetInfoBuilder.build();
		
		// Visual settings
		
		this.visualSettings = VisualSettings.builder().fogColor(reader.getSetting(PresetStandardValues.PRESET_FOG_COLOR, logger)).build();

		// Biome resources
		
		var resourceSettingsBuilder = ResourceSettings.builder();
		
		resourceSettingsBuilder.disableOreGen(reader.getSetting(PresetStandardValues.DISABLE_OREGEN, logger));

		this.resourceSettings = resourceSettingsBuilder.build();
		
		// Blocks
		var blockSettingsBuilder = BlockSettings.builder();
		
		blockSettingsBuilder.removeSurfaceStone(reader.getSetting(PresetStandardValues.REMOVE_SURFACE_STONE, logger));
		blockSettingsBuilder.waterBlock(reader.getSetting(PresetStandardValues.WATER_BLOCK, logger, materialReader));
		blockSettingsBuilder.bedrockBlock(reader.getSetting(PresetStandardValues.BEDROCK_BLOCK, logger, materialReader));		
		blockSettingsBuilder.cooledLavaBlock(reader.getSetting(PresetStandardValues.COOLED_LAVA_BLOCK, logger, materialReader));
		blockSettingsBuilder.iceBlock(reader.getSetting(PresetStandardValues.ICE_BLOCK, logger, materialReader));
		blockSettingsBuilder.carverLavaBlock(reader.getSetting(PresetStandardValues.CARVER_LAVA_BLOCK, logger, materialReader));

		this.blockSettings = blockSettingsBuilder.build();
		
		// Bedrock
		var bedrockSettingsBuilder = BedrockSettings.builder();
		
		bedrockSettingsBuilder.bedrockDisabled(reader.getSetting(PresetStandardValues.DISABLE_BEDROCK, logger));
		bedrockSettingsBuilder.ceilingBedrock(reader.getSetting(PresetStandardValues.CEILING_BEDROCK, logger));
		bedrockSettingsBuilder.flatBedrock(reader.getSetting(PresetStandardValues.FLAT_BEDROCK, logger));

		this.bedrockSettings = bedrockSettingsBuilder.build();
		
		// Biome settings
		var biomeSettingsBuilder = BiomeSettings.builder();
		
		biomeSettingsBuilder.biomeRarityScale(reader.getSetting(PresetStandardValues.BIOME_RARITY_SCALE, logger));
		biomeSettingsBuilder.generationDepth(reader.getSetting(PresetStandardValues.GENERATION_DEPTH, logger));
		biomeSettingsBuilder.oldGroupRarity(reader.getSetting(PresetStandardValues.OLD_GROUP_RARITY, logger));
		biomeSettingsBuilder.oldLandRarity(reader.getSetting(PresetStandardValues.OLD_LAND_RARITY, logger));
		biomeSettingsBuilder.landFuzzy(reader.getSetting(PresetStandardValues.LAND_FUZZY, logger));
		biomeSettingsBuilder.landRarity(reader.getSetting(PresetStandardValues.LAND_RARITY, logger));
		biomeSettingsBuilder.landSize(reader.getSetting(PresetStandardValues.LAND_SIZE, logger));
		biomeSettingsBuilder.forceLandAtSpawn(reader.getSetting(PresetStandardValues.FORCE_LAND_AT_SPAWN, logger));
		biomeSettingsBuilder.oceanBiomeSize(reader.getSetting(PresetStandardValues.OCEAN_BIOME_SIZE, logger));
		biomeSettingsBuilder.defaultOceanBiome(reader.getSetting(PresetStandardValues.DEFAULT_OCEAN_BIOME, logger));
		biomeSettingsBuilder.defaultWarmOceanBiome(reader.getSetting(PresetStandardValues.DEFAULT_WARM_OCEAN_BIOME, logger));
		biomeSettingsBuilder.defaultLukewarmOceanBiome(reader.getSetting(PresetStandardValues.DEFAULT_LUKEWARM_OCEAN_BIOME, logger));
		biomeSettingsBuilder.defaultColdOceanBiome(reader.getSetting(PresetStandardValues.DEFAULT_COLD_OCEAN_BIOME, logger));
		biomeSettingsBuilder.defaultFrozenOceanBiome(reader.getSetting(PresetStandardValues.DEFAULT_FROZEN_OCEAN_BIOME, logger));
		biomeSettingsBuilder.biomeMode(reader.getSetting(PresetStandardValues.BIOME_MODE, logger));
		biomeSettingsBuilder.frozenOcean(reader.getSetting(PresetStandardValues.FROZEN_OCEAN, logger));		
		biomeSettingsBuilder.frozenOceanTemperature(reader.getSetting(PresetStandardValues.FROZEN_OCEAN_TEMPERATURE, logger));
		biomeSettingsBuilder.isleBiomes(reader.getSetting(PresetStandardValues.ISLE_BIOMES, logger));
		biomeSettingsBuilder.borderBiomes(reader.getSetting(PresetStandardValues.BORDER_BIOMES, logger));
		biomeSettingsBuilder.randomRivers(reader.getSetting(PresetStandardValues.RANDOM_RIVERS, logger));
		biomeSettingsBuilder.riverRarity(reader.getSetting(PresetStandardValues.RIVER_RARITY, logger));
		biomeSettingsBuilder.riverSize(reader.getSetting(PresetStandardValues.RIVER_SIZE, logger));
		biomeSettingsBuilder.riversEnabled(reader.getSetting(PresetStandardValues.RIVERS_ENABLED, logger));
		
		// BiomeGroups requires that values like genDepth are initialized
		readTemplateBiomes(reader, biomeResourcesManager, logger, materialReader);
		readBiomeGroups(reader, biomeResourcesManager, logger, materialReader);

		biomeSettingsBuilder.blackListedBiomes(reader.getSetting(PresetStandardValues.BLACKLISTED_BIOMES, logger));
		
		this.biomeSettings = biomeSettingsBuilder.build();
		
		
		
		// Terrain settings
		
		var terrainSettingsBuilder = TerrainSettings.builder();
		
		terrainSettingsBuilder.fractureHorizontal(reader.getSetting(PresetStandardValues.FRACTURE_HORIZONTAL, logger));
		terrainSettingsBuilder.fractureVertical(reader.getSetting(PresetStandardValues.FRACTURE_VERTICAL, logger));
		terrainSettingsBuilder.worldHeightCap(1 << reader.getSetting(PresetStandardValues.WORLD_HEIGHT_CAP_BITS, logger));
		terrainSettingsBuilder.worldHeightScale(1 << reader.getSetting(PresetStandardValues.WORLD_HEIGHT_SCALE_BITS, logger));
		terrainSettingsBuilder.betterSnowFall(reader.getSetting(PresetStandardValues.BETTER_SNOW_FALL, logger));
		terrainSettingsBuilder.waterLevelMax(reader.getSetting(PresetStandardValues.WATER_LEVEL_MAX, logger));
		terrainSettingsBuilder.waterLevelMin(reader.getSetting(PresetStandardValues.WATER_LEVEL_MIN, logger));
		terrainSettingsBuilder.carverLavaBlockHeight(reader.getSetting(PresetStandardValues.CARVER_LAVA_BLOCK_HEIGHT, logger));
		
		this.terrainSettings = terrainSettingsBuilder.build();

		// FromImageMode

		var imageSettingsBuilder = ImageSettings.builder();
		
		imageSettingsBuilder.imageOrientation(reader.getSetting(PresetStandardValues.IMAGE_ORIENTATION, logger));		
		imageSettingsBuilder.imageFile(reader.getSetting(PresetStandardValues.IMAGE_FILE, logger));
		imageSettingsBuilder.imageFillBiome(reader.getSetting(PresetStandardValues.IMAGE_FILL_BIOME, logger));
		imageSettingsBuilder.imageMode(reader.getSetting(PresetStandardValues.IMAGE_MODE, logger));		
		imageSettingsBuilder.imageXOffset(reader.getSetting(PresetStandardValues.IMAGE_X_OFFSET, logger));
		imageSettingsBuilder.imageZOffset(reader.getSetting(PresetStandardValues.IMAGE_Z_OFFSET, logger));
		
		this.imageSettings = imageSettingsBuilder.build();

		// Vanilla structures
		var structureSettingsBuilder = StructureSettings.builder();
		structureSettingsBuilder.villageSpacing(reader.getSetting(PresetStandardValues.VILLAGE_SPACING, logger));
		structureSettingsBuilder.villageSeparation(reader.getSetting(PresetStandardValues.VILLAGE_SEPARATION, logger));
		structureSettingsBuilder.desertPyramidSpacing(reader.getSetting(PresetStandardValues.DESERTPYRAMID_SPACING, logger));
		structureSettingsBuilder.desertPyramidSeparation(reader.getSetting(PresetStandardValues.DESERTPYRAMID_SEPARATION, logger));
		structureSettingsBuilder.iglooSpacing(reader.getSetting(PresetStandardValues.IGLOO_SPACING, logger));
		structureSettingsBuilder.iglooSeparation(reader.getSetting(PresetStandardValues.IGLOO_SEPARATION, logger));
		structureSettingsBuilder.jungleTempleSpacing(reader.getSetting(PresetStandardValues.JUNGLETEMPLE_SPACING, logger));
		structureSettingsBuilder.jungleTempleSeparation(reader.getSetting(PresetStandardValues.JUNGLETEMPLE_SEPARATION, logger));
		structureSettingsBuilder.swampHutSpacing(reader.getSetting(PresetStandardValues.SWAMPHUT_SPACING, logger));
		structureSettingsBuilder.swampHutSeparation(reader.getSetting(PresetStandardValues.SWAMPHUT_SEPARATION, logger));
		structureSettingsBuilder.pillagerOutpostSpacing(reader.getSetting(PresetStandardValues.PILLAGEROUTPOST_SPACING, logger));
		structureSettingsBuilder.pillagerOutpostSeparation(reader.getSetting(PresetStandardValues.PILLAGEROUTPOST_SEPARATION, logger));
		structureSettingsBuilder.strongholdSpacing(reader.getSetting(PresetStandardValues.STRONGHOLD_SPACING, logger));
		structureSettingsBuilder.strongholdSeparation(reader.getSetting(PresetStandardValues.STRONGHOLD_SEPARATION, logger));
		structureSettingsBuilder.strongholdDistance(reader.getSetting(PresetStandardValues.STRONGHOLD_DISTANCE, logger));
		structureSettingsBuilder.strongholdSpread(reader.getSetting(PresetStandardValues.STRONGHOLD_SPREAD, logger));
		structureSettingsBuilder.strongholdCount(reader.getSetting(PresetStandardValues.STRONGHOLD_COUNT, logger));
		structureSettingsBuilder.oceanMonumentSpacing(reader.getSetting(PresetStandardValues.OCEANMONUMENT_SPACING, logger));
		structureSettingsBuilder.oceanMonumentSeparation(reader.getSetting(PresetStandardValues.OCEANMONUMENT_SEPARATION, logger));
		structureSettingsBuilder.endCitySpacing(reader.getSetting(PresetStandardValues.ENDCITY_SPACING, logger));
		structureSettingsBuilder.endCitySeparation(reader.getSetting(PresetStandardValues.ENDCITY_SEPARATION, logger));
		structureSettingsBuilder.woodlandMansionSpacing(reader.getSetting(PresetStandardValues.WOODLANDMANSION_SPACING, logger));
		structureSettingsBuilder.woodlandMansionSeparation(reader.getSetting(PresetStandardValues.WOODLANDMANSION_SEPARATION, logger));
		structureSettingsBuilder.buriedTreasureSpacing(reader.getSetting(PresetStandardValues.BURIEDTREASURE_SPACING, logger));
		structureSettingsBuilder.buriedTreasureSeparation(reader.getSetting(PresetStandardValues.BURIEDTREASURE_SEPARATION, logger));
		structureSettingsBuilder.mineshaftSpacing(reader.getSetting(PresetStandardValues.MINESHAFT_SPACING, logger));
		structureSettingsBuilder.mineshaftSeparation(reader.getSetting(PresetStandardValues.MINESHAFT_SEPARATION, logger));
		structureSettingsBuilder.ruinedPortalSpacing(reader.getSetting(PresetStandardValues.RUINEDPORTAL_SPACING, logger));
		structureSettingsBuilder.ruinedPortalSeparation(reader.getSetting(PresetStandardValues.RUINEDPORTAL_SEPARATION, logger));
		structureSettingsBuilder.shipwreckSpacing(reader.getSetting(PresetStandardValues.SHIPWRECK_SPACING, logger));
		structureSettingsBuilder.shipwreckSeparation(reader.getSetting(PresetStandardValues.SHIPWRECK_SEPARATION, logger));
		structureSettingsBuilder.oceanRuinSpacing(reader.getSetting(PresetStandardValues.OCEANRUIN_SPACING, logger));
		structureSettingsBuilder.oceanRuinSeparation(reader.getSetting(PresetStandardValues.OCEANRUIN_SEPARATION, logger));
		structureSettingsBuilder.bastionRemnantSpacing(reader.getSetting(PresetStandardValues.BASTIONREMNANT_SPACING, logger));
		structureSettingsBuilder.bastionRemnantSeparation(reader.getSetting(PresetStandardValues.BASTIONREMNANT_SEPARATION, logger));
		structureSettingsBuilder.netherFortressSpacing(reader.getSetting(PresetStandardValues.NETHERFORTRESS_SPACING, logger));
		structureSettingsBuilder.netherFortressSeparation(reader.getSetting(PresetStandardValues.NETHERFORTRESS_SEPARATION, logger));
		structureSettingsBuilder.netherFossilSpacing(reader.getSetting(PresetStandardValues.NETHERFOSSIL_SPACING, logger));
		structureSettingsBuilder.netherFossilSeparation(reader.getSetting(PresetStandardValues.NETHERFOSSIL_SEPARATION, logger));
		
		structureSettingsBuilder.woodlandMansionsEnabled(reader.getSetting(PresetStandardValues.WOODLAND_MANSIONS_ENABLED, logger));
		structureSettingsBuilder.netherFortressesEnabled(reader.getSetting(PresetStandardValues.NETHER_FORTRESSES_ENABLED, logger));
		structureSettingsBuilder.buriedTreasureEnabled(reader.getSetting(PresetStandardValues.BURIED_TREASURE_ENABLED, logger));
		structureSettingsBuilder.oceanRuinsEnabled(reader.getSetting(PresetStandardValues.OCEAN_RUINS_ENABLED, logger));
		structureSettingsBuilder.pillagerOutpostsEnabled(reader.getSetting(PresetStandardValues.PILLAGER_OUTPOSTS_ENABLED, logger));
		structureSettingsBuilder.bastionRemnantsEnabled(reader.getSetting(PresetStandardValues.BASTION_REMNANTS_ENABLED, logger));
		structureSettingsBuilder.netherFossilsEnabled(reader.getSetting(PresetStandardValues.NETHER_FOSSILS_ENABLED, logger));
		structureSettingsBuilder.endCitiesEnabled(reader.getSetting(PresetStandardValues.END_CITIES_ENABLED, logger));
		structureSettingsBuilder.ruinedPortalsEnabled(reader.getSetting(PresetStandardValues.RUINED_PORTALS_ENABLED, logger));
		structureSettingsBuilder.shipwrecksEnabled(reader.getSetting(PresetStandardValues.SHIPWRECKS_ENABLED, logger));
		structureSettingsBuilder.strongholdsEnabled(reader.getSetting(PresetStandardValues.STRONGHOLDS_ENABLED, logger));
		structureSettingsBuilder.villagesEnabled(reader.getSetting(PresetStandardValues.VILLAGES_ENABLED, logger));		
		structureSettingsBuilder.mineshaftsEnabled(reader.getSetting(PresetStandardValues.MINESHAFTS_ENABLED, logger));
		structureSettingsBuilder.oceanMonumentsEnabled(reader.getSetting(PresetStandardValues.OCEAN_MONUMENTS_ENABLED, logger));
		structureSettingsBuilder.rareBuildingsEnabled(reader.getSetting(PresetStandardValues.RARE_BUILDINGS_ENABLED, logger));

		this.structureSettings = structureSettingsBuilder.build();

		// OTG Custom structures

		// IsOTGPlus was renamed to CustomStructureType, but value types are different (bool -> enum)
		// If IsOTGPlus is true, use it. IsOTGPlus isn't written to configs, so this is only required once
		// to update configs.
		var customStructureSettingsBuilder = CustomStructureSettings.builder();

		customStructureSettingsBuilder.customStructureType(reader.getSetting(PresetStandardValues.CUSTOM_STRUCTURE_TYPE, logger));
		customStructureSettingsBuilder.useOldBO3StructureRarity(reader.getSetting(PresetStandardValues.USE_OLD_BO3_STRUCTURE_RARITY, logger));
		customStructureSettingsBuilder.decorationBoundsCheck(reader.getSetting(PresetStandardValues.DECORATION_BOUNDS_CHECK, logger));
		customStructureSettingsBuilder.maximumCustomStructureRadius(reader.getSetting(PresetStandardValues.MAXIMUM_CUSTOM_STRUCTURE_RADIUS, logger));		
		customStructureSettingsBuilder.BO3AtSpawn(reader.getSetting(PresetStandardValues.BO3_AT_SPAWN, logger));
		
		this.customStructureSettings = customStructureSettingsBuilder.build();
		
		// Caves & Ravines
		var carverSettingsBuilder = CarverSettings.builder();
		carverSettingsBuilder.cavesEnabled(reader.getSetting(PresetStandardValues.CAVES_ENABLED, logger));
		carverSettingsBuilder.caveFrequency(reader.getSetting(PresetStandardValues.CAVE_FREQUENCY, logger));
		carverSettingsBuilder.caveRarity(reader.getSetting(PresetStandardValues.CAVE_RARITY, logger));
		carverSettingsBuilder.evenCaveDistribution(reader.getSetting(PresetStandardValues.EVEN_CAVE_DISTRIBUTION, logger));		
		carverSettingsBuilder.caveMinAltitude(reader.getSetting(PresetStandardValues.CAVE_MIN_ALTITUDE, logger));
		carverSettingsBuilder.caveMaxAltitude(reader.getSetting(PresetStandardValues.CAVE_MAX_ALTITUDE, logger));
		carverSettingsBuilder.caveSystemFrequency(reader.getSetting(PresetStandardValues.CAVE_SYSTEM_FREQUENCY, logger));
		carverSettingsBuilder.individualCaveRarity(reader.getSetting(PresetStandardValues.INDIVIDUAL_CAVE_RARITY, logger));		
		carverSettingsBuilder.caveSystemPocketChance(reader.getSetting(PresetStandardValues.CAVE_SYSTEM_POCKET_CHANCE, logger));
		carverSettingsBuilder.caveSystemPocketMinSize(reader.getSetting(PresetStandardValues.CAVE_SYSTEM_POCKET_MIN_SIZE, logger));
		carverSettingsBuilder.caveSystemPocketMaxSize(reader.getSetting(PresetStandardValues.CAVE_SYSTEM_POCKET_MAX_SIZE, logger));

		carverSettingsBuilder.ravinesEnabled(reader.getSetting(PresetStandardValues.RAVINES_ENABLED, logger));
		carverSettingsBuilder.ravineRarity(reader.getSetting(PresetStandardValues.RAVINE_RARITY, logger));
		carverSettingsBuilder.ravineMinLength(reader.getSetting(PresetStandardValues.RAVINE_MIN_LENGTH, logger));
		carverSettingsBuilder.ravineMaxLength(reader.getSetting(PresetStandardValues.RAVINE_MAX_LENGTH, logger));
		carverSettingsBuilder.ravineDepth(reader.getSetting(PresetStandardValues.RAVINE_DEPTH, logger));
		carverSettingsBuilder.ravineMinAltitude(reader.getSetting(PresetStandardValues.RAVINE_MIN_ALTITUDE, logger));
		carverSettingsBuilder.ravineMaxAltitude(reader.getSetting(PresetStandardValues.RAVINE_MAX_ALTITUDE, logger));
		
		this.carverSettings = carverSettingsBuilder.build();
		
		// Spawn point
		var spawnSettingsBuilder = SpawnSettings.builder();
		
		spawnSettingsBuilder.spawnPointSet(reader.getSetting(PresetStandardValues.FIXED_SPAWN_POINT, logger, materialReader));
		spawnSettingsBuilder.spawnPointX(reader.getSetting(PresetStandardValues.SPAWN_POINT_X, logger, materialReader));
		spawnSettingsBuilder.spawnPointY(reader.getSetting(PresetStandardValues.SPAWN_POINT_Y, logger, materialReader));
		spawnSettingsBuilder.spawnPointZ(reader.getSetting(PresetStandardValues.SPAWN_POINT_Z, logger, materialReader));
		spawnSettingsBuilder.spawnPointAngle(reader.getSetting(PresetStandardValues.SPAWN_POINT_ANGLE, logger, materialReader));
		
		this.spawnSettings = spawnSettingsBuilder.build();
		
		// Portal settings
		// Only used when preset is not overworld/dimension/end.
		// Can be overridden via DimensionConfig.
		var portalSettingsBuilder = PortalSettings.builder();
		
		portalSettingsBuilder.portalBlocks(reader.getSetting(PresetStandardValues.PORTAL_BLOCKS, logger, materialReader));
		portalSettingsBuilder.portalColor(reader.getSetting(PresetStandardValues.PORTAL_COLOR, logger));
		portalSettingsBuilder.portalMob(reader.getSetting(PresetStandardValues.PORTAL_MOB, logger));
		portalSettingsBuilder.portalIgnitionSource(reader.getSetting(PresetStandardValues.PORTAL_IGNITION_SOURCE, logger));		
		
		this.portalSettings = portalSettingsBuilder.build();
		
		// Dimension settings
		
		var dimensionSettingsBuilder = DimensionSettings.builder();
		
		long fixedTime = reader.getSetting(PresetStandardValues.FIXED_TIME, logger);
		dimensionSettingsBuilder.fixedTime(fixedTime == -1L ? OptionalLong.empty() : OptionalLong.of(fixedTime));
		dimensionSettingsBuilder.hasSkyLight(reader.getSetting(PresetStandardValues.HAS_SKYLIGHT, logger));
		dimensionSettingsBuilder.hasCeiling(reader.getSetting(PresetStandardValues.HAS_CEILING, logger));
		dimensionSettingsBuilder.ultraWarm(reader.getSetting(PresetStandardValues.ULTRA_WARM, logger));
		dimensionSettingsBuilder.natural(reader.getSetting(PresetStandardValues.NATURAL, logger));
		dimensionSettingsBuilder.coordinateScale(reader.getSetting(PresetStandardValues.COORDINATE_SCALE, logger));
		dimensionSettingsBuilder.createDragonFight(reader.getSetting(PresetStandardValues.CREATE_DRAGON_FLIGHT, logger));
		dimensionSettingsBuilder.piglinSafe(reader.getSetting(PresetStandardValues.PIGLIN_SAFE, logger));
		dimensionSettingsBuilder.bedWorks(reader.getSetting(PresetStandardValues.BED_WORKS, logger));
		dimensionSettingsBuilder.respawnAnchorWorks(reader.getSetting(PresetStandardValues.RESPAWN_ANCHOR_WORKS, logger));
		dimensionSettingsBuilder.hasRaids(reader.getSetting(PresetStandardValues.HAS_RAIDS, logger)); 
		dimensionSettingsBuilder.logicalHeight(reader.getSetting(PresetStandardValues.LOGICAL_HEIGHT, logger));
		dimensionSettingsBuilder.infiniburn(reader.getSetting(PresetStandardValues.INFINIBURN, logger));
		dimensionSettingsBuilder.effectsLocation(reader.getSetting(PresetStandardValues.EFFECTS_LOCATION, logger));
		dimensionSettingsBuilder.ambientLight(reader.getSetting(PresetStandardValues.AMBIENT_LIGHT, logger).floatValue());

		this.dimensionSettings = dimensionSettingsBuilder.build();
		
		// Game rules 
		// Only used when preset is OTG overworld atm, since gamerules are shared across dimensions.
		// Can be overridden via DimensionConfig.
		var gameRuleSettingsBuilder = GameRuleSettings.builder();
		
		gameRuleSettingsBuilder.overrideGameRules(reader.getSetting(PresetStandardValues.OVERRIDE_GAME_RULES, logger));
		gameRuleSettingsBuilder.doFireTick(reader.getSetting(PresetStandardValues.DO_FIRE_TICK, logger));
		gameRuleSettingsBuilder.mobGriefing(reader.getSetting(PresetStandardValues.MOB_GRIEFING, logger));
		gameRuleSettingsBuilder.keepInventory(reader.getSetting(PresetStandardValues.KEEP_INVENTORY, logger));
		gameRuleSettingsBuilder.doMobSpawning(reader.getSetting(PresetStandardValues.DO_MOB_SPAWNING, logger));
		gameRuleSettingsBuilder.doMobLoot(reader.getSetting(PresetStandardValues.DO_MOB_LOOT, logger));
		gameRuleSettingsBuilder.doTileDrops(reader.getSetting(PresetStandardValues.DO_TILE_DROPS, logger));
		gameRuleSettingsBuilder.doEntityDrops(reader.getSetting(PresetStandardValues.DO_ENTITY_DROPS, logger));
		gameRuleSettingsBuilder.commandBlockOutput(reader.getSetting(PresetStandardValues.COMMAND_BLOCK_OUTPUT, logger));
		gameRuleSettingsBuilder.naturalRegeneration(reader.getSetting(PresetStandardValues.NATURAL_REGENERATION, logger));
		gameRuleSettingsBuilder.doDaylightCycle(reader.getSetting(PresetStandardValues.DO_DAY_LIGHT_CYCLE, logger));
		gameRuleSettingsBuilder.logAdminCommands(reader.getSetting(PresetStandardValues.LOG_ADMIN_COMMANDS, logger));
		gameRuleSettingsBuilder.showDeathMessages(reader.getSetting(PresetStandardValues.SHOW_DEATH_MESSAGES, logger));
		gameRuleSettingsBuilder.randomTickSpeed(reader.getSetting(PresetStandardValues.RANDOM_TICK_SPEED, logger));
		gameRuleSettingsBuilder.sendCommandFeedback(reader.getSetting(PresetStandardValues.SEND_COMMAND_FEEDBACK, logger));
		gameRuleSettingsBuilder.spectatorsGenerateChunks(reader.getSetting(PresetStandardValues.SPECTATORS_GENERATE_CHUNKS, logger));
		gameRuleSettingsBuilder.spawnRadius(reader.getSetting(PresetStandardValues.SPAWN_RADIUS, logger));
		gameRuleSettingsBuilder.disableElytraMovementCheck(reader.getSetting(PresetStandardValues.DISABLE_ELYTRA_MOVEMENT_CHECK, logger));
		gameRuleSettingsBuilder.maxEntityCramming(reader.getSetting(PresetStandardValues.MAX_ENTITY_CRAMMING, logger));
		gameRuleSettingsBuilder.doWeatherCycle(reader.getSetting(PresetStandardValues.DO_WEATHER_CYCLE, logger));
		gameRuleSettingsBuilder.doLimitedCrafting(reader.getSetting(PresetStandardValues.DO_LIMITED_CRAFTING, logger));
		gameRuleSettingsBuilder.maxCommandChainLength(reader.getSetting(PresetStandardValues.MAX_COMMAND_CHAIN_LENGTH, logger));
		gameRuleSettingsBuilder.announceAdvancements(reader.getSetting(PresetStandardValues.ANNOUNCE_ADVANCEMENTS, logger));
		gameRuleSettingsBuilder.disableRaids(reader.getSetting(PresetStandardValues.DISABLE_RAIDS, logger));
		gameRuleSettingsBuilder.doInsomnia(reader.getSetting(PresetStandardValues.DO_INSOMNIA, logger));
		gameRuleSettingsBuilder.drowningDamage(reader.getSetting(PresetStandardValues.DROWNING_DAMAGE, logger));
		gameRuleSettingsBuilder.fallDamage(reader.getSetting(PresetStandardValues.FALL_DAMAGE, logger));
		gameRuleSettingsBuilder.fireDamage(reader.getSetting(PresetStandardValues.FIRE_DAMAGE, logger));
		gameRuleSettingsBuilder.doPatrolSpawning(reader.getSetting(PresetStandardValues.DO_PATROL_SPAWNING, logger));
		gameRuleSettingsBuilder.doTraderSpawning(reader.getSetting(PresetStandardValues.DO_TRADER_SPAWNING, logger));
		gameRuleSettingsBuilder.forgiveDeadPlayers(reader.getSetting(PresetStandardValues.FORGIVE_DEAD_PLAYERS, logger));
		gameRuleSettingsBuilder.universalAnger(reader.getSetting(PresetStandardValues.UNIVERSAL_ANGER, logger));
		this.gameRuleSettings = gameRuleSettingsBuilder.build();
	}

	private void readTemplateBiomes(SettingsMap reader, IConfigFunctionProvider biomeResourcesManager, ILogger logger, IMaterialReader materialReader)
	{
		this.templateBiomes = new ArrayList<>();
		for (ConfigFunction<IPresetConfig> res : reader.getConfigFunctions((IPresetConfig)this, biomeResourcesManager, logger, materialReader))
		{
			if (res != null)
			{
				if (res instanceof TemplateBiome)
				{
					this.templateBiomes.add((TemplateBiome)res);
				}
			}
		}
	}	
	
	private void readBiomeGroups(SettingsMap reader, IConfigFunctionProvider biomeResourcesManager, ILogger logger, IMaterialReader materialReader)
	{
		this.biomeGroupManager = new BiomeGroupManager();
		for (ConfigFunction<IPresetConfig> res : reader.getConfigFunctions((IPresetConfig)this, biomeResourcesManager, logger, materialReader))
		{
			if (res != null)
			{
				if (res instanceof BiomeGroupFunction)
				{
					this.biomeGroupManager.registerGroup((BiomeGroupFunction) res, logger);
				}
			}
		}
	}

	@Override
	protected void writeConfigSettings(SettingsMap writer)
	{
		writer.header1("PresetConfig",
			"Contains settings which affect the entire world, biome specific settings can be found in the Biome Configs.",
			"This file controls biome groupings, ocean and land sizes/rarities, river settings, cave and canyon distribution,",
			"vanilla minecraft structure spawning, sea level, dimension/portal settings and more."
		);

		writer.header2("Config Writing");

		writer.putSetting(PresetStandardValues.SETTINGS_MODE, this.getPresetInfo().getSettingsMode(),
			"Each time " + Constants.MOD_ID + " reads the config files it can also write to them. With this setting you can change how this behaves. Possible modes:",
			"	WriteAll - Auto-update settings from old versions, order them, add comments, reset invalid settings and remove custom comments. (Recommended)",
			"	WriteWithoutComments - Same as WriteAll, but removes all comments, both the ones added by OTG and custom ones. Removing comments is a recommended optimization for release versions of presets.",
			"	WriteDisable - Doesn't write to the config files. Errors are not corrected, old settings are read but are not corrected. Custom comments won't be removed with this mode."
		);
		
		writer.header2("Preset Identity");
		
		writer.putSetting(PresetStandardValues.AUTHOR, this.getPresetInfo().getAuthor(),
			"The author of this preset"
		);

		writer.putSetting(PresetStandardValues.DESCRIPTION, this.getPresetInfo().getDescription(),
			"A short description of this preset"
		);

		writer.putSetting(PresetStandardValues.MAJOR_VERSION, this.getPresetInfo().getMajorVersion(),
			"The preset major version. Increasing the minor version makes the PresetPacker overwrite,",
			"while increasing the major version will make the PresetPacker save a new copy"
		);
		
		writer.putSetting(PresetStandardValues.MINOR_VERSION, this.getPresetInfo().getMinorVersion(),
			"The preset minor version. Increasing the minor version makes the PresetPacker overwrite,",
			"while increasing the major version will make the PresetPacker save a new copy"
		);

		writer.putSetting(PresetStandardValues.SHORT_PRESET_NAME, this.getPresetInfo().getShortPresetName(),
			"The shortened name for the preset, used in biome resource locations and similar"
		);

		writer.header2("Visual Settings",
			"Controls the world's fog colors. Sky, grass and foliage colors are defined inside the biome configs."
		);
		
		writer.putSetting(PresetStandardValues.PRESET_FOG_COLOR, this.getVisualSettings().getFogColor(),
			"Color of the distance fog, can be overridden per biome."
		);

		writer.header2("Biome Modes");

		writer.putSetting(PresetStandardValues.BIOME_MODE, this.getBiomeSettings().getBiomeMode(),
			"Possible biome modes:",
			"	Normal - standard random generation with biome groups, uses all features.",
			"	FromImage - biome layout defined by an image file."
		);
		
		writer.header1("Settings for BiomeMode: Normal");
		
		writer.putSetting(PresetStandardValues.GENERATION_DEPTH, this.getBiomeSettings().getGenerationDepth(),
			"Defines the maximum number BiomeSize, RiverSize and LandSize can be set to.", 
			"All size settings such as Biome Group Size, RiverSize, LandSize (in the PresetConfig.ini), and BiomeSize (in Biome Configs) must be between 0 (largest) and GenerationDepth (smallest).", 
			"Increasing GenerationDepth by one will roughly double the size of all biomes, similarly decreasing it by 1 will half the size of all biomes.",
			"Small values (1-2) and Large values (20+) may affect generator performance.",
			"This setting is also used in BiomeMode:FromImage when ImageMode is set to ContinueNormal"
		);

		writer.putSetting(PresetStandardValues.BIOME_RARITY_SCALE, this.getBiomeSettings().getBiomeRarityScale(),
			"Max biome rarity from 1 to infinity. By default this is 100, but you can raise it for fine-grained control, or to create biomes with a chance of occurring smaller than 1/100."
		);

		writer.putSetting(PresetStandardValues.OLD_GROUP_RARITY, this.getBiomeSettings().isOldGroupRarity(),
			"Whether or not OTG should use the old group rarity"
		);

		writer.putSetting(PresetStandardValues.OLD_LAND_RARITY, this.getBiomeSettings().isOldLandRarity(),
				"Whether or not OTG should use the old land rarity. Disabling this will make LandRarity work as a percentage"
		);

		writer.header2("Template Biomes",
				"Template biomes allow you to include non-Open Terrain Generator (OTG) biomes, which can be from vanilla Minecraft or other mods, in your OTG presets.",
				"",
				"Syntax: TemplateBiome(BiomeConfigName, BiomeRegistryName or Tags/Categories[, more BiomeRegistryName or Tags/Categories[, ...]], minTemperature, maxTemperature)",
				"BiomeConfigName: The name of the corresponding biome configuration. This is case sensitive.",
				"BiomeRegistryName: The registry name of a non-OTG biome. For example, \"minecraft:plains\".",
				"Tags/Categories: Instead of using the BiomeRegistryName, you can use Forge Biome Dictionary IDs or Minecraft Biome Categories.",
				"",
				"OTG fetches all non-OTG biomes that match the specified category/tags and associates them with the BiomeConfig. The BiomeConfig must have the setting 'TemplateForBiome' set to true, or it will be ignored.",
				"",
				"Example: TemplateBiome(MCForest, category.forest tag.overworld)",
				"This adds all forest biomes in the overworld to the 'MCForest' biome configuration. Biomes are never added twice.",
				"",
				"Use space as an AND operator. For example, \"category.forest tag.overworld\" matches biomes with both the 'forest' category and the 'overworld' tag.",
				"To target both Minecraft and modded biomes, use \"category.\" or \"tag.\".",
				"To target only modded biomes, use \"modcategory.\" or \"modtag.\".",
				"To target only Minecraft biomes, use \"mccategory.\" or \"mctag.\".",
				"To filter biomes for a specific mod, add \"mod.<namespace>\". For example, \"mod.byg category.plains tag.overworld\".",
				"To exclude specific biome registry names, tags, categories or mods, use \"-\". For example, -tag.overworld to exclude overworld biomes.",
				"",
				"MinTemperature/MaxTemperature: Optional parameters. Only biomes within this temperature range are allowed.",
				"Example: TemplateBiome(TagPlains, category.plains -tag.overworld) or TemplateBiome(TagPlains, category.plains -tag.overworld, -0.2, 0.2)",
				"The first example targets a BiomeConfig named 'TagPlains.bc', and adds to it all non-OTG biomes that are of category \"plains\" but do not have the 'overworld' tag.",
				"The second example does the same, but also includes a temperature range between -0.2 and 0.2.",
				"",
				"Note:",
				"Each biome can only be assigned to one biome config, so the order of TemplateBiome()s is important. Put your most specific TemplateBiome first, and the most generic last.",
				"When using BiomeRegistryName to include or exclude a biome, it must have its own entry. For example: \",minecraft:forest,-minecraft:plains,\""
		);

		writer.addConfigFunctions(this.templateBiomes);
		
		writer.header2("Biome Groups",
			"Biome groups group similar biomes together so that they spawn next to each other.", 
			"Only standard biomes are required to be part of biome groups, isle, border and river biomes are configured separately.",
			"",
			"Syntax: BiomeGroup(GroupName, GroupSize, GroupRarity, BiomeName or Tags/Categories[, AnotherName[, ...]], minTemperature, maxTemperature)",
			"GroupName - must be unique, choose something descriptive.",
			"Size - from 0 to GenerationDepth. Lower number = larger. All biomes in the group must be smaller (higher BiomeSize number) or equal to this value.",
			"Rarity - relative spawn chance.",
			"BiomeName - Name of a corresponding biome config. Case sensitive. Can also be a registry name (minecraft:plains), if there is a associated TemplateBiome().",
			"If the biome config is a template biome, all associated non-otg biomes are added to the group.",
			"Tags/Categories - Instead of BiomeName, Forge Biome Dictionary id's and/or MC Biome Categories. ",
			"OTG fetches all non-OTG biomes that match the specified category/tags and adds them to the biome group.",
			"A TemplateBiome() that targets the biome must exist, or it is ignored.",
			"Example: BiomeGroup(NormalBiomes, 1, 100, category.plains tag.overworld, tag.hot tag.dry)",
			"Adds 2 entries; all plains biomes in the overworld, all hot+dry biomes. Biomes are never added twice.",
			"- Use space as an AND operator, in the above example \"category.plains tag.overworld\" matches biomes with category plains AND tag overworld.",
			"To target both minecraft and modded biomes, use \"category.\" or \"tag.\".",
			"To target only modded biomes, use \"modcategory.\" or \"modtag.\".",
			"To target only minecraft biomes, use \"mccategory.\" or \"mctag.\".",
			"To filter biomes for a specific mod, add \"mod.<namespace>\", for example \"mod.byg category.plains tag.overworld\".",
			"To exclude specific biome registry names, tags, categories or mods, use \"-\", for example -tag.overworld to exclude overworld biomes.",			
			"MinTemperature/MaxTemperature - Optional, when using Tags/Categories, only biomes within this temperature range are used.",
			"Example: BiomeGroup(NormalBiomes, 1, 100, category.plains tag.overworld, tag.hot tag.dry, -1.0, 1.0)",
			"Same example as before, but only includes biomes with temperature between -1.0 and 1.0.",
			"Note:", 
			"When using BiomeRegistryName to include or exclude a biome, it must have its own entry, for example: \",minecraft:forest,-minecraft:plains,\""
		);

		writer.header2("Biome Groups",
				"Biome groups are a way to group similar biomes together, ensuring they spawn adjacent to each other. Only standard biomes need to be part of these groups, while isle, border, and river biomes are configured separately.",
				"",
				"Syntax: BiomeGroup(GroupName, GroupSize, GroupRarity, BiomeName or Tags/Categories[, AnotherName[, ...]], minTemperature, maxTemperature)",
				"GroupName: A unique, descriptive name for the group.",
				"GroupSize: A value from 0 to GenerationDepth. A lower number results in a larger group. All biomes in the group must have a BiomeSize number equal to or higher than this value.",
				"GroupRarity: The relative spawn chance of the group.",
				"BiomeName: The name of a corresponding biome configuration. This is case sensitive. It can also be a registry name (e.g., \"minecraft:plains\") if there is an associated TemplateBiome().",
				"If the biome configuration is a template biome, all associated non-OTG biomes are added to the group.",
				"Tags/Categories: Instead of BiomeName, you can use Forge Biome Dictionary IDs or Minecraft Biome Categories.",
				"",
				"OTG fetches all non-OTG biomes that match the specified category/tags and adds them to the biome group. A TemplateBiome() that targets the biome must exist, or it will be ignored.",
				"",
				"Example: BiomeGroup(NormalBiomes, 1, 100, category.plains tag.overworld, tag.hot tag.dry)",
				"This adds two entries: all plains biomes in the overworld, and all hot and dry biomes. Biomes are never added twice.",
				"",
				"Use space as an AND operator. For example, \"category.plains tag.overworld\" matches biomes with both the 'plains' category and the 'overworld' tag.",
				"To target both Minecraft and modded biomes, use \"category.\" or \"tag.\".",
				"To target only modded biomes, use \"modcategory.\" or \"modtag.\".",
				"To target only Minecraft biomes, use \"mccategory.\" or \"mctag.\".",
				"To filter biomes for a specific mod, add \"mod.<namespace>\". For example, \"mod.byg category.plains tag.overworld\".",
				"To exclude specific biome registry names, tags, categories, or mods, use \"-\". For example, -tag.overworld to exclude overworld biomes.",
				"",
				"MinTemperature/MaxTemperature: Optional parameters. When using Tags/Categories, only biomes within this temperature range are used.",
				"Example: BiomeGroup(NormalBiomes, 1, 100, category.plains tag.overworld, tag.hot tag.dry, -1.0, 1.0)",
				"This is the same as the previous example, but it only includes biomes with a temperature between -1.0 and 1.0.",
				"",
				"Note:",
				"When using BiomeRegistryName to include or exclude a biome, it must have its own entry. For example: \",minecraft:forest,-minecraft:plains,\""
		);

		writer.addConfigFunctions(this.biomeGroupManager.getGroups());

		writer.putSetting(PresetStandardValues.BLACKLISTED_BIOMES, this.getBiomeSettings().getBlackListedBiomes(),
			"When using biome dictionary tags and/or biome categories with biome groups, these (non-OTG) biomes are excluded. Example: minecraft:plains."
		);
		
		writer.header2("Isle & Border Biomes");
		
		writer.putSetting(PresetStandardValues.ISLE_BIOMES, this.getBiomeSettings().getIsleBiomes(),
			"Isle biomes are biomes which spawn inside another biome (e.g. an island in an ocean). As well as listing every isle biome here, you must set IsleInBiome in each biome config too. Biome name is case sensitive."
		);

		writer.putSetting(PresetStandardValues.BORDER_BIOMES, this.getBiomeSettings().getBorderBiomes(),
			"Biomes used as borders of other biomes. As well as listing every border biome here, you must set BiomeIsBorder in each biome config too. Biome name is case sensitive."
		);

		writer.header2("Landmass Settings");

		writer.putSetting(PresetStandardValues.LAND_RARITY, this.getBiomeSettings().getLandRarity(),
			"Land rarity from 100 to 1. Higher numbers result in more land."
		);

		writer.putSetting(PresetStandardValues.LAND_SIZE, this.getBiomeSettings().getLandSize(),
			"Land size from 0 to GenerationDepth. Higher LandSize numbers will make the size of the land smaller. Landsize number should always be lower than any biome groups."
		);

		writer.putSetting(PresetStandardValues.FORCE_LAND_AT_SPAWN, this.getBiomeSettings().isForceLandAtSpawn(),
			"If enabled, land will always spawn at or near 0,0"
		);

		writer.putSetting(PresetStandardValues.OCEAN_BIOME_SIZE, this.getBiomeSettings().getOceanBiomeSize(),
			"Ocean biome size 0 to GenerationDepth. Higher OceanBiomeSize numbers will make the size of the ocean biomes smaller."
		);

		writer.putSetting(PresetStandardValues.LAND_FUZZY, this.getBiomeSettings().getLandFuzzy(),
			"Generates more lakes (via small ocean biomes) at the edges of continents. As a side effect, the continent will also get a bit larger. Must be from 0 to GenerationDepth minus LandSize."
		);

		writer.putSetting(PresetStandardValues.DEFAULT_OCEAN_BIOME, this.getBiomeSettings().getDefaultOceanBiome(),
			"Set the default Ocean biome for this world."
		);

		writer.putSetting(PresetStandardValues.DEFAULT_WARM_OCEAN_BIOME, this.getBiomeSettings().getDefaultWarmOceanBiome(),
			"Set the default Warm Ocean biome for this world."
		);

		writer.putSetting(PresetStandardValues.DEFAULT_LUKEWARM_OCEAN_BIOME, this.getBiomeSettings().getDefaultLukewarmOceanBiome(),
			"Set the default Lukewarm Ocean biome for this world."
		);

		writer.putSetting(PresetStandardValues.DEFAULT_COLD_OCEAN_BIOME, this.getBiomeSettings().getDefaultColdOceanBiome(),
			"Set the default Cold Ocean biome for this world."
		);

		writer.putSetting(PresetStandardValues.DEFAULT_FROZEN_OCEAN_BIOME, this.getBiomeSettings().getDefaultFrozenOceanBiome(),
			"The default Frozen Ocean biome for this world."
		);

		writer.header2("Ice Area Settings");

		writer.putSetting(PresetStandardValues.FROZEN_OCEAN, this.getBiomeSettings().isFrozenOcean(),
			"Can be true or false, makes the water of the oceans near a cold biome frozen. The definition of 'cold' is controlled by the next setting.",
			"Set this to false to stop the ocean from freezing near when an \"ice area\" intersects with an ocean."
		);

		writer.putSetting(PresetStandardValues.FROZEN_OCEAN_TEMPERATURE, this.getBiomeSettings().getFrozenOceanTemperature(),
			"This is the maximum biome temperature when a biome is still considered cold. Water in oceans nearby cold biomes freezes if FrozenOcean is set to true.",
			"Temperature reference from vanilla Minecraft: < 0.15 for snow, 0.15 - 0.95 for rain, or > 1.0 for dry."
		);
		
		writer.header2("Rivers");
		
		writer.putSetting(PresetStandardValues.RIVERS_ENABLED, this.getBiomeSettings().isRiversEnabled(),
			"Set this to false to prevent the river generator from doing anything."
		);

		writer.putSetting(PresetStandardValues.RANDOM_RIVERS, this.getBiomeSettings().isRandomRivers(),
			"When this setting is false, rivers follow the biome borders most of the time. Set this setting to true to disable this behavior."
		);		
		writer.putSetting(PresetStandardValues.RIVER_RARITY, this.getBiomeSettings().getRiverRarity(),
			"Controls the rarity of rivers. Must be from 0 to GenerationDepth. A higher number means more rivers. To define which rivers flow through which biomes see the individual biome configs."
		);

		writer.putSetting(PresetStandardValues.RIVER_SIZE, this.getBiomeSettings().getRiverSize(),
			"Controls the size of rivers. Can range from 0 to GenerationDepth minus RiverRarity. Making this larger will make the rivers larger, without affecting how often rivers will spawn."
		);

		writer.header1("Settings For BiomeMode:FromImage",
			"In each of the BiomeConfigs there is a BiomeColor variable, this variable is the hexadecimal color of the biome.",
			"These colors are used to define the biome layout in the input image (as well as the colour of the biome when using the /otg map command). Two biomes must not have the same color.", 
			"The settings in this section are for FromImage mode only."
		);

		writer.putSetting(PresetStandardValues.IMAGE_MODE, this.getImageSettings().getImageMode(),
			"Defines what to do when terrain is generated outside the boundaries of the image:",
			"	Repeat - repeats the image",
			"	Mirror - repeats and mirrors the image",
			"	ContinueNormal - continues with random generation, using settings for BiomeMode: Normal",
			"	FillEmpty - fills the space with one biome (defined below)"
		);

		writer.putSetting(PresetStandardValues.IMAGE_FILE, this.getImageSettings().getImageFile(),
			"The image which will provide the Biomes must be a PNG file without transparency, once placed in the same folder as PresetConfig.ini OTG will use it as a reference for the Biomes generation.",
			"Source png file name for FromImage biome mode."
		);

		writer.putSetting(PresetStandardValues.IMAGE_ORIENTATION, this.getImageSettings().getImageOrientation(),
			"How the image is oriented: North, South, East or West. When this is set to North, the top of your picture is north (no rotation).", 
			"When it is set to East, the image is rotated 90 degrees counter-clockwise, therefore what is on the east in the image becomes north in the world.", 
			"Possible values: North, East, South, West."
		);

		writer.putSetting(PresetStandardValues.IMAGE_FILL_BIOME, this.getImageSettings().getImageFillBiome(),
			"Biome name for filling outside image boundaries with FillEmpty mode."
		);

		writer.putSetting(PresetStandardValues.IMAGE_X_OFFSET, this.getImageSettings().getImageXOffset(),
			"Translates the map origin. This number needs to be multiplied by -1 when using FillEmpty."
		);
		
		writer.putSetting(PresetStandardValues.IMAGE_Z_OFFSET, this.getImageSettings().getImageZOffset(),
			"Translates the map origin. This number needs to be multiplied by -1 when using FillEmpty." 
		);
		
		writer.header1("Terrain Height and Volatility",
			"The settings in this section control terrain settings that are not specific to any biome."
		);
		
		writer.putSetting(PresetStandardValues.WORLD_HEIGHT_SCALE_BITS, this.getTerrainSettings().getWorldHeightScale(),
			"The height scale of the world. Increasing this by one doubles the terrain height of the world, substracting one halves the terrain height. Values must be between 5 and 8, inclusive."
		);

		writer.putSetting(PresetStandardValues.WORLD_HEIGHT_CAP_BITS, this.getTerrainSettings().getWorldHeightCap(),
			"The height cap of the world. A cap of 7 will make sure that there is no terrain above 128 (y=2^7). Near this cap less and less terrain generates with no terrain above this cap.", 
			"Values must be between 5 and 8 (inclusive), and may not be lower that WorldHeightScaleBits."
		);

		writer.putSetting(PresetStandardValues.FRACTURE_HORIZONTAL, this.getTerrainSettings().getFractureHorizontal(),
			"Can increase (values greater than 0) or decrease (values less than 0) how much the landscape is fractured horizontally.",
			"Values less than 0 will 'relax' the terrain, leading to more gradual and smoother height transitions."
		);

		writer.putSetting(PresetStandardValues.FRACTURE_VERTICAL, this.getTerrainSettings().getFractureVertical(),
			"Can increase (values greater than 0) or decrease (values less than 0) how much the landscape is fractured vertically.",
			"Values above 0 will lead to large cliffs/overhangs, floating islands, and/or a cavern world depending on other settings.",
			"Values less than 0 will make terrain volatility more 'spiky' but lessen the likelihood of overhangs and floating terrain."
		);
		
		writer.header1("Blocks");
		
		writer.putSetting(PresetStandardValues.REMOVE_SURFACE_STONE, this.getBlockSettings().isRemoveSurfaceStone(),
			"Set this to true to place the biome surface block on top of all exposed stone."
		);		
		
		writer.header2("Bedrock");
		
		writer.putSetting(PresetStandardValues.BEDROCK_BLOCK, this.getBlockSettings().getBedrockBlock(),
			"Block used as bedrock."
		);
		
		writer.putSetting(PresetStandardValues.DISABLE_BEDROCK, this.getBedrockSettings().isBedrockDisabled(),
			"Disable bottom of map bedrock generation. Doesn't affect bedrock on the ceiling of the map."
		);

		writer.putSetting(PresetStandardValues.CEILING_BEDROCK, this.getBedrockSettings().isCeilingBedrock(),
			"Enable ceiling of map bedrock generation."
		);

		writer.putSetting(PresetStandardValues.FLAT_BEDROCK, this.getBedrockSettings().isFlatBedrock(),
			"Make a single flat layer of bedrock."
		);
		
		writer.header2("Water / Lava / Frozen States");
		
		writer.putSetting(PresetStandardValues.WATER_LEVEL_MAX, this.getTerrainSettings().getWaterLevelMax(),
			"Set water level. Every empty block under this level will be fill water or another block from WaterBlock."
		);
		
		writer.putSetting(PresetStandardValues.WATER_LEVEL_MIN, this.getTerrainSettings().getWaterLevelMin());

		writer.putSetting(PresetStandardValues.WATER_BLOCK, this.getBlockSettings().getWaterBlock(),
			"Block used as water in WaterLevel."
		);
		
		writer.putSetting(PresetStandardValues.ICE_BLOCK, this.getBlockSettings().getIceBlock(),
			"Block used as ice."
		);

		writer.putSetting(PresetStandardValues.COOLED_LAVA_BLOCK, this.getBlockSettings().getCooledLavaBlock(),
			"Block used as cooled or frozen lava.",
			"Set this to OBSIDIAN for \"frozen\" lava lakes in cold biomes"
		);

		writer.putSetting(PresetStandardValues.BETTER_SNOW_FALL, this.getTerrainSettings().isBetterSnowFall(),
			"When set to false, 1 layer of snow falls on the highest block only.",
			"When set to true, the number of layers (1-8) is dependent on biome temperature.",
			"Higher altitudes have lower temperatures, so snow becomes deeper higher up.",
			"Also causes snow to fall through leaves, leaves can carry 3 layers while the rest falls through."
		);

		writer.header1("Resources");
		
		writer.putSetting(PresetStandardValues.DISABLE_OREGEN, this.getResourceSettings().isDisableOreGen(),
			"Disables Ore(), UnderWaterOre() and Vein() biome resources that use any type of ore block."
		);

		// Structures

		writer.header1("Structures",
			"These are global on/off toggles and spacing/separation settings for the entire world for each",
			"vanilla structure type. Spacing/separation work the same way as they do for datapacks.",
			"When set to true, structures configured in biome configs are able to spawn.",
			"Check the biome configs for customisation options per structure type per biome (size etc)."
		);
		var structureSettings = this.getStructureSettings();
		writer.putSetting(PresetStandardValues.VILLAGES_ENABLED, structureSettings.isVillagesEnabled());
		writer.putSetting(PresetStandardValues.VILLAGE_SPACING, structureSettings.getVillageSpacing());
		writer.putSetting(PresetStandardValues.VILLAGE_SEPARATION, structureSettings.getVillageSeparation());		
		writer.putSetting(PresetStandardValues.MINESHAFTS_ENABLED, structureSettings.isMineshaftsEnabled());
		writer.putSetting(PresetStandardValues.MINESHAFT_SPACING, structureSettings.getMineshaftSpacing());
		writer.putSetting(PresetStandardValues.MINESHAFT_SEPARATION, structureSettings.getMineshaftSeparation());
		writer.putSetting(PresetStandardValues.STRONGHOLDS_ENABLED, structureSettings.isStrongholdsEnabled());
		writer.putSetting(PresetStandardValues.STRONGHOLD_SPACING, structureSettings.getStrongholdSpacing());
		writer.putSetting(PresetStandardValues.STRONGHOLD_SEPARATION, structureSettings.getStrongholdSeparation());
		writer.putSetting(PresetStandardValues.STRONGHOLD_DISTANCE, structureSettings.getStrongholdDistance());
		writer.putSetting(PresetStandardValues.STRONGHOLD_SPREAD, structureSettings.getStrongholdSpread());
		writer.putSetting(PresetStandardValues.STRONGHOLD_COUNT, structureSettings.getStrongholdCount());
		writer.putSetting(PresetStandardValues.RARE_BUILDINGS_ENABLED, structureSettings.isRareBuildingsEnabled());		
		writer.putSetting(PresetStandardValues.DESERTPYRAMID_SPACING, structureSettings.getDesertPyramidSpacing());
		writer.putSetting(PresetStandardValues.DESERTPYRAMID_SEPARATION, structureSettings.getDesertPyramidSeparation());
		writer.putSetting(PresetStandardValues.IGLOO_SPACING, structureSettings.getIglooSpacing());
		writer.putSetting(PresetStandardValues.IGLOO_SEPARATION, structureSettings.getIglooSeparation());
		writer.putSetting(PresetStandardValues.JUNGLETEMPLE_SPACING, structureSettings.getJungleTempleSpacing());
		writer.putSetting(PresetStandardValues.JUNGLETEMPLE_SEPARATION, structureSettings.getJungleTempleSeparation());
		writer.putSetting(PresetStandardValues.SWAMPHUT_SPACING, structureSettings.getSwampHutSpacing());
		writer.putSetting(PresetStandardValues.SWAMPHUT_SEPARATION, structureSettings.getSwampHutSeparation());		
		writer.putSetting(PresetStandardValues.WOODLAND_MANSIONS_ENABLED, structureSettings.isWoodlandMansionsEnabled());
		writer.putSetting(PresetStandardValues.WOODLANDMANSION_SPACING, structureSettings.getWoodlandMansionSpacing());
		writer.putSetting(PresetStandardValues.WOODLANDMANSION_SEPARATION, structureSettings.getWoodlandMansionSeparation());		
		writer.putSetting(PresetStandardValues.OCEAN_MONUMENTS_ENABLED, structureSettings.isOceanMonumentsEnabled());	
		writer.putSetting(PresetStandardValues.OCEANMONUMENT_SPACING, structureSettings.getOceanMonumentSpacing());
		writer.putSetting(PresetStandardValues.OCEANMONUMENT_SEPARATION, structureSettings.getOceanMonumentSeparation());		
		writer.putSetting(PresetStandardValues.NETHER_FORTRESSES_ENABLED, structureSettings.isNetherFortressesEnabled());
		writer.putSetting(PresetStandardValues.NETHERFORTRESS_SPACING, structureSettings.getNetherFortressSpacing());
		writer.putSetting(PresetStandardValues.NETHERFORTRESS_SEPARATION, structureSettings.getNetherFortressSeparation());		
		writer.putSetting(PresetStandardValues.BURIED_TREASURE_ENABLED, structureSettings.isBuriedTreasureEnabled());
		writer.putSetting(PresetStandardValues.BURIEDTREASURE_SPACING, structureSettings.getBuriedTreasureSpacing());
		writer.putSetting(PresetStandardValues.BURIEDTREASURE_SEPARATION, structureSettings.getBuriedTreasureSeparation());		
		writer.putSetting(PresetStandardValues.OCEAN_RUINS_ENABLED, structureSettings.isOceanRuinsEnabled());
		writer.putSetting(PresetStandardValues.OCEANRUIN_SPACING, structureSettings.getOceanRuinSpacing());
		writer.putSetting(PresetStandardValues.OCEANRUIN_SEPARATION, structureSettings.getOceanRuinSeparation());		
		writer.putSetting(PresetStandardValues.PILLAGER_OUTPOSTS_ENABLED, structureSettings.isPillagerOutpostsEnabled());
		writer.putSetting(PresetStandardValues.PILLAGEROUTPOST_SPACING, structureSettings.getPillagerOutpostSpacing());
		writer.putSetting(PresetStandardValues.PILLAGEROUTPOST_SEPARATION, structureSettings.getPillagerOutpostSeparation());		
		writer.putSetting(PresetStandardValues.BASTION_REMNANTS_ENABLED, structureSettings.isBastionRemnantsEnabled());
		writer.putSetting(PresetStandardValues.BASTIONREMNANT_SPACING, structureSettings.getBastionRemnantSpacing());
		writer.putSetting(PresetStandardValues.BASTIONREMNANT_SEPARATION, structureSettings.getBastionRemnantSeparation());		
		writer.putSetting(PresetStandardValues.NETHER_FOSSILS_ENABLED, structureSettings.isNetherFossilsEnabled());
		writer.putSetting(PresetStandardValues.NETHERFOSSIL_SPACING, structureSettings.getNetherFossilSpacing());
		writer.putSetting(PresetStandardValues.NETHERFOSSIL_SEPARATION, structureSettings.getNetherFossilSeparation());				
		writer.putSetting(PresetStandardValues.END_CITIES_ENABLED, structureSettings.isEndCitiesEnabled());
		writer.putSetting(PresetStandardValues.ENDCITY_SPACING, structureSettings.getEndCitySpacing());
		writer.putSetting(PresetStandardValues.ENDCITY_SEPARATION, structureSettings.getEndCitySeparation());		
		writer.putSetting(PresetStandardValues.RUINED_PORTALS_ENABLED, structureSettings.isRuinedPortalsEnabled());
		writer.putSetting(PresetStandardValues.RUINEDPORTAL_SPACING, structureSettings.getRuinedPortalSpacing());
		writer.putSetting(PresetStandardValues.RUINEDPORTAL_SEPARATION, structureSettings.getRuinedPortalSeparation());		
		writer.putSetting(PresetStandardValues.SHIPWRECKS_ENABLED, structureSettings.isShipwrecksEnabled());
		writer.putSetting(PresetStandardValues.SHIPWRECK_SPACING, structureSettings.getShipwreckSpacing());
		writer.putSetting(PresetStandardValues.SHIPWRECK_SEPARATION, structureSettings.getShipwreckSeparation());		
		
		writer.header2("OTG Custom structures and objects (BO2/BO3/BO4)");
		
		writer.putSetting(PresetStandardValues.CUSTOM_STRUCTURE_TYPE, this.getCustomStructureSettings().getCustomStructureType(),
			"Sets the type of structures the world should spawn, BO3 or BO4.",
			"Allowed values: BO3/BO4.",
			"BO4's allow for collision detection, fine control over structure distribution, advanced branching mechanics for",
			"procedurally generated structures, smoothing areas, extremely large structures, settings for blending structures",
			"with surrounding terrain, etc. BO3's are simpler, seed based CustomStructures, more like vanilla mc structures.",
			"Worlds currently can only use one type of structure."
		);			
		
		writer.putSetting(PresetStandardValues.BO3_AT_SPAWN, this.getCustomStructureSettings().getBO3AtSpawn(),
			"This BO3 will be spawned at the world's spawn point as a CustomObject (Max size 32x32)."
		);

		writer.header2("BO3 Custom structures");
		
		writer.putSetting(PresetStandardValues.USE_OLD_BO3_STRUCTURE_RARITY, this.getCustomStructureSettings().isUseOldBO3StructureRarity(),
			"For 1.12.2 v9.0_r11 and earlier, BO3 customstructures used 2 rarity rolls,",
			"one for the rarity in the CustomStructure() tag, one for the rarity in the BO3 itself.",
			"For 1.16, we use only the rarity roll from the CustomStructure() tag. Set this to true",
			"to use the old system."
		);
		
		writer.putSetting(PresetStandardValues.MAXIMUM_CUSTOM_STRUCTURE_RADIUS, this.getCustomStructureSettings().getMaximumCustomStructureRadius(),
			"Maximum radius of custom structures in chunks. Custom structures are spawned by",
			"the CustomStructure resource in the biome configuration files. Not used for BO4's."
		);
		
		writer.putSetting(PresetStandardValues.DECORATION_BOUNDS_CHECK, this.getCustomStructureSettings().isDecorationBoundsCheck(),
			"Set this to false to disable the bounds check during chunk decoration.",
			"While this allows you to spawn objects larger than 32x32, it also makes terrain generation dependent on the direction you explored the world in."
		);

		writer.header1("Carvers: Caves and Ravines");

		writer.putSetting(PresetStandardValues.CARVER_LAVA_BLOCK, this.getBlockSettings().getCarverLavaBlock(),
			"Block that replaces all air blocks from Y0 up to CarverLavaBlockHeight.",
			"For example, vanilla replaces air in caves with lava up to Y10.",
			"Defaults to: LAVA"
		);

		writer.putSetting(PresetStandardValues.CARVER_LAVA_BLOCK_HEIGHT, this.getTerrainSettings().getCarverLavaBlockHeight(),
			"All air blocks are replaced to CarverLavaBlock from Y0 up to CarverLavaBlockHeight.",
			"For example, vanilla replaces air in caves with lava up to Y10.",
			"Defaults to: 10"
		);

		writer.header2("Caves");		
		
		writer.putSetting(PresetStandardValues.CAVES_ENABLED, this.getCarverSettings().isCavesEnabled(),
			"Enables/disables OTG caves. OTG should automatically disable caves/carvers for biomes when modded carvers are detected."
		);
		
		writer.putSetting(PresetStandardValues.CAVE_RARITY, this.getCarverSettings().getCaveRarity(),
			"This controls the odds that a given chunk will host a single cave and/or the start of a cave system."
		);

		writer.putSetting(PresetStandardValues.CAVE_FREQUENCY, this.getCarverSettings().getCaveFrequency(),
			"The number of times the cave generation algorithm will attempt to create single caves and cave",
			"systems in the given chunk. This value is larger because the likelihood for the cave generation",
			"algorithm to bailout is fairly high and it is used in a randomizer that trends towards lower",
			"random numbers. With an input of 40 (default) the randomizer will result in an average random",
			"result of 5 to 6. This can be turned off by setting evenCaveDistribution (below) to true."
		);

		writer.putSetting(PresetStandardValues.CAVE_MIN_ALTITUDE, this.getCarverSettings().getCaveMinAltitude(),
			"Sets the minimum and maximum altitudes at which caves will be generated. These values are",
			"used in a randomizer that trends towards lower numbers so that caves become more frequent",
			"the closer you get to the bottom of the map. Setting even cave distribution (above) to true",
			"will turn off this randomizer and use a flat random number generator that will create an even",
			"density of caves at all altitudes."
		);
		writer.putSetting(PresetStandardValues.CAVE_MAX_ALTITUDE, this.getCarverSettings().getCaveMaxAltitude());

		writer.putSetting(PresetStandardValues.INDIVIDUAL_CAVE_RARITY, this.getCarverSettings().getIndividualCaveRarity(),
			"The odds that the cave generation algorithm will generate a single cavern without an accompanying",
			"cave system. Note that whenever the algorithm generates an individual cave it will also attempt to",
			"generate a pocket of cave systems in the vicinity (no guarantee of connection or that the cave system",
			"will actually be created)."
		);

		writer.putSetting(PresetStandardValues.CAVE_SYSTEM_FREQUENCY, this.getCarverSettings().getCaveSystemFrequency(),
			"The number of times the algorithm will attempt to start a cave system in a given chunk per cycle of",
			"the cave generation algorithm (see cave frequency setting above). Note that setting this value too",
			"high with an accompanying high cave frequency value can cause extremely long world generation time."
		);

		writer.putSetting(PresetStandardValues.CAVE_SYSTEM_POCKET_CHANCE, this.getCarverSettings().getCaveSystemPocketChance(),
			"This can be set to create an additional chance that a cave system pocket (a higher than normal",
			"density of cave systems) being started in a given chunk. Normally, a cave pocket will only be",
			"attempted if an individual cave is generated, but this will allow more cave pockets to be generated",
			"in addition to the individual cave trigger."
		);

		writer.putSetting(PresetStandardValues.CAVE_SYSTEM_POCKET_MIN_SIZE, this.getCarverSettings().getCaveSystemPocketMinSize(),
			"The minimum and maximum size that a cave system pocket can be. This modifies/overrides the",
			"cave system frequency setting (above) when triggered."
		);
		writer.putSetting(PresetStandardValues.CAVE_SYSTEM_POCKET_MAX_SIZE, this.getCarverSettings().getCaveSystemPocketMaxSize());

		writer.putSetting(PresetStandardValues.EVEN_CAVE_DISTRIBUTION, this.getCarverSettings().isEvenCaveDistribution(),
			"Setting this to true will turn off the randomizer for cave frequency (above). Do note that",
			"if you turn this on you will probably want to adjust the cave frequency down to avoid long",
			"load times at world creation."
		);

		writer.header2("Ravines");

		writer.putSetting(PresetStandardValues.RAVINES_ENABLED, this.getCarverSettings().isRavinesEnabled(),
			"Enables/disables OTG ravines. OTG should automatically disable ravines/carvers for biomes when modded carvers are detected."
		);

		writer.putSetting(PresetStandardValues.RAVINE_RARITY, this.getCarverSettings().getRavineRarity());
		writer.putSetting(PresetStandardValues.RAVINE_MIN_ALTITUDE, this.getCarverSettings().getRavineMinAltitude());
		writer.putSetting(PresetStandardValues.RAVINE_MAX_ALTITUDE, this.getCarverSettings().getRavineMaxAltitude());
		writer.putSetting(PresetStandardValues.RAVINE_MIN_LENGTH, this.getCarverSettings().getRavineMinLength());
		writer.putSetting(PresetStandardValues.RAVINE_MAX_LENGTH, this.getCarverSettings().getRavineMaxLength());
		writer.putSetting(PresetStandardValues.RAVINE_DEPTH, this.getCarverSettings().getRavineDepth());

		writer.header1("Spawn point settings");

		writer.putSetting(PresetStandardValues.FIXED_SPAWN_POINT, this.getSpawnSettings().isSpawnPointSet(),
			"Set this to true to enable SpawnPointX/SpawnPointY/SpawnPointZ/SpawnPointAngle."
		);
		writer.putSetting(PresetStandardValues.SPAWN_POINT_X, this.getSpawnSettings().getSpawnPointX(),
			"When FixedSpawnPoint: true, this sets the world's spawn point."
		);
		writer.putSetting(PresetStandardValues.SPAWN_POINT_Y, this.getSpawnSettings().getSpawnPointY(),
			"When FixedSpawnPoint: true, this sets the world's spawn point."		
		);
		writer.putSetting(PresetStandardValues.SPAWN_POINT_Z, this.getSpawnSettings().getSpawnPointZ(),
			"When FixedSpawnPoint: true, this sets the world's spawn point."				
		);
		writer.putSetting(PresetStandardValues.SPAWN_POINT_ANGLE, this.getSpawnSettings().getSpawnPointAngle(),
			"When FixedSpawnPoint: true, this sets the angle the player is looking when spawned at the spawn point."
		);
		
		writer.header2("Portal settings (Forge)");

		writer.putSetting(PresetStandardValues.PORTAL_BLOCKS, this.getPortalSettings().getPortalBlocks(),
			"A list of one or more portal blocks used to build a portal to this dimension, or back to the overworld.",
			"Only applies for dimensions, not overworld/nether/end."
		);
		writer.putSetting(PresetStandardValues.PORTAL_COLOR, this.getPortalSettings().getPortalColor(),
			"The portal color used for this world's portals, only applies for dimensions, not overworld/nether/end.",
			"Options: beige, black, blue, crystalblue, darkblue, darkgreen, darkred, emerald, flame, gold,",
			"green, grey, lightblue, lightgreen, orange, pink, red, white, yellow, default."
		);
		writer.putSetting(PresetStandardValues.PORTAL_MOB, this.getPortalSettings().getPortalMob(),
			"The mob that spawns from this portal, minecraft:zombified_piglin by default.",
			"Only applies for dimensions, not overworld/nether/end."
		);
		writer.putSetting(PresetStandardValues.PORTAL_IGNITION_SOURCE, this.getPortalSettings().getPortalIgnitionSource(),
			"The ignition source for this portal, minecraft:flint_and_steel by default.",
			"Only applies for dimensions, not overworld/nether/end."
		);
		
		writer.header1("Dimension settings (Forge)",
			"Note: At world creation, these settings are written to the world save's datapack folder (\\saves\\WorldName\\datapacks\\otg\\)",
			"as dimension_type json file. The json file is used by MC on world load to fetch the settings. If you want to change dimension",
			"settings for already created worlds make sure to edit the dimension_type json file, since changes to the PresetConfig dimension", 
			"settings won't be picked up on world load, only on world creation."
		);

		writer.putSetting(PresetStandardValues.FIXED_TIME, this.getDimensionSettings().getFixedTime().orElse(PresetStandardValues.FIXED_TIME.getDefaultValue()),
			"The time this dimension is fixed at, from 0 to 24000.",
			"-1 by default, meaning disabled, so time passes normally.",
			"Vanilla Nether uses 18000, End uses 6000."
		);
		writer.putSetting(PresetStandardValues.HAS_SKYLIGHT, this.getDimensionSettings().isHasSkyLight(), 
			"Whether this dimension uses a skylight, defaults to true.",
			"Vanilla nether and end use false, nether combines this with AmbientLight:0.1."
		);
		writer.putSetting(PresetStandardValues.HAS_CEILING, this.getDimensionSettings().isHasCeiling(),
			"Whether this dimension has a ceiling, affects mob spawning, weather (thunder), maps.",
			"Defaults to false, vanilla nether uses true."				
		);
		writer.putSetting(PresetStandardValues.ULTRA_WARM, this.getDimensionSettings().isUltraWarm(),
			"Whether water evaporates in this dimension. Also appears to affect lava/lava flow.",
			"Defaults to false. Vanilla nether uses true."
		);
		writer.putSetting(PresetStandardValues.NATURAL, this.getDimensionSettings().isNatural(),
			"When set to false, mobs do not spawn from portals and players cannot use beds in this dimension.",
			"Defaults to true."
		);
		writer.putSetting(PresetStandardValues.COORDINATE_SCALE, this.getDimensionSettings().getCoordinateScale(),
			"The amount of blocks traveled compared to other dimensions.",
			"1 by default, same as vanilla overworld, nether uses 8."
		);
		writer.putSetting(PresetStandardValues.CREATE_DRAGON_FLIGHT, this.getDimensionSettings().isCreateDragonFight(),
			"Probably starts a dragon fight, we think. Try it, what could possibly go wrong?"
		);
		writer.putSetting(PresetStandardValues.PIGLIN_SAFE, this.getDimensionSettings().isPiglinSafe(),
			"Whether this dimension can spawn piglins, false by default."
		);
		writer.putSetting(PresetStandardValues.BED_WORKS, this.getDimensionSettings().isBedWorks(), 
			"Whether beds can be used to sleep and skip time in this dimension, true by default.");
		writer.putSetting(PresetStandardValues.RESPAWN_ANCHOR_WORKS, this.getDimensionSettings().isRespawnAnchorWorks(),
			"Whether RespawnAnchorBlocks can be used, false by default."
		);
		writer.putSetting(PresetStandardValues.HAS_RAIDS, this.getDimensionSettings().isHasRaids(), 
			"Whether the dimension has raids, true by default."
		);
		writer.putSetting(PresetStandardValues.LOGICAL_HEIGHT, this.getDimensionSettings().getLogicalHeight(), 
			"World height, 256 by default. Affects portals and chorus fruits."
		);
		writer.putSetting(PresetStandardValues.INFINIBURN, this.getDimensionSettings().getInfiniburn(), 
			"Infiniburn block tag registry key, minecraft:infiniburn_overworld by default.",
			"Can be either overworld/nether/end (or potentially modded)."
		);
		writer.putSetting(PresetStandardValues.EFFECTS_LOCATION, this.getDimensionSettings().getEffectsLocation(), 
			"Effects registry key, minecraft:overworld by default.",
			"Can be either overworld/nether/end (or potentially modded)."
		);
		writer.putSetting(PresetStandardValues.AMBIENT_LIGHT, (double)this.getDimensionSettings().getAmbientLight(),
			"The base ambient light level for the world, 0.0 for overworld/end, 0.1 for nether."
		);
		
		writer.header1("Game rules (Forge)",
			"See: https://minecraft.fandom.com/wiki/Game_rule",
			"Since game rules are shared across all dimensions, these settings only apply if this preset is used as the overworld.",
			"These settings can be overridden via a DimensionConfig with a GameRules entry."
		);

		writer.putSetting(PresetStandardValues.OVERRIDE_GAME_RULES, this.getGameRuleSettings().isOverrideGameRules(),
			"Set this to true to enable the settings below."
		);
		var gameRuleSettings = this.getGameRuleSettings();
		writer.putSetting(PresetStandardValues.DO_FIRE_TICK, gameRuleSettings.isDoFireTick()); 
		writer.putSetting(PresetStandardValues.MOB_GRIEFING, gameRuleSettings.isMobGriefing());
		writer.putSetting(PresetStandardValues.KEEP_INVENTORY, gameRuleSettings.isKeepInventory());
		writer.putSetting(PresetStandardValues.DO_MOB_SPAWNING, gameRuleSettings.isDoMobSpawning());
		writer.putSetting(PresetStandardValues.DO_MOB_LOOT, gameRuleSettings.isDoMobLoot());
		writer.putSetting(PresetStandardValues.DO_TILE_DROPS, gameRuleSettings.isDoTileDrops());
		writer.putSetting(PresetStandardValues.DO_ENTITY_DROPS, gameRuleSettings.isDoEntityDrops());
		writer.putSetting(PresetStandardValues.COMMAND_BLOCK_OUTPUT, gameRuleSettings.isCommandBlockOutput());
		writer.putSetting(PresetStandardValues.NATURAL_REGENERATION, gameRuleSettings.isNaturalRegeneration());
		writer.putSetting(PresetStandardValues.DO_DAY_LIGHT_CYCLE, gameRuleSettings.isNaturalRegeneration());
		writer.putSetting(PresetStandardValues.LOG_ADMIN_COMMANDS, gameRuleSettings.isLogAdminCommands());
		writer.putSetting(PresetStandardValues.SHOW_DEATH_MESSAGES, gameRuleSettings.isShowDeathMessages());
		writer.putSetting(PresetStandardValues.RANDOM_TICK_SPEED, gameRuleSettings.getRandomTickSpeed());
		writer.putSetting(PresetStandardValues.SEND_COMMAND_FEEDBACK, gameRuleSettings.isSendCommandFeedback());
		writer.putSetting(PresetStandardValues.SPECTATORS_GENERATE_CHUNKS, gameRuleSettings.isSpectatorsGenerateChunks());
		writer.putSetting(PresetStandardValues.SPAWN_RADIUS, gameRuleSettings.getSpawnRadius());
		writer.putSetting(PresetStandardValues.DISABLE_ELYTRA_MOVEMENT_CHECK, gameRuleSettings.isDisableElytraMovementCheck()); 
		writer.putSetting(PresetStandardValues.MAX_ENTITY_CRAMMING, gameRuleSettings.getMaxEntityCramming());
		writer.putSetting(PresetStandardValues.DO_WEATHER_CYCLE, gameRuleSettings.isDoWeatherCycle());
		writer.putSetting(PresetStandardValues.DO_LIMITED_CRAFTING, gameRuleSettings.isDoLimitedCrafting()); 
		writer.putSetting(PresetStandardValues.MAX_COMMAND_CHAIN_LENGTH, gameRuleSettings.getMaxCommandChainLength());
		writer.putSetting(PresetStandardValues.ANNOUNCE_ADVANCEMENTS, gameRuleSettings.isAnnounceAdvancements()); 
		writer.putSetting(PresetStandardValues.DISABLE_RAIDS, gameRuleSettings.isDisableRaids()); 
		writer.putSetting(PresetStandardValues.DO_INSOMNIA, gameRuleSettings.isDoInsomnia());
		writer.putSetting(PresetStandardValues.DROWNING_DAMAGE, gameRuleSettings.isDrowningDamage()); 
		writer.putSetting(PresetStandardValues.FALL_DAMAGE, gameRuleSettings.isFallDamage());
		writer.putSetting(PresetStandardValues.FIRE_DAMAGE, gameRuleSettings.isFireDamage());
		writer.putSetting(PresetStandardValues.DO_PATROL_SPAWNING, gameRuleSettings.isDoPatrolSpawning());
		writer.putSetting(PresetStandardValues.DO_TRADER_SPAWNING, gameRuleSettings.isDoTraderSpawning()); 
		writer.putSetting(PresetStandardValues.FORGIVE_DEAD_PLAYERS, gameRuleSettings.isForgiveDeadPlayers()); 
		writer.putSetting(PresetStandardValues.UNIVERSAL_ANGER, gameRuleSettings.isUniversalAnger());
	}
}
