package com.pg85.otg.config.settings.preset;

import com.pg85.otg.config.io.SettingsMap;
import com.pg85.otg.config.standard.PresetStandardValues;
import com.pg85.otg.interfaces.IMaterialReader;
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

    public static PortalSettings getPortalSettings(SettingsMap reader, IMaterialReader materialReader) {
        var portalSettingsBuilder = builder();

        portalSettingsBuilder.portalBlocks(reader.getSetting(PresetStandardValues.PORTAL_BLOCKS, materialReader));
        portalSettingsBuilder.portalColor(reader.getSetting(PresetStandardValues.PORTAL_COLOR));
        portalSettingsBuilder.portalMob(reader.getSetting(PresetStandardValues.PORTAL_MOB));
        portalSettingsBuilder.portalIgnitionSource(reader.getSetting(PresetStandardValues.PORTAL_IGNITION_SOURCE));

        var portalSettings = portalSettingsBuilder.build();
        return portalSettings;
    }
}