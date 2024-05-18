package com.pg85.otg.config.settings.biome;

import com.pg85.otg.config.io.SettingsMap;
import com.pg85.otg.config.standard.BiomeStandardValues;
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

    public static MobSettings getMobSettings(SettingsMap reader) {
        MobSettingsBuilder builder = MobSettings.builder();

        builder.monsters(reader.getSetting(BiomeStandardValues.SPAWN_MONSTERS));
        builder.creatures(reader.getSetting(BiomeStandardValues.SPAWN_CREATURES));
        builder.waterCreatures(reader.getSetting(BiomeStandardValues.SPAWN_WATER_CREATURES));
        builder.ambientCreatures(reader.getSetting(BiomeStandardValues.SPAWN_AMBIENT_CREATURES));
        builder.waterAmbientCreatures(reader.getSetting(BiomeStandardValues.SPAWN_WATER_AMBIENT_CREATURES));
        builder.miscCreatures(reader.getSetting(BiomeStandardValues.SPAWN_MISC_CREATURES));
        builder.inheritMobsBiomeName(reader.getSetting(BiomeStandardValues.INHERIT_MOBS_BIOME_NAME));

        return builder.build();
    }
}
