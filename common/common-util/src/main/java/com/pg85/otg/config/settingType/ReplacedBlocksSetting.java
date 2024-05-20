package com.pg85.otg.config.settingType;

import com.pg85.otg.config.settings.ConfigSection;
import com.pg85.otg.constants.Constants;
import com.pg85.otg.exceptions.InvalidConfigException;
import com.pg85.otg.util.OTGMaterialReader;
import com.pg85.otg.util.biome.ReplaceBlockMatrix;

import java.util.function.Function;

/**
 * Setting that handles {@link ReplaceBlockMatrix}.
 *
 */
public class ReplacedBlocksSetting extends Setting<ReplaceBlockMatrix>
{

	public ReplacedBlocksSetting(String name, Function<ConfigSection, ReplaceBlockMatrix> getter, String ...description)
	{
		super(name, getter, description);
	}

	@Override
	public ReplaceBlockMatrix getDefaultValue()
	{
		return ReplaceBlockMatrix.createEmptyMatrix(Constants.WORLD_HEIGHT, OTGMaterialReader.get());
	}

	@Override
	public ReplaceBlockMatrix read(String string) throws InvalidConfigException
	{
		return new ReplaceBlockMatrix(string);
	}

	@Override
	public String getTypeAsString() {
		return "string";
	}
}
