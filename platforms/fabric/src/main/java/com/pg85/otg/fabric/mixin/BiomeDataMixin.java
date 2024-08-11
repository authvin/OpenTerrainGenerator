package com.pg85.otg.fabric.mixin;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.biome.BiomeData;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BiomeData.class)
public class BiomeDataMixin {

    // Static fields to store the references
    public static HolderGetter<PlacedFeature> PLACED_FEATURE_HOLDER;
    public static HolderGetter<ConfiguredWorldCarver<?>> CONFIGURED_CARVER_HOLDER;
    public static boolean INITIALIZED = false;

    // Inject code at the end of the bootstrap method
    @Inject(method = "bootstrap", at = @At("TAIL"))
    private static void storeHolderGetters(BootstapContext<Biome> arg, CallbackInfo ci) {
        PLACED_FEATURE_HOLDER = arg.lookup(Registries.PLACED_FEATURE);
        CONFIGURED_CARVER_HOLDER = arg.lookup(Registries.CONFIGURED_CARVER);
        INITIALIZED = true;
    }
}