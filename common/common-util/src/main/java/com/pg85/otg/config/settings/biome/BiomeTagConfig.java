package com.pg85.otg.config.settings.biome;

import com.pg85.otg.config.annotation.*;
import com.pg85.otg.config.io.SettingsMap;
import com.pg85.otg.config.settings.ConfigSection;
import com.pg85.otg.config.settings.biome.generated.BiomeTagSettings;
import com.pg85.otg.config.settingtype.Setting;
import com.pg85.otg.util.minecraft.WoodType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@Config
@LongDescription({
        "Settings for biome tags, used by datagen tag generation.",
        "Will be available at runtime, but can be inaccurate if not set correctly."
})
public class BiomeTagConfig extends ConfigSection {

    @LongDescription({
            "Set to true to display all biome tag settings for this biome",
            "By default, only the settings that differ from the default values are displayed."
    })
    private final boolean displayAllBiomeTags;

    // world type
    @Description("Set to true if other mods should treat this as an overworld biome")
    private final boolean overworld;
    @Description("Set to true if other mods should treat this as a nether biome")
    private final boolean nether;
    @Description("Set to true if other mods should treat this as an end biome")
    private final boolean end;

    // biome properties
    private final boolean aquatic;
    private final boolean aquaticIcy;
    private final boolean badlands;
    private final boolean beach;
    private final boolean birchForest;
    private final boolean cave;
    private final boolean cold;
    private final boolean coniferousTrees;
    private final boolean darkForest;
    private final boolean dead;
    private final boolean deciduousTrees;
    private final boolean deepOcean;
    private final boolean denseVegetation;
    private final boolean desert;
    private final boolean dry;
    private final boolean forest;
    private final boolean floral;
    private final boolean flowerForest;
    private final boolean hill;
    private final boolean hot;
    private final boolean icy;
    private final boolean jungle;
    private final boolean jungleTrees;
    private final boolean lush;
    private final boolean magical;
    private final boolean mountain;
    private final boolean mushroom;
    private final boolean peak;
    private final boolean slope;
    private final boolean netherForest;
    private final boolean ocean;
    private final boolean oldGrowth;
    private final boolean outerEndIsland;
    private final boolean plains;
    private final boolean plateau;
    private final boolean rare;
    private final boolean river;
    private final boolean sandy;
    private final boolean savanna;
    private final boolean savannaTrees;
    private final boolean shallowOcean;
    private final boolean snowy;
    private final boolean snowyPlains;
    private final boolean sparseVegetation;
    private final boolean spooky;
    private final boolean stonyShores;
    private final boolean swamp;
    private final boolean taiga;
    private final boolean temperate;
    private final boolean theVoid;
    private final boolean underground;
    private final boolean wasteland;
    private final boolean wet;
    private final boolean windswept;
    private final boolean noDefaultMonsters;
    private final boolean hiddenFromLocatorSelection;
    @EnumSetting("NONE")
    WoodType primaryWoodType;

    @Override
    public String getSectionName() {
        return "Biome Tag Settings";
    }

    public static BiomeTagConfig getBiomeTagConfig(SettingsMap reader, IdentitySettings legacy) {
        BiomeTagConfigBuilder builder = BiomeTagSettings.getBuilder(reader);

        String category = legacy.getBiomeCategory();
        if (category != null && !category.isEmpty()) {
            handleCategory(category, builder);
        }

        List<String> dictTags = legacy.getBiomeDictTags();
        if (dictTags != null && !dictTags.isEmpty()) {
            for (String dictTag : dictTags) {
                handleDictTag(dictTag, builder);
            }

            // Custom legacy behaviour for deciduous trees
            if (!dictTags.contains("coniferous") && !dictTags.contains("jungle") && !dictTags.contains("savanna")) {
                builder.deciduousTrees(true);
            }
        }
        return builder.build();
    }

    private static void handleCategory(String category, BiomeTagConfigBuilder builder) {
        switch (category.toLowerCase()) {
            case "none" -> {}
            case "taiga" -> builder.taiga(true);
            case "extreme_hills" -> {
                builder.hill(true);
                builder.mountain(true);
            }
            case "jungle" -> builder.jungle(true);
            case "mesa" -> builder.badlands(true);
            case "plains" -> builder.plains(true);
            case "savanna" -> builder.savanna(true);
            case "icy" -> builder.icy(true);
            case "the_end" -> builder.end(true);
            case "beach" -> builder.beach(true);
            case "forest" -> builder.forest(true);
            case "ocean" -> builder.ocean(true);
            case "desert" -> builder.desert(true);
            case "river" -> builder.river(true);
            case "swamp" -> builder.swamp(true);
            case "mushroom" -> builder.mushroom(true);
            case "nether" -> builder.nether(true);
        }
    }

    private static void handleDictTag(String dictTag, BiomeTagConfigBuilder builder) {
        switch (dictTag.toLowerCase()) {
            // Temperature based
            case "hot" -> builder.hot(true);
            case "cold" -> builder.cold(true);

            // Vegetation
            case "sparse" -> builder.sparseVegetation(true);
            case "dense" -> builder.denseVegetation(true);

            // Wetness
            case "wet" -> builder.wet(true);
            case "dry" -> builder.dry(true);

            // Tree type
            case "savanna" -> builder.savannaTrees(true);
            case "jungle" -> builder.jungleTrees(true);
            case "coniferous" -> builder.coniferousTrees(true);

            // Vibe
            case "spooky" -> builder.spooky(true);
            case "dead" -> builder.dead(true);
            case "lush" -> builder.lush(true);
            case "mushroom" -> builder.mushroom(true);
            case "magical" -> builder.magical(true);
            case "rare" -> builder.rare(true);
            case "plateau" -> builder.plateau(true);
            case "modified" -> {} // can't find a fitting tag


            // Water tags
            case "ocean" -> builder.ocean(true);
            case "river" -> builder.river(true);
            case "water" -> builder.aquatic(true);

            // Generic biome types
            case "mesa" -> builder.badlands(true);
            case "forest" -> builder.forest(true);
            case "plains" -> builder.plains(true);
            case "mountain" -> builder.mountain(true);
            case "hills" -> builder.hill(true);
            case "swamp" -> builder.swamp(true);
            case "sandy" -> builder.sandy(true);
            case "snowy" -> builder.snowy(true);
            case "wasteland" -> builder.wasteland(true);
            case "beach" -> builder.beach(true);
            case "void" -> builder.theVoid(true);

            // Dimension tags
            case "overworld" -> builder.overworld(true);
            case "nether" -> builder.nether(true);
            case "end" -> builder.end(true);
        }
    }

    public List<Setting<?>> getAllSettings() {
        return BiomeTagSettings.list;
    }

    public void writeConfig(SettingsMap writer) {
        List<Setting<?>> alteredSettings = getAlteredSettings();
        if (alteredSettings.isEmpty()) {
            return; // No altered settings, nothing to write
        }
        for (Setting<?> setting : alteredSettings) {
            writer.putSetting(setting, this);
        }
    }
}
