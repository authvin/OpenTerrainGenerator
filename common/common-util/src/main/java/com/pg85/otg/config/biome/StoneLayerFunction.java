package com.pg85.otg.config.biome;

import com.pg85.otg.config.ConfigFile;
import com.pg85.otg.config.ConfigFunction;
import com.pg85.otg.config.settings.biome.BiomeSettings;
import com.pg85.otg.config.settings.preset.PresetSettings;
import com.pg85.otg.exceptions.InvalidConfigException;
import com.pg85.otg.util.Pair;
import com.pg85.otg.util.biome.StoneLayer;
import lombok.Getter;

import java.util.List;

/**
 * StoneLayer(BlockName, MinY, MaxY[, Transition])
 *
 * Defines one vertical stratum of base stone. Declared in PresetConfig.ini as
 * the world default stack; a biome config may declare its own StoneLayer
 * lines, which replace the preset stack wholesale for that biome.
 */
public final class StoneLayerFunction extends ConfigFunction<ConfigFile>
{
    private static final int MAX_TRANSITION = 256;

    @Getter
    private final StoneLayer layer;

    public StoneLayerFunction(PresetSettings config, List<String> args) throws InvalidConfigException
    {
        this.layer = parse(args);
    }

    public StoneLayerFunction(BiomeSettings config, List<String> args) throws InvalidConfigException
    {
        this.layer = parse(args);
    }

    private StoneLayer parse(List<String> args) throws InvalidConfigException
    {
        assureSize(3, args);
        var block = readMaterial(args.get(0));
        Pair<Integer, Integer> elevations = readElevations(args.get(1), args.get(2));
        int transition = args.size() > 3 ? readInt(args.get(3), 0, MAX_TRANSITION) : 0;
        return new StoneLayer(block, elevations.getFirst(), elevations.getSecond(), transition);
    }

    @Override
    public String toString()
    {
        return "StoneLayer(" + this.layer.block() + "," + this.layer.minY() + "," + this.layer.maxY()
            + (this.layer.transition() > 0 ? "," + this.layer.transition() : "") + ")";
    }
}
