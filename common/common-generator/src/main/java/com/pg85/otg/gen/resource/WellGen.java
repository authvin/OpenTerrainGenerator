package com.pg85.otg.gen.resource;

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

public class WellGen extends Resource
{
    private final int maxAltitude;
    private final int minAltitude;
    private final LocalMaterialData slab;
    private final LocalMaterialData water;
    private final MaterialSet sourceBlocks;

    public WellGen(IBiomeConfig biomeConfig, List<String> args, ILogger logger, IMaterialReader materialReader) throws InvalidConfigException
    {
        super(biomeConfig, args, logger, materialReader);
        assureSize(8, args);

        material = readMaterial(args.get(0), materialReader);
        slab = readMaterial(args.get(1), materialReader);
        water = readMaterial(args.get(2), materialReader);
        frequency = readInt(args.get(3), 1, 100);
        rarity = readRarity(args.get(4));
        minAltitude = readInt(args.get(5), Constants.WORLD_DEPTH, Constants.WORLD_HEIGHT - 1);
        maxAltitude = readInt(args.get(6), minAltitude + 1, Constants.WORLD_HEIGHT - 1);
        sourceBlocks = readMaterials(args, 7, materialReader);
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
        final WellGen compare = (WellGen) other;
        return this.maxAltitude == compare.maxAltitude
               && this.minAltitude == compare.minAltitude
               && this.slab.equals(compare.slab)
               && this.sourceBlocks.equals(compare.sourceBlocks)
               && this.water.equals(compare.water);
    }

    @Override
    public int getPriority()
    {
        return 0;
    }

    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 17 * hash + super.hashCode();
        hash = 17 * hash + this.minAltitude;
        hash = 17 * hash + this.maxAltitude;
        hash = 17 * hash + this.slab.hashCode();
        hash = 17 * hash + this.water.hashCode();
        hash = 17 * hash + this.sourceBlocks.hashCode();
        return hash;
    }

    @Override
    public String toString()
    {
        String output = "Well(" + material + "," + slab + "," + water + ",";
        output += frequency + "," + rarity + "," + minAltitude + "," + maxAltitude + this.makeMaterials(sourceBlocks) + ")";
        return output;
    }

    @Override
    public void spawn(IWorldGenRegion worldGenRegion, Random random, boolean villageInChunk, int x, int z, ChunkCoordinate chunkBeingPopulated)
    {
    	// Make sure we stay within population bounds, anything outside won't be spawned (unless it's in an existing chunk).
    	
        int y = random.nextInt(maxAltitude - minAltitude) + minAltitude;

        LocalMaterialData worldMaterial;
        while (
    		y > minAltitude && 
    		(worldMaterial = worldGenRegion.getMaterial(x, y, z, chunkBeingPopulated)) != null && 
    		worldMaterial.isAir()
		)
        {
            --y;
        }
        
        //parseMaterials(worldGenRegion.getWorldConfig(), material, sourceBlocks);

        worldMaterial = worldGenRegion.getMaterial(x, y, z, chunkBeingPopulated);

        if (worldMaterial == null || !sourceBlocks.contains(worldMaterial))
        {
            return;
        }
		
        int i;
        int j;

        for (i = -2; i <= 2; ++i)
        {
            for (j = -2; j <= 2; ++j)
            {
                if (
                	(worldMaterial = worldGenRegion.getMaterial(x + i, y - 1, z + j, chunkBeingPopulated)) == null ||
                	worldMaterial.isAir() ||
                	(worldMaterial = worldGenRegion.getMaterial(x + i, y - 2, z + j, chunkBeingPopulated)) == null ||
                	worldMaterial.isAir()
        		)
                {
                    return;
                }
            }
        }

        for (i = -1; i <= 0; ++i)
        {
            for (j = -2; j <= 2; ++j)
            {
                for (int var9 = -2; var9 <= 2; ++var9)
                {
                	worldGenRegion.setBlock(x + j, y + i, z + var9, material, null, chunkBeingPopulated, false);
                }
            }
        }

        worldGenRegion.setBlock(x, y, z, water, null, chunkBeingPopulated, false);
        worldGenRegion.setBlock(x - 1, y, z, water, null, chunkBeingPopulated, false);
        worldGenRegion.setBlock(x + 1, y, z, water, null, chunkBeingPopulated, false);
        worldGenRegion.setBlock(x, y, z - 1, water, null, chunkBeingPopulated, false);
        worldGenRegion.setBlock(x, y, z + 1, water, null, chunkBeingPopulated, false);

        for (i = -2; i <= 2; ++i)
        {
            for (j = -2; j <= 2; ++j)
            {
                if (i == -2 || i == 2 || j == -2 || j == 2)
                {
                	worldGenRegion.setBlock(x + i, y + 1, z + j, material, null, chunkBeingPopulated, false);
                }
            }
        }

        worldGenRegion.setBlock(x + 2, y + 1, z, slab, null, chunkBeingPopulated, false);
        worldGenRegion.setBlock(x - 2, y + 1, z, slab, null, chunkBeingPopulated, false);
        worldGenRegion.setBlock(x, y + 1, z + 2, slab, null, chunkBeingPopulated, false);
        worldGenRegion.setBlock(x, y + 1, z - 2, slab, null, chunkBeingPopulated, false);

        for (i = -1; i <= 1; ++i)
        {
            for (j = -1; j <= 1; ++j)
            {
                if (i == 0 && j == 0)
                {
                	worldGenRegion.setBlock(x + i, y + 4, z + j, material, null, chunkBeingPopulated, false);
                } else {
                	worldGenRegion.setBlock(x + i, y + 4, z + j, slab, null, chunkBeingPopulated, false);
                }
            }
        }

        for (i = 1; i <= 3; ++i)
        {
        	worldGenRegion.setBlock(x - 1, y + i, z - 1, material, null, chunkBeingPopulated, false);
        	worldGenRegion.setBlock(x - 1, y + i, z + 1, material, null, chunkBeingPopulated, false);
        	worldGenRegion.setBlock(x + 1, y + i, z - 1, material, null, chunkBeingPopulated, false);
        	worldGenRegion.setBlock(x + 1, y + i, z + 1, material, null, chunkBeingPopulated, false);
        }
    }
}
