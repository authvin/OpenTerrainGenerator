package com.pg85.otg.settings.preset;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SpawnSettings {
    private final boolean spawnPointSet;
    private final int spawnPointX;
    private final int spawnPointY;
    private final int spawnPointZ;
    private final float spawnPointAngle;
}