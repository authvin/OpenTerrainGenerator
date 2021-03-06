package com.pg85.otg.util.interfaces;

import com.pg85.otg.exception.InvalidConfigException;
import com.pg85.otg.util.materials.LocalMaterialData;
import com.pg85.otg.util.materials.LocalMaterialTag;

public interface IMaterialReader
{
	public LocalMaterialData readMaterial(String string) throws InvalidConfigException;
	
	public LocalMaterialTag readTag(String string) throws InvalidConfigException;
}
