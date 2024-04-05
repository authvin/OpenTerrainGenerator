package com.pg85.otg.config.settings.preset;

import com.pg85.otg.config.io.SettingsMap;
import com.pg85.otg.config.standard.PresetStandardValues;
import com.pg85.otg.constants.settings.ImageMode;
import com.pg85.otg.constants.settings.ImageOrientation;
import com.pg85.otg.util.minecraft.BiomeRegistryNames;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class ImageSettings {
    private final ImageOrientation imageOrientation;
    private final String imageFile;
    private final String imageFillBiome;
    private final ImageMode imageMode;
    private final int imageXOffset;
    private final int imageZOffset;

    public static ImageSettings getImageSettings(SettingsMap reader, List<String> biomes) {
        var imageSettingsBuilder = builder();
        String imageFillBiome = reader.getSetting(PresetStandardValues.IMAGE_FILL_BIOME);

        imageSettingsBuilder.imageOrientation(reader.getSetting(PresetStandardValues.IMAGE_ORIENTATION));
        imageSettingsBuilder.imageFile(reader.getSetting(PresetStandardValues.IMAGE_FILE));
        imageSettingsBuilder.imageFillBiome((BiomeRegistryNames.Contain(imageFillBiome) || biomes.contains(imageFillBiome)) ? imageFillBiome : PresetStandardValues.IMAGE_FILL_BIOME.getDefaultValue(null));
        imageSettingsBuilder.imageMode(reader.getSetting(PresetStandardValues.IMAGE_MODE));
        imageSettingsBuilder.imageXOffset(reader.getSetting(PresetStandardValues.IMAGE_X_OFFSET));
        imageSettingsBuilder.imageZOffset(reader.getSetting(PresetStandardValues.IMAGE_Z_OFFSET));
        return imageSettingsBuilder.build();
    }
}