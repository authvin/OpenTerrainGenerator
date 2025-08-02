package com.pg85.otg.config.settings;

import com.pg85.otg.config.settings.biome.*;
import lombok.Getter;

import java.util.List;

@Getter
public enum TemplateTypes {
    FULL(
            MobSettings.class,
            BiomePlacementConfig.class,
            BiomeStructureSettings.class,
            BiomeTerrainSettings.class,
            BiomeVisualSettings.class,
            SurfaceSettings.class,
            BiomeResourceSettings.class,
            BiomeTagConfig.class,
            BiomeStructureTagConfig.class
    ),
    MOBS(MobSettings.class),
    STRUCTURES(
            BiomeStructureSettings.class,
            BiomeStructureTagConfig.class
    ),
    TAGS(BiomeTagConfig.class),
    RESOURCES(BiomeResourceSettings.class),
    SURFACE(SurfaceSettings.class),
    VISUAL(BiomeVisualSettings.class),
    TERRAIN(BiomeTerrainSettings.class),
    GENERATION(BiomePlacementConfig.class);

    private final List<Class<? extends ConfigSection>> configsPresent;

    @SafeVarargs
    TemplateTypes(Class<? extends ConfigSection>... configsPresent) {
        this.configsPresent = List.of(configsPresent);
    }
}
