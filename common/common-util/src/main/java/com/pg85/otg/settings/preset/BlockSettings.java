package com.pg85.otg.settings.preset;

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
}