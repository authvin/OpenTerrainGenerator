package com.pg85.otg.config.settings.biome;

import com.pg85.otg.config.annotation.Config;
import com.pg85.otg.config.annotation.IntSetting;
import com.pg85.otg.config.annotation.LongDescription;
import com.pg85.otg.config.annotation.Name;
import com.pg85.otg.config.io.SettingsMap;
import com.pg85.otg.config.settings.ConfigSection;
import com.pg85.otg.config.settings.biome.generated.BiomeStructureTagSettings;
import com.pg85.otg.config.settingtype.Setting;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Builder
@Getter
@Config
@LongDescription({
        "Settings for biome structure tags, used by datagen tag generation.",
        "This is used for spawning vanilla structures in a biome",
        "Will be available at runtime, but can be inaccurate if not updated correctly."
})
public class BiomeStructureTagConfig extends ConfigSection {

    @Override
    public String getSectionName() {
        return "Biome Structure Tag Settings";
    }

    @Override
    public Map<String, Setting<?>> getSettings() {
        return ConfigSection.getSettings(BiomeStructureTagSettings.class);
    }

    boolean ancient_city;
    boolean bastion_remnant;
    boolean buried_treasure;
    boolean desert_pyramid;
    boolean end_city;
    boolean igloo;
    boolean jungle_temple;
    boolean mineshaft;
    boolean mineshaft_mesa;
    boolean nether_fortress;
    boolean nether_fossil;
    boolean ocean_monument;
    boolean ocean_ruin_cold;
    boolean ocean_ruin_warm;
    boolean pillager_outpost;
    boolean ruined_portal_desert;
    boolean ruined_portal_jungle;
    boolean ruined_portal_mountain;
    boolean ruined_portal_nether;
    boolean ruined_portal_ocean;
    boolean ruined_portal_standard;
    boolean ruined_portal_swamp;
    boolean shipwreck;
    boolean shipwreck_beached;
    boolean stronghold;
    boolean swamp_hut;
    boolean trail_ruins;
    boolean trial_chambers;
    boolean village_desert;
    boolean village_plains;
    boolean village_savanna;
    boolean village_snowy;
    boolean village_taiga;
    boolean woodland_mansion;
    boolean woodland_house;
    @IntSetting(
            value=9,
            min=6,
            max=152
    )
    @Name("Steve")
    int village_distance;

    public static BiomeStructureTagConfig getBiomeStructureTagConfig(SettingsMap reader, BiomeStructureSettings legacySettings) {
        BiomeStructureTagConfigBuilder builder = BiomeStructureTagConfig.builder();
        builder.ancient_city(reader.getSetting(BiomeStructureTagSettings.ANCIENT_CITY));
        builder.bastion_remnant(reader.getSetting(BiomeStructureTagSettings.BASTION_REMNANT));
        builder.buried_treasure(reader.getSetting(BiomeStructureTagSettings.BURIED_TREASURE));
        builder.desert_pyramid(reader.getSetting(BiomeStructureTagSettings.DESERT_PYRAMID));
        builder.end_city(reader.getSetting(BiomeStructureTagSettings.END_CITY));
        builder.igloo(reader.getSetting(BiomeStructureTagSettings.IGLOO));
        builder.jungle_temple(reader.getSetting(BiomeStructureTagSettings.JUNGLE_TEMPLE));
        builder.mineshaft(reader.getSetting(BiomeStructureTagSettings.MINESHAFT));
        builder.mineshaft_mesa(reader.getSetting(BiomeStructureTagSettings.MINESHAFT_MESA));
        builder.nether_fortress(reader.getSetting(BiomeStructureTagSettings.NETHER_FORTRESS));
        builder.nether_fossil(reader.getSetting(BiomeStructureTagSettings.NETHER_FOSSIL));
        builder.ocean_monument(reader.getSetting(BiomeStructureTagSettings.OCEAN_MONUMENT));
        builder.ocean_ruin_cold(reader.getSetting(BiomeStructureTagSettings.OCEAN_RUIN_COLD));
        builder.ocean_ruin_warm(reader.getSetting(BiomeStructureTagSettings.OCEAN_RUIN_WARM));
        builder.pillager_outpost(reader.getSetting(BiomeStructureTagSettings.PILLAGER_OUTPOST));
        builder.ruined_portal_desert(reader.getSetting(BiomeStructureTagSettings.RUINED_PORTAL_DESERT));
        builder.ruined_portal_jungle(reader.getSetting(BiomeStructureTagSettings.RUINED_PORTAL_JUNGLE));
        builder.ruined_portal_mountain(reader.getSetting(BiomeStructureTagSettings.RUINED_PORTAL_MOUNTAIN));
        builder.ruined_portal_nether(reader.getSetting(BiomeStructureTagSettings.RUINED_PORTAL_NETHER));
        builder.ruined_portal_ocean(reader.getSetting(BiomeStructureTagSettings.RUINED_PORTAL_OCEAN));
        builder.ruined_portal_standard(reader.getSetting(BiomeStructureTagSettings.RUINED_PORTAL_STANDARD));
        builder.ruined_portal_swamp(reader.getSetting(BiomeStructureTagSettings.RUINED_PORTAL_SWAMP));
        builder.shipwreck(reader.getSetting(BiomeStructureTagSettings.SHIPWRECK));
        builder.shipwreck_beached(reader.getSetting(BiomeStructureTagSettings.SHIPWRECK_BEACHED));
        builder.stronghold(reader.getSetting(BiomeStructureTagSettings.STRONGHOLD));
        builder.swamp_hut(reader.getSetting(BiomeStructureTagSettings.SWAMP_HUT));
        builder.trail_ruins(reader.getSetting(BiomeStructureTagSettings.TRAIL_RUINS));
        builder.trial_chambers(reader.getSetting(BiomeStructureTagSettings.TRIAL_CHAMBERS));
        builder.village_desert(reader.getSetting(BiomeStructureTagSettings.VILLAGE_DESERT));
        builder.village_plains(reader.getSetting(BiomeStructureTagSettings.VILLAGE_PLAINS));
        builder.village_savanna(reader.getSetting(BiomeStructureTagSettings.VILLAGE_SAVANNA));
        builder.village_snowy(reader.getSetting(BiomeStructureTagSettings.VILLAGE_SNOWY));
        builder.village_taiga(reader.getSetting(BiomeStructureTagSettings.VILLAGE_TAIGA));
        builder.woodland_mansion(reader.getSetting(BiomeStructureTagSettings.WOODLAND_MANSION));

        return builder.build();
    }

    public List<Setting<?>> getAlteredSettings() {
        List<Setting<?>> list = new ArrayList<>();

        list.add(BiomeStructureTagSettings.ANCIENT_CITY);
        list.add(BiomeStructureTagSettings.BASTION_REMNANT);
        list.add(BiomeStructureTagSettings.BURIED_TREASURE);
        list.add(BiomeStructureTagSettings.DESERT_PYRAMID);
        list.add(BiomeStructureTagSettings.END_CITY);
        list.add(BiomeStructureTagSettings.IGLOO);
        list.add(BiomeStructureTagSettings.JUNGLE_TEMPLE);
        list.add(BiomeStructureTagSettings.MINESHAFT);
        list.add(BiomeStructureTagSettings.MINESHAFT_MESA);
        list.add(BiomeStructureTagSettings.NETHER_FORTRESS);
        list.add(BiomeStructureTagSettings.NETHER_FOSSIL);
        list.add(BiomeStructureTagSettings.OCEAN_MONUMENT);
        list.add(BiomeStructureTagSettings.OCEAN_RUIN_COLD);
        list.add(BiomeStructureTagSettings.OCEAN_RUIN_WARM);
        list.add(BiomeStructureTagSettings.PILLAGER_OUTPOST);
        list.add(BiomeStructureTagSettings.RUINED_PORTAL_DESERT);
        list.add(BiomeStructureTagSettings.RUINED_PORTAL_JUNGLE);
        list.add(BiomeStructureTagSettings.RUINED_PORTAL_MOUNTAIN);
        list.add(BiomeStructureTagSettings.RUINED_PORTAL_NETHER);
        list.add(BiomeStructureTagSettings.RUINED_PORTAL_OCEAN);
        list.add(BiomeStructureTagSettings.RUINED_PORTAL_STANDARD);
        list.add(BiomeStructureTagSettings.RUINED_PORTAL_SWAMP);
        list.add(BiomeStructureTagSettings.SHIPWRECK);
        list.add(BiomeStructureTagSettings.SHIPWRECK_BEACHED);
        list.add(BiomeStructureTagSettings.STRONGHOLD);
        list.add(BiomeStructureTagSettings.SWAMP_HUT);
        list.add(BiomeStructureTagSettings.TRAIL_RUINS);
        list.add(BiomeStructureTagSettings.TRIAL_CHAMBERS);
        list.add(BiomeStructureTagSettings.VILLAGE_DESERT);
        list.add(BiomeStructureTagSettings.VILLAGE_PLAINS);
        list.add(BiomeStructureTagSettings.VILLAGE_SAVANNA);
        list.add(BiomeStructureTagSettings.VILLAGE_SNOWY);
        list.add(BiomeStructureTagSettings.VILLAGE_TAIGA);
        list.add(BiomeStructureTagSettings.WOODLAND_MANSION);

        return list.stream()
                .filter(setting -> setting.getGetter().apply(this) != setting.getDefaultValue())
                .toList();
    }
}
