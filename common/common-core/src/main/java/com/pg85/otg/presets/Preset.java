package com.pg85.otg.presets;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

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
    private String presetRegistryName;

    // Note: Since we're not using Supplier<>, we need to be careful about any classes fetching
    // and caching our worldconfig/biomeconfigs etc, or they won't update when reloaded from disk.
    // BiomeGen and ChunkGen cache some settings during a session, so they'll only update on world exit/rejoin.
    @Getter
    private PresetSettings presetConfig;

    private final List<BiomeConfig> biomeConfigList;

    private HashMap<OTGBiomeID, BiomeConfig> biomeConfigs = new HashMap<>();

    private final HashSet<OTGBiomeID> biomeIDS = new HashSet<>();
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

    public Preset(Path presetFolder, String presetRegistryName, PresetConfig presetConfig, ArrayList<BiomeConfig> biomeConfigs) {
        this.presetFolder = presetFolder;
        this.folderName = presetFolder.toFile().getName();
        this.presetRegistryName = presetRegistryName;
        this.presetConfig = presetConfig;
        this.author = presetConfig.getPresetInfo().getAuthor();
        this.description = presetConfig.getPresetInfo().getDescription();
        this.majorVersion = presetConfig.getPresetInfo().getMajorVersion();

        biomeConfigList = biomeConfigs;
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
            if (biomeID.registryName().getPresetFolderName().equals(registryName)) {
                return biomeID;
            }
        }
        return null;
    }

    public ArrayList<BiomeConfig> getBiomeConfigList() {
        return new ArrayList<>(this.biomeConfigList);
    }

    public ArrayList<String> getAllBiomeNames() {
        return new ArrayList<>(this.biomeConfigs.keySet().stream().map(OTGBiomeID::biomeName).toList());
    }

    @Override
    public String toString() {
        return this.folderName;
    }
}
