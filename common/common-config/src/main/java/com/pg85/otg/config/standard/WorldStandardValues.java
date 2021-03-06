package com.pg85.otg.config.standard;

import com.pg85.otg.config.settingType.MaterialSetting;
import com.pg85.otg.config.settingType.Setting;
import com.pg85.otg.config.settingType.Settings;
import com.pg85.otg.constants.Constants;
import com.pg85.otg.constants.SettingsEnums.BiomeMode;
import com.pg85.otg.constants.SettingsEnums.ConfigMode;
import com.pg85.otg.constants.SettingsEnums.CustomStructureType;
import com.pg85.otg.constants.SettingsEnums.ImageMode;
import com.pg85.otg.constants.SettingsEnums.ImageOrientation;
import com.pg85.otg.constants.SettingsEnums.TerrainMode;
import com.pg85.otg.util.materials.LocalMaterialData;
import com.pg85.otg.util.materials.LocalMaterials;

import java.util.List;

public class WorldStandardValues extends Settings
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

    public static final Setting<ConfigMode> SETTINGS_MODE = enumSetting("SettingsMode", ConfigMode.WriteAll);
    public static final Setting<ConfigMode> SETTINGS_MODE_BO3 = enumSetting("SettingsMode", ConfigMode.WriteDisable);
    public static final Setting<BiomeMode> BIOME_MODE = enumSetting("BiomeMode", BiomeMode.Normal);
    public static final Setting<TerrainMode> TERRAIN_MODE = enumSetting("TerrainMode", TerrainMode.Normal);
    public static final Setting<ImageMode> IMAGE_MODE = enumSetting("ImageMode", ImageMode.Mirror);
    public static final Setting<ImageOrientation> IMAGE_ORIENTATION = enumSetting("ImageOrientation", ImageOrientation.West);
    public static final Setting<CustomStructureType> CUSTOM_STRUCTURE_TYPE = enumSetting("CustomStructureType", CustomStructureType.BO3);
    
    public static final Setting<String>
		VERSION = stringSetting("Version", "0.0"),
		AUTHOR = stringSetting("Author", "Unknown"),
		SHORT_PRESET_NAME = stringSetting("ShortPresetName", ""),
		DESCRIPTION = stringSetting("Description", "No description given"),
		IMAGE_FILE = stringSetting("ImageFile", "map.png"),
		IMAGE_FILL_BIOME = stringSetting("ImageFillBiome", "Ocean"),
		BO3_AT_SPAWN = stringSetting("BO3AtSpawn", ""),
		DEFAULT_OCEAN_BIOME = stringSetting("DefaultOceanBiome", "Ocean"),
		DEFAULT_FROZEN_OCEAN_BIOME = stringSetting("DefaultFrozenOceanBiome", "Ocean"),
		DEFAULT_WARM_OCEAN_BIOME = stringSetting("DefaultWarmOceanBiome", "Ocean"),
		DEFAULT_LUKEWARM_OCEAN_BIOME = stringSetting("DefaultLukewarmOceanBiome", "Ocean"),
		DEFAULT_COLD_OCEAN_BIOME = stringSetting("DefaultColdOceanBiome", "Ocean")
	;

    public static final Setting<Integer>
		WORLD_HEIGHT_SCALE_BITS = intSetting("WorldHeightScaleBits", 7, 5, 8),
		WORLD_HEIGHT_CAP_BITS = intSetting("WorldHeightCapBits", 8, 5, 8),
		GENERATION_DEPTH = intSetting("GenerationDepth", 10, 1, 20),
		BIOME_RARITY_SCALE = intSetting("BiomeRarityScale", 100, 1, Integer.MAX_VALUE),
		LAND_RARITY = intSetting("LandRarity", 99, 1, 100),
		LAND_SIZE = intSetting("LandSize", 0, 0, 20),
		OCEAN_BIOME_SIZE = intSetting("OceanBiomeSize", 6, 0, 20),
		LAND_FUZZY = intSetting("LandFuzzy", 5, 0, 20),
		ICE_RARITY = intSetting("IceRarity", 90, 1, 100),
		ICE_SIZE = intSetting("IceSize", 3, 0, 20),
		RIVER_RARITY = intSetting("RiverRarity", 4, 0, 20),
		RIVER_SIZE = intSetting("RiverSize", 0, 0, 20),
		WATER_LEVEL_MAX = intSetting("WaterLevelMax", 63, Constants.WORLD_DEPTH, Constants.WORLD_HEIGHT - 1),
		WATER_LEVEL_MIN = intSetting("WaterLevelMin", 0, Constants.WORLD_DEPTH, Constants.WORLD_HEIGHT - 1),
		IMAGE_X_OFFSET = intSetting("ImageXOffset", 0, Integer.MIN_VALUE, Integer.MAX_VALUE),
		IMAGE_Z_OFFSET = intSetting("ImageZOffset", 0, Integer.MIN_VALUE, Integer.MAX_VALUE),
		CAVE_RARITY = intSetting("CaveRarity", 14, 0, 100),
		CAVE_FREQUENCY = intSetting("CaveFrequency", 15, 0, 200),
		CAVE_MIN_ALTITUDE = intSetting("CaveMinAltitude", 8, Constants.WORLD_DEPTH, Constants.WORLD_HEIGHT - 1),
		CAVE_MAX_ALTITUDE = intSetting("CaveMaxAltitude", 127, Constants.WORLD_DEPTH, Constants.WORLD_HEIGHT - 1),
		INDIVIDUAL_CAVE_RARITY = intSetting("IndividualCaveRarity", 25, 0, 100),
		CAVE_SYSTEM_FREQUENCY = intSetting("CaveSystemFrequency", 1, 0, 200),
		CAVE_SYSTEM_POCKET_CHANCE = intSetting("CaveSystemPocketChance", 0, 0, 100),
		CAVE_SYSTEM_POCKET_MIN_SIZE = intSetting("CaveSystemPocketMinSize", 0, 0, 100),
		CAVE_SYSTEM_POCKET_MAX_SIZE = intSetting("CaveSystemPocketMaxSize", 3, 0, 100),
		RAVINE_RARITY = intSetting("RavineRarity", 2, 0, 100),
		RAVINE_MIN_ALTITUDE = intSetting("RavineMinAltitude", 20, Constants.WORLD_DEPTH, Constants.WORLD_HEIGHT - 1),
		RAVINE_MAX_ALTITUDE = intSetting("RavineMaxAltitude", 67, Constants.WORLD_DEPTH, Constants.WORLD_HEIGHT - 1),
		RAVINE_MIN_LENGTH = intSetting("RavineMinLength", 84, 1, 500),
		RAVINE_MAX_LENGTH = intSetting("RavineMaxLength", 111, 1, 500),
		MAXIMUM_CUSTOM_STRUCTURE_RADIUS = intSetting("MaximumCustomStructureRadius", 5, 1, 100)
    ;
    
    public static final Setting<Boolean>
		RIVERS_ENABLED = booleanSetting("RiversEnabled", true),
		GROUP_FREEZE_ENABLED = booleanSetting("FreezeAllBiomesInColdGroup", false),
		RANDOM_RIVERS = booleanSetting("RandomRivers", false),
		FROZEN_OCEAN = booleanSetting("FrozenOcean", true),
		BETTER_SNOW_FALL = booleanSetting("BetterSnowFall", false),
		FULLY_FREEZE_LAKES = booleanSetting("FullyFreezeLakes", false),
		EVEN_CAVE_DISTRIBUTION = booleanSetting("EvenCaveDistrubution", false),
		DISABLE_BEDROCK = booleanSetting("DisableBedrock", false),
		CEILING_BEDROCK = booleanSetting("CeilingBedrock", false),
		FLAT_BEDROCK = booleanSetting("FlatBedrock", false),
		REMOVE_SURFACE_STONE = booleanSetting("RemoveSurfaceStone", false),
		POPULATION_BOUNDS_CHECK = booleanSetting("PopulationBoundsCheck", true),
		DISABLE_OREGEN = booleanSetting("DisableOreGen", false),

		OLD_GROUP_RARITY = booleanSetting("OldGroupRarity", true), //TODO: for 1.16 1.0, switch this to false --Authvin
		
		MINESHAFTS_ENABLED = booleanSetting("MineshaftsEnabled", true),
		OCEAN_MONUMENTS_ENABLED = booleanSetting("OceanMonumentsEnabled", true),
		RARE_BUILDINGS_ENABLED = booleanSetting("RareBuildingsEnabled", true),
		STRONGHOLDS_ENABLED = booleanSetting("StrongholdsEnabled", true),
		WOODLAND_MANSIONS_ENABLED = booleanSetting("WoodlandsMansionsEnabled", true),
		NETHER_FORTRESSES_ENABLED = booleanSetting("NetherFortressesEnabled", true),
		BURIED_TREASURE_ENABLED = booleanSetting("BuriedTreasureEnabled", true),
		OCEAN_RUINS_ENABLED = booleanSetting("OceanRuinsEnabled", true),
		PILLAGER_OUTPOSTS_ENABLED = booleanSetting("PillagerOutpostsEnabled", true),
		BASTION_REMNANTS_ENABLED = booleanSetting("BastionRemnantsEnabled", true),
		NETHER_FOSSILS_ENABLED = booleanSetting("NetherFossilsEnabled", true),
		END_CITIES_ENABLED = booleanSetting("EndCitiesEnabled", true),
		RUINED_PORTALS_ENABLED = booleanSetting("RuinedPortalsEnabled", true),
		SHIPWRECKS_ENABLED = booleanSetting("ShipwrecksEnabled", true),
		VILLAGES_ENABLED = booleanSetting("VillagesEnabled", true),
		
		// Legacy, only needed for <= 1.12.2 presets, remove when presets have been updated.
		ISOTGPLUS = booleanSetting("IsOTGPlus", false)
    ;

    public static final Setting<LocalMaterialData>
		WATER_BLOCK = new MaterialSetting("WaterBlock", LocalMaterials.WATER_NAME),
		ICE_BLOCK = new MaterialSetting("IceBlock", LocalMaterials.ICE_NAME),
		COOLED_LAVA_BLOCK = new MaterialSetting("CooledLavaBlock", LocalMaterials.LAVA_NAME),
		BEDROCK_BLOCK = new MaterialSetting("BedrockBlock", LocalMaterials.BEDROCK_NAME)
    ;

    public static final Setting<List<String>>
		ISLE_BIOMES = stringListSetting("IsleBiomes", "Deep Ocean", "MushroomIsland",
			"Ice Mountains", "DesertHills", "ForestHills", "Forest", "TaigaHills",
			"JungleHills", "Cold Taiga Hills", "Birch Forest Hills", "Extreme Hills+",
			"Mesa Plateau", "Mesa Plateau F", "Mesa Plateau M", "Mesa Plateau F M",
			"Mesa (Bryce)", "Mega Taiga Hills", "Mega Spruce Taiga Hills"),
		BORDER_BIOMES = stringListSetting("BorderBiomes",
			"JungleEdge", "JungleEdge M", "MushroomIslandShore", "Beach", "Extreme Hills Edge", "Desert", "Taiga")
	;

    public static final Setting<Double>
		FROZEN_OCEAN_TEMPERATURE = doubleSetting("OceanFreezingTemperature", 0.15, 0, 2),
		RAVINE_DEPTH = doubleSetting("RavineDepth", 3, 0.1, 15),
		CANYON_DEPTH = doubleSetting("CanyonDepth", 3, 0.1, 15),
		FRACTURE_HORIZONTAL = doubleSetting("FractureHorizontal", 0, -500, 500),
		FRACTURE_VERTICAL = doubleSetting("FractureVertical", 0, -500, 500)
    ;

    public static final Setting<Integer>
		WORLD_FOG_COLOR = colorSetting("WorldFog", "0xC0D8FF")
    ;

    // Deprecated settings
    public static final Setting<Boolean> FROZEN_RIVERS = booleanSetting("FrozenRivers", true);
    public static final Setting<List<String>> NORMAL_BIOMES = stringListSetting(
		"NormalBiomes", "Desert", "Forest", "Extreme Hills", "Swampland", "Plains", "Taiga", "Jungle", "River"
    );
    public static final Setting<List<String>> ICE_BIOMES = stringListSetting("IceBiomes", "Ice Plains");
}
