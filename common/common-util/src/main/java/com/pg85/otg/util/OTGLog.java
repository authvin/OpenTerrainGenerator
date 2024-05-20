package com.pg85.otg.util;

import com.pg85.otg.interfaces.ILogger;
import com.pg85.otg.util.logging.LogCategory;
import com.pg85.otg.util.logging.LogLevel;

public final class OTGLog {
    private static ILogger logger = new BasicLogger();
    public static void setLogger(ILogger logger)
    {
        OTGLog.logger = logger;
    }
    public static ILogger getLogger()
    {
        return OTGLog.logger;
    }

    // Not for use by platform code; it's a fallback for when code is executed and the engine isn't running
    // like when generating YAML schema from settings
    private static class BasicLogger implements ILogger
    {
        private LogLevel level;
        private boolean logCustomObjects;
        private boolean logStructurePlotting;
        private boolean logConfigs;
        private boolean logPerformance;
        private boolean logBiomeRegistry;
        private boolean logDecoration;
        private boolean logMobs;

        @Override
        public void init(LogLevel level, boolean logCustomObjects, boolean logStructurePlotting, boolean logConfigs, boolean logPerformance, boolean logBiomeRegistry, boolean logDecoration, boolean logMobs, String logPresets) {

            this.level = level;
            this.logCustomObjects = logCustomObjects;
            this.logStructurePlotting = logStructurePlotting;
            this.logConfigs = logConfigs;
            this.logPerformance = logPerformance;
            this.logBiomeRegistry = logBiomeRegistry;
            this.logDecoration = logDecoration;
            this.logMobs = logMobs;
        }

        @Override
        public boolean getLogCategoryEnabled(LogCategory category) {
            switch (category) {
                case MAIN -> {
                    return true;
                }
                case CUSTOM_OBJECTS -> {
                    return logCustomObjects;
                }
                case STRUCTURE_PLOTTING -> {
                    return logStructurePlotting;
                }
                case CONFIGS -> {
                    return logConfigs;
                }
                case BIOME_REGISTRY -> {
                    return logBiomeRegistry;
                }
                case DECORATION -> {
                    return logDecoration;
                }
                case PERFORMANCE -> {
                    return logPerformance;
                }
                case MOBS -> {
                    return logMobs;
                }
            }
            return false;
        }

        @Override
        public void log(LogLevel level, LogCategory category, String message) {
            if (this.level.ordinal() <= level.ordinal()) {
                System.out.println(level.name() + " " + category.name() + " " + message);
            }
        }

        @Override
        public void printStackTrace(LogLevel marker, LogCategory category, Exception e) {
            if (this.level.ordinal() <= marker.ordinal()) {
                e.printStackTrace();
            }
        }

        @Override
        public boolean canLogForPreset(String presetFolderName) {
            return true;
        }
    }
}
