package com.pg85.otg.settings.preset;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TerrainSettings {
    private final double fractureHorizontal;
    private final double fractureVertical;
    private final int worldHeightCap;
    private final int worldHeightScale;
    private final int maxSmoothRadius;
    private final boolean betterSnowFall;
    private final int waterLevelMax;
    private final int waterLevelMin;
    private final int carverLavaBlockHeight;
}