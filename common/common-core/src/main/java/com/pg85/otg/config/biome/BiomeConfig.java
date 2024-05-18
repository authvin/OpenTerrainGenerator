package com.pg85.otg.config.biome;

import java.nio.file.Path;
import java.util.*;

import com.pg85.otg.OTG;
import com.pg85.otg.config.ConfigFunction;
import com.pg85.otg.config.biome.BiomeConfigFinder.BiomeConfigStub;
import com.pg85.otg.config.io.IConfigFunctionProvider;
import com.pg85.otg.config.io.SettingsMap;
import com.pg85.otg.config.settings.biome.*;
import com.pg85.otg.config.settings.preset.PresetSettings;
import com.pg85.otg.config.standard.BiomeStandardValues;
import com.pg85.otg.config.preset.PresetConfig;
import com.pg85.otg.constants.settings.GrassColorModifier;
import com.pg85.otg.constants.settings.TemplateBiomeType;
import com.pg85.otg.constants.settings.structure.*;
import com.pg85.otg.customobject.resource.CustomObjectResource;
import com.pg85.otg.customobject.resource.CustomStructureResource;
import com.pg85.otg.customobject.resource.SaplingResource;
import com.pg85.otg.customobject.resource.TreeResource;
import com.pg85.otg.gen.resource.*;
import com.pg85.otg.gen.surface.SurfaceGeneratorSetting;
import com.pg85.otg.interfaces.*;
import com.pg85.otg.util.biome.*;
import com.pg85.otg.util.materials.LocalMaterialData;
import com.pg85.otg.util.minecraft.EntityCategory;
import com.pg85.otg.util.minecraft.SaplingType;
import lombok.Getter;
import lombok.Setter;

/**
 * BiomeConfig (*.bc) classes
 * 
 * IBiomeConfig defines anything that's used/exposed between projects.
 * BiomeConfigBase implements anything needed for IBiomeConfig. BiomeConfig
 * contains only fields/methods used for io/serialisation/instantiation.
 * 
 * BiomeConfig should be used only in common-core and platform-specific layers,
 * when reading/writing settings on app start. IBiomeConfig should be used
 * wherever settings are used in code.
 */
public class BiomeConfig extends BiomeSettings
{
	public static final HashMap<String, Class<? extends ConfigFunction<?>>> RESOURCE_QUEUE_RESOURCES = new HashMap<>();
	static
	{
		RESOURCE_QUEUE_RESOURCES.put("AboveWaterRes", AboveWaterResource.class);
		RESOURCE_QUEUE_RESOURCES.put("Boulder", BoulderResource.class);
		RESOURCE_QUEUE_RESOURCES.put("Cactus", CactusResource.class);
		RESOURCE_QUEUE_RESOURCES.put("Dungeon", DungeonResource.class);
		RESOURCE_QUEUE_RESOURCES.put("Grass", GrassResource.class);
		RESOURCE_QUEUE_RESOURCES.put("Fossil", FossilResource.class);
		RESOURCE_QUEUE_RESOURCES.put("IceSpike", IceSpikeResource.class);
		RESOURCE_QUEUE_RESOURCES.put("Liquid", LiquidResource.class);
		RESOURCE_QUEUE_RESOURCES.put("Ore", OreResource.class);
		RESOURCE_QUEUE_RESOURCES.put("Plant", PlantResource.class);
		RESOURCE_QUEUE_RESOURCES.put("UnderWaterPlant", UnderWaterPlantResource.class);
		RESOURCE_QUEUE_RESOURCES.put("Reed", ReedResource.class);
		RESOURCE_QUEUE_RESOURCES.put("SmallLake", SmallLakeResource.class);
		RESOURCE_QUEUE_RESOURCES.put("SurfacePatch", SurfacePatchResource.class);
		RESOURCE_QUEUE_RESOURCES.put("UndergroundLake", UndergroundLakeResource.class);
		RESOURCE_QUEUE_RESOURCES.put("UnderWaterOre", UnderWaterOreResource.class);
		RESOURCE_QUEUE_RESOURCES.put("Vein", VeinResource.class);
		RESOURCE_QUEUE_RESOURCES.put("Vines", VinesResource.class);
		RESOURCE_QUEUE_RESOURCES.put("Well", WellResource.class);
		RESOURCE_QUEUE_RESOURCES.put("CustomObject", CustomObjectResource.class);
		RESOURCE_QUEUE_RESOURCES.put("CustomStructure", CustomStructureResource.class);
		RESOURCE_QUEUE_RESOURCES.put("Sapling", SaplingResource.class);
		RESOURCE_QUEUE_RESOURCES.put("Tree", TreeResource.class);
		RESOURCE_QUEUE_RESOURCES.put("Bamboo", BambooResource.class);
		RESOURCE_QUEUE_RESOURCES.put("SeaGrass", SeaGrassResource.class);
		RESOURCE_QUEUE_RESOURCES.put("Kelp", KelpResource.class);
		RESOURCE_QUEUE_RESOURCES.put("SeaPickle", SeaPickleResource.class);
		RESOURCE_QUEUE_RESOURCES.put("Registry", RegistryResource.class);
		RESOURCE_QUEUE_RESOURCES.put("CoralMushroom", CoralMushroomResource.class);
		RESOURCE_QUEUE_RESOURCES.put("CoralTree", CoralTreeResource.class);
		RESOURCE_QUEUE_RESOURCES.put("CoralClaw", CoralClawResource.class);
		RESOURCE_QUEUE_RESOURCES.put("Iceberg", IcebergResource.class);
		RESOURCE_QUEUE_RESOURCES.put("BasaltColumn", BasaltColumnResource.class);
	}

	public BiomeConfig(String biomeName) {
		super(biomeName);
	}

	public BiomeConfig(String biomeName, SettingsMap settingsMap, IMaterialReader materialReader, PresetSettings presetSettings, IConfigFunctionProvider biomeResourcesManager, String presetFolderName)
	{
		super(biomeName);
		renameOldSettings(settingsMap, OTG.getEngine().getLogger(), materialReader);
		identitySettings = IdentitySettings.buildIdentitySettings(settingsMap, biomeName);
		mobSettings = MobSettings.getMobSettings(settingsMap);
		generationSettings = BiomeGenerationSettings.getPlacementSettings(settingsMap, presetSettings.getGenerationSettings());
		structureSettings = BiomeStructureSettings.getBiomeStructureSettings(settingsMap, presetSettings.getStructureSettings());
		terrainSettings = BiomeTerrainSettings.getBiomeTerrainSettings(settingsMap, presetSettings.getTerrainSettings());
		visualSettings = BiomeVisualSettings.getBiomeVisualSettings(settingsMap, presetSettings.getVisualSettings());

		surfaceSettings = SurfaceSettings.getSurfaceSettings(
				settingsMap,
				materialReader,
				SurfaceGeneratorSetting.SURFACE_AND_GROUND_CONTROL,
				presetSettings.getBlockSettings());

		resourceSettings = BiomeResourceSettings.getResourceSettings(
				settingsMap,
				presetSettings.getResourceSettings(),
				new ArrayList<>(
						settingsMap.getConfigFunctions(
								this,
								biomeResourcesManager,
								materialReader,
								presetFolderName,
								OTG.getEngine().getPluginConfig())));
	}


    @Override
	public void setOTGBiomeId(int id) {
		this.otgBiomeId = id;
	}

	@Override
	public int getOTGBiomeId() {
		return this.otgBiomeId;
	}
	@Setter
    @Getter
    private IBiomeResourceLocation registryKey;
	private int otgBiomeId;

	// Private fields, only used when reading/writing

	// Settings container, used so we can copy a biomeconfig while 
	// changing only its id and registry key, used for non-otg 
	// biomes in otg worlds.
	protected com.pg85.otg.config.biome.SettingsContainer privateSettings = new com.pg85.otg.config.biome.SettingsContainer();

	public BiomeConfig(
			String biomeName, BiomeConfigStub biomeConfigStub, Path presetFolder, SettingsMap settings,
			PresetConfig presetConfig, String presetShortName, int presetMajorVersion,
			IConfigFunctionProvider biomeResourcesManager, ILogger logger, IMaterialReader materialReader
	)
	{
		super(biomeName);
		this.setRegistryKey(new OTGBiomeResourceLocation(presetFolder, presetShortName, presetMajorVersion, biomeName));

		// Mob inheritance
		// Mob spawning data was already loaded seperately before the rest of the
		// biomeconfig to make inheritance work properly
		// Forge: If this is a vanilla biome then mob spawning settings have been
		// inherited from vanilla MC biomes
		// This includes any mobs added to vanilla biomes by other mods when MC started.

		if (biomeConfigStub != null)
		{
			this.privateSettings.spawnMonsters.addAll(biomeConfigStub.getSpawner(EntityCategory.MONSTER));
			this.privateSettings.spawnCreatures.addAll(biomeConfigStub.getSpawner(EntityCategory.CREATURE));
			this.privateSettings.spawnWaterCreatures.addAll(biomeConfigStub.getSpawner(EntityCategory.WATER_CREATURE));
			this.privateSettings.spawnAmbientCreatures.addAll(biomeConfigStub.getSpawner(EntityCategory.AMBIENT_CREATURE));
			this.privateSettings.spawnWaterAmbientCreatures.addAll(biomeConfigStub.getSpawner(EntityCategory.WATER_AMBIENT));
			this.privateSettings.spawnMiscCreatures.addAll(biomeConfigStub.getSpawner(EntityCategory.MISC));

			this.settings.spawnMonstersMerged.addAll(biomeConfigStub.getSpawnerMerged(EntityCategory.MONSTER));
			this.settings.spawnCreaturesMerged.addAll(biomeConfigStub.getSpawnerMerged(EntityCategory.CREATURE));
			this.settings.spawnWaterCreaturesMerged.addAll(biomeConfigStub.getSpawnerMerged(EntityCategory.WATER_CREATURE));
			this.settings.spawnAmbientCreaturesMerged.addAll(biomeConfigStub.getSpawnerMerged(EntityCategory.AMBIENT_CREATURE));
			this.settings.spawnWaterAmbientCreaturesMerged.addAll(biomeConfigStub.getSpawnerMerged(EntityCategory.WATER_AMBIENT));
			this.settings.spawnMiscCreaturesMerged.addAll(biomeConfigStub.getSpawnerMerged(EntityCategory.MISC));
		}

		this.settings.presetConfig = presetConfig;

		this.renameOldSettings(settings, logger, materialReader);
		//this.readConfigSettings(settings, biomeResourcesManager, logger, materialReader, presetFolder.toFile().getName());
		//this.validateAndCorrectSettings();

		// Set water level
		if (this.settings.useWorldWaterLevel)
		{
			this.settings.waterLevelMax = presetConfig.getTerrainSettings().getWaterLevelMax();
			this.settings.waterLevelMin = presetConfig.getTerrainSettings().getWaterLevelMin();
			this.settings.waterBlock = presetConfig.getBlockSettings().getWaterBlock();
			this.settings.iceBlock = presetConfig.getBlockSettings().getIceBlock();
			this.settings.cooledLavaBlock = presetConfig.getBlockSettings().getCooledLavaBlock();
		} else {
			this.settings.waterLevelMax = this.privateSettings.configWaterLevelMax;
			this.settings.waterLevelMin = this.privateSettings.configWaterLevelMin;
			this.settings.waterBlock = this.privateSettings.configWaterBlock;
			this.settings.iceBlock = this.privateSettings.configIceBlock;
			this.settings.cooledLavaBlock = this.privateSettings.configCooledLavaBlock;
		}
	}

	public void writeConfigSettings(SettingsMap writer) {
		BiomeConfigWriter.writeConfigSettings(this, writer);
	}

	@Override
	public void renameOldSettings(SettingsMap settings, ILogger logger, IMaterialReader materialReader)
	{
		settings.renameOldSetting("DisableNotchHeightControl", BiomeStandardValues.DISABLE_BIOME_HEIGHT);
		settings.renameOldSetting("BiomeDictId", BiomeStandardValues.BIOME_DICT_TAGS);
		settings.renameOldSetting("IsleInBiome", BiomeStandardValues.ISLE_IN_BIOMES);
		settings.renameOldSetting("BiomeIsBorder", BiomeStandardValues.BORDER_IN_BIOMES);
	}

	@Override
	public BiomeSettings createTemplateBiome()
	{
		BiomeConfig biomeConfig = new BiomeConfig(this.getConfigName());
		biomeConfig.privateSettings = this.privateSettings;
		biomeConfig.settings = this.settings;
		return biomeConfig;
	}
	// Settings container, used so we can copy a biomeconfig while
	// changing only its id and registry key, used for non-otg
	// biomes in otg worlds.
	protected SettingsContainer settings = new SettingsContainer();

	@Override
	public List<ConfigFunction<BiomeSettings>> getResourceQueue() {
		return this.settings.resourceQueue;
	}

	@Override
	public List<List<String>> getCustomStructureNames() {
		List<List<String>> customStructureNamesByGen = new ArrayList<>();
		for (CustomStructureResource structureGens : this.settings.customStructures) {
			List<String> customStructureNames = new ArrayList<>(structureGens.objectNames);
			customStructureNamesByGen.add(customStructureNames);
		}
		return customStructureNamesByGen;
	}

	@Override
	public void setStructureGen(ICustomStructureGen customStructureGen) {
		this.structureGen = customStructureGen;
	}

	@Override
	public boolean getIsTemplateForBiome() {
		return this.settings.isTemplateForBiome;
	}

	@Override
	public boolean useFrozenOceanTemperature() {
		return this.settings.useFrozenOceanTemperature;
	}

	@Override
	public List<String> getBiomeDictTags() {
		return this.settings.biomeDictTags;
	}

	@Override
	public double getCHCData(int y) {
		return this.settings.chcData[y];
	}


	@Override
	public boolean biomeConfigsHaveReplacement() {
		return this.settings.presetConfig.isBiomeConfigsHaveReplacement();
	}
	@Override
	public String getRiverBiome() {
		return this.settings.riverBiome;
	}



	@Override
	public ISaplingSpawner getSaplingGen(SaplingType type) {
		SaplingResource gen = this.settings.saplingGrowers.get(type);
		if (gen == null && type.growsTree()) {
			gen = this.settings.saplingGrowers.get(SaplingType.All);
		}
		return gen;
	}
	static class SettingsContainer {
		// Misc
		protected boolean replacedBlocksInited = false;

		// TODO: Ideally, don't contain presetConfig within biomeconfig,
		// use a parent object that holds both, like a worldgenregion.
		protected PresetConfig presetConfig;

		// Identity

		protected boolean isTemplateForBiome;
		protected TemplateBiomeType templateBiomeType;
		protected String biomeCategory;

		// Inheritance

		protected List<String> biomeDictTags;

		// Placement

		protected int biomeSize;
		protected int biomeRarity;
		protected int biomeColor;
		protected List<String> isleInBiome;
		protected int biomeSizeWhenIsle;
		protected int biomeRarityWhenIsle;
		protected List<String> biomeIsBorder;
		protected List<String> onlyBorderNear;
		protected List<String> notBorderNear;
		protected int biomeSizeWhenBorder;

		// Height / volatility

		protected float biomeHeight;
		protected float biomeVolatility;
		protected int smoothRadius;
		protected int CHCSmoothRadius;
		protected double maxAverageHeight;
		protected double maxAverageDepth;
		protected double volatility1;
		protected double volatility2;
		protected double volatilityWeight1;
		protected double volatilityWeight2;
		protected boolean disableBiomeHeight;
		protected double[] chcData;

		// Rivers

		protected String riverBiome;

		// Blocks

		protected LocalMaterialData stoneBlock;
		protected LocalMaterialData surfaceBlock;
		protected LocalMaterialData underWaterSurfaceBlock;
		protected LocalMaterialData groundBlock;
		protected LocalMaterialData sandStoneBlock;
		protected LocalMaterialData redSandStoneBlock;
		protected ISurfaceGenerator surfaceAndGroundControl;
		protected ReplaceBlockMatrix replacedBlocks;

		// Water / lava / freezing

		protected boolean useWorldWaterLevel;
		protected int waterLevelMax;
		protected int waterLevelMin;
		protected LocalMaterialData waterBlock;
		protected LocalMaterialData iceBlock;
		protected LocalMaterialData packedIceBlock;
		protected LocalMaterialData snowBlock;
		protected LocalMaterialData cooledLavaBlock;

		// Visuals and weather

		protected float biomeTemperature;
		protected boolean useFrozenOceanTemperature;
		protected float biomeWetness;
		protected int grassColor;
		protected ColorSet grassColorControl;
		protected GrassColorModifier grassColorModifier;
		protected int foliageColor;
		protected ColorSet foliageColorControl;
		protected int skyColor;
		protected int waterColor;
		protected ColorSet waterColorControl;
		protected int fogColor;
		protected float fogDensity;
		protected int waterFogColor;
		protected String particleType;
		protected float particleProbability;

		// Music and sounds

		protected String music;
		protected int musicMinDelay;
		protected int musicMaxDelay;
		protected boolean replaceCurrentMusic;
		protected String ambientSound;
		protected String moodSound;
		protected int moodSoundDelay;
		protected int moodSearchRange;
		protected double moodOffset;
		protected String additionsSound;
		protected double additionsTickChance;

		// Custom structures

		protected List<CustomStructureResource> customStructures = new ArrayList<>(); // Used as a cache for fast querying, not saved
		protected boolean strongholdsEnabled;

		// Vanilla structures
		protected boolean oceanMonumentsEnabled;
		protected boolean woodLandMansionsEnabled;
		protected boolean netherFortressesEnabled;
		protected int villageSize;
		protected VillageType villageType;
		protected RareBuildingType rareBuildingType;
		protected MineshaftType mineshaftType = MineshaftType.normal;
		protected boolean buriedTreasureEnabled;
		protected boolean shipWreckEnabled;
		protected boolean shipWreckBeachedEnabled;
		protected boolean pillagerOutpostEnabled;
		protected boolean bastionRemnantEnabled;
		protected boolean netherFossilEnabled;
		protected boolean endCityEnabled;
		protected float mineshaftProbability;
		protected RuinedPortalType ruinedPortalType;
		protected OceanRuinsType oceanRuinsType;
		protected float oceanRuinsLargeProbability;
		protected float oceanRuinsClusterProbability;
		protected float buriedTreasureProbability;
		protected int pillagerOutpostSize;
		protected int bastionRemnantSize;
		protected List<WeightedMobSpawnGroup> spawnMonstersMerged = new ArrayList<>();

		// Mob spawning
		protected List<WeightedMobSpawnGroup> spawnCreaturesMerged = new ArrayList<>();
		protected List<WeightedMobSpawnGroup> spawnWaterCreaturesMerged = new ArrayList<>();
		protected List<WeightedMobSpawnGroup> spawnAmbientCreaturesMerged = new ArrayList<>();
		protected List<WeightedMobSpawnGroup> spawnWaterAmbientCreaturesMerged = new ArrayList<>();
		protected List<WeightedMobSpawnGroup> spawnMiscCreaturesMerged = new ArrayList<>();
		protected String inheritMobsBiomeName;
		protected List<ConfigFunction<BiomeSettings>> resourceQueue = new ArrayList<>();

		// Resources
		protected Map<SaplingType, SaplingResource> saplingGrowers = new EnumMap<>(SaplingType.class);

		// Saplings
		protected Map<LocalMaterialData, SaplingResource> customSaplingGrowers = new HashMap<>();
		protected Map<LocalMaterialData, SaplingResource> customBigSaplingGrowers = new HashMap<>();
	}

	public ISaplingSpawner getCustomSaplingGen(LocalMaterialData materialData, boolean wideTrunk) {
		if (wideTrunk) {
			ISaplingSpawner spawner = this.settings.customBigSaplingGrowers.get(materialData);
			if (spawner != null) {
				return spawner;
			}
		}
		return this.settings.customSaplingGrowers.get(materialData);
	}
}
