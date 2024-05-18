package com.pg85.otg.config.settings.biome;

import com.pg85.otg.config.io.SettingsMap;
import com.pg85.otg.config.settingType.Setting;
import com.pg85.otg.config.settings.preset.BlockSettings;
import com.pg85.otg.config.standard.BiomeStandardValues;
import com.pg85.otg.constants.Constants;
import com.pg85.otg.interfaces.IBiome;
import com.pg85.otg.interfaces.IMaterialReader;
import com.pg85.otg.interfaces.ISurfaceGenerator;
import com.pg85.otg.interfaces.ISurfaceGeneratorNoiseProvider;
import com.pg85.otg.util.biome.ReplaceBlockMatrix;
import com.pg85.otg.util.gen.ChunkBuffer;
import com.pg85.otg.util.gen.GeneratingChunk;
import com.pg85.otg.util.materials.LocalMaterialData;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SurfaceSettings {
    private final ISurfaceGenerator surfaceGenerator;
    private final ReplaceBlockMatrix replacedBlocks;
    private final BlockSettings blockSettings;
    private final int waterLevelMax;
    private final int waterLevelMin;
    private final boolean useWorldWaterLevel;
    private final LocalMaterialData stoneBlock;
    private final LocalMaterialData surfaceBlock;
    private final LocalMaterialData underWaterSurfaceBlock;
    private final LocalMaterialData groundBlock;
    private final LocalMaterialData sandStoneBlock;
    private final LocalMaterialData redSandStoneBlock;
    private final LocalMaterialData waterBlock;
    private final LocalMaterialData iceBlock;
    private final LocalMaterialData packedIceBlock;
    private final LocalMaterialData snowBlock;
    private final LocalMaterialData cooledLavaBlock;
    private boolean replacedBlocksInitialised = false;

    public static SurfaceSettings getSurfaceSettings(SettingsMap settingsReader, IMaterialReader materialReader, Setting<ISurfaceGenerator> surfaceGeneratorSetting, BlockSettings parent) {
        SurfaceSettingsBuilder builder = SurfaceSettings.builder();

        builder.surfaceGenerator(settingsReader.getSetting(surfaceGeneratorSetting));
        builder.replacedBlocks(settingsReader.getSetting(BiomeStandardValues.REPLACED_BLOCKS));
        builder.blockSettings(parent);
        builder.waterLevelMax(settingsReader.getSetting(BiomeStandardValues.WATER_LEVEL_MAX));
        builder.waterLevelMin(settingsReader.getSetting(BiomeStandardValues.WATER_LEVEL_MIN));
        builder.useWorldWaterLevel(settingsReader.getSetting(BiomeStandardValues.USE_WORLD_WATER_LEVEL));
        builder.stoneBlock(settingsReader.getSetting(BiomeStandardValues.STONE_BLOCK, materialReader));
        builder.surfaceBlock(settingsReader.getSetting(BiomeStandardValues.SURFACE_BLOCK, materialReader));
        builder.underWaterSurfaceBlock(settingsReader.getSetting(BiomeStandardValues.UNDER_WATER_SURFACE_BLOCK, materialReader));
        builder.groundBlock(settingsReader.getSetting(BiomeStandardValues.GROUND_BLOCK, materialReader));
        builder.sandStoneBlock(settingsReader.getSetting(BiomeStandardValues.SANDSTONE_BLOCK, materialReader));
        builder.redSandStoneBlock(settingsReader.getSetting(BiomeStandardValues.RED_SANDSTONE_BLOCK, materialReader));
        builder.waterBlock(settingsReader.getSetting(BiomeStandardValues.WATER_BLOCK, materialReader));
        builder.iceBlock(settingsReader.getSetting(BiomeStandardValues.ICE_BLOCK, materialReader));
        builder.packedIceBlock(settingsReader.getSetting(BiomeStandardValues.PACKED_ICE_BLOCK, materialReader));
        builder.snowBlock(settingsReader.getSetting(BiomeStandardValues.SNOW_BLOCK, materialReader));
        builder.cooledLavaBlock(settingsReader.getSetting(BiomeStandardValues.COOLED_LAVA_BLOCK, materialReader));

        return builder.fixSettings().build();
    }

    public static class SurfaceSettingsBuilder {
        public SurfaceSettingsBuilder fixSettings() {
            checkWaterLevelMax();
            return this;
        }
        public void checkWaterLevelMax() {
            waterLevelMax = Math.max(waterLevelMax, waterLevelMin);
        }
    }

    // Any blocks spawned/checked during base terrain gen that use the biomeconfig materials
    // call getXXXBlockReplaced to get the replaced blocks.
    // Any blocks spawned during decoration will have their materials parsed before spawning
    // them via world.setBlock(), so they use the default biomeconfig materials.

    // Note: getSurfaceBlockReplaced / getGroundBlockReplaced don't take into
    // account SAGC, so they should only be used by surfacegenerators.

    public LocalMaterialData getSurfaceBlockAtHeight(ISurfaceGeneratorNoiseProvider noiseProvider, int x, int y, int z) {
        return surfaceGenerator.getSurfaceBlockAtHeight(noiseProvider,this,  x, y, z);
    }
    public LocalMaterialData getGroundBlockAtHeight(ISurfaceGeneratorNoiseProvider noiseProvider, int x, int y, int z) {
        return surfaceGenerator.getGroundBlockAtHeight(noiseProvider,this,  x, y, z);
    }

    public LocalMaterialData getSurfaceBlockReplaced(int y) {
        if (replacedBlocks.replacesSurface) {
            return replacedBlocks.replaceBlock(y, surfaceBlock);
        }
        return surfaceBlock;
    }
    public LocalMaterialData getUnderWaterSurfaceBlockReplaced(int y) {
        if (replacedBlocks.replacesUnderWaterSurface) {
            return replacedBlocks.replaceBlock(y, underWaterSurfaceBlock);
        }
        return underWaterSurfaceBlock;
    }
    public LocalMaterialData getGroundBlockReplaced(int y) {
        if (replacedBlocks.replacesGround) {
            return replacedBlocks.replaceBlock(y, groundBlock);
        }
        return groundBlock;
    }
    public LocalMaterialData getSandStoneBlockReplaced(int y) {
        if (replacedBlocks.replacesSandStone) {
            return replacedBlocks.replaceBlock(y, sandStoneBlock);
        }
        return sandStoneBlock;
    }
    public LocalMaterialData getRedSandStoneBlockReplaced(int y) {
        if (replacedBlocks.replacesRedSandStone) {
            return replacedBlocks.replaceBlock(y, redSandStoneBlock);
        }
        return redSandStoneBlock;
    }
    public LocalMaterialData getWaterBlockReplaced(int y) {
        if (replacedBlocks.replacesWater) {
            return replacedBlocks.replaceBlock(y, waterBlock);
        }
        return waterBlock;
    }
    public LocalMaterialData getIceBlockReplaced(int y) {
        if (replacedBlocks.replacesIce) {
            return replacedBlocks.replaceBlock(y, iceBlock);
        }
        return iceBlock;
    }
    public LocalMaterialData getPackedIceBlockReplaced(int y) {
        if (replacedBlocks.replacesPackedIce) {
            return replacedBlocks.replaceBlock(y, packedIceBlock);
        }
        return packedIceBlock;
    }
    public LocalMaterialData getSnowBlockReplaced(int y) {
        if (replacedBlocks.replacesSnow) {
            return replacedBlocks.replaceBlock(y, snowBlock);
        }
        return snowBlock;
    }
    public LocalMaterialData getCooledLavaBlockReplaced(int y) {
        if (replacedBlocks.replacesCooledLava) {
            return replacedBlocks.replaceBlock(y, cooledLavaBlock);
        }
        return cooledLavaBlock;
    }
    public LocalMaterialData getStoneBlockReplaced(int y) {
        if (replacedBlocks.replacesStone) {
            return replacedBlocks.replaceBlock(y, stoneBlock);
        }
        return stoneBlock;
    }

    public LocalMaterialData getBedrockBlockReplaced(int y) {
        if (replacedBlocks.replacesBedrock) {
            return replacedBlocks.replaceBlock(y, blockSettings.getBedrockBlock());
        }
        return blockSettings.getBedrockBlock();
    }

    public LocalMaterialData getDefaultGroundBlock() {
        return groundBlock;
    }
    public void doSurfaceAndGroundControl(long worldSeed, GeneratingChunk generatingChunk, ChunkBuffer chunkBuffer, int x, int z, IBiome biome) {
        this.surfaceGenerator.spawn(worldSeed, generatingChunk, chunkBuffer, biome, x, z);
    }


    public static int getSnowHeight(float temp) {
        // OTG biome temperature is between 0.0 and 2.0.
        // Judging by WorldStandardValues.SNOW_AND_ICE_MAX_TEMP, snow should appear below 0.15.
        // According to the configs, snow and ice should appear between 0.2 (at y > 90) and 0.1 (entire biome covered in ice).
        // Let's make sure that at 0.2, snow layers start with thickness 0 at y 90 and thickness 7 around y 255.
        // In a 0.2 temp biome, y90 temp is 0.156, y255 temp is -0.12

        float snowTemp = Constants.SNOW_AND_ICE_TEMP;
        if (temp <= snowTemp) {
            float maxColdTemp = Constants.SNOW_AND_ICE_MAX_TEMP;
            float maxThickness = 7.0f;
            if (temp < maxColdTemp) {
                return (int) maxThickness;
            }
            float range = Math.abs(maxColdTemp - snowTemp);
            float fraction = Math.abs(maxColdTemp - temp);
            return (int) Math.floor((1.0f - (fraction / range)) * maxThickness);
        }

        return 0;
    }

    public ReplaceBlockMatrix getReplacedBlocks() {
        initReplaceBlocks();
        return this.replacedBlocks;
    }

    private void initReplaceBlocks() {
        if (!this.replacedBlocksInitialised) {
            // Multiple threads may be working with
            // the same biome configs async, lock.
            synchronized (this) {
                if (!this.replacedBlocksInitialised) {
                    this.replacedBlocks.init(
                            this.useWorldWaterLevel ? this.getBlockSettings().getCooledLavaBlock() : this.cooledLavaBlock,
                            this.useWorldWaterLevel ? this.getBlockSettings().getIceBlock() : this.iceBlock,
                            this.packedIceBlock,
                            this.snowBlock,
                            this.useWorldWaterLevel ? this.getBlockSettings().getWaterBlock() : this.waterBlock,
                            this.stoneBlock,
                            this.groundBlock,
                            this.surfaceBlock,
                            this.underWaterSurfaceBlock,
                            this.getBlockSettings().getDefaultBedrockBlock(),
                            this.sandStoneBlock,
                            this.redSandStoneBlock
                    );
                }
                this.replacedBlocksInitialised = true;
            }
        }
    }
}
