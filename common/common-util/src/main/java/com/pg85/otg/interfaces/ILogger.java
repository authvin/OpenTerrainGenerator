package com.pg85.otg.interfaces;

import com.pg85.otg.util.logging.LogCategory;
import com.pg85.otg.util.logging.LogLevel;

public interface ILogger
{
	void init(LogLevel level, boolean logCustomObjects, boolean logStructurePlotting, boolean logConfigs, boolean logPerformance, boolean logBiomeRegistry, boolean logDecoration, boolean logMobs, String logPresets);
	boolean getLogCategoryEnabled(LogCategory category);
	void log(LogLevel level, LogCategory category, String message);
	void printStackTrace(LogLevel marker, LogCategory category, Exception e);
	boolean canLogForPreset(String presetFolderName);

	default void info(LogCategory category, String message, Object... objects) {
		log(LogLevel.INFO, category, String.format(message, objects));
	}

	default void warn(LogCategory category, String message, Object... objects) {
		log(LogLevel.WARN, category, String.format(message, objects));
	}

	default void error(LogCategory category, String message, Object... objects) {
		log(LogLevel.ERROR, category, String.format(message, objects));
	}

	default void fatal(LogCategory category, String message, Object... objects) {
		log(LogLevel.FATAL, category, String.format(message, objects));
	}

	default void info(String message, Object... objects) {
		info(LogCategory.MAIN, message, objects);
	}

	default void warn(String message, Object... objects) {
		warn(LogCategory.MAIN, message, objects);
	}

	default void error(String message, Object... objects) {
		error(LogCategory.MAIN, message, objects);
	}

	default void fatal(String message, Object... objects) {
		fatal(LogCategory.MAIN, message, objects);
	}

	default void printStackTrace(Exception e) {
		printStackTrace(LogLevel.ERROR, LogCategory.MAIN, e);
	}

}
