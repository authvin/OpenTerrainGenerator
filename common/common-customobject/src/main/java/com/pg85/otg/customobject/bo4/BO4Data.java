package com.pg85.otg.customobject.bo4;

import com.pg85.otg.customobject.BOFileExtensions;
import com.pg85.otg.util.CompressionUtils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.zip.DataFormatException;

public class BO4Data
{
	public static boolean bo4DataExists(BO4Config config)
	{
		return bo4DataExists(config.getFile());
	}

	public static boolean bo4DataExists(File sourceFile)
	{
		return new File(BOFileExtensions.toBO4DataPath(sourceFile.getAbsolutePath())).exists();
	}

	/**
	 * Reads and decompresses a .BO4Data file, returning the raw bytes as a ByteBuffer.
	 * @param bo4DataFile the .BO4Data file to read (must exist)
	 * @throws IOException on I/O error or decompression failure
	 */
	public static ByteBuffer readBuffer(File bo4DataFile) throws IOException
	{
		ByteBuffer mapped = null;
		try (FileInputStream fis = new FileInputStream(bo4DataFile))
		{
			FileChannel channel = fis.getChannel();
			mapped = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
			byte[] compressed = new byte[(int) channel.size()];
			mapped.get(compressed);
			try
			{
				return ByteBuffer.wrap(CompressionUtils.decompress(compressed));
			}
			catch (DataFormatException e)
			{
				throw new IOException("Could not decompress BO4Data file " + bo4DataFile.getName() + ": " + e.getMessage(), e);
			}
		}
		finally
		{
			if (mapped != null) mapped.clear();
		}
	}
	
	/**
	 * Serializes a BO4Config to raw (uncompressed) bytes.
	 * Used by both generateBO4Data (individual file write) and BOPackExporter (pack write).
	 */
	public static byte[] serializeToBytes(BO4Config config) throws IOException
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		config.writeToStream(dos);
		dos.close();
		return bos.toByteArray();
	}

	/**
	 * Writes a single .BO4Data file for the given config (legacy individual-file format).
	 * Use BOPackExporter to generate .bopack files instead when exporting for distribution.
	 */
	public static void generateBO4Data(BO4Config config)
	{
		String filePath = BOFileExtensions.toBO4DataPath(config.getFile().getAbsolutePath());
		File file = new File(filePath);
		if(!file.exists())
		{
			try {
				byte[] raw = serializeToBytes(config);
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
