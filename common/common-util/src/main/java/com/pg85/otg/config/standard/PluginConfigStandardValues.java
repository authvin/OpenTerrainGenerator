package com.pg85.otg.config.standard;

import com.pg85.otg.config.settingType.Setting;
import com.pg85.otg.config.settingType.Settings;
import com.pg85.otg.constants.settings.LogLevels;

public class PluginConfigStandardValues extends Settings
{
	// Plugin Defaults
	
	public static final Setting<LogLevels> LOG_LEVEL = Settings.enumSetting("LogLevel", LogLevels.Standard);
	public static final Setting<Boolean> DECORATION_ENABLED = Settings.booleanSetting("DecorationEnabled", true);
	public static final Setting<Boolean> LOG_CUSTOM_OBJECTS = Settings.booleanSetting("LogCustomObjects", false);
	public static final Setting<Boolean> LOG_BO4_PLOTTING = Settings.booleanSetting("LogBO4Plotting", false);
	public static final Setting<Boolean> LOG_CONFIGS = Settings.booleanSetting("LogConfigs", false);
	public static final Setting<Boolean> LOG_BIOME_REGISTRY = Settings.booleanSetting("LogBiomeRegistry", false);
	public static final Setting<Boolean> LOG_DECORATION = Settings.booleanSetting("LogDecoration", false);
	public static final Setting<Boolean> LOG_MOBS = Settings.booleanSetting("LogMobs", false);
	public static final Setting<String> LOG_PRESETS = Settings.stringSetting("LogPresets", "all");
	public static final Setting<Boolean> LOG_PERFORMANCE = Settings.booleanSetting("LogPerformance", false);
	public static final Setting<Boolean> DEVELOPER_MODE = Settings.booleanSetting("DeveloperMode", false);
	public static final Setting<Integer> WORKER_THREADS = Settings.intSetting("WorkerThreads", 0, 0, 10);
}
