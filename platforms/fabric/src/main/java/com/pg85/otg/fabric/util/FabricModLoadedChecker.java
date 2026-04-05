package com.pg85.otg.fabric.util;

import com.pg85.otg.interfaces.IModLoadedChecker;
import com.pg85.otg.util.OTGModChecker;
import net.fabricmc.loader.api.FabricLoader;

public class FabricModLoadedChecker implements IModLoadedChecker {
    @Override
    public boolean isModLoaded(String mod) {
        return FabricLoader.getInstance().isModLoaded(mod);
    }

    public FabricModLoadedChecker() {
        OTGModChecker.set(this);
    }
}
