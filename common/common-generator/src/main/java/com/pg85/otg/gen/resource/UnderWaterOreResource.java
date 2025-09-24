package com.pg85.otg.gen.resource;

import com.pg85.otg.exceptions.InvalidConfigException;
import com.pg85.otg.config.settings.biome.BiomeSettings;
import com.pg85.otg.interfaces.IWorldGenRegion;
import com.pg85.otg.util.OTGMaterialReader;
import com.pg85.otg.util.gen.OTGWorldInfo;
import com.pg85.otg.util.materials.LocalMaterialData;
import com.pg85.otg.util.materials.MaterialSet;

import java.util.List;
import java.util.Random;

/**
 * Generates a disk-alike structure for sand, gravel, and clay.
 * TODO: This needs to be renamed to DiskGen()
 */
public class UnderWaterOreResource extends FrequencyResourceBase
{
	private final LocalMaterialData material;
	private final int size;
	private final MaterialSet sourceBlocks;

	public UnderWaterOreResource(BiomeSettings biomeConfig, List<String> args, OTGWorldInfo otgWorldInfo) throws InvalidConfigException
	{
		super(biomeConfig, args, otgWorldInfo);
		assureSize(5, args);
		this.material = OTGMaterialReader.get().readMaterial(args.get(0));
		this.size = readInt(args.get(1), 1, 8);
		this.frequency = readInt(args.get(2), 1, 100);
		this.rarity = readRarity(args.get(3));
		this.sourceBlocks = readMaterials(args, 4);
	}

	@Override
	public void spawn(IWorldGenRegion worldGenRegion, Random rand, int x, int z)
	{
		int firstSolidBlock = worldGenRegion.getBlockAboveSolidHeight(x, z) - 1;
		if (worldGenRegion.getBlockAboveLiquidHeight(x, z) < firstSolidBlock || firstSolidBlock == -1)
		{
			return;
		}

		if(worldGenRegion.getPresetConfig().getResourceSettings().isDisableOreGen())
		{
			if(this.material.isOre())
			{
				return;
			}
		}
		
		int currentSize = rand.nextInt(this.size) + 2;
		int deltaX;
		int deltaZ;
		LocalMaterialData sourceBlock;
		for (int currentX = x - currentSize; currentX <= x + currentSize; currentX++)
		{
			for (int currentZ = z - currentSize; currentZ <= z + currentSize; currentZ++)
			{
				deltaX = currentX - x;
				deltaZ = currentZ - z;
				if(worldGenRegion.getDecorationArea().isInAreaBeingDecorated(currentX, currentZ))
				{
					BiomeSettings biome = worldGenRegion.getBiomeConfigForDecoration(currentX, currentZ);
					if (deltaX * deltaX + deltaZ * deltaZ <= currentSize * currentSize)
					{
						for (int y = firstSolidBlock - 2; y <= firstSolidBlock + 2; y++)
						{
							sourceBlock = worldGenRegion.getMaterial(currentX, y, currentZ);
							if (this.sourceBlocks.contains(sourceBlock))
							{
								worldGenRegion.setBlock(currentX, y, currentZ, this.material, biome.getSurfaceSettings().getReplacedBlocks());
							}
						}
					}
				}
			}
		}
	}
	
	@Override
	public String toString()
	{
		return "UnderWaterOre(" + this.material + "," + this.size + "," + this.frequency + "," + this.rarity + makeMaterials(this.sourceBlocks) + ")";
	}	
}
