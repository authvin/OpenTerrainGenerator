package com.pg85.otg.config.settings.preset;

import com.pg85.otg.config.io.SettingsMap;
import com.pg85.otg.config.settingType.MaterialListSetting;
import com.pg85.otg.config.settingType.Setting;
import com.pg85.otg.config.settingType.Settings;
import com.pg85.otg.config.settings.ConfigSection;
import com.pg85.otg.util.materials.LocalMaterialData;
import com.pg85.otg.util.materials.LocalMaterials;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;

@Builder
@Getter
public class PortalSettings extends ConfigSection {
    private final ArrayList<LocalMaterialData> portalBlocks;
    private final String portalColor;
    private final String portalMob;
    private final String portalIgnitionSource;

    public static final Setting<String> PORTAL_COLOR = Settings.stringSetting(
            "PortalColor", "Default",
            t -> ((PortalSettings) t).getPortalColor(),
            "The portal color used for this world's portals, only applies for dimensions, not overworld/nether/end.",
            "Options: beige, black, blue, crystalblue, darkblue, darkgreen, darkred, emerald, flame, gold,",
            "green, grey, lightblue, lightgreen, orange, pink, red, white, yellow, default."
    );
    public static final Setting<String> PORTAL_IGNITION_SOURCE = Settings.stringSetting(
            "PortalIgnitionSource", "minecraft:flint_and_steel",
            t -> ((PortalSettings) t).getPortalIgnitionSource(),
            "The ignition source for this portal, minecraft:flint_and_steel by default.",
            "Only applies for dimensions, not overworld/nether/end."
    );
    public static final Setting<String> PORTAL_MOB = Settings.stringSetting(
            "PortalMob", "minecraft:zombified_piglin",
            t -> ((PortalSettings) t).getPortalMob(),
            "The mob that spawns from this portal, minecraft:zombified_piglin by default.",
            "Only applies for dimensions, not overworld/nether/end."
    );
    public static final Setting<ArrayList<LocalMaterialData>> PORTAL_BLOCKS = new MaterialListSetting(
            "PortalBlocks", new String[] { LocalMaterials.QUARTZ_BLOCK_NAME },
            t -> ((PortalSettings) t).getPortalBlocks(),
            "A list of one or more portal blocks used to build a portal to this dimension, or back to the overworld.",
            "Only applies for dimensions, not overworld/nether/end."
    );

    public static PortalSettings getPortalSettings(SettingsMap reader) {
        var portalSettingsBuilder = builder();

        portalSettingsBuilder.portalBlocks(reader.getSetting(PORTAL_BLOCKS));
        portalSettingsBuilder.portalColor(reader.getSetting(PORTAL_COLOR));
        portalSettingsBuilder.portalMob(reader.getSetting(PORTAL_MOB));
        portalSettingsBuilder.portalIgnitionSource(reader.getSetting(PORTAL_IGNITION_SOURCE));

        return portalSettingsBuilder.build();
    }

    @Override
    public String getSectionName() {
        return "Portal Settings";
    }
}