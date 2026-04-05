package com.pg85.otg.customobject.bo4;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Implemented by BO config classes (BO4Config, and future BO3Config etc.) that
 * can be serialized into a BOPack container.
 *
 * BOPack itself has no knowledge of specific BO types — it treats every entry
 * as an opaque compressed blob tagged with a type string. Each implementing
 * class is responsible for its own binary format; BOPack just stores, retrieves,
 * and integrity-checks the bytes.
 *
 * To add a new BO type to the pack pipeline:
 *   1. Implement this interface on the new type's config class.
 *   2. The BOPackExporter will pick it up automatically — no other changes needed.
 */
public interface BOPackSerializable
{
	/**
	 * The type tag stored in the pack's table of contents.
	 * Must be a short, stable, human-readable ASCII string, e.g. "BO4", "BO3".
	 * Used as a sanity check on read; must match what the reader expects.
	 */
	String getBOPackType();

	/**
	 * Serialize this config to raw (uncompressed) bytes suitable for pack storage.
	 * Compression is applied by BOPack.write() — do not compress here.
	 *
	 * @return serialized bytes, or null if serialization should be skipped for this object
	 */
	byte[] serializeForPack(String presetFolderName, Path otgRootFolder) throws IOException;
}
