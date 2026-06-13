package com.pg85.otg.gen.noise;

import com.pg85.otg.util.helpers.MathHelper;

/**
 * Picks a cave biome index for a position using jittered-cell (Worley)
 * selection in quart space (1 quart = 4 blocks, the resolution Minecraft
 * stores biomes at). Each cell owns one feature point, jittered by a
 * positional hash; the nearest feature point's cell decides the biome.
 * Deterministic per world seed and allocation-free.
 *
 * Cell sizes are in quarts. The distance metric is computed in cell units,
 * so regions are roughly isotropic blobs of cellSizeXZ x cellSizeY cells
 * regardless of how the two sizes differ.
 */
public record CaveBiomeSelector(long seed, int cellSizeXZ, int cellSizeY, int biomeCount)
{
    private static final long CAVE_BIOME_SALT = 0xCA7EB10E5L;

    public static CaveBiomeSelector fromBlockSizes(long seed, int regionSizeBlocks, int regionHeightBlocks, int biomeCount)
    {
        return new CaveBiomeSelector(
            seed,
            Math.max(1, regionSizeBlocks / 4),
            Math.max(1, regionHeightBlocks / 4),
            biomeCount
        );
    }

    /**
     * Returns the cave biome index in [0, biomeCount) for the given quart
     * position (block coordinates >> 2).
     */
    public int sample(int quartX, int quartY, int quartZ)
    {
        int cellX = Math.floorDiv(quartX, this.cellSizeXZ);
        int cellY = Math.floorDiv(quartY, this.cellSizeY);
        int cellZ = Math.floorDiv(quartZ, this.cellSizeXZ);

        double posX = (double) quartX / this.cellSizeXZ;
        double posY = (double) quartY / this.cellSizeY;
        double posZ = (double) quartZ / this.cellSizeXZ;

        double bestDistSq = Double.MAX_VALUE;
        long bestHash = 0;
        for (int dx = -1; dx <= 1; dx++)
        {
            for (int dy = -1; dy <= 1; dy++)
            {
                for (int dz = -1; dz <= 1; dz++)
                {
                    long hash = cellHash(cellX + dx, cellY + dy, cellZ + dz);
                    // Feature point jitter in [0, 1) per axis, in cell units.
                    double featureX = (cellX + dx) + ((hash >>> 16) & 0xFF) / 256.0;
                    double featureY = (cellY + dy) + ((hash >>> 24) & 0xFF) / 256.0;
                    double featureZ = (cellZ + dz) + ((hash >>> 32) & 0xFF) / 256.0;
                    double distX = posX - featureX;
                    double distY = posY - featureY;
                    double distZ = posZ - featureZ;
                    double distSq = distX * distX + distY * distY + distZ * distZ;
                    if (distSq < bestDistSq)
                    {
                        bestDistSq = distSq;
                        bestHash = hash;
                    }
                }
            }
        }

        // Re-mix before reducing so the index doesn't correlate with the
        // jitter bits used above.
        return (int) Math.floorMod(MathHelper.mixSeed(bestHash, this.biomeCount), this.biomeCount);
    }

    private long cellHash(int cellX, int cellY, int cellZ)
    {
        long hash = MathHelper.mixSeed(this.seed ^ CAVE_BIOME_SALT, cellX);
        hash = MathHelper.mixSeed(hash, cellY);
        hash = MathHelper.mixSeed(hash, cellZ);
        hash = MathHelper.mixSeed(hash, cellX);
        return hash;
    }
}
