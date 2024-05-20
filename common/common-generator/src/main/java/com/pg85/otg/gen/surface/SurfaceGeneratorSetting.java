package com.pg85.otg.gen.surface;

import com.pg85.otg.config.settingType.Setting;
import com.pg85.otg.config.settings.ConfigSection;
import com.pg85.otg.config.settings.biome.SurfaceSettings;
import com.pg85.otg.exceptions.InvalidConfigException;
import com.pg85.otg.interfaces.ISurfaceGenerator;
import com.pg85.otg.util.OTGMaterialReader;
import com.pg85.otg.util.helpers.StringHelper;

import java.util.function.Function;

/**
 * Setting that handles the {@link ISurfaceGenerator}.
 *
 */
public class SurfaceGeneratorSetting extends Setting<ISurfaceGenerator>
{
	static {
		SurfaceSettings.SURFACE_GENERATOR = surfaceGeneratorSetting(
				"SurfaceAndGroundControl",
				section -> ((SurfaceSettings) section).getSurfaceGenerator(),
				"Setting for biomes with more complex surface and ground blocks.",
				"Each column in the world has a noise value from what appears to be -7 to 7.",
				"Values near 0 are more common than values near -7 and 7. This setting is",
				"used to change the surface block based on the noise value for the column.",
				"1.12.2 Syntax: SurfaceBlockName,GroundBlockName,MaxNoise[,AnotherSurfaceBlockName,AnotherGroundBlockName,MaxNoise][,...]",
				"Example: " + SurfaceSettings.SURFACE_GENERATOR + ": STONE,STONE,-0.8,GRAVEL,STONE,0.0,DIRT,DIRT,10.0",
				"1.16.x Syntax: SurfaceBlockName,UnderWaterSurfaceBlockName,GroundBlockName,MaxNoise,[AnotherSurfaceBlockName,AnotherUnderWaterSurfaceBlockName,AnotherGroundBlockName,MaxNoise[,...]]",
				"  When the noise is below -0.8, stone is the surface and ground block, between -0.8 and 0",
				"  gravel with stone just below and between 0.0 and 10.0 there's only dirt.",
				"  Because 10.0 is higher than the noise can ever get, the normal " + SurfaceSettings.SURFACE_BLOCK,
				"  and " + SurfaceSettings.GROUND_BLOCK + " will never appear in this biome.", "",
				"Alternatively, you can use Mesa, MesaForest or MesaBryce to get blocks",
				"like the blocks found in the Mesa biomes.",
				"You can also use Iceberg to get iceberg generation like in vanilla frozen oceans. Iceberg accepts a normal SAGC string: \"Iceberg <SAGC>\", so you can use normal SAGC with it."
		);
	}

	private SurfaceGeneratorSetting(String name, Function<ConfigSection, ISurfaceGenerator> getter, String ...comments)
	{
		super(name, getter, comments);
	}

	@Override
	public ISurfaceGenerator getDefaultValue()
	{
		return new SimpleSurfaceGenerator();
	}

	@Override
	public ISurfaceGenerator read(String string) throws InvalidConfigException
	{
		if (!string.isEmpty())
		{
			ISurfaceGenerator mesa = MesaSurfaceGenerator.getFor(string);
			if (mesa != null)
			{
				return mesa;
			}

			ISurfaceGenerator iceberg = IcebergSurfaceGenerator.getFor(string, OTGMaterialReader.get());
			if (iceberg != null)
			{
				return iceberg;
			}

			String[] parts = StringHelper.readCommaSeperatedString(string);
			return new MultipleLayersSurfaceGenerator(parts, OTGMaterialReader.get());
		}
		return new SimpleSurfaceGenerator();
	}

	@Override
	public String getTypeAsString() {
		return "string";
	}

	/**
	 * Creates a setting that represents a {@link ISurfaceGenerator}.
	 * @param name Name of the setting.
	 * @return The newly created setting.
	 */
	public static Setting<ISurfaceGenerator> surfaceGeneratorSetting(String name, Function<ConfigSection, ISurfaceGenerator> getter, String ...comments)
	{
		return new SurfaceGeneratorSetting(name, getter, comments);
	}
}
