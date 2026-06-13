package com.pg85.otg.fabric.gen.noise;

import com.pg85.otg.config.settings.biome.SurfaceSettings;
import com.pg85.otg.fabric.materials.FabricMaterialData;
import com.pg85.otg.gen.OTGChunkGenerator;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Aquifer;

/**
 * Fluid picker honoring OTG's per-biome water levels.
 *
 * With aquifers disabled it is the sole fluid source and reproduces the legacy base terrain
 * rule (water when WaterLevelMin < y < WaterLevelMax), via {@link Aquifer.FluidStatus#at}:
 * water below the status level, air otherwise.
 *
 * With VanillaAquifersEnabled it is the global baseline for vanilla's NoiseBasedAquifer:
 * the per-biome WaterLevelMax caps each local aquifer's fluid level (so oceans/surface lakes
 * are unaffected), and the aquifer lowers levels locally below the preliminary surface.
 * Below WaterLevelMin the EMPTY status keeps everything dry, matching the legacy rule.
 *
 * In worlds extending below Y0 the vanilla deep lava baseline applies (lava status below
 * Y -54, like vanilla's overworld picker), so deep aquifer pockets aren't water-only.
 * Worlds with minY >= 0 keep the legacy behavior: no lava floor (carvers handle lava).
 */
public final class OTGFluidPicker implements Aquifer.FluidPicker {
    private static final Aquifer.FluidStatus EMPTY =
            new Aquifer.FluidStatus(Integer.MIN_VALUE, Blocks.AIR.defaultBlockState());
    private static final int LAVA_FLOOR_Y = -54;
    private static final Aquifer.FluidStatus LAVA =
            new Aquifer.FluidStatus(LAVA_FLOOR_Y, Blocks.LAVA.defaultBlockState());

    private final OTGChunkGenerator internalGenerator;
    private final boolean deepLavaEnabled;

    private final ThreadLocal<ColumnMemo> columnMemo = ThreadLocal.withInitial(ColumnMemo::new);

    public OTGFluidPicker(OTGChunkGenerator internalGenerator, int worldMinY) {
        this.internalGenerator = internalGenerator;
        this.deepLavaEnabled = worldMinY < 0;
    }

    @Override
    public Aquifer.FluidStatus computeFluid(int x, int y, int z) {
        // Before the WaterLevelMin dryness rule on purpose: sub-0 worlds get
        // vanilla deep lava even with legacy water level settings.
        if (this.deepLavaEnabled && y < LAVA_FLOOR_Y) {
            return LAVA;
        }
        ColumnMemo memo = this.columnMemo.get();
        int noiseX = x >> 2;
        int noiseZ = z >> 2;
        if (memo.noiseX != noiseX || memo.noiseZ != noiseZ) {
            memo.surfaceSettings = this.internalGenerator.getCachedBiomeProvider()
                    .getNoiseBiome(noiseX, noiseZ).getBiomeSettings().getSurfaceSettings();
            memo.noiseX = noiseX;
            memo.noiseZ = noiseZ;
        }
        SurfaceSettings surface = memo.surfaceSettings;

        if (y <= surface.getWaterLevelMin()) {
            return EMPTY;
        }
        BlockState water = ((FabricMaterialData) surface.getWaterBlockReplaced(y)).getState();
        return new Aquifer.FluidStatus(surface.getWaterLevelMax(), water);
    }

    private static final class ColumnMemo {
        private int noiseX = Integer.MAX_VALUE;
        private int noiseZ = Integer.MAX_VALUE;
        private SurfaceSettings surfaceSettings;
    }
}
