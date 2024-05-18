package com.pg85.otg.config.settings.biome;

import com.pg85.otg.config.ConfigFile;
import com.pg85.otg.config.io.SettingsMap;
import com.pg85.otg.config.settings.preset.GenerationSettings;
import com.pg85.otg.config.standard.BiomeStandardValues;
import com.pg85.otg.interfaces.ILogger;
import com.pg85.otg.interfaces.IMaterialReader;
import com.pg85.otg.util.OTGLog;
import com.pg85.otg.util.logging.LogCategory;
import com.pg85.otg.util.logging.LogLevel;
import lombok.Builder;
import lombok.Getter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Builder
@Getter
public class BiomeGenerationSettings {
    private final GenerationSettings parent;
    private final int biomeSize;
    private final int biomeRarity;
    private final int biomeColor;
    private final boolean isleBiome;
    private final List<String> isleInBiomes;
    private final int biomeSizeWhenIsle;
    private final int biomeRarityWhenIsle;
    private final boolean borderBiome;
    private final List<String> borderInBiomes;
    private final List<String> onlyBorderNearBiomes;
    private final List<String> notBorderNearBiomes;
    private final int biomeSizeWhenBorder;

    public static BiomeGenerationSettings getPlacementSettings(SettingsMap reader, GenerationSettings parent) {
        BiomeGenerationSettingsBuilder builder = BiomeGenerationSettings.builder();

        builder.parent(parent);
        builder.biomeSize(reader.getSetting(BiomeStandardValues.BIOME_SIZE));
        builder.biomeRarity(reader.getSetting(BiomeStandardValues.BIOME_RARITY));
        builder.biomeColor(reader.getSetting(BiomeStandardValues.BIOME_COLOR));
        builder.isleInBiomes(reader.getSetting(BiomeStandardValues.ISLE_IN_BIOMES));
        builder.isleBiome(!builder.isleInBiomes.isEmpty());
        builder.biomeSizeWhenIsle(reader.getSetting(BiomeStandardValues.BIOME_SIZE_WHEN_ISLE));
        builder.biomeRarityWhenIsle(reader.getSetting(BiomeStandardValues.BIOME_RARITY_WHEN_ISLE));
        builder.borderInBiomes(reader.getSetting(BiomeStandardValues.BORDER_IN_BIOMES));
        builder.borderBiome(!builder.borderInBiomes.isEmpty());
        builder.onlyBorderNearBiomes(reader.getSetting(BiomeStandardValues.ONLY_BORDER_NEAR));
        builder.notBorderNearBiomes(reader.getSetting(BiomeStandardValues.NOT_BORDER_NEAR));
        builder.biomeSizeWhenBorder(reader.getSetting(BiomeStandardValues.BIOME_SIZE_WHEN_BORDER));

        return builder.fixSettings().build();
    }

    public static class BiomeGenerationSettingsBuilder {
        public BiomeGenerationSettingsBuilder fixSettings() {
            checkBiomeSize();
            checkBiomeSizeWhenIsle();
            checkBiomeSizeWhenBorder();
            checkBiomeRarity();
            checkBiomeRarityWhenIsle();
            checkIsleInBiomes();
            checkBorderInBiomes();
            checkOnlyBorderNear();
            checkNotBorderNear();
            return this;
        }
        private void checkBiomeSize() {
            biomeSize = Math.max(biomeSize, this.parent.getGenerationDepth());
        }
        private void checkBiomeSizeWhenIsle() {
            biomeSizeWhenIsle = Math.max(biomeSizeWhenIsle, this.parent.getGenerationDepth());
        }
        private void checkBiomeSizeWhenBorder() {
            biomeSizeWhenBorder = Math.max(biomeSizeWhenBorder, this.parent.getGenerationDepth());
        }
        private void checkBiomeRarity() {
            biomeRarity = Math.max(biomeRarity, this.parent.getBiomeRarityScale());
        }
        private void checkBiomeRarityWhenIsle() {
            biomeRarityWhenIsle = Math.max(biomeRarityWhenIsle, this.parent.getBiomeRarityScale());
        }
        private void checkIsleInBiomes() {
            var output = ConfigFile.filterBiomes(isleInBiomes, this.parent.getWorldBiomes());
            if (output.size() != isleInBiomes.size()) {
                // TODO: Add context to log messages like this, perhaps by making the file extend ConfigFile and adding context there
                var invalid = new HashSet<>(isleInBiomes);
                output.forEach(invalid::remove);
                OTGLog.getLogger().log(LogLevel.WARN, LogCategory.CONFIGS, "invalid value(s) in isleInBiomes: "+invalid);
            }
            isleInBiomes = output;
        }
        private void checkBorderInBiomes() {
            var output = ConfigFile.filterBiomes(borderInBiomes, this.parent.getWorldBiomes());
            if (output.size() != borderInBiomes.size()) {
                var invalid = new HashSet<>(borderInBiomes);
                output.forEach(invalid::remove);
                OTGLog.getLogger().log(LogLevel.WARN, LogCategory.CONFIGS, "invalid value(s) in borderInBiomes: "+invalid);
            }
            borderInBiomes = output;
        }
        private void checkOnlyBorderNear() {
            var output = ConfigFile.filterBiomes(onlyBorderNearBiomes, this.parent.getWorldBiomes());
            if (output.size() != onlyBorderNearBiomes.size()) {
                var invalid = new HashSet<>(onlyBorderNearBiomes);
                output.forEach(invalid::remove);
                OTGLog.getLogger().log(LogLevel.WARN, LogCategory.CONFIGS, "invalid value(s) in onlyBorderNear: "+invalid);
            }
            onlyBorderNearBiomes = output;
        }
        private void checkNotBorderNear() {
            var output = ConfigFile.filterBiomes(notBorderNearBiomes, this.parent.getWorldBiomes());
            if (output.size() != notBorderNearBiomes.size()) {
                var invalid = new HashSet<>(notBorderNearBiomes);
                output.forEach(invalid::remove);
                OTGLog.getLogger().log(LogLevel.WARN, LogCategory.CONFIGS, "invalid value(s) in notBorderNear: "+invalid);
            }
            notBorderNearBiomes = output;
        }
    }
}
