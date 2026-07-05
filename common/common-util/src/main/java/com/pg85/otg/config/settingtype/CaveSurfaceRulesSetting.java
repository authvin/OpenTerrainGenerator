package com.pg85.otg.config.settingtype;

import com.pg85.otg.config.settings.ConfigSection;
import com.pg85.otg.exceptions.InvalidConfigException;
import com.pg85.otg.gen.surface.CaveSurfaceRules;
import com.pg85.otg.util.OTGMaterialReader;

import java.util.function.Function;

/**
 * Setting that handles {@link CaveSurfaceRules}.
 */
public class CaveSurfaceRulesSetting extends Setting<CaveSurfaceRules>
{
	CaveSurfaceRulesSetting(String name, Function<ConfigSection, CaveSurfaceRules> getter, String ...comments)
	{
		super(name, getter, comments);
	}

	@Override
	public CaveSurfaceRules getDefaultValue()
	{
		return CaveSurfaceRules.NONE;
	}

	@Override
	public CaveSurfaceRules read(String string) throws InvalidConfigException
	{
		return CaveSurfaceRules.fromString(string, OTGMaterialReader.get());
	}

	@Override
	public String getTypeAsString() {
		return "string";
	}
}
