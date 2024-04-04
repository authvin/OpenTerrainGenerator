package com.pg85.otg.settings.preset;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
public class TerrainSettings {
    private final double fractureHorizontal;
    private final double fractureVertical;
    private final int worldHeightCap;
    private final int worldHeightScale;
    private final boolean betterSnowFall;
    private final int waterLevelMax;
    private final int waterLevelMin;
    private final int carverLavaBlockHeight;
}