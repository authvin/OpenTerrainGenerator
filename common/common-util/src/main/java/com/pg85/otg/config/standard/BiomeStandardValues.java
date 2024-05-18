package com.pg85.otg.config.standard;

import com.pg85.otg.config.settingType.DoubleArraySetting;
import com.pg85.otg.config.settingType.MaterialSetting;
import com.pg85.otg.config.settingType.Setting;
import com.pg85.otg.config.settingType.Settings;
import com.pg85.otg.constants.settings.GrassColorModifier;
import com.pg85.otg.constants.settings.structure.MineshaftType;
import com.pg85.otg.constants.settings.structure.OceanRuinsType;
import com.pg85.otg.constants.settings.structure.RareBuildingType;
import com.pg85.otg.constants.settings.structure.RuinedPortalType;
import com.pg85.otg.constants.settings.TemplateBiomeType;
import com.pg85.otg.constants.settings.structure.VillageType;
import com.pg85.otg.util.biome.ColorSet;
import com.pg85.otg.util.biome.ReplaceBlockMatrix;
import com.pg85.otg.util.biome.WeightedMobSpawnGroup;
import com.pg85.otg.util.materials.LocalMaterialData;
import com.pg85.otg.util.materials.LocalMaterials;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class BiomeStandardValues extends Settings
{
	// >> Biome Extensions & Related
	public static final Collection<String> BiomeConfigExtensions = Arrays.asList(
		"BiomeConfig.ini", 
		".biome", 
		".bc", 
		".bc.ini",
		".biome.ini"
	);

	public static final Setting<Boolean>
		USE_WORLD_WATER_LEVEL = Settings.booleanSetting("UseWorldWaterLevel", true),
		DISABLE_BIOME_HEIGHT = Settings.booleanSetting("DisableBiomeHeight", false),
		STRONGHOLDS_ENABLED = Settings.booleanSetting("StrongholdsEnabled", true),
		NETHER_FORTRESSES_ENABLED = Settings.booleanSetting("NetherFortressesEnabled", false),
		OCEAN_MONUMENTS_ENABLED = Settings.booleanSetting("OceanMonumentsEnabled", false),
		WOODLAND_MANSIONS_ENABLED = Settings.booleanSetting("WoodlandMansionsEnabled", false),
		BURIED_TREASURE_ENABLED = Settings.booleanSetting("BuriedTreasureEnabled", false),
		SHIP_WRECK_ENABLED = Settings.booleanSetting("ShipWreckEnabled", false),
		SHIP_WRECK_BEACHED_ENABLED = Settings.booleanSetting("ShipWreckBeachedEnabled", false),
		PILLAGER_OUTPOST_ENABLED = Settings.booleanSetting("PillagerOutpostEnabled", false),
		BASTION_REMNANT_ENABLED = Settings.booleanSetting("BastionRemnantEnabled", false),
		NETHER_FOSSIL_ENABLED = Settings.booleanSetting("NetherFossilEnabled", false),
		END_CITY_ENABLED = Settings.booleanSetting("EndCityEnabled", false),
		REPLACE_CURRENT_MUSIC = Settings.booleanSetting("ReplaceCurrentMusic", false),
		USE_FROZEN_OCEAN_TEMPERATURE = Settings.booleanSetting("UseFrozenOceanTemperature", false),
		IS_TEMPLATE_FOR_BIOME = Settings.booleanSetting("TemplateForBiome", false)
	;

	public static final Setting<String>
		RIVER_BIOME = Settings.stringSetting("RiverBiome", "River"),
		INHERIT_MOBS_BIOME_NAME = Settings.stringSetting("InheritMobsBiomeName", ""),
		PARTICLE_TYPE = Settings.stringSetting("ParticleType", ""),
		MUSIC = Settings.stringSetting("Music", ""),
		AMBIENT_SOUND = Settings.stringSetting("AmbientSound", ""),
		MOOD_SOUND = Settings.stringSetting("MoodSound", "minecraft:ambient.cave"),
		ADDITIONS_SOUND = Settings.stringSetting("AdditionsSound", ""),
		BIOME_CATEGORY = Settings.stringSetting("BiomeCategory", "plains"),
		LEGACY_GRASS_COLOR2 = Settings.stringSetting("GrassColor2", "#FFFFFF"),
		LEGACY_FOLIAGE_COLOR2 = Settings.stringSetting("FoliageColor2", "#FFFFFF")
	;

	public static final Setting<Integer>
		BIOME_SIZE = Settings.intSetting("BiomeSize", 4, 0, 20),
		BIOME_SIZE_WHEN_ISLE = Settings.intSetting("BiomeSizeWhenIsle", 6, 0, 20),
		BIOME_SIZE_WHEN_BORDER = Settings.intSetting("BiomeSizeWhenBorder", 8, 0, 20),
		BIOME_RARITY = Settings.intSetting("BiomeRarity", 100, 0, Integer.MAX_VALUE),
		BIOME_RARITY_WHEN_ISLE = Settings.intSetting("BiomeRarityWhenIsle", 97, 0, Integer.MAX_VALUE),
		SMOOTH_RADIUS = Settings.intSetting("SmoothRadius", 2, 0, 32),
		CUSTOM_HEIGHT_CONTROL_SMOOTH_RADIUS = Settings.intSetting("CustomHeightControlSmoothRadius", 2, 0, 32),
		WATER_LEVEL_MAX = PresetStandardValues.WATER_LEVEL_MAX,
		WATER_LEVEL_MIN = PresetStandardValues.WATER_LEVEL_MIN,
		VILLAGE_SIZE = Settings.intSetting("VillageSize", 6, 0, Integer.MAX_VALUE),
		PILLAGER_OUTPOST_SIZE = Settings.intSetting("PillagerOutpostSize", 7, 0, Integer.MAX_VALUE),
		BASTION_REMNANT_SIZE = Settings.intSetting("BastionRemnantSize", 6, 0, Integer.MAX_VALUE),
		MUSIC_MIN_DELAY = Settings.intSetting("MusicMinDelay", 0, 0, Integer.MAX_VALUE),
		MUSIC_MAX_DELAY = Settings.intSetting("MusicMaxDelay", 0, 0, Integer.MAX_VALUE),
		MOOD_SOUND_DELAY = Settings.intSetting("MoodSoundDelay", 6000, 0, Integer.MAX_VALUE),
		MOOD_SEARCH_RANGE = Settings.intSetting("MoodSearchRange", 8, 0, Integer.MAX_VALUE)
	;

	public static final Setting<Integer>
		BIOME_COLOR = Settings.colorSetting("BiomeColor", "#FFFFFF"),
		SKY_COLOR = Settings.colorSetting("SkyColor", "#7BA5FF"),
		WATER_COLOR = Settings.colorSetting("WaterColor", "#FFFFFF"),
		GRASS_COLOR = Settings.colorSetting("GrassColor", "#FFFFFF"),
		FOLIAGE_COLOR = Settings.colorSetting("FoliageColor", "#FFFFFF"),
		FOG_COLOR = Settings.colorSetting("FogColor", "#000000"),
		WATER_FOG_COLOR = Settings.colorSetting("WaterFogColor", "#000000")
	;

	public static final Setting<List<String>>
		BIOME_DICT_TAGS = Settings.stringListSetting("BiomeDictTags", ""),
		ISLE_IN_BIOMES = Settings.stringListSetting("IsleInBiomes", "Ocean"),
		BORDER_IN_BIOMES = Settings.stringListSetting("BorderInBiomes"),
		ONLY_BORDER_NEAR = Settings.stringListSetting("OnlyBorderNear"),
		NOT_BORDER_NEAR = Settings.stringListSetting("NotBorderNear")
	;

	public static final Setting<Double>
		VOLATILITY_1 = Settings.doubleSetting("Volatility1", 0, -10000, 1000),
		VOLATILITY_2 = Settings.doubleSetting("Volatility2", 0, -10000, 1000),
		VOLATILITY_WEIGHT_1 = Settings.doubleSetting("VolatilityWeight1", 0.5, -1000, 1000),
		VOLATILITY_WEIGHT_2 = Settings.doubleSetting("VolatilityWeight2", 0.45, -1000, 1000),
		MAX_AVERAGE_HEIGHT = Settings.doubleSetting("MaxAverageHeight", 0, -1000, 1000),
		MAX_AVERAGE_DEPTH = Settings.doubleSetting("MaxAverageDepth", 0, -1000, 1000),
		MOOD_OFFSET = Settings.doubleSetting("MoodOffset", 2.0, 0, Double.MAX_VALUE),
		ADDITIONS_TICK_CHANCE = Settings.doubleSetting("AdditionsTickChance", 0, 0, Double.MAX_VALUE)
	;

	public static final Setting<LocalMaterialData>
		STONE_BLOCK = new MaterialSetting("StoneBlock", LocalMaterials.STONE_NAME),
		SURFACE_BLOCK = new MaterialSetting("SurfaceBlock", LocalMaterials.GRASS_NAME),
		UNDER_WATER_SURFACE_BLOCK = new MaterialSetting("UnderWaterSurfaceBlock", ""),				
		GROUND_BLOCK = new MaterialSetting("GroundBlock", LocalMaterials.DIRT_NAME),
		SANDSTONE_BLOCK = new MaterialSetting("SandstoneBlock", LocalMaterials.SANDSTONE_NAME),
		RED_SANDSTONE_BLOCK = new MaterialSetting("RedSandstoneBlock", LocalMaterials.RED_SANDSTONE_NAME),
		COOLED_LAVA_BLOCK = PresetStandardValues.COOLED_LAVA_BLOCK,
		WATER_BLOCK = PresetStandardValues.WATER_BLOCK,
		ICE_BLOCK = PresetStandardValues.ICE_BLOCK,
		PACKED_ICE_BLOCK = new MaterialSetting("PackedIceBlock", LocalMaterials.PACKED_ICE_NAME),
		SNOW_BLOCK = new MaterialSetting("SnowBlock", LocalMaterials.SNOW_BLOCK_NAME)
	;

	public static final Setting<double[]>
		CUSTOM_HEIGHT_CONTROL = new DoubleArraySetting("CustomHeightControl")
	;

	public static final Setting<Float>
		BIOME_TEMPERATURE = Settings.floatSetting("BiomeTemperature", 0.5f, 0, 2),
		BIOME_WETNESS = Settings.floatSetting("BiomeWetness", 0.5f, 0, 1),
		BIOME_HEIGHT = Settings.floatSetting("BiomeHeight", 0.1f, -10, 10),
		BIOME_VOLATILITY = Settings.floatSetting("BiomeVolatility", 0.3f, -1000, 1000),
		// TODO: Find the proper max values for these probabilities, likely 1 for most.
		MINESHAFT_PROBABILITY = Settings.floatSetting("MineshaftProbability", 0.004f, 0f, 1f),
		OCEAN_RUINS_LARGE_PROBABILITY = Settings.floatSetting("OceanRuinsLargeProbability", 0.3f, 0f, 1f),
		OCEAN_RUINS_CLUSTER_PROBABILITY = Settings.floatSetting("OceanRuinsClusterProbability", 0.9f, 0f, 1f),
		BURIED_TREASURE_PROBABILITY = Settings.floatSetting("BuriedTreasureProbability", 0.01f, 0f, 1f),
		PARTICLE_PROBABILITY = Settings.floatSetting("ParticleProbability", 0f, 0, 1f),
		FOG_DENSITY = Settings.floatSetting("FogDensity", 0.0f, 0f, 1f)
	;

	public static final Setting<List<WeightedMobSpawnGroup>>
		SPAWN_MONSTERS = Settings.mobGroupListSetting("SpawnMonsters"),
		SPAWN_CREATURES = Settings.mobGroupListSetting("SpawnCreatures"),
		SPAWN_WATER_CREATURES = Settings.mobGroupListSetting("SpawnWaterCreatures"),
		SPAWN_AMBIENT_CREATURES = Settings.mobGroupListSetting("SpawnAmbientCreatures"),
		SPAWN_WATER_AMBIENT_CREATURES = Settings.mobGroupListSetting("SpawnWaterAmbientCreatures"),
		SPAWN_MISC_CREATURES = Settings.mobGroupListSetting("SpawnMiscCreatures")
	;
	
	public static final Setting<ColorSet>
		GRASS_COLOR_CONTROL = Settings.colorSetSetting("GrassColorControl"),
		FOLIAGE_COLOR_CONTROL = Settings.colorSetSetting("FoliageColorControl"),
		WATER_COLOR_CONTROL = Settings.colorSetSetting("WaterColorControl")
	;

	public static final Setting<VillageType> VILLAGE_TYPE = Settings.enumSetting("VillageType", VillageType.disabled);
	public static final Setting<MineshaftType> MINESHAFT_TYPE = Settings.enumSetting("MineshaftType", MineshaftType.normal);
	public static final Setting<RareBuildingType> RARE_BUILDING_TYPE = Settings.enumSetting("RareBuildingType", RareBuildingType.disabled);
	public static final Setting<RuinedPortalType> RUINED_PORTAL_TYPE = Settings.enumSetting("RuinedPortalType", RuinedPortalType.disabled);
	public static final Setting<OceanRuinsType> OCEAN_RUINS_TYPE = Settings.enumSetting("OceanRuinsType", OceanRuinsType.disabled);
	public static final Setting<GrassColorModifier> GRASS_COLOR_MODIFIER = Settings.enumSetting("GrassColorModifier", GrassColorModifier.None);
	public static final Setting<TemplateBiomeType> TEMPLATE_BIOME_TYPE = Settings.enumSetting("TemplateBiomeType", TemplateBiomeType.Overworld);
	public static final Setting<ReplaceBlockMatrix> REPLACED_BLOCKS = Settings.replacedBlocksSetting("ReplacedBlocks");
	public static final Object[] SURFACE_AND_GROUND_CONTROL = new Object[0];
}
