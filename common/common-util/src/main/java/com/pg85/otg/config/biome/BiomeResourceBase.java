package com.pg85.otg.config.biome;

import java.util.ArrayList;
import java.util.List;

import com.pg85.otg.config.ConfigFunction;
import com.pg85.otg.config.settings.biome.BiomeSettings;

/** Represents a BiomeConfig ResourceQueue resource. */
public abstract class BiomeResourceBase extends ConfigFunction<BiomeSettings>
{
	static BiomeResourceBase createResource(BiomeSettings config, Class<? extends BiomeResourceBase> clazz, Object... args)
	{
		List<String> stringArgs = new ArrayList<String>(args.length);
		for (Object arg : args)
		{
			stringArgs.add("" + arg);
		}

		try
		{
			return clazz.getConstructor(BiomeSettings.class, List.class).newInstance(config, stringArgs);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// We're using reflection to match constructors for resources, so resource classes must implement this 
	// constructor or createResource / com.pg85.otg.config.biome.BiomeResourcesManager.getConfigFunction() will fail. 
	public BiomeResourceBase(BiomeSettings biomeConfig, List<String> args) { }
}
