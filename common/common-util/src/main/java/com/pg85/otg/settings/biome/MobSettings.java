package com.pg85.otg.settings.biome;

import com.pg85.otg.util.biome.WeightedMobSpawnGroup;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class MobSettings {
    private final List<WeightedMobSpawnGroup> monsters;
    private final List<WeightedMobSpawnGroup> creatures;
    private final List<WeightedMobSpawnGroup> waterCreatures;
    private final List<WeightedMobSpawnGroup> ambientCreatures;
    private final List<WeightedMobSpawnGroup> waterAmbientCreatures;
    private final List<WeightedMobSpawnGroup> miscCreatures;
    private final String inheritMobsBiomeName;
}
