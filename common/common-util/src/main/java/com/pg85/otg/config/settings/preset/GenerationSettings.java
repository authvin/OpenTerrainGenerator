package com.pg85.otg.config.settings.preset;

import com.pg85.otg.config.ConfigFile;
import com.pg85.otg.config.ConfigFunction;
import com.pg85.otg.config.biome.BiomeGroupFunction;
import com.pg85.otg.config.biome.BiomeGroupManager;
import com.pg85.otg.config.biome.TemplateBiome;
import com.pg85.otg.config.io.IConfigFunctionProvider;
import com.pg85.otg.config.io.SettingsMap;
import com.pg85.otg.config.standard.PresetStandardValues;
import com.pg85.otg.constants.settings.BiomeMode;
import com.pg85.otg.interfaces.IMaterialReader;
import com.pg85.otg.util.OTGLog;
import com.pg85.otg.util.logging.LogCategory;
import com.pg85.otg.util.logging.LogLevel;
import lombok.Builder;
import lombok.Getter;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
public class GenerationSettings {
    private final ArrayList<String> worldBiomes;
    private final List<String> blackListedBiomes;
    private final int biomeRarityScale;
    private final boolean oldGroupRarity;
    private final int generationDepth;
    private final int landFuzzy;
    private final int landRarity;
    private final int landSize;
    private final int oceanBiomeSize;
    private final String defaultOceanBiome;
    private final String defaultWarmOceanBiome;
    private final String defaultLukewarmOceanBiome;
    private final String defaultColdOceanBiome;
    private final String defaultFrozenOceanBiome;
    private final BiomeMode biomeMode;
    private final double frozenOceanTemperature;
    private final boolean frozenOcean;
    private final List<String> isleBiomes;
    private final List<String> borderBiomes;
    private final boolean randomRivers;
    private final int riverRarity;
    private final int riverSize;
    private final boolean riversEnabled;
    private final boolean oldLandRarity;
    private final boolean forceLandAtSpawn;
    private final BiomeGroupManager biomeGroupManager;
    private final List<TemplateBiome> templateBiomes;

    public static GenerationSettings getBiomeSettings(PresetSettings presetConfig, SettingsMap reader, IConfigFunctionProvider biomeResourcesManager, List<String> biomes, IMaterialReader materialReader, Path settingsDir) {
        var biomeSettingsBuilder = builder();

        biomeSettingsBuilder.biomeRarityScale(reader.getSetting(PresetStandardValues.BIOME_RARITY_SCALE));
        biomeSettingsBuilder.generationDepth(reader.getSetting(PresetStandardValues.GENERATION_DEPTH));
        biomeSettingsBuilder.oldGroupRarity(reader.getSetting(PresetStandardValues.OLD_GROUP_RARITY));
        biomeSettingsBuilder.oldLandRarity(reader.getSetting(PresetStandardValues.OLD_LAND_RARITY));
        biomeSettingsBuilder.landFuzzy(reader.getSetting(PresetStandardValues.LAND_FUZZY));
        biomeSettingsBuilder.landRarity(reader.getSetting(PresetStandardValues.LAND_RARITY));
        biomeSettingsBuilder.landSize(reader.getSetting(PresetStandardValues.LAND_SIZE));
        biomeSettingsBuilder.forceLandAtSpawn(reader.getSetting(PresetStandardValues.FORCE_LAND_AT_SPAWN));
        biomeSettingsBuilder.oceanBiomeSize(reader.getSetting(PresetStandardValues.OCEAN_BIOME_SIZE));
        biomeSettingsBuilder.defaultOceanBiome(reader.getSetting(PresetStandardValues.DEFAULT_OCEAN_BIOME));
        biomeSettingsBuilder.defaultWarmOceanBiome(reader.getSetting(PresetStandardValues.DEFAULT_WARM_OCEAN_BIOME));
        biomeSettingsBuilder.defaultLukewarmOceanBiome(reader.getSetting(PresetStandardValues.DEFAULT_LUKEWARM_OCEAN_BIOME));
        biomeSettingsBuilder.defaultColdOceanBiome(reader.getSetting(PresetStandardValues.DEFAULT_COLD_OCEAN_BIOME));
        biomeSettingsBuilder.defaultFrozenOceanBiome(reader.getSetting(PresetStandardValues.DEFAULT_FROZEN_OCEAN_BIOME));
        biomeSettingsBuilder.frozenOcean(reader.getSetting(PresetStandardValues.FROZEN_OCEAN));
        biomeSettingsBuilder.frozenOceanTemperature(reader.getSetting(PresetStandardValues.FROZEN_OCEAN_TEMPERATURE));
        biomeSettingsBuilder.randomRivers(reader.getSetting(PresetStandardValues.RANDOM_RIVERS));
        biomeSettingsBuilder.riverRarity(reader.getSetting(PresetStandardValues.RIVER_RARITY));
        biomeSettingsBuilder.riverSize(reader.getSetting(PresetStandardValues.RIVER_SIZE));
        biomeSettingsBuilder.riversEnabled(reader.getSetting(PresetStandardValues.RIVERS_ENABLED));

        BiomeMode biomeMode = reader.getSetting(PresetStandardValues.BIOME_MODE);
        if (biomeMode == BiomeMode.FromImage) {
            File mapFile = new File(settingsDir.toString(), reader.getSetting(PresetStandardValues.IMAGE_FILE));
            if (!mapFile.exists()) {
                OTGLog.getLogger().log(LogLevel.ERROR, LogCategory.MAIN, "Biome map file not found. Switching BiomeMode to Normal");
                biomeMode = BiomeMode.Normal;
            }
        }
        biomeSettingsBuilder.biomeMode(biomeMode);

        // BiomeGroups requires that values like genDepth are initialized
        biomeSettingsBuilder.templateBiomes(readTemplateBiomes(reader, presetConfig, biomeResourcesManager, materialReader));
        biomeSettingsBuilder.biomeGroupManager(readBiomeGroups(reader, presetConfig, biomeResourcesManager, materialReader));

        presetConfig.getGenerationSettings().getBiomeGroupManager().filterBiomes(biomes);

        biomeSettingsBuilder.isleBiomes(ConfigFile.filterBiomes(reader.getSetting(PresetStandardValues.ISLE_BIOMES), biomes));
        biomeSettingsBuilder.borderBiomes(ConfigFile.filterBiomes(reader.getSetting(PresetStandardValues.BORDER_BIOMES), biomes));
        biomeSettingsBuilder.blackListedBiomes(reader.getSetting(PresetStandardValues.BLACKLISTED_BIOMES));
        return biomeSettingsBuilder.fixSettings().build();
    }

    private static ArrayList<TemplateBiome> readTemplateBiomes(SettingsMap reader, PresetSettings presetSettings, IConfigFunctionProvider biomeResourcesManager, IMaterialReader materialReader) {
        var templateBiomes = new ArrayList<TemplateBiome>();
        for (ConfigFunction<PresetSettings> res : reader.getConfigFunctions(presetSettings, biomeResourcesManager, materialReader)) {
            if (res != null) {
                if (res instanceof TemplateBiome tb) {
                    templateBiomes.add(tb);
                }
            }
        }
        return templateBiomes;
    }

    private static BiomeGroupManager readBiomeGroups(SettingsMap reader, PresetSettings presetSettings, IConfigFunctionProvider biomeResourcesManager, IMaterialReader materialReader) {
        var biomeGroupManager = new BiomeGroupManager();
        for (ConfigFunction<PresetSettings> res : reader.getConfigFunctions(presetSettings, biomeResourcesManager, materialReader)) {
            if (res != null) {
                if (res instanceof BiomeGroupFunction) {
                    biomeGroupManager.registerGroup((BiomeGroupFunction) res);
                }
            }
        }
        return biomeGroupManager;
    }

    public static class GenerationSettingsBuilder {
        public GenerationSettingsBuilder fixSettings() {
            checkLandSize();
            checkLandFuzzy();
            checkRiverRarity();
            checkRiverSize();
            return this;
        }
        private void checkLandSize() {
            landSize = Math.min(landSize, generationDepth);
        }
        private void checkLandFuzzy() {
            landFuzzy = Math.min(landFuzzy, generationDepth - landSize);
        }
        private void checkRiverRarity() {
            riverRarity = Math.min(riverRarity, generationDepth);
        }
        private void checkRiverSize() {
            riverSize = Math.min(riverSize, generationDepth - riverRarity);
        }
    }
}