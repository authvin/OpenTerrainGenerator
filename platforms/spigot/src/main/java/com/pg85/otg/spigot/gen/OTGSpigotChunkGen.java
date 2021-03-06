package com.pg85.otg.spigot.gen;

import com.pg85.otg.presets.Preset;
import com.pg85.otg.spigot.OTGPlugin;
import com.pg85.otg.util.ChunkCoordinate;
import com.pg85.otg.util.FifoMap;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

public class OTGSpigotChunkGen extends ChunkGenerator
{
	public OTGNoiseChunkGenerator generator = null;
	public final Preset preset;
	private final FifoMap<ChunkCoordinate, ChunkData> chunkDataCache = new FifoMap<>(128);

	public OTGSpigotChunkGen(Preset preset)
	{
		this.preset = preset;
	}

	@Override
	public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biome)
	{
		if (generator == null)
		{
			OTGPlugin.injectInternalGenerator(world);
		}

		ChunkCoordinate chunkCoord = ChunkCoordinate.fromChunkCoords(chunkX, chunkZ);
		ChunkData chunkData = chunkDataCache.get(chunkCoord);
		if (chunkData == null)
		{
			chunkData = createChunkData(world);
			generator.buildNoiseSpigot(((CraftWorld)world).getHandle(), chunkData, chunkCoord, random);
			chunkDataCache.put(chunkCoord, chunkData);
		}
		return chunkData;
	}

	@Override
	public boolean isParallelCapable()
	{
		// Experimental, requires we're thread safe, which is not the case
		// OreGen borks with this bc of cache
		return false;
	}

	@Override
	public boolean shouldGenerateCaves()
	{
		return true;
	}

	@Override
	public boolean shouldGenerateDecorations()
	{
		return true;
	}

	@Override
	public boolean shouldGenerateMobs()
	{
		return true;
	}

	@Override
	public boolean shouldGenerateStructures()
	{
		return true;
	}

	/** Method to query the OTG chunk generator for biome at a location. This is useful if you
	 * want to do a biome check but know there are OTG biomes in the world, which you won't see
	 * in the bukkit Biome enum.
	 *
	 * To query this, get Bukkit.getWorld(worldName).getGenerator(), check it is instanceof OTGSpigotChunkGen,
	 * then call this method on it.
	 *
	 * @param blockX X coordinate of the block in question
	 * @param blockY Y coordinate - as of early 1.16 builds, this is not in use yet.
	 * @param blockZ Z coordinate of the block in question
	 * @return String value of
	 */
	public String getBiomeAtLocation(int blockX, int blockY, int blockZ) {
		return this.generator.getBiomeRegistryName(blockX, blockY, blockZ);
	}
}
