package com.pg85.otg.settings.preset;

import com.pg85.otg.constants.settings.structure.CustomStructureType;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CustomStructureSettings {
    private final String BO3AtSpawn;
    private final CustomStructureType customStructureType;
    private final boolean useOldBO3StructureRarity;
    private final boolean decorationBoundsCheck;
    private final int maximumCustomStructureRadius;
}