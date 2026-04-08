package com.pg85.otg.customobject;

import com.pg85.otg.config.ConfigFunction;
import com.pg85.otg.config.biome.BiomeConfig;
import com.pg85.otg.config.settings.biome.BiomeSettings;
import com.pg85.otg.customobject.bo4.BO4;
import com.pg85.otg.customobject.bo4.BOPack;
import com.pg85.otg.customobject.bo4.BOPackSerializable;
import com.pg85.otg.customobject.resource.CustomStructureResource;
import com.pg85.otg.customobject.structures.CustomStructureCache;
import com.pg85.otg.customobject.structures.bo4.BO4CustomStructure;
import com.pg85.otg.customobject.structures.bo4.BO4CustomStructureCoordinate;
import com.pg85.otg.exceptions.InvalidConfigException;
import com.pg85.otg.interfaces.IStructuredCustomObject;
import com.pg85.otg.interfaces.IWorldGenRegion;
import com.pg85.otg.presets.Preset;
import com.pg85.otg.util.OTGLog;
import com.pg85.otg.util.bo3.Rotation;
import com.pg85.otg.util.logging.LogCategory;
import com.pg85.otg.util.logging.LogLevel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Platform-agnostic exporter that serializes all BO objects in a preset into
 * per-directory .bopack files.
 * Usage: call exportPreset() from a platform-specific command. When a new BO
 * type gains serialization support, simply implement BOPackSerializable on its
 * config class — this exporter will pick it up automatically.
 */
public class BOPackExporter
{
	/**
	 * Export all serializable BO objects in the given preset to .bopack files.
	 *
	 * @param preset           the preset to export
	 * @param structureCache   needed for getMinimumSize on structure starts
	 * @param worldGenRegion   needed for getMinimumSize on structure starts
	 * @param otgRootFolder    root OTG folder
	 * @param progressCallback called with (current, total, objectName) as export progresses; may be null
	 */
	public static void exportPreset(
		Preset preset,
		CustomStructureCache structureCache,
		IWorldGenRegion worldGenRegion,
		Path otgRootFolder,
		ProgressCallback progressCallback)
	{
		String presetFolderName = preset.getFolderName();

		// Pass 1: initialise minimumSize for structure starts so those fields
		// are populated before serialization.
		OTGLog.log(LogLevel.INFO, LogCategory.MAIN, "BOPackExporter: initializing structure starts for preset " + presetFolderName);
		for (BiomeConfig biomeConfig : preset.getBiomeConfigList())
		{
			for (ConfigFunction<BiomeSettings> res : biomeConfig.getResourceQueue())
			{
				if (!(res instanceof CustomStructureResource customStructure))
				{
					continue;
				}
				List<IStructuredCustomObject> structures = customStructure.getObjects(presetFolderName, otgRootFolder);
				for (IStructuredCustomObject structure : structures)
				{
					if (!(structure instanceof BO4 bo4))
					{
						continue;
					}
                    if (bo4.getConfig().minimumSizeTop != -1)
					{
						continue; // already initialised (e.g. loaded from existing pack/data)
					}
					try
					{
						BO4CustomStructureCoordinate coord = new BO4CustomStructureCoordinate(
							presetFolderName, structure, null, Rotation.NORTH, 0, (short) 0, 0, 0, false, false, null);
						BO4CustomStructure structureStart = new BO4CustomStructure(
							0L, coord, otgRootFolder);
						structureStart.getMinimumSize(
							structureCache, worldGenRegion, otgRootFolder
						);
					}
					catch (InvalidConfigException e)
					{
						OTGLog.log(LogLevel.ERROR, LogCategory.MAIN,
							"BOPackExporter: failed to get minimum size for " + bo4.getName() + ": " + e.getMessage());
					}
				}
			}
		}

		ArrayList<String> allNames = CustomObjectManager.get()
			.getGlobalObjects()
			.getAllBONamesForPreset(presetFolderName, otgRootFolder);

		if (allNames == null || allNames.isEmpty())
		{
			OTGLog.log(LogLevel.INFO, LogCategory.MAIN, "BOPackExporter: no objects found, nothing to export.");
			return;
		}

		// Pass 2: lightweight scan — group serializable object names by directory.
		// Objects are unloaded after each file lookup so no byte data accumulates.
		Map<File, List<String>> namesByDir = new LinkedHashMap<>();
		for (String name : allNames)
		{
			CustomObject obj = CustomObjectManager.get()
				.getGlobalObjects()
				.getObjectByName(name, presetFolderName, otgRootFolder);

			if (obj instanceof BOPackSerializable)
			{
				File sourceFile = resolveSourceFile(obj);
				if (sourceFile == null)
				{
					OTGLog.log(LogLevel.WARN, LogCategory.MAIN,
						"BOPackExporter: could not resolve file for " + name + ", skipping.");
				}
				else
				{
					namesByDir.computeIfAbsent(sourceFile.getParentFile(), d -> new ArrayList<>()).add(name);
				}
			}
			CustomObjectManager.get().getGlobalObjects().unloadCustomObjectFiles();
		}

		// Pass 3: process and write one directory at a time.
		// At most one directory's worth of byte[] data lives in memory simultaneously.
		int packsWritten = 0;
		int current = 0;
		int total = allNames.size();

		for (Map.Entry<File, List<String>> dirEntry : namesByDir.entrySet())
		{
			File dir = dirEntry.getKey();
			List<String> dirNames = dirEntry.getValue();

			LinkedHashMap<String, byte[]> entries = new LinkedHashMap<>();
			Map<String, String> types = new LinkedHashMap<>();

			for (String name : dirNames)
			{
				current++;
				CustomObject obj = CustomObjectManager.get()
					.getGlobalObjects()
					.getObjectByName(name, presetFolderName, otgRootFolder);

				if (!(obj instanceof BOPackSerializable serializable))
				{
					CustomObjectManager.get().getGlobalObjects().unloadCustomObjectFiles();
					continue;
				}

				byte[] raw;
				try
				{
					raw = serializable.serializeForPack(presetFolderName, otgRootFolder);
				}
				catch (IOException e)
				{
					OTGLog.log(LogLevel.ERROR, LogCategory.MAIN,
						"BOPackExporter: serialization failed for " + name + ": " + e.getMessage());
					CustomObjectManager.get().getGlobalObjects().unloadCustomObjectFiles();
					continue;
				}

				if (raw != null)
				{
					entries.put(name, raw);
					types.put(name, serializable.getBOPackType());
				}

				if (progressCallback != null)
				{
					progressCallback.onProgress(current, total, name);
				}

				CustomObjectManager.get().getGlobalObjects().unloadCustomObjectFiles();
			}

			if (entries.isEmpty())
			{
				continue;
			}

			File packFile = BOPack.getPackFileForDir(dir);
			try
			{
				BOPack.write(packFile, entries, types);
				packsWritten++;
			}
			catch (IOException e)
			{
				OTGLog.log(LogLevel.ERROR, LogCategory.MAIN,
					"BOPackExporter: failed to write " + packFile.getAbsolutePath() + ": " + e.getMessage());
			}
			// entries and types go out of scope here; GC can reclaim all byte[] data
			// for this directory before the next directory is processed.
		}

		OTGLog.log(LogLevel.INFO, LogCategory.MAIN,
			"BOPackExporter: done. Wrote " + packsWritten + " pack(s) covering " + current + " object(s).");
	}

	// -------------------------------------------------------------------------

	/** Callback interface for progress reporting from the export loop. */
	public interface ProgressCallback
	{
		void onProgress(int current, int total, String objectName);
	}

	private static File resolveSourceFile(CustomObject obj)
	{
		if (obj instanceof BO4)
		{
			return ((BO4) obj).getConfig().getFile();
		}
		return null;
	}
}
