package com.pg85.otg.settings.preset;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CarverSettings {
    private final boolean cavesEnabled;
    private final int caveFrequency;
    private final int caveRarity;
    private final boolean evenCaveDistribution;
    private final int caveMinAltitude;
    private final int caveMaxAltitude;
    private final int caveSystemFrequency;
    private final int individualCaveRarity;
    private final int caveSystemPocketMinSize;
    private final int caveSystemPocketChance;
    private final int caveSystemPocketMaxSize;
    private final boolean ravinesEnabled;
    private final int ravineRarity;
    private final int ravineMinLength;
    private final int ravineMaxLength;
    private final double ravineDepth;
    private final int ravineMinAltitude;
    private final int ravineMaxAltitude;
}