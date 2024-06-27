package com.pg85.otg.presets;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.pg85.otg.config.biome.BiomeConfig;
import com.pg85.otg.config.preset.PresetConfig;
import com.pg85.otg.config.settings.biome.BiomeSettings;
import com.pg85.otg.interfaces.IMaterialReader;
import com.pg85.otg.config.settings.preset.PresetSettings;
import com.pg85.otg.util.biome.OTGBiomeID;
import lombok.Getter;

/**
 * Represents an OTG preset, with all its world and biome configs, stored in /config/OpenTerrainGenerator/Presets/\<PresetName\>/.
 */
public class Preset {
    @Getter
    private final Path presetFolder;
    @Getter
    private String folderName;
    @Getter
    private String shortPresetName;

    // Note: Since we're not using Supplier<>, we need to be careful about any classes fetching
    // and caching our worldconfig/biomeconfigs etc, or they won't update when reloaded from disk.
    // BiomeGen and ChunkGen cache some settings during a session, so they'll only update on world exit/rejoin.
    @Getter
    private PresetSettings presetConfig;
    private HashMap<String, BiomeSettings> biomeConfigs = new HashMap<>();

    private HashSet<OTGBiomeID> biomeIDS = new HashSet<>();
    @Getter
    private int majorVersion;
    @Getter
    private String author;
    @Getter
    private String description;
    @Getter
    private IMaterialReader materialReader;
    @Getter
    private HashMap<OTGBiomeID, Integer> biomeColorMap = new HashMap<>();

    public Preset(Path presetFolder, String shortPresetName, PresetConfig presetConfig, ArrayList<BiomeConfig> biomeConfigs) {
        this.presetFolder = presetFolder;
        this.folderName = presetFolder.toFile().getName();
        this.shortPresetName = shortPresetName;
        this.presetConfig = presetConfig;
        this.author = presetConfig.getPresetInfo().getAuthor();
        this.description = presetConfig.getPresetInfo().getDescription();
        this.majorVersion = presetConfig.getPresetInfo().getMajorVersion();

        for (BiomeConfig biomeConfig : biomeConfigs) {
            this.biomeConfigs.put(biomeId, biomeConfig);
        }
    }

    public void update(Preset preset) {
        this.presetConfig = preset.presetConfig;
        this.biomeConfigs = preset.biomeConfigs;
        this.author = preset.author;
        this.description = preset.description;
        this.majorVersion = preset.majorVersion;
    }

    public BiomeSettings getBiomeConfig(String biomeName) {
        OTGBiomeID biomeID = getBiomeID(biomeName);
        return this.biomeConfigs.get(biomeID);
    }

    public OTGBiomeID getBiomeID(String biomeName) {
        for (OTGBiomeID biomeID : this.biomeIDS) {
            if (biomeID.biomeName().equals(biomeName)) {
                return biomeID;
            }
        }
        return null;
    }

    public OTGBiomeID getBiomeID(int biomeId) {
        for (OTGBiomeID biomeID : this.biomeIDS) {
            if (biomeID.id() == biomeId) {
                return biomeID;
            }
        }
        return null;
    }

    public OTGBiomeID getBiomeIDByRegistryName(String registryName) {
        for (OTGBiomeID biomeID : this.biomeIDS) {
            if (biomeID.registryName().equals(registryName)) {
                return biomeID;
            }
        }
        return null;
    }

    public ArrayList<BiomeSettings> getBiomeConfigList() {
        return new ArrayList<>(this.biomeConfigs.values());
    }

    public ArrayList<String> getAllBiomeNames() {
        return new ArrayList<>(this.biomeConfigs.keySet());
    }
}
