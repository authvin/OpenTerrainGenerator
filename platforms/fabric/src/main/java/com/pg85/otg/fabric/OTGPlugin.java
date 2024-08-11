package com.pg85.otg.fabric;

import com.pg85.otg.OTG;
import com.pg85.otg.constants.Constants;
import com.pg85.otg.fabric.biome.OTGFabricBiomeProvider;
import com.pg85.otg.fabric.gen.OTGFabricChunkGenerator;
import com.pg85.otg.fabric.materials.FabricMaterialReader;
import com.pg85.otg.fabric.mixin.BuiltInRegistriesAccessor;
import com.pg85.otg.fabric.util.FabricLogger;
import com.pg85.otg.util.OTGLog;
import com.pg85.otg.util.OTGMaterialReader;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class OTGPlugin implements ModInitializer {
	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		OTG.log("OTG Engine starting");
		OTGLog.setLogger(new FabricLogger());
		OTGMaterialReader.set(new FabricMaterialReader());
		OTG.startEngine(new FabricEngine());
		OTG.log("OTG Engine started, presets loaded");

		var registries = BuiltInRegistriesAccessor.getWriteableRegistry();


		// Let MC know about our chunk generator and biome provider.
		// If they're not added, we get errors and MC does not save properly.
		Registry.register(BuiltInRegistries.BIOME_SOURCE, new ResourceLocation(Constants.MOD_ID_SHORT, "default"), OTGFabricBiomeProvider.CODEC);
		Registry.register(BuiltInRegistries.CHUNK_GENERATOR, new ResourceLocation(Constants.MOD_ID_SHORT, "default"), OTGFabricChunkGenerator.CODEC);
	}
}
