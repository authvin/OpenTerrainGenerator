package com.pg85.otg.gen.biome.layers;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.pg85.otg.gen.biome.BiomeData;
import com.pg85.otg.interfaces.IBiome;
import com.pg85.otg.interfaces.IBiomeConfig;
import com.pg85.otg.interfaces.IPresetConfig;
import com.pg85.otg.settings.preset.BiomeSettings;
import com.pg85.otg.settings.preset.ImageSettings;
import com.pg85.otg.settings.preset.PresetInfo;

public class BiomeLayerData
{
	public final ImageSettings imageSettings;
	public final BiomeSettings biomeSettings;
	public final PresetInfo presetInfo;
	public final Path presetDir;
	public final int[] oceanTemperatures;
	public final BiomeData oceanBiomeData;
	public final Map<Integer, List<BiomeGroup>> groups = new HashMap<>();
    public final int[] groupMaxRarityPerDepth;
	public final int[] oldMaxRarities;
	public final List<Integer> biomeDepths = new ArrayList<>(); // Depths with biomes
	public final Map<Integer, BiomeGroup> groupRegistry;
	public final Map<Integer, List<BiomeData>> isleBiomesAtDepth = new HashMap<>();
	public final Map<Integer, List<BiomeData>> borderBiomesAtDepth = new HashMap<>();
	public final int[] riverBiomes;

	// FromImageMode
	public HashMap<Integer, Integer> biomeColorMap;

	// TODO: imageFillBiome is not currently using its setting - always 0
	public int imageFillBiome;


	public BiomeLayerData(Path presetDir,
						  IPresetConfig presetConfig,
						  IBiomeConfig oceanBiomeConfig,
						  int[] oceanTemperatures,
						  Map<Integer, BiomeGroup> groupRegistry,
						  Set<Integer> biomeDepths,
						  Map<Integer, List<BiomeGroup>> groupDepth,
						  Map<Integer, List<BiomeData>> isleBiomesAtDepth,
						  Map<Integer, List<BiomeData>> borderBiomesAtDepth,
						  Map<String, List<Integer>> biomeIdsByName,
						  HashMap<Integer, Integer> biomeColorMap,
						  IBiome[] biomes)
	{
		this.presetDir = presetDir;
		this.biomeSettings = presetConfig.getBiomeSettings();
		this.imageSettings = presetConfig.getImageSettings();
		this.presetInfo = presetConfig.getPresetInfo();

		this.oceanTemperatures = oceanTemperatures;

        int[] cumulativeGroupRarities = new int[biomeSettings.getGenerationDepth() + 1];
		this.groupMaxRarityPerDepth = new int[biomeSettings.getGenerationDepth() + 1];
		this.oldMaxRarities = new int[biomeSettings.getGenerationDepth() + 1];

		if (oceanBiomeConfig == null)
		{
			this.oceanBiomeData = new BiomeData(0, 0, 0, 0, ImmutableList.of(), ImmutableList.of(), ImmutableList.of(), ImmutableList.of());
		} else {
			this.oceanBiomeData = new BiomeData(
				0,
				oceanBiomeConfig.getPlacementSettings().getBiomeRarity(),
				oceanBiomeConfig.getPlacementSettings().getBiomeSize(),
				oceanBiomeConfig.getVisualSettings().getBiomeTemperature(),
				oceanBiomeConfig.getPlacementSettings().getIsleInBiomes(),
				oceanBiomeConfig.getPlacementSettings().getBorderInBiomes(),
				oceanBiomeConfig.getPlacementSettings().getOnlyBorderNearBiomes(),
				oceanBiomeConfig.getPlacementSettings().getNotBorderNearBiomes()
			);
		}

		this.groupRegistry = groupRegistry;

		this.biomeDepths.addAll(biomeDepths);
		this.groups.putAll(groupDepth);
		this.isleBiomesAtDepth.putAll(isleBiomesAtDepth);
		this.borderBiomesAtDepth.putAll(borderBiomesAtDepth);

		for(Entry<Integer, List<BiomeGroup>> entry : this.groups.entrySet())
		{
			if(entry.getValue() != null)
			{
				int cumulativeRarity = 0;
				for(BiomeGroup group : entry.getValue())
				{
					group.init(biomeIdsByName);
					cumulativeRarity += group.rarity;
					oldMaxRarities[entry.getKey()]++;
				}
				cumulativeGroupRarities[entry.getKey()] = cumulativeRarity;
			}
		}
		for (int depth = 0; depth < cumulativeGroupRarities.length; depth++)
		{
			for (int j = depth; j < cumulativeGroupRarities.length; j++)
			{
				this.groupMaxRarityPerDepth[depth] += cumulativeGroupRarities[j];
			}
		}

		for (int i = 0; i < oldMaxRarities.length; i++)
		{
			oldMaxRarities[i] *= 100;
		}

		for(Entry<Integer, List<BiomeData>> entry : this.isleBiomesAtDepth.entrySet())
		{
			if(entry.getValue() != null)
			{
				for(BiomeData biome : entry.getValue())
				{
					biome.init(biomeIdsByName);
				}
			}
		}

		for(Entry<Integer, List<BiomeData>> entry : this.borderBiomesAtDepth.entrySet())
		{
			if(entry.getValue() != null)
			{
				for(BiomeData biome : entry.getValue())
				{
					biome.init(biomeIdsByName);
				}
			}
		}

		this.biomeColorMap = biomeColorMap;
		this.riverBiomes = new int[biomes.length];
		for(int i = 0; i < biomes.length; i++)
		{
			List<Integer> ids = biomeIdsByName.getOrDefault(biomes[i].getBiomeConfig().getRiverBiome(), null);
			this.riverBiomes[i] = ids == null ? -1 : ids.get(0);
		}
	}
}
