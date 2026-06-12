package com.pg85.otg.gen.noise;

import com.pg85.otg.constants.Constants;
import com.pg85.otg.util.helpers.MathHelper;

public record TerrainNoisePipeline(
        OctavePerlinNoiseSampler interpolationNoise,
        OctavePerlinNoiseSampler lowerNoise,
        OctavePerlinNoiseSampler upperNoise,
        OctavePerlinNoiseSampler depthNoise,
        int noiseSizeY,
        int surfaceSections,
        double continentalScale,
        double continentalBias,
        double baseHeightFraction,
        double biomeHeightWeight,
        double continentalHeightWeight,
        double falloffSteepness,
        double noiseAmplitude
) {

    // Vanilla Minecraft base frequency for terrain noise coordinate scaling.
    // Not normalization — controls feature size in world space.
    // "It's a number that made the worldgen look good!" - Dinnerbone 2020
    private static final double WORLD_GEN_CONSTANT = 684.412;
    private static final int INTERPOLATION_OCTAVES = 8;
    private static final int TERRAIN_OCTAVES = 16;
    private static final double LEGACY_NOISE_SCALE = 128.0D;

    // --- Raw accumulation ---
    // Inverted amplitude pattern: later octaves contribute exponentially more.
    // Normalized by theoretical max (2^octaveCount - 1) and clamped to [-1, 1].
    // Output is always bounded regardless of octave count or world height.

    public double accumulateOctaves(
            OctavePerlinNoiseSampler sampler,
            int x, int y, int z,
            double horizontalScale, double verticalScale,
            int octaveCount
    ) {
        double noise = 0.0D;
        double amplitude = 1.0D;
        for (int i = 0; i < octaveCount; ++i) {
            double scaledX = OctavePerlinNoiseSampler.maintainPrecision((double) x * horizontalScale * amplitude);
            double scaledY = OctavePerlinNoiseSampler.maintainPrecision((double) y * verticalScale * amplitude);
            double scaledZ = OctavePerlinNoiseSampler.maintainPrecision((double) z * horizontalScale * amplitude);
            double scaledVerticalScale = verticalScale * amplitude;

            PerlinNoiseSampler perlinNoiseSampler = sampler.getOctave(i);
            if (perlinNoiseSampler != null) {
                noise += perlinNoiseSampler.sample(
                        scaledX, scaledY, scaledZ,
                        scaledVerticalScale,
                        (double) y * scaledVerticalScale
                ) / amplitude;
            }

            amplitude /= 2.0D;
        }

        double theoreticalMax = Math.pow(2, octaveCount) - 1;
        return MathHelper.clamp(noise / theoreticalMax, -1.0, 1.0);
    }

    // --- Composed operations (used by generateColumn) ---

    // Maps normalized [-1,1] noise to [0,1] for use as volatility blend weight.
    public double interpolate(int x, int y, int z, double horizontalStretch, double verticalStretch) {
        double raw = accumulateOctaves(this.interpolationNoise, x, y, z, horizontalStretch, verticalStretch, INTERPOLATION_OCTAVES);
        return MathHelper.clamp((raw * 25.5 + 1.0) / 2.0, 0.0, 1.0); // 25.5 = 255/10, restores old range
    }

    // Selects/blends between two noise layers based on interpolation delta.
    // Noise is [-1,1], volatility controls per-biome amplitude, noiseAmplitude scales globally.
    public double selectVolatility(
            double delta,
            int x, int y, int z,
            double horizontalScale, double verticalScale,
            double volatility1, double volatility2,
            double volatilityWeight1, double volatilityWeight2
    ) {
        double result;
        if (delta < volatilityWeight1) {
            double noise = accumulateOctaves(this.lowerNoise, x, y, z, horizontalScale, verticalScale, TERRAIN_OCTAVES);
            result = noise * volatility1;
        } else if (delta > volatilityWeight2) {
            double noise = accumulateOctaves(this.upperNoise, x, y, z, horizontalScale, verticalScale, TERRAIN_OCTAVES);
            result = noise * volatility2;
        } else {
            double noise1 = accumulateOctaves(this.lowerNoise, x, y, z, horizontalScale, verticalScale, TERRAIN_OCTAVES);
            double noise2 = accumulateOctaves(this.upperNoise, x, y, z, horizontalScale, verticalScale, TERRAIN_OCTAVES);
            result = MathHelper.lerp(delta, noise1 * volatility1, noise2 * volatility2);
        }
        return result * LEGACY_NOISE_SCALE * this.noiseAmplitude;
    }

    // Continental height: large-scale vertical offset to terrain surface.
    // Depth noise is standard path (bounded [-1,1]). Bias shifts the zero-crossing to control
    // valley/peak distribution. Per-biome factors control response strength.
    // Output range: [-valleyFactor, peakFactor], then * continentalScale in generateColumn.
    public double computeExtraHeight(int x, int z, double valleyFactor, double peakFactor) {
        // Raw multi-octave sample is typically ±0.3; *3 spreads it over ~±1 so the clamps below engage.
        double noise = this.depthNoise.sample(x * 200, 10.0D, z * 200, 1.0D, 0.0D, true) * 3.0D;

        // Bias shifts the zero-crossing. Positive bias = more valleys than peaks.
        noise += this.continentalBias;

        if (noise > 0) {
            return Math.min(noise, 1.0) * peakFactor;
        } else {
            return Math.max(noise, -1.0) * valleyFactor;
        }
    }

    // Places terrain surface as a fraction of column height.
    // baseHeightFraction: where surface sits with no biome offset (0.47 ≈ sea level at half world)
    // biomeHeightWeight: how much biome height config shifts the surface
    // continentalHeightWeight: how much continental noise shifts the surface
    public double computeColumnHeight(float height, float extraHeight, int usedYSections) {
        return usedYSections * (this.baseHeightFraction + height * this.biomeHeightWeight + extraHeight * this.continentalHeightWeight);
    }

    // Density gradient: the primary signal determining solid vs air.
    // falloffSteepness: controls transition zone thickness. Default 6.0 → ~80 block transition at vol=0.3.
    // / biomeVolatility: per-biome modulation. Higher vol = gentler = more noise features visible.
    // *4 when positive (below center): asymmetric — solid side steeper than air side.
    public double computeFalloff(double centerHeight, int y, double biomeVolatility) {
        double falloff = (centerHeight - y) * this.falloffSteepness / biomeVolatility;
        if (falloff > 0.0) {
            falloff *= 4.0;
        }
        return falloff;
    }

    // --- Main verb ---

    public void generateColumn(
            double[] noiseColumn,
            int noiseX, int noiseZ,
            BlendedBiomeParams params,
            double[] chc,
            boolean disableBiomeHeight
    ) {
        float extraHeight = (float) (computeExtraHeight(noiseX, noiseZ, params.valleyFactor(), params.peakFactor()) * this.continentalScale);

        // *0.9+0.1: CLAMPING — ensures biomeVolatility never reaches 0 (would cause /0 in falloff).
        // Maps config range [0,∞) to [0.1,∞). At vol=1.0 config → 1.0 effective.
        float biomeVolatility = params.biomeVolatility() * 0.9f + 0.1f;
        double centerHeight = computeColumnHeight(params.height(), extraHeight, this.surfaceSections);

        // COORDINATE SCALING: WORLD_GEN_CONSTANT * fracture controls feature size.
        // /80 and /160 for interpolation noise: samples at much lower frequency than terrain noise.
        // This makes the biomeVolatility blend regions very large (spanning many chunks).
        double horizontalScale = WORLD_GEN_CONSTANT * params.horizontalFracture();
        double verticalScale = WORLD_GEN_CONSTANT * params.verticalFracture();

        for (int y = 0; y <= this.noiseSizeY; ++y) {
            double falloff = computeFalloff(centerHeight, y, biomeVolatility);

            double delta = interpolate(noiseX, y, noiseZ, horizontalScale / 80, verticalScale / 160);
            double noise = selectVolatility(
                    delta, noiseX, y, noiseZ,
                    horizontalScale, verticalScale,
                    params.volatility1(), params.volatility2(),
                    params.volatilityWeight1(), params.volatilityWeight2()
            );

            if (!disableBiomeHeight) {
                // noise + falloff: these are on DIFFERENT scales (noise ~±58, falloff ~±1920).
                // Falloff dominates. Noise only matters near the surface where falloff ≈ 0.
                noise += falloff;

                // Top-of-world fade: forces air in the top 4 noise cells.
                // Lerps toward -10 (strongly negative = air) over last 4 Y sections.
                if (y > this.noiseSizeY - 4) {
                    noise = MathHelper.clampedLerp(noise, -10, ((double) y - (this.noiseSizeY - 4)) / 4.0);
                }
            }

            // CHC (Custom Height Control): user-configured per-Y density offset. Additive, same scale as noise.
            noise += chc[y];

            noiseColumn[y] = noise;
        }
    }
}
