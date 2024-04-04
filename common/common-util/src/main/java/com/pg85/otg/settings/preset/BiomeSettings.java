package com.pg85.otg.settings.preset;

import com.pg85.otg.constants.settings.BiomeMode;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
public class BiomeSettings {
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
}