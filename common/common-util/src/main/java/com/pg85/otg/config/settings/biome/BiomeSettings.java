package com.pg85.otg.config.settings.biome;

import com.pg85.otg.config.ConfigFile;
import com.pg85.otg.config.ConfigFunction;
import com.pg85.otg.interfaces.IBiomeResourceLocation;
import com.pg85.otg.interfaces.ICustomStructureGen;
import com.pg85.otg.interfaces.ISaplingSpawner;
import com.pg85.otg.util.materials.LocalMaterialData;
import com.pg85.otg.util.minecraft.SaplingType;
import lombok.Getter;

import java.util.List;

/**
 * BiomeConfig (*.bc) classes
 * <p>
 * IBiomeConfig defines anything that's used/exposed between projects.
 * BiomeConfigBase implements anything needed for IBiomeConfig.
 * BiomeConfig contains only fields/methods used for io/serialisation/instantiation.
 * <p>
 * BiomeConfig should be used only in common-core and platform-specific layers, when reading/writing settings on app start.
 * IBiomeConfig should be used wherever settings are used in code.
 */
@Getter
public abstract class BiomeSettings implements ConfigFile {
    protected IdentitySettings identitySettings;
    protected MobSettings mobSettings;
    protected BiomeGenerationSettings generationSettings;
    protected BiomeStructureSettings structureSettings;
    protected BiomeTerrainSettings terrainSettings;
    protected BiomeVisualSettings visualSettings;
    protected SurfaceSettings surfaceSettings;
    protected BiomeResourceSettings resourceSettings;

    private final String configName;
    protected ICustomStructureGen structureGen;

    protected BiomeSettings(String configName) {
        this.configName = configName;
    }

    @Override
    public String getConfigName() {
        return configName;
    }

    // Misc

    abstract public IBiomeResourceLocation getRegistryKey();

    abstract public void setRegistryKey(IBiomeResourceLocation registryKey);

    abstract public int getOldOTGBiomeID();

    abstract public void setOTGBiomeId(int id);

    // PresetConfig getters
    // TODO: Ideally, don't contain presetConfig within biomeconfig,
    // use a parent object that holds both, like a worldgenregion.

    abstract public boolean biomeConfigsHaveReplacement();

    // Inheritance

    abstract public List<String> getBiomeDictTags();

    abstract public boolean getIsTemplateForBiome();

    // Height / volatility
    abstract public double getCHCData(int y);


    // OTG Custom structures (BO's)

    abstract public List<List<String>> getCustomStructureNames();

    abstract public void setStructureGen(ICustomStructureGen customStructureGen);


    // Saplings

    abstract public ISaplingSpawner getSaplingGen(SaplingType type);

    abstract public ISaplingSpawner getCustomSaplingGen(LocalMaterialData materialData, boolean wideTrunk);

    // Misc

    //abstract public BiomeSettings createTemplateBiome();

    abstract public List<ConfigFunction<BiomeSettings>> getResourceQueue();
}
