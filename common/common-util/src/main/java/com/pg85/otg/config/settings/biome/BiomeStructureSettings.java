package com.pg85.otg.config.settings.biome;

import com.pg85.otg.config.settings.preset.StructureSettings;
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
}
