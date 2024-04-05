package com.pg85.otg.config.preset;

import com.pg85.otg.config.io.SettingsMap;
import com.pg85.otg.config.standard.PresetStandardValues;
import com.pg85.otg.constants.Constants;

public class PresetWriter {
    static void writePresetConfig(PresetConfig presetConfig, SettingsMap writer) {
        writer.header1("PresetConfig",
                "Contains settings which affect the entire world, biome specific settings can be found in the Biome Configs.",
                "This file controls biome groupings, ocean and land sizes/rarities, river settings, cave and canyon distribution,",
                "vanilla minecraft structure spawning, sea level, dimension/portal settings and more."
        );

        writer.header2("Config Writing");

        writer.putSetting(PresetStandardValues.SETTINGS_MODE, presetConfig.getPresetInfo().getSettingsMode(),
                "Each time " + Constants.MOD_ID + " reads the config files it can also write to them. With this setting you can change how this behaves. Possible modes:",
                "	WriteAll - Auto-update settings from old versions, order them, add comments, reset invalid settings and remove custom comments. (Recommended)",
                "	WriteWithoutComments - Same as WriteAll, but removes all comments, both the ones added by OTG and custom ones. Removing comments is a recommended optimization for release versions of presets.",
                "	WriteDisable - Doesn't write to the config files. Errors are not corrected, old settings are read but are not corrected. Custom comments won't be removed with this mode."
        );

        writer.header2("Preset Identity");

        writer.putSetting(PresetStandardValues.AUTHOR, presetConfig.getPresetInfo().getAuthor(),
                "The author of this preset"
        );

        writer.putSetting(PresetStandardValues.DESCRIPTION, presetConfig.getPresetInfo().getDescription(),
                "A short description of this preset"
        );

        writer.putSetting(PresetStandardValues.MAJOR_VERSION, presetConfig.getPresetInfo().getMajorVersion(),
                "The preset major version. Increasing the minor version makes the PresetPacker overwrite,",
                "while increasing the major version will make the PresetPacker save a new copy"
        );

        writer.putSetting(PresetStandardValues.MINOR_VERSION, presetConfig.getPresetInfo().getMinorVersion(),
                "The preset minor version. Increasing the minor version makes the PresetPacker overwrite,",
                "while increasing the major version will make the PresetPacker save a new copy"
        );

        writer.putSetting(PresetStandardValues.SHORT_PRESET_NAME, presetConfig.getPresetInfo().getShortPresetName(),
                "The shortened name for the preset, used in biome resource locations and similar"
        );

        writer.header2("Visual Settings",
                "Controls the world's fog colors. Sky, grass and foliage colors are defined inside the biome configs."
        );

        writer.putSetting(PresetStandardValues.PRESET_FOG_COLOR, presetConfig.getVisualSettings().getFogColor(),
                "Color of the distance fog, can be overridden per biome."
        );

        writer.header2("Biome Modes");

        writer.putSetting(PresetStandardValues.BIOME_MODE, presetConfig.getBiomeSettings().getBiomeMode(),
                "Possible biome modes:",
                "	Normal - standard random generation with biome groups, uses all features.",
                "	FromImage - biome layout defined by an image file."
        );

        writer.header1("Settings for BiomeMode: Normal");

        writer.putSetting(PresetStandardValues.GENERATION_DEPTH, presetConfig.getBiomeSettings().getGenerationDepth(),
                "Defines the maximum number BiomeSize, RiverSize and LandSize can be set to.",
                "All size settings such as Biome Group Size, RiverSize, LandSize (in the PresetConfig.ini), and BiomeSize (in Biome Configs) must be between 0 (largest) and GenerationDepth (smallest).",
                "Increasing GenerationDepth by one will roughly double the size of all biomes, similarly decreasing it by 1 will half the size of all biomes.",
                "Small values (1-2) and Large values (20+) may affect generator performance.",
                "This setting is also used in BiomeMode:FromImage when ImageMode is set to ContinueNormal"
        );

        writer.putSetting(PresetStandardValues.BIOME_RARITY_SCALE, presetConfig.getBiomeSettings().getBiomeRarityScale(),
                "Max biome rarity from 1 to infinity. By default this is 100, but you can raise it for fine-grained control, or to create biomes with a chance of occurring smaller than 1/100."
        );

        writer.putSetting(PresetStandardValues.OLD_GROUP_RARITY, presetConfig.getBiomeSettings().isOldGroupRarity(),
                "Whether or not OTG should use the old group rarity"
        );

        writer.putSetting(PresetStandardValues.OLD_LAND_RARITY, presetConfig.getBiomeSettings().isOldLandRarity(),
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

        writer.addConfigFunctions(presetConfig.getBiomeSettings().getTemplateBiomes());

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

        writer.addConfigFunctions(presetConfig.getBiomeSettings().getBiomeGroupManager().getGroups());

        writer.putSetting(PresetStandardValues.BLACKLISTED_BIOMES, presetConfig.getBiomeSettings().getBlackListedBiomes(),
                "When using biome dictionary tags and/or biome categories with biome groups, these (non-OTG) biomes are excluded. Example: minecraft:plains."
        );

        writer.header2("Isle & Border Biomes");

        writer.putSetting(PresetStandardValues.ISLE_BIOMES, presetConfig.getBiomeSettings().getIsleBiomes(),
                "Isle biomes are biomes which spawn inside another biome (e.g. an island in an ocean). As well as listing every isle biome here, you must set IsleInBiome in each biome config too. Biome name is case sensitive."
        );

        writer.putSetting(PresetStandardValues.BORDER_BIOMES, presetConfig.getBiomeSettings().getBorderBiomes(),
                "Biomes used as borders of other biomes. As well as listing every border biome here, you must set BiomeIsBorder in each biome config too. Biome name is case sensitive."
        );

        writer.header2("Landmass Settings");

        writer.putSetting(PresetStandardValues.LAND_RARITY, presetConfig.getBiomeSettings().getLandRarity(),
                "Land rarity from 100 to 1. Higher numbers result in more land."
        );

        writer.putSetting(PresetStandardValues.LAND_SIZE, presetConfig.getBiomeSettings().getLandSize(),
                "Land size from 0 to GenerationDepth. Higher LandSize numbers will make the size of the land smaller. Landsize number should always be lower than any biome groups."
        );

        writer.putSetting(PresetStandardValues.FORCE_LAND_AT_SPAWN, presetConfig.getBiomeSettings().isForceLandAtSpawn(),
                "If enabled, land will always spawn at or near 0,0"
        );

        writer.putSetting(PresetStandardValues.OCEAN_BIOME_SIZE, presetConfig.getBiomeSettings().getOceanBiomeSize(),
                "Ocean biome size 0 to GenerationDepth. Higher OceanBiomeSize numbers will make the size of the ocean biomes smaller."
        );

        writer.putSetting(PresetStandardValues.LAND_FUZZY, presetConfig.getBiomeSettings().getLandFuzzy(),
                "Generates more lakes (via small ocean biomes) at the edges of continents. As a side effect, the continent will also get a bit larger. Must be from 0 to GenerationDepth minus LandSize."
        );

        writer.putSetting(PresetStandardValues.DEFAULT_OCEAN_BIOME, presetConfig.getBiomeSettings().getDefaultOceanBiome(),
                "Set the default Ocean biome for this world."
        );

        writer.putSetting(PresetStandardValues.DEFAULT_WARM_OCEAN_BIOME, presetConfig.getBiomeSettings().getDefaultWarmOceanBiome(),
                "Set the default Warm Ocean biome for this world."
        );

        writer.putSetting(PresetStandardValues.DEFAULT_LUKEWARM_OCEAN_BIOME, presetConfig.getBiomeSettings().getDefaultLukewarmOceanBiome(),
                "Set the default Lukewarm Ocean biome for this world."
        );

        writer.putSetting(PresetStandardValues.DEFAULT_COLD_OCEAN_BIOME, presetConfig.getBiomeSettings().getDefaultColdOceanBiome(),
                "Set the default Cold Ocean biome for this world."
        );

        writer.putSetting(PresetStandardValues.DEFAULT_FROZEN_OCEAN_BIOME, presetConfig.getBiomeSettings().getDefaultFrozenOceanBiome(),
                "The default Frozen Ocean biome for this world."
        );

        writer.header2("Ice Area Settings");

        writer.putSetting(PresetStandardValues.FROZEN_OCEAN, presetConfig.getBiomeSettings().isFrozenOcean(),
                "Can be true or false, makes the water of the oceans near a cold biome frozen. The definition of 'cold' is controlled by the next setting.",
                "Set this to false to stop the ocean from freezing near when an \"ice area\" intersects with an ocean."
        );

        writer.putSetting(PresetStandardValues.FROZEN_OCEAN_TEMPERATURE, presetConfig.getBiomeSettings().getFrozenOceanTemperature(),
                "This is the maximum biome temperature when a biome is still considered cold. Water in oceans nearby cold biomes freezes if FrozenOcean is set to true.",
                "Temperature reference from vanilla Minecraft: < 0.15 for snow, 0.15 - 0.95 for rain, or > 1.0 for dry."
        );

        writer.header2("Rivers");

        writer.putSetting(PresetStandardValues.RIVERS_ENABLED, presetConfig.getBiomeSettings().isRiversEnabled(),
                "Set this to false to prevent the river generator from doing anything."
        );

        writer.putSetting(PresetStandardValues.RANDOM_RIVERS, presetConfig.getBiomeSettings().isRandomRivers(),
                "When this setting is false, rivers follow the biome borders most of the time. Set this setting to true to disable this behavior."
        );
        writer.putSetting(PresetStandardValues.RIVER_RARITY, presetConfig.getBiomeSettings().getRiverRarity(),
                "Controls the rarity of rivers. Must be from 0 to GenerationDepth. A higher number means more rivers. To define which rivers flow through which biomes see the individual biome configs."
        );

        writer.putSetting(PresetStandardValues.RIVER_SIZE, presetConfig.getBiomeSettings().getRiverSize(),
                "Controls the size of rivers. Can range from 0 to GenerationDepth minus RiverRarity. Making this larger will make the rivers larger, without affecting how often rivers will spawn."
        );

        writer.header1("Settings For BiomeMode:FromImage",
                "In each of the BiomeConfigs there is a BiomeColor variable, this variable is the hexadecimal color of the biome.",
                "These colors are used to define the biome layout in the input image (as well as the colour of the biome when using the /otg map command). Two biomes must not have the same color.",
                "The settings in this section are for FromImage mode only."
        );

        writer.putSetting(PresetStandardValues.IMAGE_MODE, presetConfig.getImageSettings().getImageMode(),
                "Defines what to do when terrain is generated outside the boundaries of the image:",
                "	Repeat - repeats the image",
                "	Mirror - repeats and mirrors the image",
                "	ContinueNormal - continues with random generation, using settings for BiomeMode: Normal",
                "	FillEmpty - fills the space with one biome (defined below)"
        );

        writer.putSetting(PresetStandardValues.IMAGE_FILE, presetConfig.getImageSettings().getImageFile(),
                "The image which will provide the Biomes must be a PNG file without transparency, once placed in the same folder as PresetConfig.ini OTG will use it as a reference for the Biomes generation.",
                "Source png file name for FromImage biome mode."
        );

        writer.putSetting(PresetStandardValues.IMAGE_ORIENTATION, presetConfig.getImageSettings().getImageOrientation(),
                "How the image is oriented: North, South, East or West. When this is set to North, the top of your picture is north (no rotation).",
                "When it is set to East, the image is rotated 90 degrees counter-clockwise, therefore what is on the east in the image becomes north in the world.",
                "Possible values: North, East, South, West."
        );

        writer.putSetting(PresetStandardValues.IMAGE_FILL_BIOME, presetConfig.getImageSettings().getImageFillBiome(),
                "Biome name for filling outside image boundaries with FillEmpty mode."
        );

        writer.putSetting(PresetStandardValues.IMAGE_X_OFFSET, presetConfig.getImageSettings().getImageXOffset(),
                "Translates the map origin. This number needs to be multiplied by -1 when using FillEmpty."
        );

        writer.putSetting(PresetStandardValues.IMAGE_Z_OFFSET, presetConfig.getImageSettings().getImageZOffset(),
                "Translates the map origin. This number needs to be multiplied by -1 when using FillEmpty."
        );

        writer.header1("Terrain Height and Volatility",
                "The settings in this section control terrain settings that are not specific to any biome."
        );

        writer.putSetting(PresetStandardValues.WORLD_HEIGHT_SCALE_BITS, presetConfig.getTerrainSettings().getWorldHeightScale(),
                "The height scale of the world. Increasing this by one doubles the terrain height of the world, substracting one halves the terrain height. Values must be between 5 and 8, inclusive."
        );

        writer.putSetting(PresetStandardValues.WORLD_HEIGHT_CAP_BITS, presetConfig.getTerrainSettings().getWorldHeightCap(),
                "The height cap of the world. A cap of 7 will make sure that there is no terrain above 128 (y=2^7). Near this cap less and less terrain generates with no terrain above this cap.",
                "Values must be between 5 and 8 (inclusive), and may not be lower that WorldHeightScaleBits."
        );

        writer.putSetting(PresetStandardValues.FRACTURE_HORIZONTAL, presetConfig.getTerrainSettings().getFractureHorizontal(),
                "Can increase (values greater than 0) or decrease (values less than 0) how much the landscape is fractured horizontally.",
                "Values less than 0 will 'relax' the terrain, leading to more gradual and smoother height transitions."
        );

        writer.putSetting(PresetStandardValues.FRACTURE_VERTICAL, presetConfig.getTerrainSettings().getFractureVertical(),
                "Can increase (values greater than 0) or decrease (values less than 0) how much the landscape is fractured vertically.",
                "Values above 0 will lead to large cliffs/overhangs, floating islands, and/or a cavern world depending on other settings.",
                "Values less than 0 will make terrain volatility more 'spiky' but lessen the likelihood of overhangs and floating terrain."
        );

        writer.header1("Blocks");

        writer.putSetting(PresetStandardValues.REMOVE_SURFACE_STONE, presetConfig.getBlockSettings().isRemoveSurfaceStone(),
                "Set this to true to place the biome surface block on top of all exposed stone."
        );

        writer.header2("Bedrock");

        writer.putSetting(PresetStandardValues.BEDROCK_BLOCK, presetConfig.getBlockSettings().getBedrockBlock(),
                "Block used as bedrock."
        );

        writer.putSetting(PresetStandardValues.DISABLE_BEDROCK, presetConfig.getBedrockSettings().isBedrockDisabled(),
                "Disable bottom of map bedrock generation. Doesn't affect bedrock on the ceiling of the map."
        );

        writer.putSetting(PresetStandardValues.CEILING_BEDROCK, presetConfig.getBedrockSettings().isCeilingBedrock(),
                "Enable ceiling of map bedrock generation."
        );

        writer.putSetting(PresetStandardValues.FLAT_BEDROCK, presetConfig.getBedrockSettings().isFlatBedrock(),
                "Make a single flat layer of bedrock."
        );

        writer.header2("Water / Lava / Frozen States");

        writer.putSetting(PresetStandardValues.WATER_LEVEL_MAX, presetConfig.getTerrainSettings().getWaterLevelMax(),
                "Set water level. Every empty block under this level will be fill water or another block from WaterBlock."
        );

        writer.putSetting(PresetStandardValues.WATER_LEVEL_MIN, presetConfig.getTerrainSettings().getWaterLevelMin());

        writer.putSetting(PresetStandardValues.WATER_BLOCK, presetConfig.getBlockSettings().getWaterBlock(),
                "Block used as water in WaterLevel."
        );

        writer.putSetting(PresetStandardValues.ICE_BLOCK, presetConfig.getBlockSettings().getIceBlock(),
                "Block used as ice."
        );

        writer.putSetting(PresetStandardValues.COOLED_LAVA_BLOCK, presetConfig.getBlockSettings().getCooledLavaBlock(),
                "Block used as cooled or frozen lava.",
                "Set this to OBSIDIAN for \"frozen\" lava lakes in cold biomes"
        );

        writer.putSetting(PresetStandardValues.BETTER_SNOW_FALL, presetConfig.getTerrainSettings().isBetterSnowFall(),
                "When set to false, 1 layer of snow falls on the highest block only.",
                "When set to true, the number of layers (1-8) is dependent on biome temperature.",
                "Higher altitudes have lower temperatures, so snow becomes deeper higher up.",
                "Also causes snow to fall through leaves, leaves can carry 3 layers while the rest falls through."
        );

        writer.header1("Resources");

        writer.putSetting(PresetStandardValues.DISABLE_OREGEN, presetConfig.getResourceSettings().isDisableOreGen(),
                "Disables Ore(), UnderWaterOre() and Vein() biome resources that use any type of ore block."
        );

        // Structures

        writer.header1("Structures",
                "These are global on/off toggles and spacing/separation settings for the entire world for each",
                "vanilla structure type. Spacing/separation work the same way as they do for datapacks.",
                "When set to true, structures configured in biome configs are able to spawn.",
                "Check the biome configs for customisation options per structure type per biome (size etc)."
        );
        var structureSettings = presetConfig.getStructureSettings();
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

        writer.putSetting(PresetStandardValues.CUSTOM_STRUCTURE_TYPE, presetConfig.getCustomStructureSettings().getCustomStructureType(),
                "Sets the type of structures the world should spawn, BO3 or BO4.",
                "Allowed values: BO3/BO4.",
                "BO4's allow for collision detection, fine control over structure distribution, advanced branching mechanics for",
                "procedurally generated structures, smoothing areas, extremely large structures, settings for blending structures",
                "with surrounding terrain, etc. BO3's are simpler, seed based CustomStructures, more like vanilla mc structures.",
                "Worlds currently can only use one type of structure."
        );

        writer.putSetting(PresetStandardValues.BO3_AT_SPAWN, presetConfig.getCustomStructureSettings().getBO3AtSpawn(),
                "This BO3 will be spawned at the world's spawn point as a CustomObject (Max size 32x32)."
        );

        writer.header2("BO3 Custom structures");

        writer.putSetting(PresetStandardValues.USE_OLD_BO3_STRUCTURE_RARITY, presetConfig.getCustomStructureSettings().isUseOldBO3StructureRarity(),
                "For 1.12.2 v9.0_r11 and earlier, BO3 customstructures used 2 rarity rolls,",
                "one for the rarity in the CustomStructure() tag, one for the rarity in the BO3 itself.",
                "For 1.16, we use only the rarity roll from the CustomStructure() tag. Set this to true",
                "to use the old system."
        );

        writer.putSetting(PresetStandardValues.MAXIMUM_CUSTOM_STRUCTURE_RADIUS, presetConfig.getCustomStructureSettings().getMaximumCustomStructureRadius(),
                "Maximum radius of custom structures in chunks. Custom structures are spawned by",
                "the CustomStructure resource in the biome configuration files. Not used for BO4's."
        );

        writer.putSetting(PresetStandardValues.DECORATION_BOUNDS_CHECK, presetConfig.getCustomStructureSettings().isDecorationBoundsCheck(),
                "Set this to false to disable the bounds check during chunk decoration.",
                "While this allows you to spawn objects larger than 32x32, it also makes terrain generation dependent on the direction you explored the world in."
        );

        writer.header1("Carvers: Caves and Ravines");

        writer.putSetting(PresetStandardValues.CARVER_LAVA_BLOCK, presetConfig.getBlockSettings().getCarverLavaBlock(),
                "Block that replaces all air blocks from Y0 up to CarverLavaBlockHeight.",
                "For example, vanilla replaces air in caves with lava up to Y10.",
                "Defaults to: LAVA"
        );

        writer.putSetting(PresetStandardValues.CARVER_LAVA_BLOCK_HEIGHT, presetConfig.getTerrainSettings().getCarverLavaBlockHeight(),
                "All air blocks are replaced to CarverLavaBlock from Y0 up to CarverLavaBlockHeight.",
                "For example, vanilla replaces air in caves with lava up to Y10.",
                "Defaults to: 10"
        );

        writer.header2("Caves");

        writer.putSetting(PresetStandardValues.CAVES_ENABLED, presetConfig.getCarverSettings().isCavesEnabled(),
                "Enables/disables OTG caves. OTG should automatically disable caves/carvers for biomes when modded carvers are detected."
        );

        writer.putSetting(PresetStandardValues.CAVE_RARITY, presetConfig.getCarverSettings().getCaveRarity(),
                "This controls the odds that a given chunk will host a single cave and/or the start of a cave system."
        );

        writer.putSetting(PresetStandardValues.CAVE_FREQUENCY, presetConfig.getCarverSettings().getCaveFrequency(),
                "The number of times the cave generation algorithm will attempt to create single caves and cave",
                "systems in the given chunk. This value is larger because the likelihood for the cave generation",
                "algorithm to bailout is fairly high and it is used in a randomizer that trends towards lower",
                "random numbers. With an input of 40 (default) the randomizer will result in an average random",
                "result of 5 to 6. This can be turned off by setting evenCaveDistribution (below) to true."
        );

        writer.putSetting(PresetStandardValues.CAVE_MIN_ALTITUDE, presetConfig.getCarverSettings().getCaveMinAltitude(),
                "Sets the minimum and maximum altitudes at which caves will be generated. These values are",
                "used in a randomizer that trends towards lower numbers so that caves become more frequent",
                "the closer you get to the bottom of the map. Setting even cave distribution (above) to true",
                "will turn off this randomizer and use a flat random number generator that will create an even",
                "density of caves at all altitudes."
        );
        writer.putSetting(PresetStandardValues.CAVE_MAX_ALTITUDE, presetConfig.getCarverSettings().getCaveMaxAltitude());

        writer.putSetting(PresetStandardValues.INDIVIDUAL_CAVE_RARITY, presetConfig.getCarverSettings().getIndividualCaveRarity(),
                "The odds that the cave generation algorithm will generate a single cavern without an accompanying",
                "cave system. Note that whenever the algorithm generates an individual cave it will also attempt to",
                "generate a pocket of cave systems in the vicinity (no guarantee of connection or that the cave system",
                "will actually be created)."
        );

        writer.putSetting(PresetStandardValues.CAVE_SYSTEM_FREQUENCY, presetConfig.getCarverSettings().getCaveSystemFrequency(),
                "The number of times the algorithm will attempt to start a cave system in a given chunk per cycle of",
                "the cave generation algorithm (see cave frequency setting above). Note that setting this value too",
                "high with an accompanying high cave frequency value can cause extremely long world generation time."
        );

        writer.putSetting(PresetStandardValues.CAVE_SYSTEM_POCKET_CHANCE, presetConfig.getCarverSettings().getCaveSystemPocketChance(),
                "This can be set to create an additional chance that a cave system pocket (a higher than normal",
                "density of cave systems) being started in a given chunk. Normally, a cave pocket will only be",
                "attempted if an individual cave is generated, but this will allow more cave pockets to be generated",
                "in addition to the individual cave trigger."
        );

        writer.putSetting(PresetStandardValues.CAVE_SYSTEM_POCKET_MIN_SIZE, presetConfig.getCarverSettings().getCaveSystemPocketMinSize(),
                "The minimum and maximum size that a cave system pocket can be. This modifies/overrides the",
                "cave system frequency setting (above) when triggered."
        );
        writer.putSetting(PresetStandardValues.CAVE_SYSTEM_POCKET_MAX_SIZE, presetConfig.getCarverSettings().getCaveSystemPocketMaxSize());

        writer.putSetting(PresetStandardValues.EVEN_CAVE_DISTRIBUTION, presetConfig.getCarverSettings().isEvenCaveDistribution(),
                "Setting this to true will turn off the randomizer for cave frequency (above). Do note that",
                "if you turn this on you will probably want to adjust the cave frequency down to avoid long",
                "load times at world creation."
        );

        writer.header2("Ravines");

        writer.putSetting(PresetStandardValues.RAVINES_ENABLED, presetConfig.getCarverSettings().isRavinesEnabled(),
                "Enables/disables OTG ravines. OTG should automatically disable ravines/carvers for biomes when modded carvers are detected."
        );

        writer.putSetting(PresetStandardValues.RAVINE_RARITY, presetConfig.getCarverSettings().getRavineRarity());
        writer.putSetting(PresetStandardValues.RAVINE_MIN_ALTITUDE, presetConfig.getCarverSettings().getRavineMinAltitude());
        writer.putSetting(PresetStandardValues.RAVINE_MAX_ALTITUDE, presetConfig.getCarverSettings().getRavineMaxAltitude());
        writer.putSetting(PresetStandardValues.RAVINE_MIN_LENGTH, presetConfig.getCarverSettings().getRavineMinLength());
        writer.putSetting(PresetStandardValues.RAVINE_MAX_LENGTH, presetConfig.getCarverSettings().getRavineMaxLength());
        writer.putSetting(PresetStandardValues.RAVINE_DEPTH, presetConfig.getCarverSettings().getRavineDepth());

        writer.header1("Spawn point settings");

        writer.putSetting(PresetStandardValues.FIXED_SPAWN_POINT, presetConfig.getSpawnSettings().isSpawnPointSet(),
                "Set this to true to enable SpawnPointX/SpawnPointY/SpawnPointZ/SpawnPointAngle."
        );
        writer.putSetting(PresetStandardValues.SPAWN_POINT_X, presetConfig.getSpawnSettings().getSpawnPointX(),
                "When FixedSpawnPoint: true, this sets the world's spawn point."
        );
        writer.putSetting(PresetStandardValues.SPAWN_POINT_Y, presetConfig.getSpawnSettings().getSpawnPointY(),
                "When FixedSpawnPoint: true, this sets the world's spawn point."
        );
        writer.putSetting(PresetStandardValues.SPAWN_POINT_Z, presetConfig.getSpawnSettings().getSpawnPointZ(),
                "When FixedSpawnPoint: true, this sets the world's spawn point."
        );
        writer.putSetting(PresetStandardValues.SPAWN_POINT_ANGLE, presetConfig.getSpawnSettings().getSpawnPointAngle(),
                "When FixedSpawnPoint: true, this sets the angle the player is looking when spawned at the spawn point."
        );

        writer.header2("Portal settings (Forge)");

        writer.putSetting(PresetStandardValues.PORTAL_BLOCKS, presetConfig.getPortalSettings().getPortalBlocks(),
                "A list of one or more portal blocks used to build a portal to this dimension, or back to the overworld.",
                "Only applies for dimensions, not overworld/nether/end."
        );
        writer.putSetting(PresetStandardValues.PORTAL_COLOR, presetConfig.getPortalSettings().getPortalColor(),
                "The portal color used for this world's portals, only applies for dimensions, not overworld/nether/end.",
                "Options: beige, black, blue, crystalblue, darkblue, darkgreen, darkred, emerald, flame, gold,",
                "green, grey, lightblue, lightgreen, orange, pink, red, white, yellow, default."
        );
        writer.putSetting(PresetStandardValues.PORTAL_MOB, presetConfig.getPortalSettings().getPortalMob(),
                "The mob that spawns from this portal, minecraft:zombified_piglin by default.",
                "Only applies for dimensions, not overworld/nether/end."
        );
        writer.putSetting(PresetStandardValues.PORTAL_IGNITION_SOURCE, presetConfig.getPortalSettings().getPortalIgnitionSource(),
                "The ignition source for this portal, minecraft:flint_and_steel by default.",
                "Only applies for dimensions, not overworld/nether/end."
        );

        writer.header1("Dimension settings (Forge)",
                "Note: At world creation, these settings are written to the world save's datapack folder (\\saves\\WorldName\\datapacks\\otg\\)",
                "as dimension_type json file. The json file is used by MC on world load to fetch the settings. If you want to change dimension",
                "settings for already created worlds make sure to edit the dimension_type json file, since changes to the PresetConfig dimension",
                "settings won't be picked up on world load, only on world creation."
        );

        writer.putSetting(PresetStandardValues.FIXED_TIME, presetConfig.getDimensionSettings().getFixedTime().orElse(PresetStandardValues.FIXED_TIME.getDefaultValue()),
                "The time this dimension is fixed at, from 0 to 24000.",
                "-1 by default, meaning disabled, so time passes normally.",
                "Vanilla Nether uses 18000, End uses 6000."
        );
        writer.putSetting(PresetStandardValues.HAS_SKYLIGHT, presetConfig.getDimensionSettings().isHasSkyLight(),
                "Whether this dimension uses a skylight, defaults to true.",
                "Vanilla nether and end use false, nether combines this with AmbientLight:0.1."
        );
        writer.putSetting(PresetStandardValues.HAS_CEILING, presetConfig.getDimensionSettings().isHasCeiling(),
                "Whether this dimension has a ceiling, affects mob spawning, weather (thunder), maps.",
                "Defaults to false, vanilla nether uses true."
        );
        writer.putSetting(PresetStandardValues.ULTRA_WARM, presetConfig.getDimensionSettings().isUltraWarm(),
                "Whether water evaporates in this dimension. Also appears to affect lava/lava flow.",
                "Defaults to false. Vanilla nether uses true."
        );
        writer.putSetting(PresetStandardValues.NATURAL, presetConfig.getDimensionSettings().isNatural(),
                "When set to false, mobs do not spawn from portals and players cannot use beds in this dimension.",
                "Defaults to true."
        );
        writer.putSetting(PresetStandardValues.COORDINATE_SCALE, presetConfig.getDimensionSettings().getCoordinateScale(),
                "The amount of blocks traveled compared to other dimensions.",
                "1 by default, same as vanilla overworld, nether uses 8."
        );
        writer.putSetting(PresetStandardValues.CREATE_DRAGON_FLIGHT, presetConfig.getDimensionSettings().isCreateDragonFight(),
                "Probably starts a dragon fight, we think. Try it, what could possibly go wrong?"
        );
        writer.putSetting(PresetStandardValues.PIGLIN_SAFE, presetConfig.getDimensionSettings().isPiglinSafe(),
                "Whether this dimension can spawn piglins, false by default."
        );
        writer.putSetting(PresetStandardValues.BED_WORKS, presetConfig.getDimensionSettings().isBedWorks(),
                "Whether beds can be used to sleep and skip time in this dimension, true by default.");
        writer.putSetting(PresetStandardValues.RESPAWN_ANCHOR_WORKS, presetConfig.getDimensionSettings().isRespawnAnchorWorks(),
                "Whether RespawnAnchorBlocks can be used, false by default."
        );
        writer.putSetting(PresetStandardValues.HAS_RAIDS, presetConfig.getDimensionSettings().isHasRaids(),
                "Whether the dimension has raids, true by default."
        );
        writer.putSetting(PresetStandardValues.LOGICAL_HEIGHT, presetConfig.getDimensionSettings().getLogicalHeight(),
                "World height, 256 by default. Affects portals and chorus fruits."
        );
        writer.putSetting(PresetStandardValues.INFINIBURN, presetConfig.getDimensionSettings().getInfiniburn(),
                "Infiniburn block tag registry key, minecraft:infiniburn_overworld by default.",
                "Can be either overworld/nether/end (or potentially modded)."
        );
        writer.putSetting(PresetStandardValues.EFFECTS_LOCATION, presetConfig.getDimensionSettings().getEffectsLocation(),
                "Effects registry key, minecraft:overworld by default.",
                "Can be either overworld/nether/end (or potentially modded)."
        );
        writer.putSetting(PresetStandardValues.AMBIENT_LIGHT, presetConfig.getDimensionSettings().getAmbientLight(),
                "The base ambient light level for the world, 0.0 for overworld/end, 0.1 for nether."
        );

        writer.header1("Game rules (Forge)",
                "See: https://minecraft.fandom.com/wiki/Game_rule",
                "Since game rules are shared across all dimensions, these settings only apply if this preset is used as the overworld.",
                "These settings can be overridden via a DimensionConfig with a GameRules entry."
        );

        writer.putSetting(PresetStandardValues.OVERRIDE_GAME_RULES, presetConfig.getGameRuleSettings().isOverrideGameRules(),
                "Set this to true to enable the settings below."
        );
        var gameRuleSettings = presetConfig.getGameRuleSettings();
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
