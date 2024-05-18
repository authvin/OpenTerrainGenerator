package com.pg85.otg.forge.api;

import java.util.Optional;

import com.pg85.otg.forge.gen.OTGNoiseChunkGenerator;
import com.pg85.otg.config.settings.biome.BiomeSettings;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class OTGForgeAPI
{
	@SuppressWarnings("resource")
	public static BiomeSettings getOTGBiome(ServerWorld world, BlockPos pos)
	{
		if (!(world.getChunkSource().generator instanceof OTGNoiseChunkGenerator))
		{
			return null;
		}

		return ((OTGNoiseChunkGenerator) world.getChunkSource().generator).getCachedBiomeProvider()
				.getBiomeConfig(pos.getX(), pos.getZ());
	}

	public static Optional<BiomeSettings> getOTGBiomeOptional(ServerWorld world, BlockPos pos)
	{
		return Optional.ofNullable(getOTGBiome(world, pos));
	}
}
