package com.pg85.otg.config.biome;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.pg85.otg.config.ConfigFunction;
import com.pg85.otg.config.ErroredFunction;
import com.pg85.otg.config.io.IConfigFunctionProvider;
import com.pg85.otg.config.settings.biome.BiomeSettings;
import com.pg85.otg.config.settings.preset.PresetSettings;

public class BiomeResourcesManager implements IConfigFunctionProvider
{
	private final Map<String, Class<? extends ConfigFunction<?>>> configFunctions;

	public BiomeResourcesManager(Map<String, Class<? extends ConfigFunction<?>>> configFunctions)
	{
		// Also store in this class
		this.configFunctions = new HashMap<>();

		for(Entry<String, Class<? extends ConfigFunction<?>>> resource : configFunctions.entrySet())
		{
			registerConfigFunction(resource.getKey(), resource.getValue());
		}
	}

	private void registerConfigFunction(String name, Class<? extends ConfigFunction<?>> value)
	{
		configFunctions.put(name.toLowerCase(), value);
	}

	/**
	 * Returns a config function with the given name.
	 *
	 * @param <T>    Type of the holder of the config function.
	 * @param name   The name of the config function.
	 * @param holder The holder of the config function, like
	 *               {@link PresetSettings}.
	 * @param args   The args of the function.
	 * @return A config function with the given name, or null if the config
	 * function requires another holder. For invalid or non-existing config
	 * functions, it returns an instance of {@link ErroredFunction}.
	 */
	// It's checked with clazz.getConstructor(holder.getClass(), ...))
	@SuppressWarnings("unchecked")
	public <T> ConfigFunction<T> getConfigFunction(String name, T holder, List<String> args)
	{
		// Get the class of the config function
		Class<? extends ConfigFunction<?>> clazz = configFunctions.get(name.toLowerCase());
		if (clazz == null)
		{
			return new ErroredFunction<T>(name, args, "Resource type " + name + " not found");
		}

		// Get a config function
		try
		{
			Constructor<? extends ConfigFunction<?>> constructor = getConstructor(holder, clazz);
            assert constructor != null;
            return (ConfigFunction<T>) constructor.newInstance(holder, args);
		}
		catch (NoSuchMethodException e1)
		{
			// Probably uses another holder type
			return null;
		}
		catch (InstantiationException | IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
		catch (InvocationTargetException e)
		{
			Throwable cause = e.getCause();
			return new ErroredFunction<T>(name, args, "Resource type " + name + " had invalid parameters and could not be parsed, error: " + cause);
		}
	}

	private static <T> Constructor<? extends ConfigFunction<?>> getConstructor(T holder, Class<? extends ConfigFunction<?>> clazz) throws NoSuchMethodException {
		Constructor<? extends ConfigFunction<?>> constructor = null;
		if(holder instanceof BiomeSettings)
		{
			// Every BiomeConfig resource should have a constructor that conforms to this method signature
			constructor = clazz.getConstructor(List.class);
		}
		else if(holder instanceof PresetSettings)
		{
			// Every PresetConfig resource should have a constructor that conforms to this method signature
			constructor = clazz.getConstructor(PresetSettings.class, List.class);
		}
		return constructor;
	}
}
