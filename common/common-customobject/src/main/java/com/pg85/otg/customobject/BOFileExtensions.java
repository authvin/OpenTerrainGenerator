package com.pg85.otg.customobject;

import java.util.Locale;

/**
 * Central registry of file extensions used by BO custom object formats.
 * All extension constants and extension-related helpers live here so they
 * can be kept in sync and referenced from a single place.
 */
public final class BOFileExtensions
{
	public static final String BO2      = ".bo2";
	public static final String BO3      = ".bo3";
	public static final String BO4      = ".bo4";
	/** Legacy individual binary cache file produced alongside a .bo4 source. */
	public static final String BO4DATA  = ".bo4data";
	/** Combined pack container produced by BOPackExporter. */
	public static final String BOPACK   = ".bopack";
	/** Template files used as a base for BO3 generation. */
	public static final String BO3TEMPLATE = ".bo3template";

	private BOFileExtensions() {}

	/**
	 * Returns true if the given (lowercase) extension belongs to a loadable
	 * custom object file (.bo2, .bo3, .bo4, .bo4data).
	 */
	public static boolean isCustomObjectExtension(String ext)
	{
		if (ext == null) {
			return false;
		}
		ext = ext.toLowerCase(Locale.ROOT);
		return BO4DATA.equals(ext) || BO4.equals(ext) || BO3.equals(ext) || BO2.equals(ext);
	}

	/**
	 * Converts a .bo4 / .bo3 source file path to the companion .BO4Data path.
	 * If the path already ends in .bo4data (case-insensitive) it is returned
	 * unchanged.
	 *
	 * @param path absolute path of the source file
	 * @return absolute path of the corresponding .BO4Data file
	 */
	public static String toBO4DataPath(String path)
	{
		path = path.toLowerCase(Locale.ROOT);
		if (path.endsWith(BO4DATA))
		{
			return path;
		}
		if (path.endsWith(".bo4")) return path.replace(".bo4", ".BO4Data");
		if (path.endsWith(".bo3")) return path.replace(".bo3", ".BO4Data");
		return path;
	}
}