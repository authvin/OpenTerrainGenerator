package com.pg85.otg.settings.preset;

import com.pg85.otg.util.materials.LocalMaterialData;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;

@Builder
@Getter
public class PortalSettings {
    private final ArrayList<LocalMaterialData> portalBlocks;
    private final String portalColor;
    private final String portalMob;
    private final String portalIgnitionSource;
}