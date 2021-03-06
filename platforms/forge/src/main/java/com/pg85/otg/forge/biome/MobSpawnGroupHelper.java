package com.pg85.otg.forge.biome;

import java.util.ArrayList;
import java.util.List;

import com.pg85.otg.util.biome.WeightedMobSpawnGroup;

import net.minecraft.entity.EntityClassification;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo.Spawners;

public class MobSpawnGroupHelper
{
	public static List<WeightedMobSpawnGroup> getListFromMinecraftBiome(Biome biome, EntityClassification type)
	{
		List<Spawners> mobList = biome.getMobSettings().getMobs(type);       
		List<WeightedMobSpawnGroup> result = new ArrayList<WeightedMobSpawnGroup>();
		for (Spawners spawner : mobList)
		{
			WeightedMobSpawnGroup wMSG = new WeightedMobSpawnGroup(spawner.type.getRegistryName().toString(), spawner.weight, spawner.minCount, spawner.maxCount);
			if(wMSG != null)
			{
				result.add(wMSG);
			}
		}
		return result;
	}
}
