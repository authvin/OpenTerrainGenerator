package com.pg85.otg.customobject.bo4;

import com.pg85.otg.util.CompressionUtils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

public class BO4Data
{
	public static boolean bo4DataExists(BO4Config config)
	{
		String filePath = 
			config.getFile().getAbsolutePath().endsWith(".BO4") ? config.getFile().getAbsolutePath().replace(".BO4", ".BO4Data") :
			config.getFile().getAbsolutePath().endsWith(".bo4") ? config.getFile().getAbsolutePath().replace(".bo4", ".BO4Data") :
			config.getFile().getAbsolutePath().endsWith(".BO3") ? config.getFile().getAbsolutePath().replace(".BO3", ".BO4Data") :
			config.getFile().getAbsolutePath().endsWith(".bo3") ? config.getFile().getAbsolutePath().replace(".bo3", ".BO4Data") :
			config.getFile().getAbsolutePath();

		File file = new File(filePath);
		return file.exists();
	}
	
	/**
	 * Serializes a BO4Config to raw (uncompressed) bytes.
	 * Used by both generateBO4Data (individual file write) and BOPackExporter (pack write).
	 */
	public static byte[] serializeToBytes(BO4Config config, String presetFolderName, Path otgRootFolder) throws IOException
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		config.writeToStream(dos, presetFolderName, otgRootFolder);
		dos.close();
		return bos.toByteArray();
	}

	/**
	 * Writes a single .BO4Data file for the given config (legacy individual-file format).
	 * Use BOPackExporter to generate .bopack files instead when exporting for distribution.
	 */
	public static void generateBO4Data(BO4Config config, String presetFolderName, Path otgRootFolder)
	{
		String filePath =
			config.getFile().getAbsolutePath().endsWith(".BO4") ? config.getFile().getAbsolutePath().replace(".BO4", ".BO4Data") :
			config.getFile().getAbsolutePath().endsWith(".bo4") ? config.getFile().getAbsolutePath().replace(".bo4", ".BO4Data") :
			config.getFile().getAbsolutePath().endsWith(".BO3") ? config.getFile().getAbsolutePath().replace(".BO3", ".BO4Data") :
			config.getFile().getAbsolutePath().endsWith(".bo3") ? config.getFile().getAbsolutePath().replace(".bo3", ".BO4Data") :
			config.getFile().getAbsolutePath();

		File file = new File(filePath);
		if(!file.exists())
		{
			try {
				byte[] raw = serializeToBytes(config, presetFolderName, otgRootFolder);
				byte[] compressedBytes = CompressionUtils.compress(raw);
				FileOutputStream fos = new FileOutputStream(file);
				DataOutputStream dos = new DataOutputStream(fos);
				dos.write(compressedBytes, 0, compressedBytes.length);
				dos.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
