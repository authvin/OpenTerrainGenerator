package com.pg85.otg.gen.resource;

import com.pg85.otg.config.ConfigFunction;
import com.pg85.otg.config.biome.Resource;
import com.pg85.otg.constants.Constants;
import com.pg85.otg.exception.InvalidConfigException;
import com.pg85.otg.logging.ILogger;
import com.pg85.otg.util.ChunkCoordinate;
import com.pg85.otg.util.interfaces.IBiomeConfig;
import com.pg85.otg.util.interfaces.IMaterialReader;
import com.pg85.otg.util.interfaces.IWorldGenRegion;
import com.pg85.otg.util.materials.LocalMaterialData;
import com.pg85.otg.util.materials.MaterialSet;

import java.util.List;
import java.util.Random;

public class GrassGen extends Resource
{
    public static enum GroupOption
    {
        Grouped,
        NotGrouped
    }

    private GroupOption groupOption;
    private PlantType plant;
    private final MaterialSet sourceBlocks;

    public GrassGen(IBiomeConfig biomeConfig, List<String> args, ILogger logger, IMaterialReader materialReader) throws InvalidConfigException
    {
        super(biomeConfig, args, logger, materialReader);
        assureSize(5, args);

        // The syntax for the first to arguments used to be blockId,blockData
        // Then it became plantType,unusedParam (plantType can still be
        // blockId:blockData)
        // Now it is plantType,groupOption
        this.groupOption = GroupOption.NotGrouped;
        String secondArgument = args.get(1);
        try
        {
            // Test whether the second argument is the data value (deprecated)
            readInt(secondArgument, 0, 16);
            // If yes, parse it
            plant = PlantType.getPlant(args.get(0) + ":" + secondArgument, materialReader);
        } catch (InvalidConfigException e)
        {
            // Nope, second argument is not a number
            plant = PlantType.getPlant(args.get(0), materialReader);
            if (secondArgument.equalsIgnoreCase(GroupOption.Grouped.toString()))
            {
                this.groupOption = GroupOption.Grouped;
                // For backwards compatibility, the second argument is not
                // checked further
            }
        }

        // Not used for terrain generation, they are used by the Forge
        // implementation to fire Forge events
        material = plant.getBottomMaterial();

        frequency = readInt(args.get(2), 1, 500);
        rarity = readRarity(args.get(3));
        sourceBlocks = readMaterials(args, 4, materialReader);
    }

    @Override
    public boolean equals(Object other)
    {
        if (!super.equals(other))
            return false;
        if (other == null)
            return false;
        if (other == this)
            return true;
        if (getClass() != other.getClass())
            return false;
        final GrassGen compare = (GrassGen) other;
        return (this.sourceBlocks == null ? this.sourceBlocks == compare.sourceBlocks
                : this.sourceBlocks.equals(compare.sourceBlocks));
    }

    @Override
    public int getPriority()
    {
        return -32;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 11 * hash + super.hashCode();
        hash = 11 * hash + (this.sourceBlocks != null ? this.sourceBlocks.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean isAnalogousTo(ConfigFunction<IBiomeConfig> other, ILogger logger)
    {
        return getClass() == other.getClass() && ((GrassGen) other).plant.equals(plant);
    }

    @Override
    public String toString()
    {
        return "Grass(" + plant.getName() + "," + groupOption + "," + frequency + "," + rarity + makeMaterials(sourceBlocks) + ")";
    }

    @Override
    public void spawn(IWorldGenRegion worldGenregion, Random random, boolean villageInChunk, int x, int z, ChunkCoordinate chunkBeingPopulated)
    {
        // Left blank, as spawnInChunk already handles this.
    }

    @Override
    protected void spawnInChunk(IWorldGenRegion worldGenRegion, Random random, boolean villageInChunk, ChunkCoordinate chunkBeingPopulated, ILogger logger, IMaterialReader materialReader)
    {
        //sourceBlocks.parseForWorld(worldGenregion.getWorldConfig());
        switch (groupOption)
        {
            case Grouped:
                spawnGrouped(worldGenRegion, random, chunkBeingPopulated);
                break;
            case NotGrouped:
                spawnNotGrouped(worldGenRegion, random, chunkBeingPopulated);
                break;
        }
    }
    
    private void spawnGrouped(IWorldGenRegion worldGenregion, Random random, ChunkCoordinate chunkBeingPopulated)
    {
    	// Make sure we stay within population bounds, anything outside won't be spawned (unless it's in an existing chunk).
        if (random.nextDouble() * 100.0 <= this.rarity)
        {
            // Passed Rarity test, place about Frequency grass in this chunk
            int centerX = chunkBeingPopulated.getBlockXCenter() + random.nextInt(ChunkCoordinate.CHUNK_SIZE);
            int centerZ = chunkBeingPopulated.getBlockZCenter() + random.nextInt(ChunkCoordinate.CHUNK_SIZE);
            int centerY = worldGenregion.getHighestBlockAboveYAt(centerX, centerZ, chunkBeingPopulated);
            
            if(centerY < Constants.WORLD_DEPTH)
            {
            	return;
            }
            
            LocalMaterialData worldMaterial;

            // Fix y position
            while (
        		(
    				(centerY >= Constants.WORLD_DEPTH && centerY < Constants.WORLD_HEIGHT) &&
					(worldMaterial = worldGenregion.getMaterial(centerX, centerY, centerZ, chunkBeingPopulated)) != null &&
					(
						worldMaterial.isAir() || 
						worldMaterial.isLeaves()
					) &&
    				(worldMaterial = worldGenregion.getMaterial(centerX, centerY - 1, centerZ, chunkBeingPopulated)) != null
				) && (
					centerY > 0
				)
    		)
            {
                centerY--;
            }
            centerY++;

            // Try to place grass
            // Because of the changed y position, only one in four attempts
            // will have success
            int x;
            int y;
            int z;
            for (int i = 0; i < frequency * 4; i++)
            {
                x = centerX + random.nextInt(8) - random.nextInt(8);
                y = centerY + random.nextInt(4) - random.nextInt(4);
                z = centerZ + random.nextInt(8) - random.nextInt(8);
                if (
    				(worldMaterial = worldGenregion.getMaterial(x, y, z, chunkBeingPopulated)) != null && 
    				worldMaterial.isAir() &&
    				(
        				(worldMaterial = worldGenregion.getMaterial(x, y - 1, z, chunkBeingPopulated)) != null && 
        				this.sourceBlocks.contains(worldMaterial)
    				)
				)
                {
                    plant.spawn(worldGenregion, x, y, z, chunkBeingPopulated);
                }
            }
        }
    }

    private void spawnNotGrouped(IWorldGenRegion worldGenregion, Random random, ChunkCoordinate chunkBeingPopulated)
    {
        LocalMaterialData worldMaterial;
        int x;
        int z;
        int y;
    	// Make sure we stay within population bounds, anything outside won't be spawned.
        for (int t = 0; t < frequency; t++)
        {
            if (random.nextInt(100) >= rarity)
            {
                continue;
            }
            
            x = chunkBeingPopulated.getBlockXCenter() + random.nextInt(ChunkCoordinate.CHUNK_SIZE);
            z = chunkBeingPopulated.getBlockZCenter() + random.nextInt(ChunkCoordinate.CHUNK_SIZE);
            y = worldGenregion.getHighestBlockAboveYAt(x, z, chunkBeingPopulated);

            if(y < Constants.WORLD_DEPTH)
            {
            	return;
            }
            
            while (
        		(
    				(worldMaterial = worldGenregion.getMaterial(x, y, z, chunkBeingPopulated)) != null &&
    				(
	    				worldMaterial.isAir() || 
						worldMaterial.isLeaves()
					) &&
    				(worldMaterial = worldGenregion.getMaterial(x, y - 1, z, chunkBeingPopulated)) != null
				) && 
        		y > 0
    		)
            {
                y--;
            }

            if (            		
        		(
    				(worldMaterial = worldGenregion.getMaterial(x, y + 1, z, chunkBeingPopulated)) == null ||
					!worldMaterial.isAir()
				) || (
					(worldMaterial = worldGenregion.getMaterial(x, y, z, chunkBeingPopulated)) == null ||
					!sourceBlocks.contains(worldMaterial)
				)
    		)
            {
                continue;
            }
            plant.spawn(worldGenregion, x, y + 1, z, chunkBeingPopulated);
        }
    }
}
