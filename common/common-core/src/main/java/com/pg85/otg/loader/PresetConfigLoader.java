package com.pg85.otg.loader;

import com.pg85.otg.config.io.FileSettingsReader;
import com.pg85.otg.config.io.FileSettingsWriter;
import com.pg85.otg.config.io.SettingsMap;
import com.pg85.otg.config.preset.PresetConfig;
import com.pg85.otg.constants.Constants;
import com.pg85.otg.util.OTGMaterialReader;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PresetConfigLoader {

    public static ArrayList<String> getAllBiomeNamesInFolderRecursive(File biomesDirectory) {
        ArrayList<String> biomes = new ArrayList<>();
        if(biomesDirectory.exists() && biomesDirectory.isDirectory())
        {
            for(File biomeConfig : biomesDirectory.listFiles())
            {
                if(biomeConfig.isFile() && biomeConfig.getName().endsWith(Constants.BiomeConfigFileExtension))
                {
                    biomes.add(biomeConfig.getName().replace(Constants.BiomeConfigFileExtension, ""));
                }
                else if(biomeConfig.isDirectory())
                {
                    biomes.addAll(getAllBiomeNamesInFolderRecursive(biomeConfig));
                }
            }
        }
        return biomes;
    }

    public static PresetConfig loadPresetConfig(Path presetDir) {

        SettingsMap presetConfigSettings = readPresetConfig(presetDir);

        PresetConfig presetConfig = createPresetConfig(presetDir, presetConfigSettings);

        writePresetConfig(presetConfig, presetDir);
        return presetConfig;
    }

    public static void writePresetConfig(PresetConfig presetConfig, Path presetDir) {
        File presetConfigFile = getPresetConfigFile(presetDir);
        FileSettingsWriter.writeToFile(presetConfig.getSettingsAsMap(), presetConfigFile, presetConfig.getPresetInfo().getSettingsMode());
    }

    public static PresetConfig createPresetConfig(Path presetDir, SettingsMap presetConfigSettings) {
        File biomesDirectory = BiomeConfigLoader.getBiomeDirectory(presetDir);

        return new PresetConfig(
                presetDir,
                presetConfigSettings,
                getAllBiomeNamesInFolderRecursive(biomesDirectory),
                OTGMaterialReader.get()
        );
    }

    public static SettingsMap readPresetConfig(Path presetDir) {
        File presetConfigFile = getPresetConfigFile(presetDir);
        String presetFolderName = presetDir.toFile().getName();

        if (presetConfigFile != null) {
            return FileSettingsReader.read(presetFolderName, presetConfigFile);
        }
        throw new IllegalArgumentException("Preset config file not found in directory: " + presetDir);
    }

    public static File getPresetConfigFile(Path presetDir) {
        File presetConfigFile = new File(presetDir.toString(), Constants.PRESET_CONFIG_FILE);
        if (presetConfigFile.exists()) {
            return presetConfigFile;
        }
        File worldConfigFile = new File(presetDir.toString(), Constants.LEGACY_WORLD_CONFIG_FILE);
        if (worldConfigFile.exists()) {
            return worldConfigFile;
        }
        return null;
    }

    public static List<Path> findPresetDirectories(Path presetsDir) {
        List<Path> presetDirectories = new ArrayList<>();
        if (presetsDir.toFile().exists() && presetsDir.toFile().isDirectory()) {
            var files = presetsDir.toFile().listFiles();
            if (files == null) {
                return presetDirectories; // Return empty list if no files found
            }
            for (File file : files) {
                if (file.isDirectory() && getPresetConfigFile(file.toPath()) != null) {
                    presetDirectories.add(file.toPath());
                }
            }
        }
        return presetDirectories;
    }
}
