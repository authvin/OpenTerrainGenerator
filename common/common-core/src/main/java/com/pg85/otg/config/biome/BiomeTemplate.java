package com.pg85.otg.config.biome;

import com.pg85.otg.config.io.IConfigFunctionProvider;
import com.pg85.otg.config.io.SettingsMap;
import com.pg85.otg.config.standard.BiomeStandardValues;
import com.pg85.otg.constants.Constants;
import com.pg85.otg.constants.settings.TemplateBiomeType;
import com.pg85.otg.gen.surface.SimpleSurfaceGenerator;
import com.pg85.otg.gen.surface.SurfaceGeneratorSetting;
import com.pg85.otg.interfaces.ILogger;
import com.pg85.otg.interfaces.IMaterialReader;
import com.pg85.otg.util.materials.LocalMaterials;

public class BiomeTemplate extends BiomeConfig {
    public BiomeTemplate(String biomeName) {
        super(biomeName);
    }

    @Override
    protected void readConfigSettings(SettingsMap reader, IConfigFunctionProvider biomeResourcesManager, ILogger logger, IMaterialReader materialReader, String presetFolderName) {
        this.settings.templateBiomeType = reader.getSetting(BiomeStandardValues.TEMPLATE_BIOME_TYPE, logger);
        this.settings.biomeCategory = BiomeStandardValues.BIOME_CATEGORY.getDefaultValue();
        this.settings.biomeTemperature = BiomeStandardValues.BIOME_TEMPERATURE.getDefaultValue();
        this.settings.biomeWetness = BiomeStandardValues.BIOME_WETNESS.getDefaultValue();
        if(this.settings.templateBiomeType == TemplateBiomeType.Nether)
        {
            this.settings.stoneBlock = LocalMaterials.NETHERRACK;
            this.settings.surfaceBlock = LocalMaterials.NETHERRACK;
            this.settings.groundBlock = LocalMaterials.NETHERRACK;
            this.settings.underWaterSurfaceBlock = LocalMaterials.NETHERRACK;
        }
        else if(this.settings.templateBiomeType == TemplateBiomeType.End)
        {
            this.settings.stoneBlock = LocalMaterials.END_STONE;
            this.settings.surfaceBlock = LocalMaterials.END_STONE;
            this.settings.groundBlock = LocalMaterials.END_STONE;
            this.settings.underWaterSurfaceBlock = LocalMaterials.END_STONE;
        } else {
            this.settings.stoneBlock = LocalMaterials.STONE;
            this.settings.surfaceBlock = LocalMaterials.STONE;
            this.settings.groundBlock = LocalMaterials.STONE;
            this.settings.underWaterSurfaceBlock = LocalMaterials.STONE;
        }
        this.settings.skyColor = BiomeStandardValues.SKY_COLOR.getDefaultValue();
        this.settings.waterColor = BiomeStandardValues.WATER_COLOR.getDefaultValue();
        this.settings.waterColorControl = BiomeStandardValues.WATER_COLOR_CONTROL.getDefaultValue();
        this.settings.grassColor = BiomeStandardValues.GRASS_COLOR.getDefaultValue();
        this.settings.grassColorControl = BiomeStandardValues.GRASS_COLOR_CONTROL.getDefaultValue();
        this.settings.grassColorModifier = BiomeStandardValues.GRASS_COLOR_MODIFIER.getDefaultValue();
        this.settings.foliageColor = BiomeStandardValues.FOLIAGE_COLOR.getDefaultValue();
        this.settings.foliageColorControl = BiomeStandardValues.FOLIAGE_COLOR_CONTROL.getDefaultValue();
        this.settings.fogColor = BiomeStandardValues.FOG_COLOR.getDefaultValue();
        this.settings.fogDensity = BiomeStandardValues.FOG_DENSITY.getDefaultValue();
        this.settings.waterFogColor = BiomeStandardValues.WATER_FOG_COLOR.getDefaultValue();
        this.settings.particleType = BiomeStandardValues.PARTICLE_TYPE.getDefaultValue();
        this.settings.music = BiomeStandardValues.MUSIC.getDefaultValue();
        this.settings.musicMinDelay = BiomeStandardValues.MUSIC_MIN_DELAY.getDefaultValue();
        this.settings.musicMaxDelay = BiomeStandardValues.MUSIC_MAX_DELAY.getDefaultValue();
        this.settings.replaceCurrentMusic = BiomeStandardValues.REPLACE_CURRENT_MUSIC.getDefaultValue();
        this.settings.ambientSound = BiomeStandardValues.AMBIENT_SOUND.getDefaultValue();
        this.settings.moodSound = BiomeStandardValues.MOOD_SOUND.getDefaultValue();
        this.settings.moodSoundDelay = BiomeStandardValues.MOOD_SOUND_DELAY.getDefaultValue();
        this.settings.moodSearchRange = BiomeStandardValues.MOOD_SEARCH_RANGE.getDefaultValue();
        this.settings.moodOffset = BiomeStandardValues.MOOD_OFFSET.getDefaultValue();
        this.settings.additionsSound = BiomeStandardValues.ADDITIONS_SOUND.getDefaultValue();
        this.settings.additionsTickChance = BiomeStandardValues.ADDITIONS_TICK_CHANCE.getDefaultValue();
        this.settings.particleProbability = BiomeStandardValues.PARTICLE_PROBABILITY.getDefaultValue();
        this.settings.strongholdsEnabled = BiomeStandardValues.STRONGHOLDS_ENABLED.getDefaultValue();
        this.settings.oceanMonumentsEnabled = BiomeStandardValues.OCEAN_MONUMENTS_ENABLED.getDefaultValue();
        this.settings.woodLandMansionsEnabled = BiomeStandardValues.WOODLAND_MANSIONS_ENABLED.getDefaultValue();
        this.settings.netherFortressesEnabled = BiomeStandardValues.NETHER_FORTRESSES_ENABLED.getDefaultValue();
        this.settings.villageType = BiomeStandardValues.VILLAGE_TYPE.getDefaultValue();
        this.settings.villageSize = BiomeStandardValues.VILLAGE_SIZE.getDefaultValue();
        this.settings.mineshaftType = BiomeStandardValues.MINESHAFT_TYPE.getDefaultValue();
        this.settings.rareBuildingType = BiomeStandardValues.RARE_BUILDING_TYPE.getDefaultValue();
        this.settings.buriedTreasureEnabled = BiomeStandardValues.BURIED_TREASURE_ENABLED.getDefaultValue();
        this.settings.shipWreckEnabled = BiomeStandardValues.SHIP_WRECK_ENABLED.getDefaultValue();
        this.settings.shipWreckBeachedEnabled = BiomeStandardValues.SHIP_WRECK_BEACHED_ENABLED.getDefaultValue();
        this.settings.pillagerOutpostEnabled = BiomeStandardValues.PILLAGER_OUTPOST_ENABLED.getDefaultValue();
        this.settings.bastionRemnantEnabled = BiomeStandardValues.BASTION_REMNANT_ENABLED.getDefaultValue();
        this.settings.netherFossilEnabled = BiomeStandardValues.NETHER_FOSSIL_ENABLED.getDefaultValue();
        this.settings.endCityEnabled = BiomeStandardValues.END_CITY_ENABLED.getDefaultValue();
        this.settings.mineshaftProbability = BiomeStandardValues.MINESHAFT_PROBABILITY.getDefaultValue();
        this.settings.ruinedPortalType = BiomeStandardValues.RUINED_PORTAL_TYPE.getDefaultValue();
        this.settings.oceanRuinsType = BiomeStandardValues.OCEAN_RUINS_TYPE.getDefaultValue();
        this.settings.oceanRuinsLargeProbability = BiomeStandardValues.OCEAN_RUINS_LARGE_PROBABILITY.getDefaultValue();
        this.settings.oceanRuinsClusterProbability = BiomeStandardValues.OCEAN_RUINS_CLUSTER_PROBABILITY.getDefaultValue();
        this.settings.buriedTreasureProbability = BiomeStandardValues.BURIED_TREASURE_PROBABILITY.getDefaultValue();
        this.settings.pillagerOutpostSize = BiomeStandardValues.PILLAGER_OUTPOST_SIZE.getDefaultValue();
        this.settings.bastionRemnantSize = BiomeStandardValues.BASTION_REMNANT_SIZE.getDefaultValue();
        this.settings.biomeDictTags = BiomeStandardValues.BIOME_DICT_TAGS.getDefaultValue();
        this.settings.inheritMobsBiomeName = BiomeStandardValues.INHERIT_MOBS_BIOME_NAME.getDefaultValue();
        this.settings.useFrozenOceanTemperature = BiomeStandardValues.USE_FROZEN_OCEAN_TEMPERATURE.getDefaultValue();

        this.settings.biomeSize = reader.getSetting(BiomeStandardValues.BIOME_SIZE, logger);
        this.settings.biomeRarity = reader.getSetting(BiomeStandardValues.BIOME_RARITY, logger);
        this.settings.biomeRarityWhenIsle = reader.getSetting(BiomeStandardValues.BIOME_RARITY_WHEN_ISLE, logger);
        this.settings.biomeColor = reader.getSetting(BiomeStandardValues.BIOME_COLOR, logger);
        this.settings.riverBiome = reader.getSetting(BiomeStandardValues.RIVER_BIOME, logger);
        this.settings.isleInBiome = reader.getSetting(BiomeStandardValues.ISLE_IN_BIOME, logger);
        this.settings.biomeSizeWhenIsle = reader.getSetting(BiomeStandardValues.BIOME_SIZE_WHEN_ISLE, logger);
        this.settings.biomeIsBorder = reader.getSetting(BiomeStandardValues.BIOME_IS_BORDER, logger);
        this.settings.onlyBorderNear = reader.getSetting(BiomeStandardValues.ONLY_BORDER_NEAR, logger);
        this.settings.notBorderNear = reader.getSetting(BiomeStandardValues.NOT_BORDER_NEAR, logger);
        this.settings.biomeSizeWhenBorder = reader.getSetting(BiomeStandardValues.BIOME_SIZE_WHEN_BORDER, logger);
        this.settings.biomeHeight = reader.getSetting(BiomeStandardValues.BIOME_HEIGHT, logger);
        this.settings.biomeVolatility = reader.getSetting(BiomeStandardValues.BIOME_VOLATILITY, logger);
        this.settings.smoothRadius = reader.getSetting(BiomeStandardValues.SMOOTH_RADIUS, logger);
        this.settings.CHCSmoothRadius = reader.getSetting(BiomeStandardValues.CUSTOM_HEIGHT_CONTROL_SMOOTH_RADIUS, logger);
        this.privateSettings.configWaterBlock = reader.getSetting(BiomeStandardValues.WATER_BLOCK, logger, materialReader);
        this.privateSettings.configIceBlock = reader.getSetting(BiomeStandardValues.ICE_BLOCK, logger, materialReader);
        this.settings.packedIceBlock = reader.getSetting(BiomeStandardValues.PACKED_ICE_BLOCK, logger, materialReader);
        this.settings.snowBlock = reader.getSetting(BiomeStandardValues.SNOW_BLOCK, logger, materialReader);
        this.privateSettings.configCooledLavaBlock = reader.getSetting(BiomeStandardValues.COOLED_LAVA_BLOCK, logger, materialReader);
        this.settings.replacedBlocks = reader.getSetting(BiomeStandardValues.REPLACED_BLOCKS, logger, materialReader);
        this.settings.sandStoneBlock = LocalMaterials.SANDSTONE;
        this.settings.redSandStoneBlock = LocalMaterials.RED_SANDSTONE;
        this.settings.surfaceAndGroundControl = reader.getSetting(SurfaceGeneratorSetting.SURFACE_AND_GROUND_CONTROL, new SimpleSurfaceGenerator(), logger, materialReader);
        this.settings.useWorldWaterLevel = reader.getSetting(BiomeStandardValues.USE_WORLD_WATER_LEVEL, logger);
        this.privateSettings.configWaterLevelMax = reader.getSetting(BiomeStandardValues.WATER_LEVEL_MAX, logger);
        this.privateSettings.configWaterLevelMin = reader.getSetting(BiomeStandardValues.WATER_LEVEL_MIN, logger);
        this.privateSettings.volatilityRaw1 = reader.getSetting(BiomeStandardValues.VOLATILITY_1, logger);
        this.privateSettings.volatilityRaw2 = reader.getSetting(BiomeStandardValues.VOLATILITY_2, logger);
        this.privateSettings.volatilityWeightRaw1 = reader.getSetting(BiomeStandardValues.VOLATILITY_WEIGHT_1, logger);
        this.privateSettings.volatilityWeightRaw2 = reader.getSetting(BiomeStandardValues.VOLATILITY_WEIGHT_2, logger);
        this.settings.disableBiomeHeight = reader.getSetting(BiomeStandardValues.DISABLE_BIOME_HEIGHT, logger);
        this.settings.maxAverageHeight = reader.getSetting(BiomeStandardValues.MAX_AVERAGE_HEIGHT, logger);
        this.settings.maxAverageDepth = reader.getSetting(BiomeStandardValues.MAX_AVERAGE_DEPTH, logger);

        this.readResourceSettings(reader, biomeResourcesManager, logger, materialReader, presetFolderName);

        this.settings.chcData = new double[this.settings.presetConfig.getTerrainSettings().getWorldHeightCap() / Constants.PIECE_Y_SIZE + 1];
        this.readHeightSettings(reader, this.settings.chcData, BiomeStandardValues.CUSTOM_HEIGHT_CONTROL, BiomeStandardValues.CUSTOM_HEIGHT_CONTROL.getDefaultValue(), logger);
    }
}
