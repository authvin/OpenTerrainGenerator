package com.pg85.otg.config.settings.biome;

import com.pg85.otg.config.annotation.Description;
import com.pg85.otg.config.annotation.Config;
import com.pg85.otg.config.annotation.EnumSetting;
import com.pg85.otg.config.annotation.LongDescription;
import com.pg85.otg.config.io.SettingsMap;
import com.pg85.otg.config.settings.ConfigSection;
import com.pg85.otg.config.settings.biome.generated.BiomeTagSettings;
import com.pg85.otg.config.settingtype.Setting;
import com.pg85.otg.util.minecraft.WoodType;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Config
@LongDescription({"Settings for biome tags, used by datagen tag generation.",
        "Will be available at runtime, but can be inaccurate if not set correctly."})
public class BiomeTagConfig extends ConfigSection {

    // world type
    @Description("Set to true if other mods should treat this as an overworld biome")
    boolean overworld;
    @Description("Set to true if other mods should treat this as a nether biome")
    boolean nether;
    @Description("Set to true if other mods should treat this as an end biome")
    boolean end;

    // biome properties
    boolean aquatic;
    boolean aquaticIcy;
    boolean badlands;
    boolean beach;
    boolean birchForest;
    boolean cave;
    boolean cold;
    boolean darkForest;
    boolean dead;
    boolean deepOcean;
    boolean denseVegetation;
    boolean desert;
    boolean dry;
    boolean floral;
    boolean flowerForest;
    boolean hill;
    boolean hot;
    boolean icy;
    boolean jungle;
    boolean lush;
    boolean magical;
    boolean mountain;
    boolean peak;
    boolean slope;
    boolean netherForest;
    boolean oldGrowth;
    boolean outerEndIsland;
    boolean plains;
    boolean plateau;
    boolean rare;
    boolean river;
    boolean sandy;
    boolean savanna;
    boolean shallowOcean;
    boolean snowy;
    boolean snowyPlains;
    boolean sparseVegetation;
    boolean spooky;
    boolean stonyShores;
    boolean swamp;
    boolean taiga;
    boolean temperate;
    boolean underground;
    boolean theVoid;
    boolean wasteland;
    boolean wet;
    boolean windswept;
    boolean noDefaultMonsters;
    boolean hiddenFromLocatorSelection;
    @EnumSetting("OAK")
    WoodType primaryWoodType;

    @Override
    public String getSectionName() {
        return "Biome Tag Settings";
    }

    public static BiomeTagConfig getBiomeTagConfig(SettingsMap reader) {
        BiomeTagConfigBuilder builder = BiomeTagConfig.builder();
        builder.aquatic(reader.getSetting(BiomeTagSettings.AQUATIC));
        builder.aquaticIcy(reader.getSetting(BiomeTagSettings.AQUATICICY));
        builder.badlands(reader.getSetting(BiomeTagSettings.BADLANDS));
        builder.beach(reader.getSetting(BiomeTagSettings.BEACH));
        builder.birchForest(reader.getSetting(BiomeTagSettings.BIRCHFOREST));
        builder.cave(reader.getSetting(BiomeTagSettings.CAVE));
        builder.cold(reader.getSetting(BiomeTagSettings.COLD));
        builder.darkForest(reader.getSetting(BiomeTagSettings.DARKFOREST));
        builder.dead(reader.getSetting(BiomeTagSettings.DEAD));
        builder.deepOcean(reader.getSetting(BiomeTagSettings.DEEPOCEAN));
        builder.denseVegetation(reader.getSetting(BiomeTagSettings.DENSEVEGETATION));
        builder.desert(reader.getSetting(BiomeTagSettings.DESERT));
        builder.dry(reader.getSetting(BiomeTagSettings.DRY));
        builder.floral(reader.getSetting(BiomeTagSettings.FLORAL));
        builder.flowerForest(reader.getSetting(BiomeTagSettings.FLOWERFOREST));
        builder.hill(reader.getSetting(BiomeTagSettings.HILL));
        builder.hot(reader.getSetting(BiomeTagSettings.HOT));
        builder.icy(reader.getSetting(BiomeTagSettings.ICY));
        builder.jungle(reader.getSetting(BiomeTagSettings.JUNGLE));
        builder.lush(reader.getSetting(BiomeTagSettings.LUSH));
        builder.magical(reader.getSetting(BiomeTagSettings.MAGICAL));
        builder.mountain(reader.getSetting(BiomeTagSettings.MOUNTAIN));
        builder.peak(reader.getSetting(BiomeTagSettings.PEAK));
        builder.slope(reader.getSetting(BiomeTagSettings.SLOPE));
        builder.netherForest(reader.getSetting(BiomeTagSettings.NETHERFOREST));
        builder.oldGrowth(reader.getSetting(BiomeTagSettings.OLDGROWTH));
        builder.outerEndIsland(reader.getSetting(BiomeTagSettings.OUTERENDISLAND));
        builder.overworld(reader.getSetting(BiomeTagSettings.OVERWORLD));
        builder.nether(reader.getSetting(BiomeTagSettings.NETHER));
        builder.end(reader.getSetting(BiomeTagSettings.END));
        builder.plains(reader.getSetting(BiomeTagSettings.PLAINS));
        builder.plateau(reader.getSetting(BiomeTagSettings.PLATEAU));
        builder.rare(reader.getSetting(BiomeTagSettings.RARE));
        builder.river(reader.getSetting(BiomeTagSettings.RIVER));
        builder.sandy(reader.getSetting(BiomeTagSettings.SANDY));
        builder.savanna(reader.getSetting(BiomeTagSettings.SAVANNA));
        builder.shallowOcean(reader.getSetting(BiomeTagSettings.SHALLOWOCEAN));
        builder.snowy(reader.getSetting(BiomeTagSettings.SNOWY));
        builder.snowyPlains(reader.getSetting(BiomeTagSettings.SNOWYPLAINS));
        builder.sparseVegetation(reader.getSetting(BiomeTagSettings.SPARSEVEGETATION));
        builder.spooky(reader.getSetting(BiomeTagSettings.SPOOKY));
        builder.stonyShores(reader.getSetting(BiomeTagSettings.STONYSHORES));
        builder.swamp(reader.getSetting(BiomeTagSettings.SWAMP));
        builder.taiga(reader.getSetting(BiomeTagSettings.TAIGA));
        builder.temperate(reader.getSetting(BiomeTagSettings.TEMPERATE));
        builder.underground(reader.getSetting(BiomeTagSettings.UNDERGROUND));
        builder.theVoid(reader.getSetting(BiomeTagSettings.THEVOID));
        builder.wasteland(reader.getSetting(BiomeTagSettings.WASTELAND));
        builder.wet(reader.getSetting(BiomeTagSettings.WET));
        builder.windswept(reader.getSetting(BiomeTagSettings.WINDSWEPT));
        builder.noDefaultMonsters(reader.getSetting(BiomeTagSettings.NODEFAULTMONSTERS));
        builder.hiddenFromLocatorSelection(reader.getSetting(BiomeTagSettings.HIDDENFROMLOCATORSELECTION));
        builder.primaryWoodType(reader.getSetting(BiomeTagSettings.PRIMARYWOODTYPE));
        return builder.build();
    }

    public List<Setting<?>> getAlteredSettings() {
        ArrayList <Setting<?>> list = new ArrayList<>();
        list.add(BiomeTagSettings.OVERWORLD);
        list.add(BiomeTagSettings.NETHER);
        list.add(BiomeTagSettings.END);
        list.add(BiomeTagSettings.AQUATIC);
        list.add(BiomeTagSettings.AQUATICICY);
        list.add(BiomeTagSettings.BADLANDS);
        list.add(BiomeTagSettings.BEACH);
        list.add(BiomeTagSettings.BIRCHFOREST);
        list.add(BiomeTagSettings.CAVE);
        list.add(BiomeTagSettings.COLD);
        list.add(BiomeTagSettings.DARKFOREST);
        list.add(BiomeTagSettings.DEAD);
        list.add(BiomeTagSettings.DEEPOCEAN);
        list.add(BiomeTagSettings.DENSEVEGETATION);
        list.add(BiomeTagSettings.DESERT);
        list.add(BiomeTagSettings.DRY);
        list.add(BiomeTagSettings.FLORAL);
        list.add(BiomeTagSettings.FLOWERFOREST);
        list.add(BiomeTagSettings.HILL);
        list.add(BiomeTagSettings.HOT);
        list.add(BiomeTagSettings.ICY);
        list.add(BiomeTagSettings.JUNGLE);
        list.add(BiomeTagSettings.LUSH);
        list.add(BiomeTagSettings.MAGICAL);
        list.add(BiomeTagSettings.MOUNTAIN);
        list.add(BiomeTagSettings.PEAK);
        list.add(BiomeTagSettings.SLOPE);
        list.add(BiomeTagSettings.NETHERFOREST);
        list.add(BiomeTagSettings.OLDGROWTH);
        list.add(BiomeTagSettings.OUTERENDISLAND);
        list.add(BiomeTagSettings.PLAINS);
        list.add(BiomeTagSettings.PLATEAU);
        list.add(BiomeTagSettings.RARE);
        list.add(BiomeTagSettings.RIVER);
        list.add(BiomeTagSettings.SANDY);
        list.add(BiomeTagSettings.SAVANNA);
        list.add(BiomeTagSettings.SHALLOWOCEAN);
        list.add(BiomeTagSettings.SNOWY);
        list.add(BiomeTagSettings.SNOWYPLAINS);
        list.add(BiomeTagSettings.SPARSEVEGETATION);
        list.add(BiomeTagSettings.SPOOKY);
        list.add(BiomeTagSettings.STONYSHORES);
        list.add(BiomeTagSettings.SWAMP);
        list.add(BiomeTagSettings.TAIGA);
        list.add(BiomeTagSettings.TEMPERATE);
        list.add(BiomeTagSettings.UNDERGROUND);
        list.add(BiomeTagSettings.THEVOID);
        list.add(BiomeTagSettings.WASTELAND);
        list.add(BiomeTagSettings.WET);
        list.add(BiomeTagSettings.WINDSWEPT);
        list.add(BiomeTagSettings.NODEFAULTMONSTERS);
        list.add(BiomeTagSettings.HIDDENFROMLOCATORSELECTION);
        list.add(BiomeTagSettings.PRIMARYWOODTYPE);

        return list.stream()
                .filter(setting -> setting.getGetter().apply(this) != setting.getDefaultValue())
                .toList();
    }
}
