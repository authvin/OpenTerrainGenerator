package com.pg85.otg.interfaces;

import java.nio.file.Path;
import java.util.List;

public interface ICustomStructureGen
{
	List<IStructuredCustomObject> getObjects(String presetFolderName, Path otgRootFolder);
	Double getObjectChance(int i);
	String getObjectName(int i);
	String[] getObjectNames();
	Double[] getObjectChances();
	boolean isEmpty();
}
