package com.pg85.otg.config.settings.biome;

import com.pg85.otg.config.ConfigFunction;
import com.pg85.otg.config.io.IConfigFunctionProvider;
import com.pg85.otg.config.io.SettingsMap;
import com.pg85.otg.config.settings.preset.ResourceSettings;
import com.pg85.otg.config.standard.BiomeStandardValues;
import com.pg85.otg.interfaces.ICustomStructureGen;
import com.pg85.otg.interfaces.ISaplingSpawner;
import com.pg85.otg.util.materials.LocalMaterialData;
import com.pg85.otg.util.minecraft.SaplingType;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Builder
@Getter
public class BiomeResourceSettings {
    private final ResourceSettings parent;
    private final boolean strongholdsEnabled;
    private final List<ICustomStructureGen> customStructures; // Used as a cache for fast querying, not saved
    private final Map<SaplingType, ISaplingSpawner> saplingGrowers;
    private final Map<LocalMaterialData, ISaplingSpawner> customSaplingGrowers;
    private final Map<LocalMaterialData, ISaplingSpawner> customBigSaplingGrowers;
    private final List<ConfigFunction<BiomeSettings>> resourceQueue;

    public static BiomeResourceSettings getResourceSettings(SettingsMap reader, ResourceSettings parent, List<ConfigFunction<BiomeSettings>> resources) {
        BiomeResourceSettingsBuilder builder = BiomeResourceSettings.builder();

        builder.parent(parent);
        builder.strongholdsEnabled(reader.getSetting(BiomeStandardValues.STRONGHOLDS_ENABLED));
        builder.saplingGrowers(new HashMap<>());
        builder.customSaplingGrowers(new HashMap<>());
        builder.customBigSaplingGrowers(new HashMap<>());
        builder.customStructures(new ArrayList<>());
        builder.resourceQueue(new ArrayList<>());


        BiomeResourceSettings settings = builder.build();
        settings.readResourceSettings(resources);
        return settings;
    }

    protected void readResourceSettings(List<ConfigFunction<BiomeSettings>> resources)
    {
        for (ConfigFunction<BiomeSettings> res : resources)
        {
            if (res != null)
            {
                if (res instanceof ISaplingSpawner sapling)
                {
                    if (sapling.getSaplingType() == SaplingType.Custom)
                    {
                        // Puts big custom saplings in the big list and small in the small list
                        if (sapling.hasWideTrunk())
                        {
                            this.customBigSaplingGrowers.put(sapling.getSaplingMaterial(), sapling);
                        } else {
                            this.customSaplingGrowers.put(sapling.getSaplingMaterial(), sapling);
                        }
                    } else {
                        this.saplingGrowers.put(sapling.getSaplingType(), sapling);
                    }
                } else {
                    this.resourceQueue.add(res);
                    if (res instanceof ICustomStructureGen)
                    {
                        this.customStructures.add((ICustomStructureGen) res);
                    }
                }
            }
        }
    }
}
