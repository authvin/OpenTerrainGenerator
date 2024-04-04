package com.pg85.otg.settings.preset;

import com.pg85.otg.constants.settings.ImageMode;
import com.pg85.otg.constants.settings.ImageOrientation;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ImageSettings {
    private final ImageOrientation imageOrientation;
    private final String imageFile;
    private final String imageFillBiome;
    private final ImageMode imageMode;
    private final int imageXOffset;
    private final int imageZOffset;
}