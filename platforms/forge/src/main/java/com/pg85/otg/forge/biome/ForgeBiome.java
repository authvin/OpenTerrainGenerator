package com.pg85.otg.forge.biome;

import java.util.List;
import java.util.Optional;
import com.pg85.otg.OTG;
import com.pg85.otg.config.ConfigFunction;
import com.pg85.otg.config.biome.BiomeConfig;
import com.pg85.otg.config.standard.BiomeStandardValues;
import com.pg85.otg.constants.Constants;
import com.pg85.otg.constants.settings.structure.MineshaftType;
import com.pg85.otg.constants.settings.structure.OceanRuinsType;
import com.pg85.otg.constants.settings.structure.RareBuildingType;
import com.pg85.otg.constants.settings.structure.RuinedPortalType;
import com.pg85.otg.constants.settings.structure.VillageType;
import com.pg85.otg.gen.resource.RegistryResource;
import com.pg85.otg.interfaces.IBiome;
import com.pg85.otg.interfaces.IBiomeConfig;
import com.pg85.otg.config.settings.preset.PresetSettings;
import com.pg85.otg.config.settings.biome.BiomeStructureSettings;
import com.pg85.otg.config.settings.biome.BiomeVisualSettings;
import com.pg85.otg.config.settings.preset.StructureSettings;
import com.pg85.otg.util.biome.OTGBiomeResourceLocation;
import com.pg85.otg.util.biome.WeightedMobSpawnGroup;
import com.pg85.otg.util.logging.LogCategory;
import com.pg85.otg.util.logging.LogLevel;

import net.minecraft.client.audio.BackgroundMusicSelector;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.*;
import net.minecraft.world.biome.Biome.TemperatureModifier;
import net.minecraft.world.biome.BiomeGenerationSettings.Builder;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.GenerationStage.Decoration;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.BastionRemnantsPieces;
import net.minecraft.world.gen.feature.structure.DesertVillagePools;
import net.minecraft.world.gen.feature.structure.MineshaftConfig;
import net.minecraft.world.gen.feature.structure.MineshaftStructure;
import net.minecraft.world.gen.feature.structure.OceanRuinConfig;
import net.minecraft.world.gen.feature.structure.OceanRuinStructure;
import net.minecraft.world.gen.feature.structure.PillagerOutpostPools;
import net.minecraft.world.gen.feature.structure.PlainsVillagePools;
import net.minecraft.world.gen.feature.structure.SavannaVillagePools;
import net.minecraft.world.gen.feature.structure.SnowyVillagePools;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureFeatures;
import net.minecraft.world.gen.feature.structure.TaigaVillagePools;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilders;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.common.world.MobSpawnInfoBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class ForgeBiome implements IBiome
{
	private final Biome biomeBase;
	private final IBiomeConfig biomeConfig;

	public ForgeBiome(Biome biomeBase, IBiomeConfig biomeConfig)
	{
		this.biomeBase = biomeBase;
		this.biomeConfig = biomeConfig;
	}

	@Override
	public float getTemperatureAt(int x, int y, int z)
	{
		return this.biomeBase.getTemperature(new BlockPos(x, y, z));
	}

	@Override
	public IBiomeConfig getBiomeConfig()
	{
		return this.biomeConfig;
	}

	public Biome getBiomeBase()
	{
		return biomeBase;
	}

	public static Biome createOTGBiome(boolean isOceanBiome, PresetSettings presetConfig, IBiomeConfig biomeConfig)
	{
		BiomeGenerationSettings.Builder biomeGenerationSettingsBuilder = new BiomeGenerationSettings.Builder();

		// Mob spawning
		MobSpawnInfo.Builder mobSpawnInfoBuilder = createMobSpawnInfo(biomeConfig);

		// Surface/ground/stone blocks / sagc are done during base terrain gen.
		// Spawn point detection checks for surfacebuilder blocks, so using ConfiguredSurfaceBuilders.GRASS.
		// TODO: What if there's no grass around spawn?
		biomeGenerationSettingsBuilder.surfaceBuilder(ConfiguredSurfaceBuilders.GRASS);

		// Register default carvers, we won't actually use these since we have
		// our own carvers, but if they're replaced we'll know there are modded carvers.
		DefaultBiomeFeatures.addDefaultCarvers(biomeGenerationSettingsBuilder);

		// Register any Registry() resources to the biome, to be handled by MC.
		for (ConfigFunction<IBiomeConfig> res : ((BiomeConfig)biomeConfig).getResourceQueue())
		{
			if (res instanceof RegistryResource)
			{
				RegistryResource registryResource = (RegistryResource)res;
				Decoration stage = GenerationStage.Decoration.valueOf(registryResource.getDecorationStage());
				ConfiguredFeature<?, ?> registry = WorldGenRegistries.CONFIGURED_FEATURE.get(new ResourceLocation(registryResource.getFeatureKey()));
				if(registry != null)
				{
					biomeGenerationSettingsBuilder.addFeature(stage, registry);
				} else {
					if(OTG.getEngine().getLogger().getLogCategoryEnabled(LogCategory.DECORATION))
					{
						OTG.getEngine().getLogger().log(LogLevel.ERROR, LogCategory.DECORATION, "Registry() " + registryResource.getFeatureKey() + " could not be found for biomeconfig " + biomeConfig.getIdentitySettings().getBiomeName());
					}
				}				
			}
		}

		// Default structures
		addVanillaStructures(biomeGenerationSettingsBuilder, presetConfig, biomeConfig);
		BiomeVisualSettings biomeVisualSettings = biomeConfig.getVisualSettings();
		float safeTemperature = biomeConfig.getVisualSettings().getBiomeTemperature();
		if (safeTemperature >= 0.1 && safeTemperature <= 0.2)
		{
			// Avoid temperatures between 0.1 and 0.2, Minecraft restriction
			safeTemperature = safeTemperature >= 1.5 ? 0.2f : 0.1f;
		}

		BiomeAmbience.Builder biomeAmbienceBuilder =
			new BiomeAmbience.Builder()			
				.fogColor(biomeVisualSettings.getFogColor() != BiomeStandardValues.FOG_COLOR.getDefaultValue() ? biomeVisualSettings.getFogColor() : presetConfig.getVisualSettings().getFogColor())
				.waterFogColor(biomeVisualSettings.getWaterFogColor() != BiomeStandardValues.WATER_FOG_COLOR.getDefaultValue() ? biomeVisualSettings.getWaterFogColor() : 329011)
				.waterColor(biomeVisualSettings.getWaterColor() != BiomeStandardValues.WATER_COLOR.getDefaultValue() ? biomeVisualSettings.getWaterColor() : 4159204)
				.skyColor(biomeVisualSettings.getSkyColor() != BiomeStandardValues.SKY_COLOR.getDefaultValue() ? biomeVisualSettings.getSkyColor() : getSkyColorForTemp(safeTemperature)) // TODO: Sky color is normally based on temp, make a setting for that?
		;

		@SuppressWarnings("deprecation")
		Optional<ParticleType<?>> particleType = Registry.PARTICLE_TYPE.getOptional(new ResourceLocation(biomeVisualSettings.getParticleType()));
		if(particleType.isPresent() && particleType.get() instanceof IParticleData)
		{
			biomeAmbienceBuilder.ambientParticle(new ParticleEffectAmbience((IParticleData)particleType.get(), biomeVisualSettings.getParticleProbability()));	
		}

		SoundEvent event = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(biomeVisualSettings.getMusic()));
		if (event != null)
		{
			biomeAmbienceBuilder.backgroundMusic(new BackgroundMusicSelector(event,
				biomeVisualSettings.getMusicMinDelay(),
				biomeVisualSettings.getMusicMaxDelay(),
				biomeVisualSettings.isReplaceCurrentMusic()));
		}

		event = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(biomeVisualSettings.getAmbientSound()));
		if (event != null)
		{
			biomeAmbienceBuilder.ambientLoopSound(event);
		}

		event = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(biomeVisualSettings.getMoodSound()));
		if (event != null)
		{
			biomeAmbienceBuilder.ambientMoodSound(new MoodSoundAmbience(event,
				biomeVisualSettings.getMoodSoundDelay(),
				biomeVisualSettings.getMoodSearchRange(),
				biomeVisualSettings.getMoodOffset()));
		}

		event = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(biomeVisualSettings.getAdditionsSound()));
		if (event != null)
		{
			biomeAmbienceBuilder.ambientAdditionsSound(new SoundAdditionsAmbience(event, biomeVisualSettings.getAdditionsTickChance()));
		}

		if(biomeVisualSettings.getFoliageColor() != 0xffffff)
		{
			biomeAmbienceBuilder.foliageColorOverride(biomeVisualSettings.getFoliageColor());
		}

		if(biomeVisualSettings.getGrassColor() != 0xffffff)
		{
			biomeAmbienceBuilder.grassColorOverride(biomeVisualSettings.getGrassColor());
		}
		
		switch(biomeVisualSettings.getGrassColorModifier())
		{
			case Swamp:
				biomeAmbienceBuilder.grassColorModifier(BiomeAmbience.GrassColorModifier.SWAMP);
				break;
			case DarkForest:
				biomeAmbienceBuilder.grassColorModifier(BiomeAmbience.GrassColorModifier.DARK_FOREST);
				break;
			default:
				break;
		}
		
		ResourceLocation registryName = new ResourceLocation(biomeConfig.getRegistryKey().toResourceLocationString());
		Biome.Category category = Biome.Category.byName(biomeConfig.getIdentitySettings().getBiomeCategory());
		if (category == null)
		{
			if(OTG.getEngine().getLogger().getLogCategoryEnabled(LogCategory.CONFIGS))
			{
				OTG.getEngine().getLogger().log(LogLevel.ERROR, LogCategory.CONFIGS, "Could not parse biome category " + biomeConfig.getIdentitySettings().getBiomeCategory());
			}
			category = isOceanBiome ? Biome.Category.OCEAN : Biome.Category.NONE;
		}
		Biome.RainType rainType = 
			biomeVisualSettings.getBiomeWetness() <= 0.0001 ? Biome.RainType.NONE :
			biomeVisualSettings.getBiomeTemperature() > Constants.SNOW_AND_ICE_TEMP ? Biome.RainType.RAIN :
			Biome.RainType.SNOW
		;

		// Fire Forge BiomeLoadingEvent to allow other mods to enrich otg biomes with decoration features, structure features and mob spawns.
		BiomeGenerationSettingsBuilder genBuilder = new BiomeGenerationSettingsBuilder(biomeGenerationSettingsBuilder.build());
		MobSpawnInfoBuilder spawnBuilder = new MobSpawnInfoBuilder(mobSpawnInfoBuilder.build());
		BiomeLoadingEvent event1 = new BiomeLoadingEvent(registryName, new Biome.Climate(rainType, safeTemperature, TemperatureModifier.NONE, biomeConfig.getVisualSettings().getBiomeWetness()), category, biomeConfig.getTerrainSettings().getBiomeHeight(), biomeConfig.getTerrainSettings().getBiomeVolatility(), biomeAmbienceBuilder.build(), genBuilder, spawnBuilder);
		MinecraftForge.EVENT_BUS.post(event1);
		BiomeAmbience biomeAmbienceBuilder2 = event1.getEffects();
		BiomeGenerationSettingsBuilder biomeGenerationSettingsBuilder2 = event1.getGeneration();
		MobSpawnInfoBuilder mobSpawnInfoBuilder2 = event1.getSpawns();
		//

		Biome.Builder biomeBuilder = 
			new Biome.Builder()
			.precipitation(rainType)
			.depth(biomeConfig.getTerrainSettings().getBiomeHeight())
			.scale(biomeConfig.getTerrainSettings().getBiomeVolatility())
			.temperature(safeTemperature)
			.downfall(biomeConfig.getVisualSettings().getBiomeWetness())
			.specialEffects(biomeAmbienceBuilder2)
			.mobSpawnSettings(mobSpawnInfoBuilder2.build())
			.generationSettings(biomeGenerationSettingsBuilder2.build())
		;
		
		if(biomeConfig.useFrozenOceanTemperature())
		{
			biomeBuilder.temperatureAdjustment(Biome.TemperatureModifier.FROZEN);
		}

		biomeBuilder.biomeCategory(category != null ? category : isOceanBiome ? Biome.Category.OCEAN : Biome.Category.PLAINS);
		
		return biomeBuilder.build().setRegistryName(registryName);
	}

	private static MobSpawnInfo.Builder createMobSpawnInfo(IBiomeConfig biomeConfig)
	{
		MobSpawnInfo.Builder mobSpawnInfoBuilder = new MobSpawnInfo.Builder();
		addMobGroup(EntityClassification.MONSTER, mobSpawnInfoBuilder, biomeConfig.getMobSettings().getMonsters(), biomeConfig.getIdentitySettings().getBiomeName());
		addMobGroup(EntityClassification.CREATURE, mobSpawnInfoBuilder, biomeConfig.getMobSettings().getCreatures(), biomeConfig.getIdentitySettings().getBiomeName());
		addMobGroup(EntityClassification.WATER_CREATURE, mobSpawnInfoBuilder, biomeConfig.getMobSettings().getWaterCreatures(), biomeConfig.getIdentitySettings().getBiomeName());
		addMobGroup(EntityClassification.AMBIENT, mobSpawnInfoBuilder, biomeConfig.getMobSettings().getAmbientCreatures(), biomeConfig.getIdentitySettings().getBiomeName());
		addMobGroup(EntityClassification.WATER_AMBIENT, mobSpawnInfoBuilder, biomeConfig.getMobSettings().getWaterAmbientCreatures(), biomeConfig.getIdentitySettings().getBiomeName());
		addMobGroup(EntityClassification.MISC, mobSpawnInfoBuilder, biomeConfig.getMobSettings().getMiscCreatures(), biomeConfig.getIdentitySettings().getBiomeName());
		mobSpawnInfoBuilder.setPlayerCanSpawn();
		return mobSpawnInfoBuilder;
	}

	private static void addMobGroup(EntityClassification entitiClassification, MobSpawnInfo.Builder mobSpawnInfoBuilder, List<WeightedMobSpawnGroup> mobSpawnGroupList, String biomeName)
	{
		for(WeightedMobSpawnGroup mobSpawnGroup : mobSpawnGroupList)
		{
			Optional<EntityType<?>> entityType = EntityType.byString(mobSpawnGroup.getInternalName());
			if(entityType.isPresent())
			{
				mobSpawnInfoBuilder.addSpawn(entitiClassification, new MobSpawnInfo.Spawners(entityType.get(), mobSpawnGroup.getWeight(), mobSpawnGroup.getMin(), mobSpawnGroup.getMax()));
			} else {
				if(OTG.getEngine().getLogger().getLogCategoryEnabled(LogCategory.MOBS))
				{
					OTG.getEngine().getLogger().log(LogLevel.ERROR, LogCategory.MOBS, "Could not find entity for mob: " + mobSpawnGroup.getMob() + " in BiomeConfig " + biomeName);
				}
			}
		}
	}	
	
	private static void addVanillaStructures(Builder biomeGenerationSettingsBuilder, PresetSettings presetConfig, IBiomeConfig biomeConfig)
	{
		// TODO: Currently we can only enable/disable structures per biome and use any configuration options exposed by the vanilla structure 
		// classes (size for villages fe). If we want to be able to customise more, we'll need to implement our own structure classes.
		// TODO: Allow users to create their own jigsaw patterns (for villages, end cities, pillager outposts etc)?
		// TODO: Amethyst Geodes (1.17?)	
		StructureSettings structureSettings = presetConfig.getStructureSettings();
		BiomeStructureSettings biomeStructureSettings = biomeConfig.getStructureSettings();
		// Villages
		// TODO: Allow spawning multiple types in a single biome?
		if(structureSettings.isVillagesEnabled() && biomeStructureSettings.getVillageType() != VillageType.disabled)
		{
			int villageSize = biomeStructureSettings.getVillageSize();
			VillageType villageType = biomeStructureSettings.getVillageType();
			StructureFeature<VillageConfig, ? extends Structure<VillageConfig>> customVillage = register(
				((OTGBiomeResourceLocation)biomeConfig.getRegistryKey()).withBiomeResource("village").toResourceLocationString(),
				Structure.VILLAGE.configured(
					new VillageConfig(
						() -> {
							switch(villageType)
							{
								case sandstone:
									return DesertVillagePools.START;
								case savanna:
									return SavannaVillagePools.START;
								case taiga:
									return TaigaVillagePools.START;
								case wood:
									return PlainsVillagePools.START;
								case snowy:
									return SnowyVillagePools.START;
								case disabled: // Should never happen
									break;
							}
							return PlainsVillagePools.START;
						},
						villageSize
					)
				)
			);
			biomeGenerationSettingsBuilder.addStructureStart(customVillage);
		}
		
		// Strongholds
		if(structureSettings.isStrongholdsEnabled() && biomeStructureSettings.isStrongholdsEnabled())
		{
			biomeGenerationSettingsBuilder.addStructureStart(StructureFeatures.STRONGHOLD);
		}

		// Ocean Monuments
		if(structureSettings.isOceanMonumentsEnabled() && biomeStructureSettings.isOceanMonumentsEnabled())
		{
			biomeGenerationSettingsBuilder.addStructureStart(StructureFeatures.OCEAN_MONUMENT);
		}
		
		// Rare buildings
		// TODO: Allow spawning multiple types in a single biome?
		if(structureSettings.isRareBuildingsEnabled() && biomeStructureSettings.getRareBuildingType() != RareBuildingType.disabled)
		{
			switch(biomeStructureSettings.getRareBuildingType())
			{
				case desertPyramid:
					biomeGenerationSettingsBuilder.addStructureStart(StructureFeatures.DESERT_PYRAMID);
					break;
				case igloo:
					biomeGenerationSettingsBuilder.addStructureStart(StructureFeatures.IGLOO);
					break;
				case jungleTemple:
					biomeGenerationSettingsBuilder.addStructureStart(StructureFeatures.JUNGLE_TEMPLE);				
					break;
				case swampHut:
					biomeGenerationSettingsBuilder.addStructureStart(StructureFeatures.SWAMP_HUT);
					break;
				case disabled:
					break;					
			}
		}
		
		// Woodland Mansions
		if(structureSettings.isWoodlandMansionsEnabled() && biomeStructureSettings.isWoodlandMansionsEnabled())
		{
			biomeGenerationSettingsBuilder.addStructureStart(StructureFeatures.WOODLAND_MANSION);
		}
		
		// Nether Fortresses
		if(structureSettings.isNetherFortressesEnabled() && biomeStructureSettings.isNetherFortressesEnabled())
		{
			biomeGenerationSettingsBuilder.addStructureStart(StructureFeatures.NETHER_BRIDGE);
		}

		// Mineshafts
		if(structureSettings.isMineshaftsEnabled() && biomeStructureSettings.getMineshaftType() != MineshaftType.disabled)
		{
			float mineShaftProbability = biomeStructureSettings.getMineshaftProbability();
			MineshaftType mineShaftType = biomeStructureSettings.getMineshaftType();
			StructureFeature<MineshaftConfig, ? extends Structure<MineshaftConfig>> customMineShaft = register(
				((OTGBiomeResourceLocation)biomeConfig.getRegistryKey()).withBiomeResource("mineshaft").toResourceLocationString(),
				Structure.MINESHAFT.configured(
					new MineshaftConfig(
						mineShaftProbability,
						mineShaftType == MineshaftType.mesa ? MineshaftStructure.Type.MESA : MineshaftStructure.Type.NORMAL
					)
				)
			);
			biomeGenerationSettingsBuilder.addStructureStart(customMineShaft);
		}
		
		// Buried Treasure
		if(structureSettings.isBuriedTreasureEnabled() && biomeStructureSettings.isBuriedTreasureEnabled())
		{
			float buriedTreasureProbability = biomeStructureSettings.getBuriedTreasureProbability();
			StructureFeature<ProbabilityConfig, ? extends Structure<ProbabilityConfig>> customBuriedTreasure = register(
				((OTGBiomeResourceLocation)biomeConfig.getRegistryKey()).withBiomeResource("buried_treasure").toResourceLocationString(),
				Structure.BURIED_TREASURE.configured(new ProbabilityConfig(buriedTreasureProbability))
			);
			biomeGenerationSettingsBuilder.addStructureStart(customBuriedTreasure);
		}
		
		// Ocean Ruins
		if(structureSettings.isOceanRuinsEnabled() && biomeStructureSettings.getOceanRuinsType() != OceanRuinsType.disabled)
		{
			float oceanRuinsLargeProbability = biomeStructureSettings.getOceanRuinsLargeProbability();
			float oceanRuinsClusterProbability = biomeStructureSettings.getOceanRuinsClusterProbability();
			OceanRuinsType oceanRuinsType = biomeStructureSettings.getOceanRuinsType();
			StructureFeature<OceanRuinConfig, ? extends Structure<OceanRuinConfig>> customOceanRuins = register(
				((OTGBiomeResourceLocation)biomeConfig.getRegistryKey()).withBiomeResource("ocean_ruin").toResourceLocationString(),
				Structure.OCEAN_RUIN.configured(
					new OceanRuinConfig(
						oceanRuinsType == OceanRuinsType.cold ? OceanRuinStructure.Type.COLD : OceanRuinStructure.Type.WARM,
						oceanRuinsLargeProbability,
						oceanRuinsClusterProbability
					)
				)
			);
			biomeGenerationSettingsBuilder.addStructureStart(customOceanRuins);
		}

		// Shipwrecks
		// TODO: Allowing both types in the same biome, make sure this won't cause problems.
		if(structureSettings.isShipWrecksEnabled())
		{
			if(biomeStructureSettings.isShipWreckEnabled())
			{
				biomeGenerationSettingsBuilder.addStructureStart(StructureFeatures.SHIPWRECK);
			}
			if(biomeStructureSettings.isShipWreckBeachedEnabled())
			{
				biomeGenerationSettingsBuilder.addStructureStart(StructureFeatures.SHIPWRECH_BEACHED);
			}			
		}
		
		// Pillager Outpost
		if(structureSettings.isPillagerOutpostsEnabled() && biomeStructureSettings.isPillagerOutpostEnabled())
		{
			int outpostSize = biomeStructureSettings.getPillagerOutPostSize();
			StructureFeature<VillageConfig, ? extends Structure<VillageConfig>> customOutpost = register(
				((OTGBiomeResourceLocation)biomeConfig.getRegistryKey()).withBiomeResource("pillager_outpost").toResourceLocationString(), 
				Structure.PILLAGER_OUTPOST.configured(
					new VillageConfig(
						() -> {
							return PillagerOutpostPools.START;
						},
						outpostSize
					)
				)
			);
			biomeGenerationSettingsBuilder.addStructureStart(customOutpost);			
		}
		
		// Bastion Remnants
		if(structureSettings.isBastionRemnantsEnabled() && biomeStructureSettings.isBastionRemnantEnabled())
		{
			int bastionRemnantSize = biomeStructureSettings.getBastionRemnantSize();
			StructureFeature<VillageConfig, ? extends Structure<VillageConfig>> customBastionRemnant = register(
				((OTGBiomeResourceLocation)biomeConfig.getRegistryKey()).withBiomeResource("bastion_remnant").toResourceLocationString(), 
				Structure.BASTION_REMNANT.configured(
					new VillageConfig(
						() -> {
							return BastionRemnantsPieces.START;
						},
						bastionRemnantSize
					)
				)
			);
			biomeGenerationSettingsBuilder.addStructureStart(customBastionRemnant);
		}
		
		// Nether Fossils
		if(structureSettings.isNetherFossilsEnabled() && biomeStructureSettings.isNetherFossilEnabled())
		{
			biomeGenerationSettingsBuilder.addStructureStart(StructureFeatures.NETHER_FOSSIL);
		}
		
		// End Cities
		if(structureSettings.isEndCitiesEnabled() && biomeStructureSettings.isEndCityEnabled())
		{
			biomeGenerationSettingsBuilder.addStructureStart(StructureFeatures.END_CITY);
		}
		
		// Ruined Portals
		if(structureSettings.isRuinedPortalsEnabled() && biomeStructureSettings.getRuinedPortalType() != RuinedPortalType.disabled)
		{
			switch(biomeStructureSettings.getRuinedPortalType())
			{
				case normal:
					biomeGenerationSettingsBuilder.addStructureStart(StructureFeatures.RUINED_PORTAL_STANDARD);
					break;
				case desert:
					biomeGenerationSettingsBuilder.addStructureStart(StructureFeatures.RUINED_PORTAL_DESERT);
					break;
				case jungle:
					biomeGenerationSettingsBuilder.addStructureStart(StructureFeatures.RUINED_PORTAL_JUNGLE);
					break;
				case swamp:
					biomeGenerationSettingsBuilder.addStructureStart(StructureFeatures.RUINED_PORTAL_SWAMP);
					break;
				case mountain:
					biomeGenerationSettingsBuilder.addStructureStart(StructureFeatures.RUINED_PORTAL_MOUNTAIN);
					break;
				case ocean:
					biomeGenerationSettingsBuilder.addStructureStart(StructureFeatures.RUINED_PORTAL_OCEAN);
					break;
				case nether:
					biomeGenerationSettingsBuilder.addStructureStart(StructureFeatures.RUINED_PORTAL_NETHER);
					break;
				case disabled:
					break;
			}
		}
	}

	// StructureFeatures.register()
	private static <FC extends IFeatureConfig, F extends Structure<FC>> StructureFeature<FC, F> register(String name, StructureFeature<FC, F> structure)
	{
		return WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE, name, structure);
	}
	
	private static int getSkyColorForTemp(float p_244206_0_)
	{
		float lvt_1_1_ = p_244206_0_ / 3.0F;
		lvt_1_1_ = MathHelper.clamp(lvt_1_1_, -1.0F, 1.0F);
		return MathHelper.hsvToRgb(0.62222224F - lvt_1_1_ * 0.05F, 0.5F + lvt_1_1_ * 0.1F, 1.0F);
	}
}
