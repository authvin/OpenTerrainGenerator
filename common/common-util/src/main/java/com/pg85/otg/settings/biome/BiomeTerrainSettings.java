package com.pg85.otg.settings.biome;

import com.pg85.otg.settings.preset.TerrainSettings;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BiomeTerrainSettings {
    private final TerrainSettings parent;
    private final float biomeHeight;
    private final float biomeVolatility;
    private final int smoothRadius;
    private final int CHCSmoothRadius;
    private final int maxAverageHeight;
    private final int maxAverageDepth;
    private final float volatility1;
    private final float volatility2;
    private final float volatilityWeight1;
    private final float volatilityWeight2;
    private final boolean disableBiomeHeight;

}