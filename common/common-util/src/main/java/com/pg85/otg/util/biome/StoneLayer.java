package com.pg85.otg.util.biome;

import com.pg85.otg.util.materials.LocalMaterialData;

/**
 * A single vertical stratum of base stone, spanning minY to maxY (inclusive).
 * The transition is the number of blocks the layer dithers upward past its
 * maxY into whatever lies above, like vanilla's stone/deepslate gradient.
 */
public record StoneLayer(LocalMaterialData block, int minY, int maxY, int transition)
{
}
