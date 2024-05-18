package com.pg85.otg.config.settings.biome;

import com.pg85.otg.config.io.SettingsMap;
import com.pg85.otg.config.settings.preset.StructureSettings;
import com.pg85.otg.config.standard.BiomeStandardValues;
import com.pg85.otg.constants.settings.structure.*;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BiomeStructureSettings {
    private final StructureSettings parent;
    private final boolean strongholdsEnabled;
    private final boolean oceanMonumentsEnabled;
    private final boolean woodlandMansionsEnabled;
    private final boolean netherFortressesEnabled;
    private final VillageType villageType;
    private final int villageSize;
    private final MineshaftType mineshaftType;
    private final RareBuildingType rareBuildingType;
    private final boolean buriedTreasureEnabled;
    private final boolean shipWreckEnabled;
    private final boolean shipWreckBeachedEnabled;
    private final boolean pillagerOutpostEnabled;
    private final boolean bastionRemnantEnabled;
    private final boolean netherFossilEnabled;
    private final boolean endCityEnabled;
    private final float mineshaftProbability;
    private final RuinedPortalType ruinedPortalType;
    private final OceanRuinsType oceanRuinsType;
    private final float oceanRuinsLargeProbability;
    private final float oceanRuinsClusterProbability;
    private final float buriedTreasureProbability;
    private final int pillagerOutpostSize;
    private final int bastionRemnantSize;

    public static BiomeStructureSettings getBiomeStructureSettings(SettingsMap reader, StructureSettings parent) {
        BiomeStructureSettingsBuilder builder = BiomeStructureSettings.builder();

        builder.parent(parent);
        builder.strongholdsEnabled(reader.getSetting(BiomeStandardValues.STRONGHOLDS_ENABLED));
        builder.oceanMonumentsEnabled(reader.getSetting(BiomeStandardValues.OCEAN_MONUMENTS_ENABLED));
        builder.woodlandMansionsEnabled(reader.getSetting(BiomeStandardValues.WOODLAND_MANSIONS_ENABLED));
        builder.netherFortressesEnabled(reader.getSetting(BiomeStandardValues.NETHER_FORTRESSES_ENABLED));
        builder.villageType(reader.getSetting(BiomeStandardValues.VILLAGE_TYPE));
        builder.villageSize(reader.getSetting(BiomeStandardValues.VILLAGE_SIZE));
        builder.mineshaftType(reader.getSetting(BiomeStandardValues.MINESHAFT_TYPE));
        builder.rareBuildingType(reader.getSetting(BiomeStandardValues.RARE_BUILDING_TYPE));
        builder.buriedTreasureEnabled(reader.getSetting(BiomeStandardValues.BURIED_TREASURE_ENABLED));
        builder.shipWreckEnabled(reader.getSetting(BiomeStandardValues.SHIP_WRECK_ENABLED));
        builder.shipWreckBeachedEnabled(reader.getSetting(BiomeStandardValues.SHIP_WRECK_BEACHED_ENABLED));
        builder.pillagerOutpostEnabled(reader.getSetting(BiomeStandardValues.PILLAGER_OUTPOST_ENABLED));
        builder.bastionRemnantEnabled(reader.getSetting(BiomeStandardValues.BASTION_REMNANT_ENABLED));
        builder.netherFossilEnabled(reader.getSetting(BiomeStandardValues.NETHER_FOSSIL_ENABLED));
        builder.endCityEnabled(reader.getSetting(BiomeStandardValues.END_CITY_ENABLED));
        builder.mineshaftProbability(reader.getSetting(BiomeStandardValues.MINESHAFT_PROBABILITY));
        builder.ruinedPortalType(reader.getSetting(BiomeStandardValues.RUINED_PORTAL_TYPE));
        builder.oceanRuinsType(reader.getSetting(BiomeStandardValues.OCEAN_RUINS_TYPE));
        builder.oceanRuinsLargeProbability(reader.getSetting(BiomeStandardValues.OCEAN_RUINS_LARGE_PROBABILITY));
        builder.oceanRuinsClusterProbability(reader.getSetting(BiomeStandardValues.OCEAN_RUINS_CLUSTER_PROBABILITY));
        builder.buriedTreasureProbability(reader.getSetting(BiomeStandardValues.BURIED_TREASURE_PROBABILITY));
        builder.pillagerOutpostSize(reader.getSetting(BiomeStandardValues.PILLAGER_OUTPOST_SIZE));
        builder.bastionRemnantSize(reader.getSetting(BiomeStandardValues.BASTION_REMNANT_SIZE));

        return builder.build();
    }
}
