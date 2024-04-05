package com.pg85.otg.config.standard;

import com.pg85.otg.config.settingType.MaterialListSetting;
import com.pg85.otg.config.settingType.MaterialSetting;
import com.pg85.otg.config.settingType.Setting;
import com.pg85.otg.config.settingType.Settings;
import com.pg85.otg.constants.Constants;
import com.pg85.otg.constants.settings.BiomeMode;
import com.pg85.otg.constants.settings.ConfigMode;
import com.pg85.otg.constants.settings.structure.CustomStructureType;
import com.pg85.otg.constants.settings.ImageMode;
import com.pg85.otg.constants.settings.ImageOrientation;
import com.pg85.otg.util.materials.LocalMaterialData;
import com.pg85.otg.util.materials.LocalMaterials;

import java.util.ArrayList;
import java.util.List;

public class PresetStandardValues extends Settings
{
	public static class BiomeGroupNames
	{
		public static final String NORMAL = "NormalBiomes";
		public static final String ICE = "IceBiomes";
		public static final String COLD = "ColdBiomes";
		public static final String HOT = "HotBiomes";
		public static final String MESA = "MesaBiomes";
		public static final String JUNGLE = "JungleBiomes";
		public static final String MEGA_TAIGA = "Mega TaigaBiomes";
	}

	public static final Setting<ConfigMode> SETTINGS_MODE = Settings.enumSetting("SettingsMode", ConfigMode.WriteAll);
	public static final Setting<ConfigMode> SETTINGS_MODE_BO3 = Settings.enumSetting("SettingsMode", ConfigMode.WriteDisable);
	public static final Setting<BiomeMode> BIOME_MODE = Settings.enumSetting("BiomeMode", BiomeMode.Normal);
	public static final Setting<ImageMode> IMAGE_MODE = Settings.enumSetting("ImageMode", ImageMode.Mirror);
	public static final Setting<ImageOrientation> IMAGE_ORIENTATION = Settings.enumSetting("ImageOrientation", ImageOrientation.West);
	public static final Setting<CustomStructureType> CUSTOM_STRUCTURE_TYPE = Settings.enumSetting("CustomStructureType", CustomStructureType.BO3);
	
	public static final Setting<String>
		AUTHOR = Settings.stringSetting("Author", "Unknown"),
		SHORT_PRESET_NAME = Settings.stringSetting("ShortPresetName", ""),
		DESCRIPTION = Settings.stringSetting("Description", "No description given"),
		IMAGE_FILE = Settings.stringSetting("ImageFile", "map.png"),
		IMAGE_FILL_BIOME = Settings.stringSetting("ImageFillBiome", "Ocean"),
		BO3_AT_SPAWN = Settings.stringSetting("BO3AtSpawn", ""),
		DEFAULT_OCEAN_BIOME = Settings.stringSetting("DefaultOceanBiome", "Ocean"),
		DEFAULT_FROZEN_OCEAN_BIOME = Settings.stringSetting("DefaultFrozenOceanBiome", "Ocean"),
		DEFAULT_WARM_OCEAN_BIOME = Settings.stringSetting("DefaultWarmOceanBiome", "Ocean"),
		DEFAULT_LUKEWARM_OCEAN_BIOME = Settings.stringSetting("DefaultLukewarmOceanBiome", "Ocean"),
		DEFAULT_COLD_OCEAN_BIOME = Settings.stringSetting("DefaultColdOceanBiome", "Ocean"),
		INFINIBURN = Settings.stringSetting("InfiniBurn", "minecraft:infiniburn_overworld"),
		EFFECTS_LOCATION = Settings.stringSetting("EffectsLocation", "minecraft:overworld"),
		PORTAL_COLOR = Settings.stringSetting("PortalColor", "Default"),
		PORTAL_IGNITION_SOURCE = Settings.stringSetting("PortalIgnitionSource", "minecraft:flint_and_steel"),
		PORTAL_MOB = Settings.stringSetting("PortalMob", "minecraft:zombified_piglin")
	;

	public static final Setting<Integer>
		MAJOR_VERSION = Settings.intSetting("MajorVersion", 0 , 0, Integer.MAX_VALUE),
		MINOR_VERSION = Settings.intSetting("MinorVersion", 0 , 0, Integer.MAX_VALUE),
		WORLD_HEIGHT_SCALE_BITS = Settings.intSetting("WorldHeightScaleBits", 7, 5, 8),
		WORLD_HEIGHT_CAP_BITS = Settings.intSetting("WorldHeightCapBits", 8, 5, 8),
		GENERATION_DEPTH = Settings.intSetting("GenerationDepth", 10, 1, 20),
		BIOME_RARITY_SCALE = Settings.intSetting("BiomeRarityScale", 100, 1, Integer.MAX_VALUE),
		LAND_RARITY = Settings.intSetting("LandRarity", 99, 0, 100),
		LAND_SIZE = Settings.intSetting("LandSize", 0, 0, 20),
		OCEAN_BIOME_SIZE = Settings.intSetting("OceanBiomeSize", 6, 0, 20),
		LAND_FUZZY = Settings.intSetting("LandFuzzy", 5, 0, 20),
		ICE_RARITY = Settings.intSetting("IceRarity", 90, 1, 100),
		ICE_SIZE = Settings.intSetting("IceSize", 3, 0, 20),
		RIVER_RARITY = Settings.intSetting("RiverRarity", 4, 0, 20),
		RIVER_SIZE = Settings.intSetting("RiverSize", 0, 0, 20),
		WATER_LEVEL_MAX = Settings.intSetting("WaterLevelMax", 63, Constants.WORLD_DEPTH, Constants.WORLD_HEIGHT - 1),
		WATER_LEVEL_MIN = Settings.intSetting("WaterLevelMin", 0, Constants.WORLD_DEPTH, Constants.WORLD_HEIGHT - 1),
		IMAGE_X_OFFSET = Settings.intSetting("ImageXOffset", 0, Integer.MIN_VALUE, Integer.MAX_VALUE),
		IMAGE_Z_OFFSET = Settings.intSetting("ImageZOffset", 0, Integer.MIN_VALUE, Integer.MAX_VALUE),
		CAVE_RARITY = Settings.intSetting("CaveRarity", 14, 0, 100),
		CAVE_FREQUENCY = Settings.intSetting("CaveFrequency", 15, 0, 200),
		CAVE_MIN_ALTITUDE = Settings.intSetting("CaveMinAltitude", 8, Constants.WORLD_DEPTH, Constants.WORLD_HEIGHT - 1),
		CAVE_MAX_ALTITUDE = Settings.intSetting("CaveMaxAltitude", 128, Constants.WORLD_DEPTH, Constants.WORLD_HEIGHT - 1),
		INDIVIDUAL_CAVE_RARITY = Settings.intSetting("IndividualCaveRarity", 25, 0, 100),
		CAVE_SYSTEM_FREQUENCY = Settings.intSetting("CaveSystemFrequency", 1, 0, 200),
		CAVE_SYSTEM_POCKET_CHANCE = Settings.intSetting("CaveSystemPocketChance", 0, 0, 100),
		CAVE_SYSTEM_POCKET_MIN_SIZE = Settings.intSetting("CaveSystemPocketMinSize", 0, 0, 100),
		CAVE_SYSTEM_POCKET_MAX_SIZE = Settings.intSetting("CaveSystemPocketMaxSize", 3, 0, 100),
		RAVINE_RARITY = Settings.intSetting("RavineRarity", 2, 0, 100),
		RAVINE_MIN_ALTITUDE = Settings.intSetting("RavineMinAltitude", 20, Constants.WORLD_DEPTH, Constants.WORLD_HEIGHT - 1),
		RAVINE_MAX_ALTITUDE = Settings.intSetting("RavineMaxAltitude", 68, Constants.WORLD_DEPTH, Constants.WORLD_HEIGHT - 1),
		RAVINE_MIN_LENGTH = Settings.intSetting("RavineMinLength", 84, 1, 500),
		RAVINE_MAX_LENGTH = Settings.intSetting("RavineMaxLength", 112, 1, 500),
		MAXIMUM_CUSTOM_STRUCTURE_RADIUS = Settings.intSetting("MaximumCustomStructureRadius", 5, 1, 100),
		CARVER_LAVA_BLOCK_HEIGHT = Settings.intSetting("CarverLavaBlockHeight", 10, 0, 255),
		RANDOM_TICK_SPEED = Settings.intSetting("RandomTickSpeed", 3, 0, Integer.MAX_VALUE),
		SPAWN_RADIUS = Settings.intSetting("SpawnRadius", 10, 0, Integer.MAX_VALUE),
		MAX_ENTITY_CRAMMING = Settings.intSetting("MaxEntityCramming", 24, 0, Integer.MAX_VALUE),
		MAX_COMMAND_CHAIN_LENGTH = Settings.intSetting("MaxCommandChainLength", 65536, 0, Integer.MAX_VALUE),
		LOGICAL_HEIGHT = Settings.intSetting("LogicalHeight", 256, 0, Integer.MAX_VALUE),
		SPAWN_POINT_X = Settings.intSetting("SpawnPointX", 0, Integer.MIN_VALUE, Integer.MAX_VALUE),
		SPAWN_POINT_Y = Settings.intSetting("SpawnPointY", 0, Integer.MIN_VALUE, Integer.MAX_VALUE),
		SPAWN_POINT_Z = Settings.intSetting("SpawnPointZ", 0, Integer.MIN_VALUE, Integer.MAX_VALUE),
		VILLAGE_SPACING = Settings.intSetting("VillageSpacing", 32, Integer.MIN_VALUE, Integer.MAX_VALUE),
		VILLAGE_SEPARATION = Settings.intSetting("VillageSeparation", 8, Integer.MIN_VALUE, Integer.MAX_VALUE),
		DESERTPYRAMID_SPACING = Settings.intSetting("DesertPyramidSpacing", 32, Integer.MIN_VALUE, Integer.MAX_VALUE),
		DESERTPYRAMID_SEPARATION = Settings.intSetting("DesertPyramidSeparation", 8, Integer.MIN_VALUE, Integer.MAX_VALUE),
		IGLOO_SPACING = Settings.intSetting("IglooSpacing", 32, Integer.MIN_VALUE, Integer.MAX_VALUE),
		IGLOO_SEPARATION = Settings.intSetting("IglooSeparation", 8, Integer.MIN_VALUE, Integer.MAX_VALUE),
		JUNGLETEMPLE_SPACING = Settings.intSetting("JungleTempleSpacing", 32, Integer.MIN_VALUE, Integer.MAX_VALUE),
		JUNGLETEMPLE_SEPARATION = Settings.intSetting("JungleTempleSeparation", 8, Integer.MIN_VALUE, Integer.MAX_VALUE),
		SWAMPHUT_SPACING = Settings.intSetting("SwampHutSpacing", 32, Integer.MIN_VALUE, Integer.MAX_VALUE),
		SWAMPHUT_SEPARATION = Settings.intSetting("SwampHutSeparation", 8, Integer.MIN_VALUE, Integer.MAX_VALUE),
		PILLAGEROUTPOST_SPACING = Settings.intSetting("PillagerOutpostSpacing", 32, Integer.MIN_VALUE, Integer.MAX_VALUE),
		PILLAGEROUTPOST_SEPARATION = Settings.intSetting("PillagerOutpostSeparation", 8, Integer.MIN_VALUE, Integer.MAX_VALUE),
		STRONGHOLD_SPACING = Settings.intSetting("StrongholdSpacing", 1, Integer.MIN_VALUE, Integer.MAX_VALUE),
		STRONGHOLD_SEPARATION = Settings.intSetting("StrongholdSeparation", 0, Integer.MIN_VALUE, Integer.MAX_VALUE),
		STRONGHOLD_DISTANCE = Settings.intSetting("StrongholdDistance", 32, Integer.MIN_VALUE, Integer.MAX_VALUE),
		STRONGHOLD_SPREAD = Settings.intSetting("StrongholdSpread", 3, Integer.MIN_VALUE, Integer.MAX_VALUE),
		STRONGHOLD_COUNT = Settings.intSetting("StrongholdCount", 128, Integer.MIN_VALUE, Integer.MAX_VALUE),
		OCEANMONUMENT_SPACING = Settings.intSetting("OceanMonumentSpacing", 32, Integer.MIN_VALUE, Integer.MAX_VALUE),
		OCEANMONUMENT_SEPARATION = Settings.intSetting("OceanMonumentSeparation", 5, Integer.MIN_VALUE, Integer.MAX_VALUE),
		ENDCITY_SPACING = Settings.intSetting("EndCitySpacing", 20, Integer.MIN_VALUE, Integer.MAX_VALUE),
		ENDCITY_SEPARATION = Settings.intSetting("EndCitySeparation", 11, Integer.MIN_VALUE, Integer.MAX_VALUE),
		WOODLANDMANSION_SPACING = Settings.intSetting("WoodlandMansionSpacing", 80, Integer.MIN_VALUE, Integer.MAX_VALUE),
		WOODLANDMANSION_SEPARATION = Settings.intSetting("WoodlandMansionSeparation", 20, Integer.MIN_VALUE, Integer.MAX_VALUE),
		BURIEDTREASURE_SPACING = Settings.intSetting("BuriedTreasureSpacing", 1, Integer.MIN_VALUE, Integer.MAX_VALUE),
		BURIEDTREASURE_SEPARATION = Settings.intSetting("BuriedTreasureSeparation", 0, Integer.MIN_VALUE, Integer.MAX_VALUE),
		MINESHAFT_SPACING = Settings.intSetting("MineshaftSpacing", 1, Integer.MIN_VALUE, Integer.MAX_VALUE),
		MINESHAFT_SEPARATION = Settings.intSetting("MineshaftSeparation", 0, Integer.MIN_VALUE, Integer.MAX_VALUE),
		RUINEDPORTAL_SPACING = Settings.intSetting("RuinedPortalSpacing", 40, Integer.MIN_VALUE, Integer.MAX_VALUE),
		RUINEDPORTAL_SEPARATION = Settings.intSetting("RuinedPortalSeparation", 15, Integer.MIN_VALUE, Integer.MAX_VALUE),
		SHIPWRECK_SPACING = Settings.intSetting("ShipwreckSpacing", 24, Integer.MIN_VALUE, Integer.MAX_VALUE),
		SHIPWRECK_SEPARATION = Settings.intSetting("ShipwreckSeparation", 4, Integer.MIN_VALUE, Integer.MAX_VALUE),
		OCEANRUIN_SPACING = Settings.intSetting("OceanRuinSpacing", 20, Integer.MIN_VALUE, Integer.MAX_VALUE),
		OCEANRUIN_SEPARATION = Settings.intSetting("OceanRuinSeparation", 8, Integer.MIN_VALUE, Integer.MAX_VALUE),
		BASTIONREMNANT_SPACING = Settings.intSetting("BastionRemnantSpacing", 27, Integer.MIN_VALUE, Integer.MAX_VALUE),
		BASTIONREMNANT_SEPARATION = Settings.intSetting("BastionRemnantSeparation", 4, Integer.MIN_VALUE, Integer.MAX_VALUE),
		NETHERFORTRESS_SPACING = Settings.intSetting("NetherFortressSpacing", 27, Integer.MIN_VALUE, Integer.MAX_VALUE),
		NETHERFORTRESS_SEPARATION = Settings.intSetting("NetherFortressSeparation", 4, Integer.MIN_VALUE, Integer.MAX_VALUE),
		NETHERFOSSIL_SPACING = Settings.intSetting("NetherFossilSpacing", 2, Integer.MIN_VALUE, Integer.MAX_VALUE),
		NETHERFOSSIL_SEPARATION = Settings.intSetting("NetherFossilSeparation", 1, Integer.MIN_VALUE, Integer.MAX_VALUE)
	;
	
	public static final Setting<Long>
		FIXED_TIME = Settings.longSetting("FixedTime", -1l, -1l, 24000)
	;
	
	public static final Setting<Boolean>
		FORCE_LAND_AT_SPAWN = Settings.booleanSetting("ForceLandAtSpawn", true),
		RIVERS_ENABLED = Settings.booleanSetting("RiversEnabled", true),
		RANDOM_RIVERS = Settings.booleanSetting("RandomRivers", false),
		FROZEN_OCEAN = Settings.booleanSetting("FrozenOcean", true),
		BETTER_SNOW_FALL = Settings.booleanSetting("BetterSnowFall", false),
		EVEN_CAVE_DISTRIBUTION = Settings.booleanSetting("EvenCaveDistribution", false),
		DISABLE_BEDROCK = Settings.booleanSetting("DisableBedrock", false),
		CEILING_BEDROCK = Settings.booleanSetting("CeilingBedrock", false),
		FLAT_BEDROCK = Settings.booleanSetting("FlatBedrock", false),
		REMOVE_SURFACE_STONE = Settings.booleanSetting("RemoveSurfaceStone", false),
		USE_OLD_BO3_STRUCTURE_RARITY = Settings.booleanSetting("UseOldBO3StructureRarity", true),
		DECORATION_BOUNDS_CHECK = Settings.booleanSetting("DecorationBoundsCheck", true),
		DISABLE_OREGEN = Settings.booleanSetting("DisableOreGen", false),

		OLD_GROUP_RARITY = Settings.booleanSetting("OldGroupRarity", true), //TODO: for 1.16 1.0, switch this to false --Authvin
		OLD_LAND_RARITY = Settings.booleanSetting("OldLandRarity", true), //TODO: for 1.16 1.0, switch this to false --Authvin
		CAVES_ENABLED = Settings.booleanSetting("CavesEnabled", true),
		RAVINES_ENABLED = Settings.booleanSetting("RavinesEnabled", true),
		MINESHAFTS_ENABLED = Settings.booleanSetting("MineshaftsEnabled", true),
		OCEAN_MONUMENTS_ENABLED = Settings.booleanSetting("OceanMonumentsEnabled", true),
		RARE_BUILDINGS_ENABLED = Settings.booleanSetting("RareBuildingsEnabled", true),
		STRONGHOLDS_ENABLED = Settings.booleanSetting("StrongholdsEnabled", true),
		WOODLAND_MANSIONS_ENABLED = Settings.booleanSetting("WoodlandsMansionsEnabled", true),
		NETHER_FORTRESSES_ENABLED = Settings.booleanSetting("NetherFortressesEnabled", true),
		BURIED_TREASURE_ENABLED = Settings.booleanSetting("BuriedTreasureEnabled", true),
		OCEAN_RUINS_ENABLED = Settings.booleanSetting("OceanRuinsEnabled", true),
		PILLAGER_OUTPOSTS_ENABLED = Settings.booleanSetting("PillagerOutpostsEnabled", true),
		BASTION_REMNANTS_ENABLED = Settings.booleanSetting("BastionRemnantsEnabled", true),
		NETHER_FOSSILS_ENABLED = Settings.booleanSetting("NetherFossilsEnabled", true),
		END_CITIES_ENABLED = Settings.booleanSetting("EndCitiesEnabled", true),
		RUINED_PORTALS_ENABLED = Settings.booleanSetting("RuinedPortalsEnabled", true),
		SHIPWRECKS_ENABLED = Settings.booleanSetting("ShipwrecksEnabled", true),
		VILLAGES_ENABLED = Settings.booleanSetting("VillagesEnabled", true),
		
		OVERRIDE_GAME_RULES = Settings.booleanSetting("OverrideGameRules", false),
		DO_FIRE_TICK = Settings.booleanSetting("DoFireTick", true),
		MOB_GRIEFING = Settings.booleanSetting("MobGriefing", true),
		KEEP_INVENTORY = Settings.booleanSetting("KeepInventory", false),
		DO_MOB_SPAWNING = Settings.booleanSetting("DoMobSpawning", true),
		DO_MOB_LOOT = Settings.booleanSetting("DoMobLoot", true),
		DO_TILE_DROPS = Settings.booleanSetting("DoTileDrops", true),
		DO_ENTITY_DROPS = Settings.booleanSetting("DoEntityDrops", true),
		COMMAND_BLOCK_OUTPUT = Settings.booleanSetting("CommandBlockOutput", true),
		NATURAL_REGENERATION = Settings.booleanSetting("NaturalRegeneration", true),
		DO_DAY_LIGHT_CYCLE = Settings.booleanSetting("DoDaylightCycle", true),
		LOG_ADMIN_COMMANDS = Settings.booleanSetting("LogAdminCommands", true),
		SHOW_DEATH_MESSAGES = Settings.booleanSetting("ShowDeathMessages", true),
		SEND_COMMAND_FEEDBACK = Settings.booleanSetting("SendCommandFeedback", true),
		SPECTATORS_GENERATE_CHUNKS = Settings.booleanSetting("SpectatorsGenerateChunks", true),
		DISABLE_ELYTRA_MOVEMENT_CHECK = Settings.booleanSetting("DisableElytraMovementCheck", false),
		DO_WEATHER_CYCLE = Settings.booleanSetting("DoWeatherCycle", true),
		DO_LIMITED_CRAFTING = Settings.booleanSetting("DoLimitedCrafting", false),
		ANNOUNCE_ADVANCEMENTS = Settings.booleanSetting("AnnounceAdvancements", true),
		DISABLE_RAIDS = Settings.booleanSetting("DisableRaids", false),
		DO_INSOMNIA = Settings.booleanSetting("DoInsomnia", true),
		DROWNING_DAMAGE = Settings.booleanSetting("DrowningDamage", true),
		FALL_DAMAGE = Settings.booleanSetting("FallDamage", true),
		FIRE_DAMAGE = Settings.booleanSetting("FireDamage", true),
		DO_PATROL_SPAWNING = Settings.booleanSetting("DoPatrolSpawning", true),
		DO_TRADER_SPAWNING = Settings.booleanSetting("DoTraderSpawning", true),
		FORGIVE_DEAD_PLAYERS = Settings.booleanSetting("ForgiveDeadPlayers", true),
		UNIVERSAL_ANGER = Settings.booleanSetting("UniversalAnger", false),
	
		HAS_SKYLIGHT = Settings.booleanSetting("HasSkylight", true),
		HAS_CEILING = Settings.booleanSetting("HasCeiling", false),
		ULTRA_WARM = Settings.booleanSetting("UltraWarm", false),
		NATURAL = Settings.booleanSetting("Natural", true),
		CREATE_DRAGON_FLIGHT = Settings.booleanSetting("CreateDragonFight", false),
		PIGLIN_SAFE = Settings.booleanSetting("PiglinSafe", false),
		BED_WORKS = Settings.booleanSetting("BedWorks", true),
		RESPAWN_ANCHOR_WORKS = Settings.booleanSetting("RespawnAnchorWorks", true),
		HAS_RAIDS = Settings.booleanSetting("HasRaids", true),
		FIXED_SPAWN_POINT = Settings.booleanSetting("FixedSpawnPoint", false)
	;

	public static final Setting<LocalMaterialData>
		WATER_BLOCK = new MaterialSetting("WaterBlock", LocalMaterials.WATER_NAME),
		ICE_BLOCK = new MaterialSetting("IceBlock", LocalMaterials.ICE_NAME),
		COOLED_LAVA_BLOCK = new MaterialSetting("CooledLavaBlock", LocalMaterials.LAVA_NAME),
		BEDROCK_BLOCK = new MaterialSetting("BedrockBlock", LocalMaterials.BEDROCK_NAME),
		CARVER_LAVA_BLOCK = new MaterialSetting("CarverLavaBlock", LocalMaterials.LAVA_NAME)
	;

    public static final Setting<ArrayList<LocalMaterialData>> PORTAL_BLOCKS = new MaterialListSetting("PortalBlocks", new String[] { LocalMaterials.QUARTZ_BLOCK_NAME });
	
	public static final Setting<List<String>>
		ISLE_BIOMES = Settings.stringListSetting("IsleBiomes", "Deep Ocean", "MushroomIsland",
			"Ice Mountains", "DesertHills", "ForestHills", "Forest", "TaigaHills",
			"JungleHills", "Cold Taiga Hills", "Birch Forest Hills", "Extreme Hills+",
			"Mesa Plateau", "Mesa Plateau F", "Mesa Plateau M", "Mesa Plateau F M",
			"Mesa (Bryce)", "Mega Taiga Hills", "Mega Spruce Taiga Hills"),
		BORDER_BIOMES = Settings.stringListSetting("BorderBiomes",
			"JungleEdge", "JungleEdge M", "MushroomIslandShore", "Beach", "Extreme Hills Edge", "Desert", "Taiga")
	;

	public static final Setting<Float>
		SPAWN_POINT_ANGLE = Settings.floatSetting("SpawnPointAngle", 0.0f, Integer.MIN_VALUE, Integer.MAX_VALUE)
	;
	
	public static final Setting<Double>
		FROZEN_OCEAN_TEMPERATURE = Settings.doubleSetting("OceanFreezingTemperature", 0.15, 0, 2),
		RAVINE_DEPTH = Settings.doubleSetting("RavineDepth", 3, 0.1, 15),
		CANYON_DEPTH = Settings.doubleSetting("CanyonDepth", 3, 0.1, 15),
		FRACTURE_HORIZONTAL = Settings.doubleSetting("FractureHorizontal", 0, -500, 500),
		FRACTURE_VERTICAL = Settings.doubleSetting("FractureVertical", 0, -500, 500),
		COORDINATE_SCALE = Settings.doubleSetting("CoordinateScale", 1.0D, 0.0D, Integer.MAX_VALUE),
		AMBIENT_LIGHT = Settings.doubleSetting("AmbientLight", 0.0D, 0.0D, Integer.MAX_VALUE)
	;

	public static final Setting<Integer>
			PRESET_FOG_COLOR = Settings.colorSetting("WorldFog", "0xC0D8FF")
	;

	// Deprecated settings
	public static final Setting<Boolean> FROZEN_RIVERS = Settings.booleanSetting("FrozenRivers", true);
	public static final Setting<List<String>> NORMAL_BIOMES = Settings.stringListSetting(
		"NormalBiomes", "Desert", "Forest", "Extreme Hills", "Swampland", "Plains", "Taiga", "Jungle", "River"
	);
	public static final Setting<List<String>> BLACKLISTED_BIOMES = Settings.stringListSetting("BlacklistedBiomes", "");
	
	public static final Setting<List<String>> ICE_BIOMES = Settings.stringListSetting("IceBiomes", "Ice Plains");
}
