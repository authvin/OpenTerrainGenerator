package com.pg85.otg.fabric.biome;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;

import com.mojang.serialization.Lifecycle;
import com.pg85.otg.OTG;
import com.pg85.otg.config.biome.BiomeConfig;
import com.pg85.otg.config.biome.BiomeGroupFunction;
import com.pg85.otg.constants.Constants;
import com.pg85.otg.gen.biome.BiomeData;
import com.pg85.otg.gen.biome.layers.BiomeLayerData;
import com.pg85.otg.gen.biome.layers.BiomeGroup;
import com.pg85.otg.interfaces.IBiome;
import com.pg85.otg.config.settings.biome.BiomeSettings;
import com.pg85.otg.interfaces.IBiomeResourceLocation;
import com.pg85.otg.config.settings.preset.PresetSettings;
import com.pg85.otg.interfaces.ILogger;
import com.pg85.otg.presets.LocalPresetLoader;
import com.pg85.otg.presets.Preset;
import com.pg85.otg.util.OTGLog;
import com.pg85.otg.util.biome.OTGBiomeResourceLocation;
import com.pg85.otg.util.biome.WeightedMobSpawnGroup;
import com.pg85.otg.util.logging.LogCategory;
import com.pg85.otg.util.logging.LogLevel;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import static com.pg85.otg.util.logging.LogCategory.CONFIGS;


public class LegacyFabricBiomeLoader extends LocalPresetLoader {
    // Static fields to store the references
    public static HolderGetter<PlacedFeature> PLACED_FEATURE_HOLDER;
    public static HolderGetter<ConfiguredWorldCarver<?>> CONFIGURED_CARVER_HOLDER;
    public static boolean BIOME_DATA_INITIALIZED = false;
    private Map<String, List<ResourceKey<Biome>>> biomesByPresetFolderName = new LinkedHashMap<>();
    private HashMap<String, IBiome[]> globalIdMapping = new HashMap<>();
    private Map<String, BiomeLayerData> presetGenerationData = new HashMap<>();

    // Place to store our LevelStem between registry rounds
    @Getter
    @Setter
    private Map<ResourceKey<LevelStem>, LevelStem> levelStems = new HashMap<>();
    
    public LegacyFabricBiomeLoader(Path otgRootFolder)
    {
        super(otgRootFolder);
    }

    public List<ResourceKey<Biome>> getBiomeResourceKeys(String presetFolderName)
    {
        return this.biomesByPresetFolderName.get(presetFolderName);
    }

    @Override
    public IBiome[] getGlobalIdMapping(String presetFolderName)
    {
        return globalIdMapping.get(presetFolderName);
    }

    @Override
    public Map<String, BiomeLayerData> getPresetGenerationData()
    {
        return new HashMap<>(this.presetGenerationData);
    }

    // Note: BiomeGen and ChunkGen cache some settings during a session, so they'll only update on world exit/rejoin.
    public void reloadPresetFromDisk(String presetFolderName, WritableRegistry<Biome> biomeRegistry)
    {
        clearCaches();

        if(this.presetsDir.exists() && this.presetsDir.isDirectory())
        {
            for(File presetDir : Objects.requireNonNull(this.presetsDir.listFiles()))
            {
                if(presetDir.isDirectory() && presetDir.getName().equals(presetFolderName))
                {
                    for(File file : Objects.requireNonNull(presetDir.listFiles()))
                    {
                        if(file.getName().equals(Constants.PRESET_CONFIG_FILE))
                        {
                            Preset preset = loadPreset(presetDir.toPath());
                            Preset existingPreset = this.presets.get(preset.getFolderName());
                            existingPreset.update(preset);
                            break;
                        }
                    }
                }
            }
        }
        registerBiomes(biomeRegistry);
    }

    protected void clearCaches()
    {
        this.globalIdMapping = new HashMap<>();
        this.presetGenerationData = new HashMap<>();
        this.biomesByPresetFolderName = new LinkedHashMap<>();
    }

    public void reRegisterBiomes(String presetFolderName, WritableRegistry<Biome> biomeRegistry)
    {
        this.globalIdMapping.remove(presetFolderName);
        this.presetGenerationData.remove(presetFolderName);
        this.biomesByPresetFolderName.remove(presetFolderName);

        registerBiomes(biomeRegistry);
    }

    public void registerBiomes()
    {
        registerBiomes(null);
    }

    public void registerBiomes(WritableRegistry<Biome> biomeRegistry)
    {
        for(Preset preset : this.presets.values())
        {
            registerBiomesForPreset(preset, biomeRegistry);
        }
    }

    private void registerBiomesForPreset(Preset preset, WritableRegistry<Biome> biomeRegistry)
    {
        if (!BIOME_DATA_INITIALIZED) {
            // should always be initialized, but better safe than sorry. Would rather have a sensical error message than nonsensical
            throw new IllegalStateException("BiomeDataMixin not initialized");
        }
        HolderGetter<PlacedFeature> featureHolder = PLACED_FEATURE_HOLDER;
        HolderGetter<ConfiguredWorldCarver<?>> carverHolder = CONFIGURED_CARVER_HOLDER;

        // Index BiomeColors for FromImageMode and /otg map
        HashMap<Integer, Integer> biomeColorMap = new HashMap<Integer, Integer>();

        // Start at 1, 0 is the fallback for the biome generator (the world's ocean biome).
        int currentId = 1;

        List<ResourceKey<Biome>> presetBiomes = new ArrayList<>();
        this.biomesByPresetFolderName.put(preset.getFolderName(), presetBiomes);

        PresetSettings presetConfig = preset.getPresetConfig();
        BiomeSettings oceanBiomeConfig = null;
        int[] oceanTemperatures = new int[]{0, 0, 0, 0};

        List<BiomeConfig> biomeConfigs = preset.getBiomeConfigList();

        Map<Integer, List<BiomeData>> isleBiomesAtDepth = new HashMap<>();
        Map<Integer, List<BiomeData>> borderBiomesAtDepth = new HashMap<>();

        Map<String, List<Integer>> worldBiomes = new HashMap<>();
        Map<String, BiomeConfig> biomeConfigsByName = new HashMap<>();

        // Create registry keys for each biomeconfig, create template 
        // biome configs for any non-otg biomes targeted via TemplateForBiome.
        Map<IBiomeResourceLocation, BiomeConfig> biomeConfigsByResourceLocation = new LinkedHashMap<>();
        List<String> blackListedBiomes = presetConfig.getGenerationSettings().getBlackListedBiomes();

        //processTemplateBiomes(preset.getFolderName(), presetConfig, biomeConfigs, biomeConfigsByResourceLocation, biomeConfigsByName, blackListedBiomes);

        for(BiomeConfig biomeConfig : biomeConfigs)
        {
            if(!biomeConfig.getIsTemplateForBiome())
            {
                // Normal OTG biome, not a template biome.
                IBiomeResourceLocation otgLocation = new OTGBiomeResourceLocation(preset.getPresetFolder(), preset.getPresetRegistryName(), preset.getMajorVersion(), biomeConfig.getIdentitySettings().getBiomeName());
                biomeConfigsByResourceLocation.put(otgLocation, biomeConfig);
                biomeConfigsByName.put(biomeConfig.getIdentitySettings().getBiomeName(), biomeConfig);
            }
        }

        IBiome[] presetIdMapping = new IBiome[biomeConfigsByResourceLocation.entrySet().size()];
        for(Entry<IBiomeResourceLocation, BiomeConfig> biomeConfigEntry : biomeConfigsByResourceLocation.entrySet())
        {
            IBiomeResourceLocation iBiomeResourceLocation = biomeConfigEntry.getKey();
            BiomeSettings biomeConfig = biomeConfigEntry.getValue();
            boolean isOceanBiome = false;
            // Biome id 0 is reserved for ocean, used when a land column has 
            // no biome assigned, which can happen due to biome group rarity.
            if(biomeConfig.getIdentitySettings().getBiomeName().equals(presetConfig.getGenerationSettings().getDefaultOceanBiome()))
            {
                oceanBiomeConfig = biomeConfig;
                isOceanBiome = true;
            }

            int otgBiomeId = isOceanBiome ? 0 : currentId;

            // When using TemplateForBiome, we'll fetch the non-OTG biome from the registry, including any settings registered to it.
            // For normal biomes we create our own new OTG biome and apply settings from the biome config.
            ResourceLocation resourceLocation = new ResourceLocation(iBiomeResourceLocation.toResourceLocationString());
            ResourceKey<Biome> resourceKey;
            Biome biome;
            Holder.Reference<Biome> ref;
            // templates, and non-developer refresh, both just get the biome from the registry
            if(biomeConfig.getIsTemplateForBiome()
//                    || (refresh
//                        && !OTG.getEngine().getPluginConfig().getDeveloperModeEnabled())
            ) {
                biome = biomeRegistry.get(resourceLocation);
                if (biome == null) {
                    if(OTG.getEngine().getLogger().getLogCategoryEnabled(LogCategory.BIOME_REGISTRY))
                    {
                        OTG.log(LogLevel.ERROR, LogCategory.BIOME_REGISTRY, "Could not find biome " + resourceLocation + " for biomeconfig " + biomeConfig.getIdentitySettings().getBiomeName());
                    }
                    continue;
                }
                Optional<ResourceKey<Biome>> key = biomeRegistry.getResourceKey(biome);
                resourceKey = key.orElse(null);
                if (resourceKey == null) {
                    if(OTG.getEngine().getLogger().getLogCategoryEnabled(LogCategory.BIOME_REGISTRY))
                    {
                        OTG.getEngine().getLogger().log(LogLevel.ERROR, LogCategory.BIOME_REGISTRY, "Could not find resource key for biome " + resourceLocation + " for biomeconfig " + biomeConfig.getIdentitySettings().getBiomeName());
                    }
                    continue;
                }
                ref = biomeRegistry.getHolder(resourceKey).orElseThrow();

            } else {
                if(!(iBiomeResourceLocation instanceof OTGBiomeResourceLocation))
                {
                    if(OTG.getEngine().getLogger().getLogCategoryEnabled(LogCategory.BIOME_REGISTRY))
                    {
                        OTG.getEngine().getLogger().log(LogLevel.ERROR, LogCategory.BIOME_REGISTRY, "Could not process template biomeconfig " + biomeConfig.getIdentitySettings().getBiomeName() + ", did you set TemplateForBiome:true in the BiomeConfig?");
                    }
                    continue;
                }
                resourceKey = ResourceKey.create(Registries.BIOME, resourceLocation);
                // For OTG biomes, add Fabric biome dictionary tags.
                biomeConfig.getBiomeDictTags().forEach(biomeDictId -> {
//                    if(biomeDictId != null && !biomeDictId.trim().isEmpty())
//                    {
//                        BiomeDictionary.addTypes(resourceKey, BiomeDictionary.Type.getType(biomeDictId.trim()));
//                        // TODO: BiomeDictionary missing, needs replacing
//                    }
                });

                biome = LegacyFabricBiomeLoader.createOTGBiome(isOceanBiome, preset.getPresetConfig(), biomeConfig, featureHolder, carverHolder);
                /*if (refresh) {
                    biomeRegistry.registerMapping(0, resourceKey, biome, Lifecycle.stable());
                }*/
                ref = biomeRegistry.register(resourceKey, biome, Lifecycle.stable());
            }
            presetBiomes.add(resourceKey);
            biomeConfig.setRegistryKey(iBiomeResourceLocation);
            biomeConfig.setOTGBiomeId(otgBiomeId);

            // Populate our map for syncing
            //OTGClientSyncManager.getSyncedData().put(resourceLocation.toString(), new FabricBiomeSyncWrapper(biomeConfig));

            // Ocean temperature mappings. Probably a better way to do this?
            if (biomeConfig.getIdentitySettings().getBiomeName().equals(presetConfig.getGenerationSettings().getDefaultWarmOceanBiome()))
            {
                oceanTemperatures[0] = otgBiomeId;
            }
            if (biomeConfig.getIdentitySettings().getBiomeName().equals(presetConfig.getGenerationSettings().getDefaultLukewarmOceanBiome()))
            {
                oceanTemperatures[1] = otgBiomeId;
            }
            if (biomeConfig.getIdentitySettings().getBiomeName().equals(presetConfig.getGenerationSettings().getDefaultColdOceanBiome()))
            {
                oceanTemperatures[2] = otgBiomeId;
            }
            if (biomeConfig.getIdentitySettings().getBiomeName().equals(presetConfig.getGenerationSettings().getDefaultFrozenOceanBiome()))
            {
                oceanTemperatures[3] = otgBiomeId;
            }

            IBiome otgBiome = new FabricBiome(biomeConfig, biome, ref);
            if(otgBiomeId >= presetIdMapping.length)
            {
                OTGLog.fatal(CONFIGS, "Fatal error while registering OTG biome id's for preset " + preset.getFolderName() + ", most likely you've assigned a DefaultOceanBiome that doesn't exist.");

                OTGLog.info(CONFIGS, "Registered biomes: " + presetIdMapping.length);
                OTGLog.info(CONFIGS, "Current id: " + otgBiomeId);
                OTGLog.info(CONFIGS, "List of biomes: " + Arrays.toString(presetIdMapping));

                throw new RuntimeException("Fatal error while registering OTG biome id's for preset " + preset.getFolderName() + ", most likely you've assigned a DefaultOceanBiome that doesn't exist.");
            }
            presetIdMapping[otgBiomeId] = otgBiome;

            List<Integer> idsForBiome = worldBiomes.computeIfAbsent(biomeConfig.getIdentitySettings().getBiomeName(), k -> new ArrayList<>());
            idsForBiome.add(otgBiomeId);

            // Make a list of isle and border biomes per generation depth
            if(biomeConfig.getGenerationSettings().isIsleBiome())
            {
                // Make or get a list for this group depth, then add
                List<BiomeData> biomesAtDepth = isleBiomesAtDepth.getOrDefault(biomeConfig.getGenerationSettings().getBiomeSizeWhenIsle(), new ArrayList<>());
                biomesAtDepth.add(
                        new BiomeData(
                                otgBiomeId,
                                biomeConfig.getGenerationSettings().getBiomeRarityWhenIsle(),
                                biomeConfig.getGenerationSettings().getBiomeSizeWhenIsle(),
                                biomeConfig.getVisualSettings().getBiomeTemperature(),
                                biomeConfig.getGenerationSettings().getIsleInBiomes(),
                                biomeConfig.getGenerationSettings().getBorderInBiomes(),
                                biomeConfig.getGenerationSettings().getOnlyBorderNearBiomes(),
                                biomeConfig.getGenerationSettings().getNotBorderNearBiomes()
                        )
                );
                isleBiomesAtDepth.put(biomeConfig.getGenerationSettings().getBiomeSizeWhenIsle(), biomesAtDepth);
            }

            if(biomeConfig.getGenerationSettings().isBorderBiome())
            {
                // Make or get a list for this group depth, then add
                List<BiomeData> biomesAtDepth = borderBiomesAtDepth.getOrDefault(biomeConfig.getGenerationSettings().getBiomeSizeWhenBorder(), new ArrayList<>());
                biomesAtDepth.add(
                        new BiomeData(
                                otgBiomeId,
                                biomeConfig.getGenerationSettings().getBiomeRarity(),
                                biomeConfig.getGenerationSettings().getBiomeSizeWhenBorder(),
                                biomeConfig.getVisualSettings().getBiomeTemperature(),
                                biomeConfig.getGenerationSettings().getIsleInBiomes(),
                                biomeConfig.getGenerationSettings().getBorderInBiomes(),
                                biomeConfig.getGenerationSettings().getOnlyBorderNearBiomes(),
                                biomeConfig.getGenerationSettings().getNotBorderNearBiomes()
                        )
                );
                borderBiomesAtDepth.put(biomeConfig.getGenerationSettings().getBiomeSizeWhenBorder(), biomesAtDepth);
            }

            // Index BiomeColor for FromImageMode and /otg map
            biomeColorMap.put(biomeConfig.getGenerationSettings().getBiomeColor().getColor(), otgBiomeId);

            if(OTG.getEngine().getLogger().getLogCategoryEnabled(LogCategory.BIOME_REGISTRY))
            {
                OTG.getEngine().getLogger().log(LogLevel.INFO, LogCategory.BIOME_REGISTRY, "Registered biome " + resourceLocation.toString() + " | " + biomeConfig.getIdentitySettings().getBiomeName() + " with OTG id " + otgBiomeId);
            }

            currentId += isOceanBiome ? 0 : 1;
        }

        // If the ocean config is null, shift the array downwards to fill id 0
        if (oceanBiomeConfig == null)
        {
            System.arraycopy(presetIdMapping, 1, presetIdMapping, 0, presetIdMapping.length - 1);
        }

        this.globalIdMapping.put(preset.getFolderName(), presetIdMapping);


        Set<Integer> biomeDepths = new HashSet<>();
        Map<Integer, List<BiomeGroup>> groupDepths = new HashMap<>();

        // Iterate through the groups and add it to the layer data
        Map<Integer, BiomeGroup> groupRegistry = processBiomeGroups(preset.getFolderName(), presetConfig, biomeConfigsByResourceLocation, biomeConfigsByName, blackListedBiomes, biomeDepths, groupDepths);

        // Set the base data
        BiomeLayerData data = new BiomeLayerData(preset.getPresetFolder(), presetConfig, oceanBiomeConfig, oceanTemperatures, groupRegistry, biomeDepths, groupDepths, isleBiomesAtDepth, borderBiomesAtDepth, worldBiomes, biomeColorMap, presetIdMapping);

        // Set data for this preset
        this.presetGenerationData.put(preset.getFolderName(), data);
    }

    public static Biome createOTGBiome(boolean isOceanBiome, PresetSettings presetConfig, BiomeSettings biomeConfig, HolderGetter<PlacedFeature> featureHolderGetter, HolderGetter<ConfiguredWorldCarver<?>> carverHolderGetter) {
        BiomeGenerationSettings.Builder generationSettings = new BiomeGenerationSettings.Builder(featureHolderGetter, carverHolderGetter);

        // Mob spawning
        MobSpawnSettings.Builder mobSpawnSettings = createMobSpawnSettings(biomeConfig);

        //BiomeDefaultFeatures.addDefaultCarversAndLakes(generationSettings);

        float temperature = biomeConfig.getVisualSettings().getBiomeTemperature();
        
        float downfall = biomeConfig.getVisualSettings().getBiomeWetness();

        BiomeSpecialEffects.Builder specialEffects = new BiomeSpecialEffects.Builder();
        specialEffects.waterColor(biomeConfig.getVisualSettings().getWaterColor().getColor());
        specialEffects.waterFogColor(biomeConfig.getVisualSettings().getWaterFogColor().getColor());
        specialEffects.fogColor(biomeConfig.getVisualSettings().getFogColor().getColor());
        specialEffects.skyColor(biomeConfig.getVisualSettings().getSkyColor().getColor());

        return new Biome.BiomeBuilder()
                .generationSettings(generationSettings.build())
                .mobSpawnSettings(mobSpawnSettings.build())
                .specialEffects(specialEffects.build())
                .downfall(downfall)
                .temperature(temperature)
                .build();
    }

    private static MobSpawnSettings.Builder createMobSpawnSettings(BiomeSettings biomeConfig)
    {
        MobSpawnSettings.Builder mobSpawnInfoBuilder = new MobSpawnSettings.Builder();
        addMobGroup(MobCategory.MONSTER, mobSpawnInfoBuilder, biomeConfig.getMobSettings().getMonsters(), biomeConfig.getIdentitySettings().getBiomeName());
        addMobGroup(MobCategory.CREATURE, mobSpawnInfoBuilder, biomeConfig.getMobSettings().getCreatures(), biomeConfig.getIdentitySettings().getBiomeName());
        addMobGroup(MobCategory.WATER_CREATURE, mobSpawnInfoBuilder, biomeConfig.getMobSettings().getWaterCreatures(), biomeConfig.getIdentitySettings().getBiomeName());
        addMobGroup(MobCategory.AMBIENT, mobSpawnInfoBuilder, biomeConfig.getMobSettings().getAmbientCreatures(), biomeConfig.getIdentitySettings().getBiomeName());
        addMobGroup(MobCategory.WATER_AMBIENT, mobSpawnInfoBuilder, biomeConfig.getMobSettings().getWaterAmbientCreatures(), biomeConfig.getIdentitySettings().getBiomeName());
        addMobGroup(MobCategory.MISC, mobSpawnInfoBuilder, biomeConfig.getMobSettings().getMiscCreatures(), biomeConfig.getIdentitySettings().getBiomeName());
        return mobSpawnInfoBuilder;
    }

    private static void addMobGroup(MobCategory entitiClassification, MobSpawnSettings.Builder mobSpawnInfoBuilder, List<WeightedMobSpawnGroup> mobSpawnGroupList, String biomeName)
    {
        for(WeightedMobSpawnGroup mobSpawnGroup : mobSpawnGroupList)
        {
            Optional<EntityType<?>> entityType = EntityType.byString(mobSpawnGroup.internalName());
            if(entityType.isPresent())
            {
                mobSpawnInfoBuilder.addSpawn(entitiClassification, new MobSpawnSettings.SpawnerData(entityType.get(), mobSpawnGroup.getWeight(), mobSpawnGroup.getMin(), mobSpawnGroup.getMax()));
            } else {
                if(OTG.getEngine().getLogger().getLogCategoryEnabled(LogCategory.MOBS))
                {
                    OTG.getEngine().getLogger().log(LogLevel.ERROR, LogCategory.MOBS, "Could not find entity for mob: " + mobSpawnGroup.getMob() + " in BiomeConfig " + biomeName);
                }
            }
        }
    }

    private Map<Integer, BiomeGroup> processBiomeGroups(String presetFolderName, PresetSettings presetConfig, Map<IBiomeResourceLocation, BiomeConfig> biomeConfigsByResourceLocation, Map<String, BiomeConfig> biomeConfigsByName, List<String> blackListedBiomes, Set<Integer> biomeDepths, Map<Integer, List<BiomeGroup>> groupDepths)
    {
        int genDepth = presetConfig.getGenerationSettings().getGenerationDepth();
        Map<Integer, BiomeGroup> groupRegistry = new HashMap<>();
        for (BiomeGroupFunction group : presetConfig.getGenerationSettings().getBiomeGroupManager().getGroups())
        {
            if(OTG.getEngine().getLogger().getLogCategoryEnabled(LogCategory.BIOME_REGISTRY))
            {
                OTG.getEngine().getLogger().log(LogLevel.INFO, LogCategory.BIOME_REGISTRY, "Processing " + group.toString());
            }

            // Initialize biome group data
            List<BiomeData> biomes = new ArrayList<>();

            // init to genDepth as it will have one value per depth
            var totalDepthRarity = new int[genDepth + 1];
            var maxRarityPerDepth = new int[genDepth + 1];

            float totalTemp = 0;

            HashMap<String, BiomeConfig> groupBiomes = new LinkedHashMap<>();

            for (String biomeGroupEntry : group.getBiomes()) {
                BiomeConfig biomeConfig = biomeConfigsByName.get(biomeGroupEntry);
                if(biomeConfig == null)
                {
                    if(OTG.getEngine().getLogger().getLogCategoryEnabled(LogCategory.BIOME_REGISTRY))
                    {
                        OTG.getEngine().getLogger().log(LogLevel.ERROR, LogCategory.BIOME_REGISTRY, "Could not find biome " + biomeGroupEntry + " in biome group " + group.getGroupId());
                    }
                    continue;
                }
                groupBiomes.put(biomeGroupEntry, biomeConfig);
            }


            // Add each biome to the group
            for (Entry<String, BiomeConfig> biome : groupBiomes.entrySet())
            {
                if(biome.getValue() != null)
                {
                    BiomeConfig config = biome.getValue();
                    // Make and add the generation data
                    BiomeData newBiomeData = new BiomeData(
                            config.getOldOTGBiomeID(),
                            config.getGenerationSettings().getBiomeRarity(),
                            config.getGenerationSettings().getBiomeSize(),
                            config.getVisualSettings().getBiomeTemperature(),
                            config.getGenerationSettings().getIsleInBiomes(),
                            config.getGenerationSettings().getBorderInBiomes(),
                            config.getGenerationSettings().getOnlyBorderNearBiomes(),
                            config.getGenerationSettings().getNotBorderNearBiomes()
                    );
                    biomes.add(newBiomeData);

                    // Add the biome size- if it's already there, nothing is done
                    biomeDepths.add(config.getGenerationSettings().getBiomeSize());

                    totalTemp += config.getVisualSettings().getBiomeTemperature();

                    // Add this biome's rarity to the total for its depth in the group
                    totalDepthRarity[config.getGenerationSettings().getBiomeSize()] += config.getGenerationSettings().getBiomeRarity();
                }
            }

            // We have filled out the biome group's totalDepthRarity array, use it to fill the maxRarityPerDepth array
            for (int depth = 0; depth < totalDepthRarity.length; depth++)
            {
                // maxRarityPerDepth is the sum of totalDepthRarity for this and subsequent depths
                for (int j = depth; j < totalDepthRarity.length; j++)
                {
                    maxRarityPerDepth[depth] += totalDepthRarity[j];
                }
            }

            float avgTemp = totalTemp / group.getBiomes().size();
            BiomeGroup bg = new BiomeGroup(group.getGroupId(), group.getGroupRarity(), biomes, avgTemp, totalDepthRarity, maxRarityPerDepth);

            int groupSize = group.getGenerationDepth();

            // Make or get a list for this group depth, then add
            List<BiomeGroup> groupsAtDepth = groupDepths.getOrDefault(groupSize, new ArrayList<>());
            groupsAtDepth.add(bg);

            // Replace entry
            groupDepths.put(groupSize, groupsAtDepth);

            // Register group id
            groupRegistry.put(bg.id, bg);
        }
        return groupRegistry;
    }
}
