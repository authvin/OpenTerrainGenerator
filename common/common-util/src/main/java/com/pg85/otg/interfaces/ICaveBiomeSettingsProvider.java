package com.pg85.otg.interfaces;

import com.pg85.otg.config.settings.biome.SurfaceSettings;

/**
 * Resolves the cave biome's SurfaceSettings at a block position, or null when no cave
 * biome applies there (no cave biomes configured, position above the cave biome depth,
 * or an unresolvable CaveBiomes entry). Callers fall back to the surface biome's own
 * settings for cave surface rules when this returns null.
 */
@FunctionalInterface
public interface ICaveBiomeSettingsProvider
{
	SurfaceSettings getCaveBiomeSurfaceSettings(int blockX, int blockY, int blockZ);
}
