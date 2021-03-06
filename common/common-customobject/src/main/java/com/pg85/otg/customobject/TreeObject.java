package com.pg85.otg.customobject;

import com.pg85.otg.constants.Constants;
import com.pg85.otg.customobject.config.CustomObjectResourcesManager;
import com.pg85.otg.customobject.structures.CustomStructureCache;
import com.pg85.otg.logging.ILogger;
import com.pg85.otg.util.ChunkCoordinate;
import com.pg85.otg.util.bo3.Rotation;
import com.pg85.otg.util.interfaces.IMaterialReader;
import com.pg85.otg.util.interfaces.IModLoadedChecker;
import com.pg85.otg.util.interfaces.IWorldGenRegion;
import com.pg85.otg.util.minecraft.TreeType;

import java.nio.file.Path;
import java.util.Random;

/**
 * A Minecraft tree, viewed as a custom object.
 *
 * <p>For historical reasons, TreeObject implements {@link CustomObject} instead
 * of just {@link SpawnableObject}. We can probably refactor the Tree resource
 * to accept {@link SpawnableObject}s instead of {@link CustomObject}s, so that
 * all the extra methods are no longer needed.
 */
class TreeObject implements CustomObject
{
    private TreeType type;
    private int minHeight = Constants.WORLD_DEPTH;
    private int maxHeight = Constants.WORLD_HEIGHT - 1;

    TreeObject(TreeType type)
    {
        this.type = type;
    }

    @Override
    public boolean onEnable(String presetName, Path otgRootFolder, boolean spawnLog, ILogger logger, CustomObjectManager customObjectManager, IMaterialReader materialReader, CustomObjectResourcesManager manager, IModLoadedChecker modLoadedChecker)
    {
    	return true;
    }

    @Override
    public String getName()
    {
        return type.name();
    }

    @Override
    public boolean canSpawnAsTree()
    {
        return true;
    }
    
    // Called during population.
    @Override
    public boolean process(CustomStructureCache structureCache, IWorldGenRegion worldGenRegion, Random random, ChunkCoordinate chunkCoord)
    {
        // A tree has no frequency or rarity, so spawn it once in the chunk
    	// Make sure we stay within population bounds.
        int x = chunkCoord.getBlockXCenter() + random.nextInt(ChunkCoordinate.CHUNK_SIZE);
        int z = chunkCoord.getBlockZCenter() + random.nextInt(ChunkCoordinate.CHUNK_SIZE);
                
        int y = worldGenRegion.getHighestBlockAboveYAt(x, z, chunkCoord);        
        if (y < minHeight || y > maxHeight)
        {
            return false;
        }
        
        return spawnForced(structureCache, worldGenRegion, random, Rotation.NORTH, x, y, z);
    }
    
    @Override
    public boolean spawnFromSapling(CustomStructureCache structureCache, IWorldGenRegion worldGenRegion, Random random, Rotation rotation, int x, int y, int z)
    {
        return worldGenRegion.placeTree(type, random, x, y, z);
    }
    
    @Override
    public boolean spawnForced(CustomStructureCache structureCache, IWorldGenRegion worldGenRegion, Random random, Rotation rotation, int x, int y, int z)
    {
        return worldGenRegion.placeTree(type, random, x, y, z);
    }
    
    @Override
    public boolean spawnAsTree(CustomStructureCache structureCache, IWorldGenRegion worldGenRegion, Random random, int x, int z, int minY, int maxY, ChunkCoordinate chunkBeingPopulated)
    {
        int y = worldGenRegion.getHighestBlockAboveYAt(x, z, chunkBeingPopulated);
        Rotation rotation = Rotation.getRandomRotation(random);

        if(!(minY == -1 && maxY == -1))
        {
	        if (y < minY || y > maxY)
	        {
	            return false;
	        }
        }
        
        if (y < minHeight || y > maxHeight)
        {
            return false;
        }
        
        return spawnForced(structureCache, worldGenRegion, random, rotation, x, y, z);
    }
    
    @Override
    public boolean canRotateRandomly()
    {
        // Trees cannot be rotated
        return false;
    }

	@Override
	public boolean loadChecks(IModLoadedChecker modLoadedChecker)
	{
		return true;
	}

	@Override
	public boolean doReplaceBlocks()
	{
		return false;
	}
}
