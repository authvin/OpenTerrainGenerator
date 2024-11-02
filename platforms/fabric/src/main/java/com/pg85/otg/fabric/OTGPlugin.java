package com.pg85.otg.fabric;

import com.pg85.otg.OTG;
import com.pg85.otg.fabric.materials.FabricMaterialReader;
import com.pg85.otg.fabric.util.FabricLogger;
import com.pg85.otg.util.OTGLog;
import com.pg85.otg.util.OTGMaterialReader;
import com.pg85.otg.util.logging.LogCategory;
import com.pg85.otg.util.logging.LogLevel;
import net.fabricmc.api.ModInitializer;

public class OTGPlugin implements ModInitializer {
	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		OTGLog.setLogger(new FabricLogger());
		OTGLog.getLogger().log(LogLevel.INFO, LogCategory.MAIN, "OTG Engine starting");
		OTGMaterialReader.set(new FabricMaterialReader());
		OTG.startEngine(new FabricEngine());
		OTG.log("OTG Engine started, presets loaded");
	}
}
