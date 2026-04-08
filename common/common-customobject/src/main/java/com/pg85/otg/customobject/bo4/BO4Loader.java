package com.pg85.otg.customobject.bo4;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import com.pg85.otg.customobject.BOFileExtensions;
import com.pg85.otg.customobject.CustomObject;
import com.pg85.otg.customobject.CustomObjectLoader;
import com.pg85.otg.customobject.bo4.bo4function.BO4BlockFunction;
import com.pg85.otg.customobject.bo4.bo4function.BO4BranchFunction;
import com.pg85.otg.customobject.bo4.bo4function.BO4EntityFunction;
import com.pg85.otg.customobject.bo4.bo4function.BO4RandomBlockFunction;
import com.pg85.otg.customobject.bo4.bo4function.BO4WeightedBranchFunction;
import com.pg85.otg.customobject.config.CustomObjectResourcesManager;
import com.pg85.otg.exceptions.InvalidConfigException;
import com.pg85.otg.util.OTGLog;
import com.pg85.otg.util.logging.LogCategory;
import com.pg85.otg.util.logging.LogLevel;

public class BO4Loader implements CustomObjectLoader
{
	public BO4Loader(CustomObjectResourcesManager registry)
	{
		// Register BO4 ConfigFunctions
		registry.registerConfigFunction("Block", BO4BlockFunction.class);
		registry.registerConfigFunction("B", BO4BlockFunction.class);
		registry.registerConfigFunction("Branch", BO4BranchFunction.class);
		registry.registerConfigFunction("BR", BO4BranchFunction.class);
		registry.registerConfigFunction("WeightedBranch", BO4WeightedBranchFunction.class);
		registry.registerConfigFunction("WBR", BO4WeightedBranchFunction.class);
		registry.registerConfigFunction("RandomBlock", BO4RandomBlockFunction.class);
		registry.registerConfigFunction("RB", BO4RandomBlockFunction.class);
		registry.registerConfigFunction("Entity", BO4EntityFunction.class);
		registry.registerConfigFunction("E", BO4EntityFunction.class);
	}

	@Override
	public CustomObject loadFromFile(String objectName, File file)
	{
		String ext = file.getName().toLowerCase();

		// Explicitly binary formats — resolve immediately, no text reader needed.
		if(ext.endsWith(BOFileExtensions.BOPACK))
		{
			return loadFromPack(objectName, file);
		}
		if(ext.endsWith(BOFileExtensions.BO4DATA))
		{
			return loadFromBO4Data(objectName, file);
		}

		// Text source (.bo4 / .bo3) — check for binary cache in the same directory
		// before falling back to deferred text parsing.
		BOPack pack = BOPack.getForDirectory(file.getParentFile());
		if(pack != null && pack.contains(objectName))
		{
			try
			{
				ByteBuffer buffer = pack.getEntryBuffer(objectName);
				File packFile = BOPack.getPackFileForDir(file.getParentFile());
				BO4Config config = BO4Config.fromBuffer(buffer, objectName, packFile);
				return new BO4(objectName, file, config);
			}
			catch(IOException | InvalidConfigException e)
			{
				if(OTGLog.getLogCategoryEnabled(LogCategory.CUSTOM_OBJECTS))
				{
					OTGLog.log(LogLevel.WARN, LogCategory.CUSTOM_OBJECTS,
						"Failed to load " + objectName + " from BOPack, falling back to text: " + e.getMessage());
				}
			}
		}

		File bo4DataFile = new File(BOFileExtensions.toBO4DataPath(file.getAbsolutePath()));
		if(bo4DataFile.exists())
		{
			return loadFromBO4Data(objectName, bo4DataFile);
		}

		// No binary cache found — defer to text parsing in BO4.onEnable.
		return new BO4(objectName, file);
	}

	private static BO4 loadFromPack(String objectName, File packFile)
	{
		BOPack pack = BOPack.forFile(packFile);
		if(pack == null)
		{
			if(OTGLog.getLogCategoryEnabled(LogCategory.CUSTOM_OBJECTS))
			{
				OTGLog.log(LogLevel.ERROR, LogCategory.CUSTOM_OBJECTS,
					"Could not load BOPack: " + packFile.getAbsolutePath());
			}
			throw new RuntimeException("Could not load BOPack: "+packFile);
		}
		try
		{
			ByteBuffer buffer = pack.getEntryBuffer(objectName);
			BO4Config config = BO4Config.fromBuffer(buffer, objectName, packFile);
			return new BO4(objectName, packFile, config);
		}
		catch(IOException | InvalidConfigException e)
		{
			if(OTGLog.getLogCategoryEnabled(LogCategory.CUSTOM_OBJECTS))
			{
				OTGLog.log(LogLevel.ERROR, LogCategory.CUSTOM_OBJECTS,
					"Failed to load " + objectName + " from BOPack " + packFile.getName() + ": " + e.getMessage());
			}
			throw new RuntimeException("Failed to load " + objectName + " from BOPack " + packFile.getName() + ": " + e.getMessage(), e);
		}
	}

	private static BO4 loadFromBO4Data(String objectName, File bo4DataFile)
	{
		try
		{
			ByteBuffer buffer = BO4Data.readBuffer(bo4DataFile);
			BO4Config config = BO4Config.fromBuffer(buffer, objectName, bo4DataFile);
			return new BO4(objectName, bo4DataFile, config);
		}
		catch(IOException | InvalidConfigException e)
		{
			if(OTGLog.getLogCategoryEnabled(LogCategory.CUSTOM_OBJECTS))
			{
				OTGLog.log(LogLevel.ERROR, LogCategory.CUSTOM_OBJECTS,
					"Failed to load " + objectName + " from BO4Data " + bo4DataFile.getName() + ": " + e.getMessage());
			}
			return null;
		}
	}
}