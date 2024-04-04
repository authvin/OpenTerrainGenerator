package com.pg85.otg.interfaces;

import java.util.List;

import com.pg85.otg.settings.biome.*;
import com.pg85.otg.util.biome.ReplaceBlockMatrix;
import com.pg85.otg.util.gen.ChunkBuffer;
import com.pg85.otg.util.gen.GeneratingChunk;
import com.pg85.otg.util.materials.LocalMaterialData;
import com.pg85.otg.util.minecraft.SaplingType;

/**
 * BiomeConfig (*.bc) classes
 * 
 * IBiomeConfig defines anything that's used/exposed between projects.
 * BiomeConfigBase implements anything needed for IBiomeConfig. 
 * BiomeConfig contains only fields/methods used for io/serialisation/instantiation.
 * 
 * BiomeConfig should be used only in common-core and platform-specific layers, when reading/writing settings on app start.
 * IBiomeConfig should be used wherever settings are used in code. 
 */
public interface IBiomeConfig
{
	BiomeBlockSettings getBlockSettings();
	IdentitySettings getIdentitySettings();
	MobSettings getMobSettings();
	PlacementSettings getPlacementSettings();
	BiomeStructureSettings getStructureSettings();
	BiomeTerrainSettings getTerrainSettings();
	BiomeVisualSettings getVisualSettings();


	// Misc

	IBiomeResourceLocation getRegistryKey();
	void setOTGBiomeId(int id);
	int getOTGBiomeId();
	void setRegistryKey(IBiomeResourceLocation registryKey);

	// PresetConfig getters
	// TODO: Ideally, don't contain presetConfig within biomeconfig,  
	// use a parent object that holds both, like a worldgenregion.

	boolean biomeConfigsHaveReplacement();
	boolean isFlatBedrock();
	boolean isCeilingBedrock();
	boolean isBedrockDisabled();
	boolean isRemoveSurfaceStone();

	// Inheritance

	List<String> getBiomeDictTags();
	boolean getIsTemplateForBiome();
	
	// Height / volatility
	double getCHCData(int y);
	
	// Rivers
	
	String getRiverBiome();
	
	// Blocks

	// Any blocks spawned/checked during base terrain gen that use the biomeconfig materials
	// call getXXXBlockReplaced to get the replaced blocks.
	// Any blocks spawned during decoration will have their materials parsed before spawning
	// them via world.setBlock(), so they use the default biomeconfig materials.	
	
	// Note: getSurfaceBlockReplaced / getGroundBlockReplaced don't take into
	// account SAGC, so they should only be used by surfacegenerators.
	
	LocalMaterialData getSurfaceBlockAtHeight(ISurfaceGeneratorNoiseProvider noiseProvider, int x, int y, int z);
	LocalMaterialData getGroundBlockAtHeight(ISurfaceGeneratorNoiseProvider noiseProvider, int x, int y, int z);
	LocalMaterialData getSurfaceBlockReplaced(int y);
	LocalMaterialData getGroundBlockReplaced(int y);
	LocalMaterialData getStoneBlockReplaced(int y);
	LocalMaterialData getBedrockBlockReplaced(int y);
	LocalMaterialData getSandStoneBlockReplaced(int y);
	LocalMaterialData getDefaultGroundBlock();
	LocalMaterialData getDefaultStoneBlock();
	LocalMaterialData getDefaultWaterBlock();
	void doSurfaceAndGroundControl(long worldSeed, GeneratingChunk generatingChunk, ChunkBuffer chunkBuffer, int x, int z, IBiome biome);
	boolean hasReplaceBlocksSettings();
	ReplaceBlockMatrix getReplaceBlocks();
	
	// Water / lava / freezing
	
	int getWaterLevelMax();
	int getWaterLevelMin();
	LocalMaterialData getWaterBlockReplaced(int y);
	LocalMaterialData getUnderWaterSurfaceBlockReplaced(int y);	
	LocalMaterialData getIceBlockReplaced(int y);
	LocalMaterialData getPackedIceBlockReplaced(int y);
	LocalMaterialData getSnowBlockReplaced(int y);
	LocalMaterialData getCooledLavaBlockReplaced(int y);
	
	// Visuals / weather

	boolean useFrozenOceanTemperature();
	int getSnowHeight(float tempAtBlockToFreeze);

	// OTG Custom structures (BO's)
	
	List<List<String>> getCustomStructureNames();
	List<ICustomStructureGen> getCustomStructures();
	ICustomStructureGen getStructureGen();
	void setStructureGen(ICustomStructureGen customStructureGen);

	
	// Saplings
	
	ISaplingSpawner getSaplingGen(SaplingType type);
	ISaplingSpawner getCustomSaplingGen(LocalMaterialData materialData, boolean wideTrunk);
	
	// Misc
	
	public IBiomeConfig createTemplateBiome();
}
