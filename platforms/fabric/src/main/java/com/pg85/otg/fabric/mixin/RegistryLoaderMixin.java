package com.pg85.otg.fabric.mixin;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Lifecycle;
import com.pg85.otg.OTG;
import com.pg85.otg.config.settings.preset.DimensionSettings;
import com.pg85.otg.constants.Constants;
import com.pg85.otg.fabric.biome.LegacyFabricBiomeLoader;
import com.pg85.otg.fabric.biome.OTGFabricBiomeProvider;
import com.pg85.otg.fabric.gen.OTGFabricChunkGenerator;
import com.pg85.otg.presets.Preset;
import com.pg85.otg.util.OTGLog;
import com.pg85.otg.util.minecraft.OTGDimensionType;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.*;

@Mixin(RegistryDataLoader.class)
public class RegistryLoaderMixin {
    // Explainer: The injector below needs to match:
    // 1. The method signature of the target method
    // 2. The CallbackInfoReturnable parameter (not normal CallbackInfo)
    // 3. The local variables in the target method, in order
    // Only then do we get access to the list of registries
    @Inject(
            method = "load(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/core/RegistryAccess;" +
                    "Ljava/util/List;)Lnet/minecraft/core/RegistryAccess$Frozen;",
            at = @At(
                value = "INVOKE",
                target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V",
                ordinal = 1
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private static void loadOTGPresets(ResourceManager resourceManager, RegistryAccess registryAccess, List<RegistryDataLoader.RegistryData<?>> list,
                                       CallbackInfoReturnable ci, Map errorMap, List<Pair<WritableRegistry<?>, Object>> registries) {
        WritableRegistry<WorldPreset> worldPresets = getRegistry(registries, Registries.WORLD_PRESET);
        if (worldPresets == null ) {
            // vanilla auto-registers the level stems based on the world preset, so we can ignore this
            return;
        }

        OTGLog.getLogger().info("Registering the following OTG presets: %s", OTG.getEngine().getPresetLoader().getAllPresets());
        // register dimension types

        //printAllRegistriesForDebug(registries);

        registerBiomes(registries);

        HashMap<Preset, ResourceKey<DimensionType>> map = registerDimensionTypes(registries);
        Map<ResourceKey<LevelStem>, LevelStem> levelStems = createLevelStems(registries, map);
        if (OTG.getEngine().getPresetLoader() instanceof LegacyFabricBiomeLoader loader) {
            loader.setLevelStems(levelStems);
        }
        // TODO: register noise generator settings
        // register world presets
        registerWorldPresets(worldPresets, map, levelStems);
    }

    private static void registerBiomes(List<Pair<WritableRegistry<?>, Object>> registries) {
        LegacyFabricBiomeLoader loader = (LegacyFabricBiomeLoader) OTG.getEngine().getPresetLoader();
        WritableRegistry<Biome> biomeWritableRegistry = getRegistry(registries, Registries.BIOME);
        if (biomeWritableRegistry == null) {
            OTGLog.getLogger().error("Could not find biome registry");
            return;
        }
        loader.registerBiomes(biomeWritableRegistry);
    }

    private static void printAllRegistriesForDebug(List<Pair<WritableRegistry<?>, Object>> registries) {
        System.out.println("--*--");
        for (Pair<WritableRegistry<?>, Object> registry : registries) {
            OTGLog.info("Registry: %s", registry.getFirst().key());
            //OTGLog.info("Object: %s", registry.getSecond());
        }
        System.out.println("--*--");
    }

    private static Map<ResourceKey<LevelStem>, LevelStem> createLevelStems(
            List<Pair<WritableRegistry<?>, Object>> registries,
            HashMap<Preset, ResourceKey<DimensionType>> map
    ) {
        Map<ResourceKey<LevelStem>, LevelStem> levelStems = new HashMap<>();
        for (Map.Entry<Preset, ResourceKey<DimensionType>> entry : map.entrySet()) {
            Preset preset = entry.getKey();
            ResourceKey<DimensionType> dimensionKey = entry.getValue();
            HolderGetter<DimensionType> dimensionHolders = getRegistry(registries, Registries.DIMENSION_TYPE).asLookup();
            HolderGetter<NoiseGeneratorSettings> noiseHolders = getRegistry(registries, Registries.NOISE_SETTINGS).asLookup();

            ResourceKey<LevelStem> key = ResourceKey.create(Registries.LEVEL_STEM, new ResourceLocation(Constants.MOD_ID_SHORT, preset.getPresetRegistryName()));
            // Get dimension, then if it doesn't exist, check if it's an OTG location. If so, create it and register, then move on.
            LevelStem levelStem = handleMissingLevelStem(preset, dimensionHolders, noiseHolders, dimensionKey);
            levelStems.put(key, levelStem);
        }
        return levelStems;
    }

    private static void registerWorldPresets(
            WritableRegistry<WorldPreset> worldPresets,
            HashMap<Preset, ResourceKey<DimensionType>> map,
            Map<ResourceKey<LevelStem>, LevelStem> levelStems
    ) {
        OTGLog.info("%s", levelStems.keySet());
        OTGLog.info("%s", levelStems.values());
        for (Map.Entry<Preset, ResourceKey<DimensionType>> entry : map.entrySet()) {
            // for each preset, we set up a WorldPreset with an overworld, nether and end according to its settings
            // potentially also with custom dimensions set up under OTG tags
            // or with all vanilla biomes, plus an OTG dimension
            // (latter will be good for testing a single preset, hopefully)
            Preset preset = entry.getKey();
            List<String> dimensionNames = preset.getPresetConfig().getDimensionSettings().getDefaultDimensions();
            dimensionNames = dimensionNames.stream()
                    .map(
                            string -> string.equalsIgnoreCase("this")
                                    ? Constants.MOD_ID_SHORT + ':' + preset.getPresetRegistryName()
                                    : string)
                    .map(s -> s.toLowerCase(Locale.ROOT))
                    .toList();
            Map<ResourceKey<LevelStem>, LevelStem> presetLevelStems = new HashMap<>();
            boolean first = true;
            for (String dimensionName : dimensionNames) {
                ResourceKey<LevelStem> key = ResourceKey.create(Registries.LEVEL_STEM, new ResourceLocation(dimensionName));
                LevelStem levelStem = levelStems.get(key);
                if (levelStem == null) {
                    OTGLog.getLogger().error("Could not find level stem for dimension %s", dimensionName);
                    continue;
                }
                if (first) {
                    // there always needs to be an overworld, so we'll use the first one as that
                    key = LevelStem.OVERWORLD;
                    first = false;
                }
                presetLevelStems.put(key, levelStem);
            }
            WorldPreset worldPreset = new WorldPreset(presetLevelStems);
            // create a world preset for each preset
            ResourceLocation id = new ResourceLocation(Constants.MOD_ID_SHORT, preset.getPresetRegistryName().toLowerCase(Locale.ROOT));
            ResourceKey<WorldPreset> key = ResourceKey.create(Registries.WORLD_PRESET, id);
            OTGLog.getLogger().info("Registering world preset: " + key.location());
            worldPresets.register(key, worldPreset, Lifecycle.stable());
        }
    }

    private static LevelStem handleMissingLevelStem(Preset preset, HolderGetter<DimensionType> dimensionHolders, HolderGetter<NoiseGeneratorSettings> noiseHolders, ResourceKey<DimensionType> dimensionKey) {
        return new LevelStem(
                    dimensionHolders.getOrThrow(dimensionKey),
                    new OTGFabricChunkGenerator(
                            new OTGFabricBiomeProvider(preset.getFolderName()),
                            noiseHolders.getOrThrow(NoiseGeneratorSettings.OVERWORLD)
                    )
            );
    }

    private static @NotNull HashMap<Preset, ResourceKey<DimensionType>> registerDimensionTypes(List<Pair<WritableRegistry<?>, Object>> list2) {
        var map = new HashMap<Preset, ResourceKey<DimensionType>>();
        for (Preset preset : OTG.getEngine().getPresetLoader().getAllPresets()) {
            OTGDimensionType otgDimensionType = preset.getPresetConfig().getDimensionSettings().getDimensionType();
            ResourceKey<DimensionType> dimensionKey = switch (otgDimensionType) {
                case OVERWORLD -> BuiltinDimensionTypes.OVERWORLD;
                case NETHER -> BuiltinDimensionTypes.NETHER;
                case END -> BuiltinDimensionTypes.END;
                case OTG -> {
                    ResourceLocation id = new ResourceLocation(Constants.MOD_ID_SHORT, preset.getPresetRegistryName().toLowerCase(Locale.ROOT));
                    ResourceKey<DimensionType> dimensionTypeKey = ResourceKey.create(Registries.DIMENSION_TYPE, id);
                    // create settings for OTG dimension
                    DimensionType dimensionType = getDimensionType(preset.getPresetConfig().getDimensionSettings());
                    // register the dimension
                    WritableRegistry<DimensionType> dimensionTypes = getRegistry(list2, Registries.DIMENSION_TYPE);
                    dimensionTypes.register(dimensionTypeKey, dimensionType, Lifecycle.stable());
                    // return the key for use elsewhere
                    yield dimensionTypeKey;
                }
            };
            map.put(preset, dimensionKey);
        }
        return map;
    }

    private static @NotNull DimensionType getDimensionType(DimensionSettings settings) {
        return new DimensionType(
                settings.getFixedTime(),
                settings.isHasSkyLight(),
                settings.isHasCeiling(),
                settings.isUltraWarm(),
                settings.isNatural(),
                settings.getCoordinateScale(),
                settings.isBedWorks(),
                settings.isRespawnAnchorWorks(),
                settings.getMinY(),
                settings.getHeight(),
                settings.getLogicalHeight(),
                TagKey.create(Registries.BLOCK, new ResourceLocation(settings.getInfiniburn())),
                new ResourceLocation(settings.getEffectsLocation().toLowerCase(Locale.ROOT)),
                (float) settings.getAmbientLight(),
                new DimensionType.MonsterSettings(
                        settings.isPiglinSafe(),
                        settings.isHasRaids(),
                        // monsterSpawnLightTest
                        UniformInt.of(
                                settings.getMonsterSpawnLightVariationMin(),
                                settings.getMonsterSpawnLightVariationMax()
                        ),
                        // monsterSpawnBlockLightLimit
                        settings.getMonsterSpawnLightLimit()
                )
        );
    }

    @SuppressWarnings("unchecked")
    private static <T> WritableRegistry<T> getRegistry(List<Pair<WritableRegistry<?>, Object>> registries, ResourceKey<Registry<T>> key) {
        List<? extends WritableRegistry<?>> x = registries.stream()
                .map(Pair::getFirst)
                .filter(result -> result.key().equals(key))
                .toList();
        if (x.isEmpty()) {
            return null;
        }
        return (WritableRegistry<T>) x.get(0);
    }
}
