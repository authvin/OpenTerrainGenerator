package com.pg85.otg.gen.surface;

import com.pg85.otg.config.settingType.Setting;
import com.pg85.otg.exceptions.InvalidConfigException;
import com.pg85.otg.interfaces.IMaterialReader;
import com.pg85.otg.interfaces.ISurfaceGenerator;
import com.pg85.otg.util.helpers.StringHelper;

/**
 * Setting that handles the {@link ISurfaceGenerator}.
 *
 */
public class SurfaceGeneratorSetting extends Setting<ISurfaceGenerator>
{
	public static final Setting<ISurfaceGenerator> SURFACE_AND_GROUND_CONTROL = surfaceGeneratorSetting("SurfaceAndGroundControl");

	private SurfaceGeneratorSetting(String name)
	{
		super(name);
	}

	@Override
	public ISurfaceGenerator getDefaultValue(IMaterialReader materialReader)
	{
		return new SimpleSurfaceGenerator();
	}

	@Override
	public ISurfaceGenerator read(String string, IMaterialReader materialReader) throws InvalidConfigException
	{
		if (string.length() > 0)
		{
			ISurfaceGenerator mesa = MesaSurfaceGenerator.getFor(string);
			if (mesa != null)
			{
				return mesa;
			}

			ISurfaceGenerator iceberg = IcebergSurfaceGenerator.getFor(string, materialReader);
			if (iceberg != null)
			{
				return iceberg;
			}

			String[] parts = StringHelper.readCommaSeperatedString(string);
			return new MultipleLayersSurfaceGenerator(parts, materialReader);
		}
		return new SimpleSurfaceGenerator();
	}
	
	/**
	 * Creates a setting that represents a {@link ISurfaceGenerator}.
	 * @param name Name of the setting.
	 * @return The newly created setting.
	 */
	private static final Setting<ISurfaceGenerator> surfaceGeneratorSetting(String name)
	{
		return new SurfaceGeneratorSetting(name);
	}
}
