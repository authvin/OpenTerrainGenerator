package com.pg85.otg.util.biome;

import com.pg85.otg.util.OTGLog;
import com.pg85.otg.util.helpers.MathHelper;
import com.pg85.otg.util.logging.LogCategory;
import com.pg85.otg.util.logging.LogLevel;
import com.pg85.otg.util.materials.LocalMaterialData;

import java.util.ArrayList;
import java.util.List;

/**
 * Precomputed per-Y lookup for a stack of {@link StoneLayer}s. Built once at
 * config load; sampling is a single array index, plus one positional hash
 * inside transition bands.
 *
 * Layers are painted in config order, so a later-defined layer overwrites an
 * earlier one where they overlap. Y levels not covered by any layer sample as
 * null, which callers treat as "use the biome's StoneBlock". Below the lowest
 * layer the lowest layer's block is used, so a stack doesn't need to reach
 * world minY.
 */
public final class StoneLayerStack
{
    public static final StoneLayerStack EMPTY = new StoneLayerStack(List.of());

    private static final long STONE_LAYER_SALT = 0x51AB1A7E5L;

    // Solid entries have chanceOfPrimary >= 1 and never hash. In a transition
    // band, primary is the lower layer's block and fallback is whatever layer
    // occupies this y (null = gap, caller falls back to StoneBlock).
    private record StoneEntry(LocalMaterialData primary, LocalMaterialData fallback, float chanceOfPrimary)
    {
    }

    private final List<StoneLayer> layers;
    private final StoneEntry[] entries;
    private final int tableMinY;
    private final LocalMaterialData bottomBlock;

    public StoneLayerStack(List<StoneLayer> layers)
    {
        this.layers = List.copyOf(layers);
        if (layers.isEmpty())
        {
            this.entries = new StoneEntry[0];
            this.tableMinY = 0;
            this.bottomBlock = null;
            return;
        }

        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (StoneLayer layer : layers)
        {
            minY = Math.min(minY, layer.minY());
            maxY = Math.max(maxY, layer.maxY() + layer.transition());
        }
        this.tableMinY = minY;

        LocalMaterialData[] base = new LocalMaterialData[maxY - minY + 1];
        for (StoneLayer layer : layers)
        {
            for (int y = layer.minY(); y <= layer.maxY(); y++)
            {
                if (base[y - minY] != null && OTGLog.getLogger().getLogCategoryEnabled(LogCategory.CONFIGS))
                {
                    OTGLog.getLogger().log(LogLevel.WARN, LogCategory.CONFIGS,
                        "StoneLayer " + layer.block() + " overlaps another stone layer at Y " + y + ", the later-defined layer wins.");
                }
                base[y - minY] = layer.block();
            }
        }

        this.entries = new StoneEntry[base.length];
        for (int i = 0; i < base.length; i++)
        {
            if (base[i] != null)
            {
                this.entries[i] = new StoneEntry(base[i], null, 1.0f);
            }
        }
        // Transition bands dither each layer's block upward past its maxY,
        // later-defined layers winning where bands overlap.
        for (StoneLayer layer : layers)
        {
            for (int d = 1; d < layer.transition(); d++)
            {
                int i = layer.maxY() + d - minY;
                if (i < 0 || i >= base.length)
                {
                    continue;
                }
                float chance = (float) (layer.transition() - d) / (float) layer.transition();
                this.entries[i] = new StoneEntry(layer.block(), base[i], chance);
            }
        }

        this.bottomBlock = this.entries[0].primary();
    }

    public boolean isEmpty()
    {
        return this.entries.length == 0;
    }

    public List<LocalMaterialData> getLayerBlocks()
    {
        List<LocalMaterialData> blocks = new ArrayList<>();
        for (StoneLayer layer : this.layers)
        {
            blocks.add(layer.block());
        }
        return blocks;
    }

    /**
     * Returns the strata block at the given position, or null if no layer
     * covers this y (caller should fall back to the biome's StoneBlock).
     */
    public LocalMaterialData sample(long seed, int x, int y, int z)
    {
        int idx = y - this.tableMinY;
        if (idx < 0)
        {
            return this.bottomBlock;
        }
        if (idx >= this.entries.length)
        {
            return null;
        }
        StoneEntry entry = this.entries[idx];
        if (entry == null)
        {
            return null;
        }
        if (entry.chanceOfPrimary() >= 1.0f)
        {
            return entry.primary();
        }
        return positionalRandom(seed, x, y, z) < entry.chanceOfPrimary() ? entry.primary() : entry.fallback();
    }

    private static float positionalRandom(long seed, int x, int y, int z)
    {
        long hash = MathHelper.mixSeed(seed ^ STONE_LAYER_SALT, x);
        hash = MathHelper.mixSeed(hash, y);
        hash = MathHelper.mixSeed(hash, z);
        hash = MathHelper.mixSeed(hash, x);
        return (float) ((hash >>> 40) & 0xFFFFFF) / (float) (1 << 24);
    }
}
