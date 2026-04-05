package com.pg85.otg.util;

import com.pg85.otg.interfaces.IModLoadedChecker;

public class OTGModChecker {
    private static IModLoadedChecker modLoadedChecker = null;

    public static IModLoadedChecker get() {
        return modLoadedChecker;
    }

    public static void set(IModLoadedChecker instance) {
        modLoadedChecker = instance;
    }
}
