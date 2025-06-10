package com.pg85.otg.loader;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.pg85.otg.config.dimensions.DimensionConfig;
import com.pg85.otg.constants.Constants;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DimensionConfigLoader {
    public static DimensionConfig fromDisk(String fileName, Path otgRootFolder)
    {
        File dimensionConfig = new File(otgRootFolder.toFile(), Constants.DIMENSION_CONFIGS_FOLDER + File.separator + fileName + ".yaml");
        if(dimensionConfig.exists())
        {
            DimensionConfig dimConfig = new DimensionConfig();
            String content = "";
            try
            {
                content = new String(Files.readAllBytes(dimensionConfig.toPath()));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            DimensionConfig loadedConfig = fromYamlString(content);
            if(loadedConfig != null)
            {
                dimConfig.isModpackConfig = true;
                dimConfig.Version = loadedConfig.Version;
                dimConfig.ModpackName = loadedConfig.ModpackName;
                dimConfig.Overworld = loadedConfig.Overworld;
                dimConfig.Nether = loadedConfig.Nether;
                dimConfig.End = loadedConfig.End;
                dimConfig.Dimensions = loadedConfig.Dimensions;
                dimConfig.GameRules = loadedConfig.GameRules;
                dimConfig.Settings = loadedConfig.Settings;
                return dimConfig;
            }
        }
        return null;
    }

    public static DimensionConfig fromYamlString(String input)
    {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        DimensionConfig dimConfig = null;

        try {
            dimConfig = mapper.readValue(input, DimensionConfig.class);
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return dimConfig;
    }
}
