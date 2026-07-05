package com.pg85.otg.gen.surface;

import com.pg85.otg.exceptions.InvalidConfigException;
import com.pg85.otg.interfaces.IMaterialReader;
import com.pg85.otg.util.helpers.StringHelper;
import com.pg85.otg.util.materials.LocalMaterialData;

/**
 * Per-biome surface rules for cave interiors, applied by the surface pass to cave
 * floor/ceiling transitions found below the surface band (see
 * {@link SimpleSurfaceGenerator#onCaveFloor}). The rules come from the cave biome at the
 * position (CaveBiomes in the preset config), not the surface biome above it.
 *
 * Syntax: FloorBlockName,FloorDepth[,CeilingBlockName,CeilingDepth]
 */
public record CaveSurfaceRules(
        LocalMaterialData floorBlock,
        int floorDepth,
        LocalMaterialData ceilingBlock,
        int ceilingDepth
) {
    /** No cave surface rules; floors and ceilings keep their stone. */
    public static final CaveSurfaceRules NONE = new CaveSurfaceRules(null, 0, null, 0);

    private static final int MAX_DEPTH = 16;

    public static CaveSurfaceRules fromString(String string, IMaterialReader materialReader) throws InvalidConfigException {
        if (string == null || string.trim().isEmpty()) {
            return NONE;
        }
        String[] parts = StringHelper.readCommaSeperatedString(string);
        if (parts.length != 2 && parts.length != 4) {
            throw new InvalidConfigException(
                    "Expected FloorBlockName,FloorDepth[,CeilingBlockName,CeilingDepth], got: " + string);
        }
        LocalMaterialData floorBlock = materialReader.readMaterial(parts[0]);
        int floorDepth = StringHelper.readInt(parts[1], 0, MAX_DEPTH);
        LocalMaterialData ceilingBlock = null;
        int ceilingDepth = 0;
        if (parts.length == 4) {
            ceilingBlock = materialReader.readMaterial(parts[2]);
            ceilingDepth = StringHelper.readInt(parts[3], 0, MAX_DEPTH);
        }
        return new CaveSurfaceRules(floorBlock, floorDepth, ceilingBlock, ceilingDepth);
    }

    public boolean hasFloor() {
        return this.floorBlock != null && this.floorDepth > 0;
    }

    public boolean hasCeiling() {
        return this.ceilingBlock != null && this.ceilingDepth > 0;
    }

    @Override
    public String toString() {
        // Empty string means "no rules" in the config, like SimpleSurfaceGenerator.
        if (!hasFloor() && !hasCeiling()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        builder.append(this.floorBlock).append(',').append(this.floorDepth);
        if (hasCeiling()) {
            builder.append(',').append(this.ceilingBlock).append(',').append(this.ceilingDepth);
        }
        return builder.toString();
    }
}
