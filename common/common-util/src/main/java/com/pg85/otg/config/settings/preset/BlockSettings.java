package com.pg85.otg.config.settings.preset;

import com.pg85.otg.config.io.SettingsMap;
import com.pg85.otg.config.standard.PresetStandardValues;
import com.pg85.otg.interfaces.IMaterialReader;
import com.pg85.otg.util.materials.LocalMaterialData;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BlockSettings {
    private final boolean removeSurfaceStone;
    private final LocalMaterialData waterBlock;
    private final LocalMaterialData bedrockBlock;
    private final LocalMaterialData defaultBedrockBlock;
    private final LocalMaterialData cooledLavaBlock;
    private final LocalMaterialData iceBlock;
    private final LocalMaterialData carverLavaBlock;

    public static BlockSettings getBlockSettings(SettingsMap reader, IMaterialReader materialReader) {
        var blockSettingsBuilder = builder();

        blockSettingsBuilder.removeSurfaceStone(reader.getSetting(PresetStandardValues.REMOVE_SURFACE_STONE));
        blockSettingsBuilder.waterBlock(reader.getSetting(PresetStandardValues.WATER_BLOCK, materialReader));
        blockSettingsBuilder.bedrockBlock(reader.getSetting(PresetStandardValues.BEDROCK_BLOCK, materialReader));
        blockSettingsBuilder.cooledLavaBlock(reader.getSetting(PresetStandardValues.COOLED_LAVA_BLOCK, materialReader));
        blockSettingsBuilder.iceBlock(reader.getSetting(PresetStandardValues.ICE_BLOCK, materialReader));
        blockSettingsBuilder.carverLavaBlock(reader.getSetting(PresetStandardValues.CARVER_LAVA_BLOCK, materialReader));
        return blockSettingsBuilder.build();
    }
}