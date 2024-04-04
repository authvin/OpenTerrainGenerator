package com.pg85.otg.presets;

import com.pg85.otg.config.biome.BiomeTemplate;

public abstract class TemplateLoader {
    // load templates for a preset
    public abstract void loadTemplates(Preset preset);
    // get a template by name
    public abstract BiomeTemplate getTemplate(String name);
}
