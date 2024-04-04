package com.pg85.otg.settings.preset;

import lombok.Builder;
import lombok.Getter;

import java.util.OptionalLong;

@Builder
@Getter
public class DimensionSettings {
    private final OptionalLong fixedTime;
    private final boolean hasSkyLight;
    private final boolean hasCeiling;
    private final boolean ultraWarm;
    private final boolean natural;
    private final double coordinateScale;
    private final boolean createDragonFight;
    private final boolean piglinSafe;
    private final boolean bedWorks;
    private final boolean respawnAnchorWorks;
    private final boolean hasRaids;
    private final int logicalHeight;
    private final String infiniburn;
    private final String effectsLocation;
    private final double ambientLight;
}