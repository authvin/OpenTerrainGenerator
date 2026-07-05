package com.pg85.otg.util.gen;

import com.pg85.otg.config.settings.biome.SurfaceSettings;
import com.pg85.otg.constants.Constants;
import com.pg85.otg.interfaces.ICaveBiomeSettingsProvider;
import com.pg85.otg.util.ChunkCoordinate;
import lombok.Getter;

import java.util.Random;

/**
 * Holds early generator information about a chunk, like water levels, noise
 * values, etc.
 */
public final class GeneratingChunk
{

	private static final int BEDROCK_LAYER_HEIGHT = 5;
	// How far below the blended terrain center height surface/ground rules may still
	// re-apply after a mid-column air gap (overhangs, near-surface cave floors). Below
	// this depth an air gap is considered underground (cave), not surface.
	private static final int SURFACE_GATE_DEPTH = 16;
	@Getter
	private final OTGWorldInfo worldHeight;
	public final Random random;
	private final int[] waterLevel;
	private final double[] surfaceNoise;
	private final double[] centerHeight;
	private final ICaveBiomeSettingsProvider caveBiomeSettingsProvider;

	public GeneratingChunk(Random random, int[] waterLevel, double[] surfaceNoise, double[] centerHeight, ICaveBiomeSettingsProvider caveBiomeSettingsProvider, OTGWorldInfo worldHeight)
	{
		this.random = random;
		this.waterLevel = waterLevel;
		this.surfaceNoise = surfaceNoise;
		this.centerHeight = centerHeight;
		this.caveBiomeSettingsProvider = caveBiomeSettingsProvider;
		this.worldHeight = worldHeight;
	}

	/**
	 * Gets the surface noise value at the given position.
	 *
	 * @return The surface noise value.
	 */
	public double getNoise(int x, int z)
	{
		return this.surfaceNoise[x + z * Constants.CHUNK_SIZE];
	}

	/**
	 * Gets the water level at the given position.
	 *
	 * @return The water level.
	 */
	public int getWaterLevel(int x, int z)
	{
		return this.waterLevel[z + x * Constants.CHUNK_SIZE];
	}

	/**
	 * Lowest Y where surface and ground blocks may still be applied after the column scan
	 * has passed a mid-column air gap. Above this (or within the first solid run from the
	 * top of the column) air-to-solid transitions count as surface; below it they are cave
	 * floors and are left to cave surface rules.
	 *
	 * @return The lowest Y the surface band reaches for this column.
	 */
	public int getSurfaceGateY(int x, int z)
	{
		return (int) this.centerHeight[z + x * Constants.CHUNK_SIZE] - SURFACE_GATE_DEPTH;
	}

	/**
	 * SurfaceSettings of the cave biome at a world block position, or null when no cave
	 * biome applies there. Callers fall back to the surface biome's settings for cave
	 * surface rules.
	 */
	public SurfaceSettings getCaveBiomeSurfaceSettings(int blockX, int blockY, int blockZ)
	{
		if (this.caveBiomeSettingsProvider == null)
		{
			return null;
		}
		return this.caveBiomeSettingsProvider.getCaveBiomeSurfaceSettings(blockX, blockY, blockZ);
	}

	/**
	 * Gets whether bedrock should be created at the given position.
	 *
	 * @param y			The y position.
	 * @return True if bedrock should be created, false otherwise.
	 */
	public boolean mustCreateBedrockAt(boolean flatBedrock, boolean disableBedrock, boolean ceilingBedrock, int y)
	{
		// The "- 2" that appears in this method, comes from that heightCap -
		// 1 is the highest place where a block can be placed, and heightCap -
		// 2 is the highest place where bedrock can be generated to make sure
		// there are no light glitches - see #117

		// Handle flat bedrock
		if (flatBedrock)
		{
			if (!disableBedrock && y <= worldHeight.getXAboveMin(1))
			{
				return true;
			}
            return ceilingBedrock && y >= worldHeight.getXBelowMax(1);
        }

		// Otherwise we have normal bedrock
		if (!disableBedrock && y < worldHeight.getXAboveMin(BEDROCK_LAYER_HEIGHT))
		{
			return y <= worldHeight.getXAboveMin(BEDROCK_LAYER_HEIGHT, random);
		}
		if (ceilingBedrock && y > worldHeight.getXBelowMax(BEDROCK_LAYER_HEIGHT) && y < worldHeight.maxY())
		{
			int amountBelowHeightCap = worldHeight.getXBelowMax(y + 1);
			if (amountBelowHeightCap < 0 || amountBelowHeightCap > BEDROCK_LAYER_HEIGHT)
			{
				return false;
			}

			return amountBelowHeightCap <= this.random.nextInt(BEDROCK_LAYER_HEIGHT);
		}
		return false;
	} 
}
