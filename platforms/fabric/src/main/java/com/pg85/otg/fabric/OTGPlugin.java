package com.pg85.otg.fabric;

import com.pg85.otg.OTG;
import net.fabricmc.api.ModInitializer;

public class OTGPlugin implements ModInitializer {
	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		OTG.log("OTG Engine starting");
		OTG.startEngine(new FabricEngine());

		OTG.log("Loading presets");
		// Load presets

	}


}
