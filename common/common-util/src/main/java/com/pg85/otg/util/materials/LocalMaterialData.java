package com.pg85.otg.util.materials;

import com.pg85.otg.util.biome.ReplacedBlocksMatrix;

/**
 * Represents one of Minecraft's materials.
 * Immutable.
 */
public abstract class LocalMaterialData extends LocalMaterialBase
{
	protected String rawEntry;
	protected boolean isBlank = false;
	protected boolean checkedFallbacks = false;
	protected boolean parsedDefaultMaterial = false;
	protected LocalMaterialData[] rotations = new LocalMaterialData[] {this, null, null, null};
	protected LocalMaterialData rotated = null;

	public abstract <T extends Comparable<T>> LocalMaterialData withProperty(MaterialProperty<T> state, T value);
	
	public abstract String getName();
	
    public abstract boolean canSnowFallOn();
    
    public abstract boolean canFall();
	
    public abstract boolean isMaterial(LocalMaterialData material);
	
	public abstract boolean isBlockTag(LocalMaterialTag tag);
    
    public abstract boolean isLiquid();

    public abstract boolean isSolid();

    public abstract boolean isEmptyOrAir();
    
    public abstract boolean isAir();

    public abstract boolean isEmpty();
       
    public boolean isLogOrLeaves()
    {
		return isLog() || isLeaves();
    }
    
    public boolean isLog()
    {
		return
			isMaterial(LocalMaterials.ACACIA_LOG) ||
			isMaterial(LocalMaterials.BIRCH_LOG) ||
			isMaterial(LocalMaterials.DARK_OAK_LOG) ||
			isMaterial(LocalMaterials.OAK_LOG) ||
			isMaterial(LocalMaterials.SPRUCE_LOG) ||
			isMaterial(LocalMaterials.STRIPPED_ACACIA_LOG) ||
			isMaterial(LocalMaterials.STRIPPED_BIRCH_LOG) ||
			isMaterial(LocalMaterials.STRIPPED_DARK_OAK_LOG) ||
			isMaterial(LocalMaterials.STRIPPED_JUNGLE_LOG) ||
			isMaterial(LocalMaterials.STRIPPED_OAK_LOG) ||
			isMaterial(LocalMaterials.STRIPPED_SPRUCE_LOG);
	}
        
    /**
     * Gets whether this material can be used as an anchor point for a smoothing area    
     * @return True if this material is a solid block, false if it is a tile-entity, half-slab, stairs(?), water, wood or leaves.
     */    
    public boolean isSmoothAreaAnchor(boolean allowWood, boolean ignoreWater)
    {
    	return
			(
				isSolid() || 
				(
					!ignoreWater && isLiquid()
				)
			) || (
	    		(
					isMaterial(LocalMaterials.ICE) ||
					isMaterial(LocalMaterials.PACKED_ICE) ||
					isMaterial(LocalMaterials.FROSTED_ICE) ||
					(
						isSolid() || 
						(
							!ignoreWater && isLiquid()
						)
					)
				) && (
					allowWood || 
					!(
						isLog()
					)
				) &&
				!isMaterial(LocalMaterials.WATER_LILY)
			);
    }
    
	public boolean isOre()
	{
    	return
			isMaterial(LocalMaterials.COAL_ORE) ||
			isMaterial(LocalMaterials.DIAMOND_ORE) ||
			isMaterial(LocalMaterials.EMERALD_ORE) ||
			isMaterial(LocalMaterials.GLOWING_REDSTONE_ORE) ||
			isMaterial(LocalMaterials.GOLD_ORE) ||
			isMaterial(LocalMaterials.IRON_ORE) ||
			isMaterial(LocalMaterials.LAPIS_ORE) ||
			isMaterial(LocalMaterials.QUARTZ_ORE) ||
			isMaterial(LocalMaterials.REDSTONE_ORE)
		;
	}
	
    public boolean isLeaves()
    {
		return
			isMaterial(LocalMaterials.ACACIA_LEAVES) ||
			isMaterial(LocalMaterials.BIRCH_LEAVES) ||
			isMaterial(LocalMaterials.DARK_OAK_LEAVES) ||
			isMaterial(LocalMaterials.JUNGLE_LEAVES) ||
			isMaterial(LocalMaterials.OAK_LEAVES) ||
			isMaterial(LocalMaterials.SPRUCE_LEAVES);
    }

    /**
     * Gets a new material that is rotated 90 degrees. North -> west -> south ->
     * east. If this material cannot be rotated, the material itself is
     * returned.
     * 
     * @return The rotated material.
     */
    public LocalMaterialData rotate()
    {
    	return rotate(1);
    }
    
    /**
     * Gets a new material that is rotated 90 degrees. North -> west -> south ->
     * east. If this material cannot be rotated, the material itself is
     * returned.
     * 
     * @return The rotated material.
     */
    public abstract LocalMaterialData rotate(int rotateTimes);
      
	public LocalMaterialData parseWithBiomeAndHeight(boolean biomeConfigsHaveReplacement, ReplacedBlocksMatrix replaceBlocks, int y)
	{	
        if (!biomeConfigsHaveReplacement)
        {
            // Don't waste time here, ReplacedBlocks is empty everywhere
            return this;
        }
        return replaceBlocks.replaceBlock(y, this);
	}

	@Override
	public boolean isTag()
	{		
		return false;
	}
	
    @Override
    public String toString()
    {
    	return getName();
    }
	
    public abstract boolean equals(Object other);
    
    public abstract int hashCode();    
}
