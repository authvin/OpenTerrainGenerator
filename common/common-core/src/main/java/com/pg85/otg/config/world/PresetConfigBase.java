package com.pg85.otg.config.world;

import com.pg85.otg.config.ConfigFile;
import com.pg85.otg.constants.settings.BiomeMode;
import com.pg85.otg.constants.settings.ConfigMode;
import com.pg85.otg.constants.settings.ImageMode;
import com.pg85.otg.constants.settings.ImageOrientation;
import com.pg85.otg.constants.settings.structure.CustomStructureType;
import com.pg85.otg.interfaces.IPresetConfig;
import com.pg85.otg.settings.preset.*;
import com.pg85.otg.util.biome.ReplaceBlockMatrix;
import com.pg85.otg.util.materials.LocalMaterialData;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalLong;

/**
 * PresetConfig.ini classes
 * <p>
 * IPresetConfig defines anything that's used/exposed between projects.
 * PresetConfigBase implements anything needed for IPresetConfig.
 * PresetConfig contains only fields/methods used for io/serialisation/instantiation.
 * <p>
 * PresetConfig should be used only in common-core and platform-specific layers, when reading/writing settings on app start.
 * IPresetConfig should be used wherever settings are used in code.
 */
abstract class PresetConfigBase extends ConfigFile implements IPresetConfig {
    // Misc

    protected ConfigMode settingsMode;

    protected int majorVersion;
    protected int minorVersion;
    protected String author;
    protected String description;
    protected String shortPresetName;

    // Visual settings

    protected int worldFogColor;

    // Biome resources

    protected boolean disableOreGen;
    protected boolean disableBedrock;

    // Blocks

    protected boolean removeSurfaceStone;
    protected LocalMaterialData waterBlock;
    protected LocalMaterialData bedrockBlock;
    protected LocalMaterialData cooledLavaBlock;
    protected LocalMaterialData iceBlock;
    protected LocalMaterialData carverLavaBlock;

    // Bedrock

    protected boolean ceilingBedrock;
    protected boolean flatBedrock;
    protected int carverLavaBlockHeight;

    // Biome settings

    protected ArrayList<String> worldBiomes = new ArrayList<String>();
    protected List<String> blackListedBiomes = new ArrayList<String>();
    protected int biomeRarityScale;
    protected boolean oldGroupRarity;
    protected boolean oldLandRarity;
    protected int generationDepth;
    protected int landFuzzy;
    protected int landRarity;
    protected int landSize;
    protected boolean forceLandAtSpawn;
    protected int oceanBiomeSize;
    protected String defaultOceanBiome;
    protected String defaultWarmOceanBiome;
    protected String defaultLukewarmOceanBiome;
    protected String defaultColdOceanBiome;
    protected String defaultFrozenOceanBiome;
    protected BiomeMode biomeMode;
    protected double frozenOceanTemperature;
    protected List<String> isleBiomes = new ArrayList<String>();
    protected List<String> borderBiomes = new ArrayList<String>();
    protected boolean randomRivers;
    protected int riverRarity;
    protected int riverSize;
    protected boolean riversEnabled;
    protected boolean biomeConfigsHaveReplacement = false;
    protected boolean improvedBorderDecoration = false;

    // Terrain settings

    protected double fractureHorizontal;
    protected double fractureVertical;
    protected int worldHeightCap;
    protected int worldHeightScale;
    protected int maxSmoothRadius = 2;
    protected boolean betterSnowFall;
    protected int waterLevelMin;
    protected int waterLevelMax;

    // FromImageMode

    protected ImageOrientation imageOrientation;
    protected String imageFile;
    protected String imageFillBiome;
    protected ImageMode imageMode;
    protected int imageXOffset;
    protected int imageZOffset;



    // OTG Custom structures

    protected String bo3AtSpawn;
    protected CustomStructureType customStructureType;
    protected boolean useOldBO3StructureRarity;
    protected boolean decorationBoundsCheck;
    protected int maximumCustomStructureRadius;

    // Caves & Ravines

    protected boolean cavesEnabled;
    protected int caveFrequency;
    protected int caveRarity;
    protected boolean evenCaveDistribution;
    protected int caveMinAltitude;
    protected int caveMaxAltitude;
    protected int caveSystemFrequency;
    protected int individualCaveRarity;
    protected int caveSystemPocketChance;
    protected int caveSystemPocketMinSize;
    protected int caveSystemPocketMaxSize;

    protected boolean ravinesEnabled;
    protected int ravineRarity;
    protected int ravineMinLength;
    protected int ravineMaxLength;
    protected double ravineDepth;
    protected int ravineMinAltitude;
    protected int ravineMaxAltitude;

    protected GameRuleSettings gameRuleSettings;
    protected StructureSettings structureSettings;
    protected CarverSettings carverSettings;
    protected SpawnSettings spawnSettings;
    protected PortalSettings portalSettings;
    protected DimensionSettings dimensionSettings;
    protected BiomeSettings biomeSettings;
    protected TerrainSettings terrainSettings;
    protected ImageSettings imageSettings;
    protected CustomStructureSettings customStructureSettings;
    protected VisualSettings visualSettings;
    protected PresetInfo presetInfo;
    protected ResourceSettings resourceSettings;
    protected BlockSettings blockSettings;
    protected BedrockSettings bedrockSettings;

    protected PresetConfigBase(String configName) {
        super(configName);
    }


    @Override
    public LocalMaterialData getBedrockBlockReplaced(ReplaceBlockMatrix replaceBlocks, int y) {
        if (replaceBlocks.replacesBedrock) {
            return this.bedrockBlock.parseWithBiomeAndHeight(this.biomeConfigsHaveReplacement, replaceBlocks, y);
        }
        return this.bedrockBlock;
    }

    @Override
    public boolean setBiomeConfigsHaveReplacement(boolean biomeConfigsHaveReplacement) {
        return this.biomeConfigsHaveReplacement = biomeConfigsHaveReplacement;
    }

    @Override
    public void setMaxSmoothRadius(int smoothRadius) {
        this.maxSmoothRadius = smoothRadius;
    }

    @Override
    public BedrockSettings getBedrockSettings() {
        return bedrockSettings;
    }

    @Override
    public BiomeSettings getBiomeSettings() {
        return biomeSettings;
    }

    @Override
    public BlockSettings getBlockSettings() {
        return blockSettings;
    }

    @Override
    public CarverSettings getCarverSettings() {
        return carverSettings;
    }

    @Override
    public CustomStructureSettings getCustomStructureSettings() {
        return customStructureSettings;
    }

    @Override
    public DimensionSettings getDimensionSettings() {
        return dimensionSettings;
    }

    @Override
    public GameRuleSettings getGameRuleSettings() {
        return gameRuleSettings;
    }

    @Override
    public ImageSettings getImageSettings() {
        return imageSettings;
    }

    @Override
    public PortalSettings getPortalSettings() {
        return portalSettings;
    }

    @Override
    public PresetInfo getPresetInfo() {
        return presetInfo;
    }

    @Override
    public ResourceSettings getResourceSettings() {
        return resourceSettings;
    }

    @Override
    public SpawnSettings getSpawnSettings() {
        return spawnSettings;
    }

    @Override
    public StructureSettings getStructureSettings() {
        return structureSettings;
    }

    @Override
    public TerrainSettings getTerrainSettings() {
        return terrainSettings;
    }

    @Override
    public VisualSettings getVisualSettings() {
        return visualSettings;
    }
}
