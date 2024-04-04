package com.pg85.otg.settings.biome;

import com.pg85.otg.settings.preset.BlockSettings;
import com.pg85.otg.util.materials.LocalMaterialData;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BiomeBlockSettings {
    private final BlockSettings parent;
    private final int configWaterLevelMax;
    private final int configWaterLevelMin;
    private final LocalMaterialData configWaterBlock;
    private final LocalMaterialData configIceBlock;
    private final LocalMaterialData configCooledLavaBlock;
    private final double volatilityRaw1;
    private final double volatilityRaw2;
    private final double volatilityWeightRaw1;
    private final double volatilityWeightRaw2;
}
