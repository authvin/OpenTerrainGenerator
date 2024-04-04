package com.pg85.otg.settings.biome;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class PlacementSettings {
    private final int biomeSize;
    private final int biomeRarity;
    private final int biomeColor;
    private final boolean isleBiome;
    private final List<String> isleInBiomes;
    private final int biomeSizeWhenIsle;
    private final int biomeRarityWhenIsle;
    private final boolean borderBiome;
    private final List<String> borderInBiomes;
    private final List<String> onlyBorderNearBiomes;
    private final List<String> notBorderNearBiomes;
    private final int biomeSizeWhenBorder;
}
