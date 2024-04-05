package com.pg85.otg.util;

import com.pg85.otg.interfaces.ILogger;

public final class OTGLog {
    private static ILogger logger;
    public static void setLogger(ILogger logger)
    {
        OTGLog.logger = logger;
    }
    public static ILogger getLogger()
    {
        return OTGLog.logger;
    }
}
