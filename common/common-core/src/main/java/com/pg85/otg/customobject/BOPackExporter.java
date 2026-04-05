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

		// Pass 2: iterate every known BO object and collect those that are serializable,
		// grouped by parent directory.
		//
		// directory → (objectName → rawBytes), (objectName → typeTag)
		Map<File, LinkedHashMap<String, byte[]>> rawByDir = new LinkedHashMap<>();
		Map<File, Map<String, String>> typeByDir = new LinkedHashMap<>();

		ArrayList<String> allNames = CustomObjectManager.get()
			.getGlobalObjects()
			.getAllBONamesForPreset(presetFolderName, otgRootFolder);

		int total = allNames == null ? 0 : allNames.size();
		int current = 0;

		if (allNames != null)
		{
			for (String name : allNames)
			{
				current++;
				CustomObject obj = CustomObjectManager.get()
					.getGlobalObjects()
					.getObjectByName(name, presetFolderName, otgRootFolder);

				if (!(obj instanceof BOPackSerializable serializable))
				{
					continue;
				}

                // Resolve the source file to determine the directory
				File sourceFile = resolveSourceFile(obj);
				if (sourceFile == null)
				{
					OTGLog.log(LogLevel.WARN, LogCategory.MAIN,
						"BOPackExporter: could not resolve file for " + name + ", skipping.");
					continue;
				}
				File dir = sourceFile.getParentFile();

				byte[] raw;
				try
				{
					raw = serializable.serializeForPack(presetFolderName, otgRootFolder);
				}
				catch (IOException e)
				{
					OTGLog.log(LogLevel.ERROR, LogCategory.MAIN,
						"BOPackExporter: serialization failed for " + name + ": " + e.getMessage());
					continue;
				}

				if (raw == null)
				{
					continue; // object opted out
				}

				rawByDir.computeIfAbsent(dir, d -> new LinkedHashMap<>()).put(name, raw);
				typeByDir.computeIfAbsent(dir, d -> new LinkedHashMap<>()).put(name, serializable.getBOPackType());

				if (progressCallback != null)
				{
					progressCallback.onProgress(current, total, name);
				}

				// Unload to free memory between objects
				CustomObjectManager.get().getGlobalObjects().unloadCustomObjectFiles();
			}
		}

		// Pass 3: write one .bopack per directory
		int packsWritten = 0;
		for (Map.Entry<File, LinkedHashMap<String, byte[]>> entry : rawByDir.entrySet())
		{
			File dir = entry.getKey();
			LinkedHashMap<String, byte[]> entries = entry.getValue();
			Map<String, String> types = typeByDir.get(dir);

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
