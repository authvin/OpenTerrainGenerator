package com.pg85.otg.customobject.bo4;

import com.pg85.otg.customobject.BOFileExtensions;
import com.pg85.otg.util.CompressionUtils;
import com.pg85.otg.util.OTGLog;
import com.pg85.otg.util.logging.LogCategory;
import com.pg85.otg.util.logging.LogLevel;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.zip.CRC32;
import java.util.zip.DataFormatException;

/**
 * BOPack — a generic container for serialized BO objects.
 *
 * File layout:
 *   Header (12 bytes):
 *     magic[4]        "OPK\0"
 *     version[4]      int, currently 1
 *     entryCount[4]   int
 *
 *   Table of Contents (entryCount entries):
 *     nameLen[2]          short
 *     name[nameLen]       UTF-8
 *     typeLen[2]          short
 *     type[typeLen]       UTF-8 (e.g. "BO4", "BO3")
 *     dataOffset[8]       long  (absolute byte offset from file start)
 *     dataLength[4]       int   (size of compressed data)
 *     crcCompressed[4]    int   (CRC32 of compressed bytes)
 *     crcRaw[4]           int   (CRC32 of uncompressed bytes)
 *
 *   Data section:
 *     compressed bytes for each entry, in ToC order
 *
 * BOPack has no knowledge of what is inside each entry. Type validation is the
 * responsibility of the caller (e.g. BO4Config checks that type == "BO4").
 */
public class BOPack
{
	private static final int MAGIC = 0x4F504B00; // "OPK\0"
	private static final int FORMAT_VERSION = 1;
	static final String FILE_EXTENSION = BOFileExtensions.BOPACK;

	// Static cache: pack file → loaded BOPack (header + ToC only, data is lazy)
	private static final ConcurrentHashMap<File, BOPack> cache = new ConcurrentHashMap<>();

	// -------------------------------------------------------------------------
	// Public entry type (immutable)
	// -------------------------------------------------------------------------

	public static final class Entry
	{
		public final String name;
		public final String type;
		final long dataOffset;
		final int dataLength;
		final int crcCompressed;
		final int crcRaw;

		Entry(String name, String type, long dataOffset, int dataLength,
			  int crcCompressed, int crcRaw)
		{
			this.name = name;
			this.type = type;
			this.dataOffset = dataOffset;
			this.dataLength = dataLength;
			this.crcCompressed = crcCompressed;
			this.crcRaw = crcRaw;
		}
	}

	// -------------------------------------------------------------------------
	// Instance state (ToC only — data stays on disk)
	// -------------------------------------------------------------------------

	private final File packFile;
	private final Map<String, Entry> toc; // name → entry

	private BOPack(File packFile, Map<String, Entry> toc)
	{
		this.packFile = packFile;
		this.toc = toc;
	}

	// -------------------------------------------------------------------------
	// Static factory / cache
	// -------------------------------------------------------------------------

	/**
	 * Returns a BOPack for the given pack file, loading and caching it on first
	 * access. Returns null if the file does not exist or fails to load.
	 */
	public static BOPack forFile(File packFile)
	{
		if (!packFile.exists())
		{
			return null;
		}
		BOPack cached = cache.get(packFile);
		if (cached != null)
		{
			return cached;
		}
		BOPack loaded = loadFromFile(packFile);
		if (loaded == null)
		{
			return null;
		}
		cache.put(packFile, loaded);
		return loaded;
	}

	/**
	 * Returns a BOPack for the given directory if a .bopack file exists there,
	 * otherwise returns null. The result is cached after the first load.
	 */
	public static BOPack getForDirectory(File directory)
	{
		return forFile(getPackFileForDir(directory));
	}

	/** Evict the cached BOPack for a directory (call after writing a new pack). */
	public static void invalidateCache(File directory)
	{
		cache.remove(getPackFileForDir(directory));
	}

	/** Returns the names of all entries in this pack (order matches the ToC). */
	public Set<String> getEntryNames()
	{
		return Collections.unmodifiableSet(toc.keySet());
	}

	/** The canonical pack file path for a given directory. */
	public static File getPackFileForDir(File directory)
	{
		return new File(directory, directory.getName() + FILE_EXTENSION);
	}

	// -------------------------------------------------------------------------
	// Reading
	// -------------------------------------------------------------------------

	public boolean contains(String name)
	{
		return toc.containsKey(name.toLowerCase());
	}

	/**
	 * Reads and returns the decompressed bytes for the named entry.
	 * Validates both CRC32 checksums (compressed and raw).
	 *
	 * @throws IOException on I/O error, CRC mismatch, or decompression failure
	 */
	public ByteBuffer getEntryBuffer(String name) throws IOException
	{
		Entry entry = toc.get(name.toLowerCase());
		if (entry == null)
		{
			throw new IOException("Entry '" + name + "' not found in pack " + packFile.getName());
		}

		byte[] compressed = new byte[entry.dataLength];
		try (RandomAccessFile raf = new RandomAccessFile(packFile, "r"))
		{
			raf.seek(entry.dataOffset);
			raf.readFully(compressed);
		}

		int actualCrcCompressed = crc32(compressed);
		if (actualCrcCompressed != entry.crcCompressed)
		{
			throw new IOException(
				"CRC mismatch (compressed) for entry '" + name + "' in " + packFile.getName()
				+ ": expected " + Integer.toUnsignedString(entry.crcCompressed, 16)
				+ ", got " + Integer.toUnsignedString(actualCrcCompressed, 16)
			);
		}

		byte[] raw;
		try
		{
			raw = CompressionUtils.decompress(compressed);
		}
		catch (DataFormatException e)
		{
			throw new IOException("Decompression failed for entry '" + name + "' in " + packFile.getName(), e);
		}

		int actualCrcRaw = crc32(raw);
		if (actualCrcRaw != entry.crcRaw)
		{
			throw new IOException(
				"CRC mismatch (raw) for entry '" + name + "' in " + packFile.getName()
				+ ": expected " + Integer.toUnsignedString(entry.crcRaw, 16)
				+ ", got " + Integer.toUnsignedString(actualCrcRaw, 16)
			);
		}

		return ByteBuffer.wrap(raw);
	}

	/** Returns the entry metadata from the ToC without reading data, or null. */
	public Entry getEntryInfo(String name)
	{
		return toc.get(name.toLowerCase());
	}

	// -------------------------------------------------------------------------
	// Writing
	// -------------------------------------------------------------------------

	/**
	 * Writes a new .bopack file containing the given entries.
	 * rawEntries maps object-name → uncompressed serialized bytes.
	 * The type string for each entry comes from the map value's associated
	 * type, passed in the parallel typeMap.
	 *
	 * After writing, the static cache for the destination directory is invalidated.
	 */
	public static void write(File packFile,
							 LinkedHashMap<String, byte[]> rawEntries,
							 Map<String, String> typeMap) throws IOException
	{
		// Compress all entries and compute offsets
		String[] names = rawEntries.keySet().toArray(new String[0]);
		byte[][] compressed = new byte[names.length][];
		int[] crcCompressed = new int[names.length];
		int[] crcRaw = new int[names.length];

		for (int i = 0; i < names.length; i++)
		{
			byte[] raw = rawEntries.get(names[i]);
			crcRaw[i] = crc32(raw);
			compressed[i] = CompressionUtils.compress(raw);
			crcCompressed[i] = crc32(compressed[i]);
		}

		// Calculate ToC size to determine data section start offset
		long tocSize = 0;
		for (String name : names)
		{
			byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);
			String type = typeMap.getOrDefault(name, "");
			byte[] typeBytes = type.getBytes(StandardCharsets.UTF_8);
			// nameLen(2) + name + typeLen(2) + type + offset(8) + length(4) + crcC(4) + crcR(4)
			tocSize += 2 + nameBytes.length + 2 + typeBytes.length + 8 + 4 + 4 + 4;
		}
		// Header: magic(4) + version(4) + entryCount(4)
		long dataStart = 12 + tocSize;

		// Build data offsets
		long[] dataOffsets = new long[names.length];
		long offset = dataStart;
		for (int i = 0; i < names.length; i++)
		{
			dataOffsets[i] = offset;
			offset += compressed[i].length;
		}

		// Write everything to a ByteArrayOutputStream first, then flush to file atomically
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);

		// Header
		dos.writeInt(MAGIC);
		dos.writeInt(FORMAT_VERSION);
		dos.writeInt(names.length);

		// ToC
		for (int i = 0; i < names.length; i++)
		{
			byte[] nameBytes = names[i].getBytes(StandardCharsets.UTF_8);
			String type = typeMap.getOrDefault(names[i], "");
			byte[] typeBytes = type.getBytes(StandardCharsets.UTF_8);

			dos.writeShort(nameBytes.length);
			dos.write(nameBytes);
			dos.writeShort(typeBytes.length);
			dos.write(typeBytes);
			dos.writeLong(dataOffsets[i]);
			dos.writeInt(compressed[i].length);
			dos.writeInt(crcCompressed[i]);
			dos.writeInt(crcRaw[i]);
		}

		// Data section
		for (byte[] compressedEntry : compressed)
		{
			dos.write(compressedEntry);
		}

		dos.close();

		packFile.getParentFile().mkdirs();
		try (FileOutputStream fos = new FileOutputStream(packFile))
		{
			fos.write(bos.toByteArray());
		}

		// Invalidate cache for this file
		cache.remove(packFile);

		if (OTGLog.getLogCategoryEnabled(LogCategory.CUSTOM_OBJECTS))
		{
			OTGLog.log(LogLevel.INFO, LogCategory.CUSTOM_OBJECTS,
				"BOPack written: " + packFile.getName() + " (" + names.length + " entries)");
		}
	}

	// -------------------------------------------------------------------------
	// Private helpers
	// -------------------------------------------------------------------------

	private static BOPack loadFromFile(File packFile)
	{
		try (RandomAccessFile raf = new RandomAccessFile(packFile, "r"))
		{
			int magic = raf.readInt();
			if (magic != MAGIC)
			{
				OTGLog.log(LogLevel.ERROR, LogCategory.CUSTOM_OBJECTS,
					"BOPack: bad magic in " + packFile.getName() + ", ignoring.");
				return null;
			}
			int version = raf.readInt();
			if (version != FORMAT_VERSION)
			{
				OTGLog.log(LogLevel.ERROR, LogCategory.CUSTOM_OBJECTS,
					"BOPack: unsupported version " + version + " in " + packFile.getName() + ", ignoring.");
				return null;
			}
			int entryCount = raf.readInt();

			Map<String, Entry> toc = new LinkedHashMap<>(entryCount * 2);
			for (int i = 0; i < entryCount; i++)
			{
				short nameLen = raf.readShort();
				byte[] nameBytes = new byte[nameLen];
				raf.readFully(nameBytes);
				String name = new String(nameBytes, StandardCharsets.UTF_8);

				short typeLen = raf.readShort();
				byte[] typeBytes = new byte[typeLen];
				raf.readFully(typeBytes);
				String type = new String(typeBytes, StandardCharsets.UTF_8);

				long dataOffset = raf.readLong();
				int dataLength = raf.readInt();
				int crcComp = raf.readInt();
				int crcRaw = raf.readInt();

				toc.put(name.toLowerCase(), new Entry(name, type, dataOffset, dataLength, crcComp, crcRaw));
			}
			return new BOPack(packFile, toc);
		}
		catch (IOException e)
		{
			OTGLog.log(LogLevel.ERROR, LogCategory.CUSTOM_OBJECTS,
				"BOPack: failed to load " + packFile.getAbsolutePath() + ": " + e.getMessage());
			return null;
		}
	}

	private static int crc32(byte[] data)
	{
		CRC32 crc = new CRC32();
		crc.update(data);
		return (int) crc.getValue();
	}
}
