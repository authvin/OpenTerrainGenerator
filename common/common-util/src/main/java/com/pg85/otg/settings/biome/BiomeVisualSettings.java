package com.pg85.otg.settings.biome;

import com.pg85.otg.settings.preset.VisualSettings;
import com.pg85.otg.util.biome.ColorSet;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BiomeVisualSettings {
    private final VisualSettings parent;
    private final int skyColor;
    private final int waterColor;
    private final ColorSet waterColorControl;
    private final int grassColor;
    private final ColorSet grassColorControl;
    private final int grassColorModifier;
    private final int foliageColor;
    private final ColorSet foliageColorControl;
    private final int fogColor;
    private final float fogDensity;
    private final int waterFogColor;
    private final String particleType;
    private final String music;
    private final int musicMinDelay;
    private final int musicMaxDelay;
    private final boolean replaceCurrentMusic;
    private final String ambientSound;
    private final String moodSound;
    private final int moodSoundDelay;
    private final int moodSearchRange;
    private final int moodOffset;
    private final String additionsSound;
    private final int additionsTickChance;
    private final int particleProbability;
    private final float biomeTemperature;
    private final float biomeWetness;
}