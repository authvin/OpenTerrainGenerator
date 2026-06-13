package com.pg85.otg.fabric.commands;

import com.pg85.otg.OTG;
import com.pg85.otg.constants.Constants;
import com.pg85.otg.customobject.CustomObject;
import com.pg85.otg.customobject.structures.StructuredCustomObject;
import com.pg85.otg.customobject.creator.ObjectType;
import com.pg85.otg.customobject.util.BoundingBox;
import com.pg85.otg.customobject.util.Corner;
import com.pg85.otg.fabric.gen.FabricWorldGenRegion;
import com.pg85.otg.fabric.gen.OTGFabricChunkGenerator;
import com.pg85.otg.presets.Preset;
import com.pg85.otg.util.bo3.Rotation;
import com.pg85.otg.util.gen.LocalWorldGenRegion;
import com.pg85.otg.util.gen.OTGWorldInfo;
import com.pg85.otg.util.materials.LocalMaterials;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.nio.file.Path;

/** Shared helpers for the export/edit/region command family on Fabric. */
final class ObjectUtils {

    /**
     * Path to the object folder for a preset — its {@code Objects} folder, or the global objects
     * folder when {@code presetFolder} is null. Falls back to the legacy {@code WorldObjects} folder
     * if it exists and {@code Objects} does not.
     */
    static Path getObjectFolderPath(Path presetFolder) {
        Path objectPath;
        if (presetFolder == null) {
            objectPath = OTG.getEngine().getGlobalObjectsFolder();
        } else {
            objectPath = presetFolder.resolve(Constants.OBJECTS_FOLDER);
        }

        if (!objectPath.toFile().exists()
            && objectPath.resolve("..").resolve(Constants.LEGACY_WORLD_OBJECTS_FOLDER).toFile().exists()) {
            objectPath = objectPath.resolve("..").resolve(Constants.LEGACY_WORLD_OBJECTS_FOLDER);
        }
        return objectPath;
    }

    /** The named preset, or the default preset when {@code presetName} is null. */
    static Preset getPresetOrDefault(String presetName) {
        if (presetName == null) {
            return OTG.getEngine().getPresetLoader().getPresetByShortNameOrFolderName(
                OTG.getEngine().getPresetLoader().getDefaultPresetFolderName());
        }
        return OTG.getEngine().getPresetLoader().getPresetByShortNameOrFolderName(presetName);
    }

    /** The sub-path from the base object folder down to the folder containing {@code object}. */
    static String getFoldersFromObject(StructuredCustomObject object) {
        Path parent = object.getConfig().getFile().toPath().getParent();
        StringBuilder sb = new StringBuilder();
        while (!parent.getFileName().toString().equalsIgnoreCase(Constants.LEGACY_WORLD_OBJECTS_FOLDER)
            && !parent.getFileName().toString().equalsIgnoreCase(Constants.OBJECTS_FOLDER)
            && !parent.getFileName().toString().equalsIgnoreCase(Constants.GLOBAL_OBJECTS_FOLDER)) {
            sb.insert(0, "/");
            sb.insert(0, parent.getFileName());
            parent = parent.getParent();
        }
        return sb.toString();
    }

    /**
     * Clears the working area for an edit session. When {@code preparing}, fills with structure void
     * (so the object spawns into empty space); otherwise fills with air to clean up afterwards.
     */
    static void cleanArea(LocalWorldGenRegion region, Corner min, Corner max, boolean preparing) {
        for (int x = min.x() - 1; x <= max.x() + 1; x++) {
            for (int z = min.z() - 1; z <= max.z() + 1; z++) {
                for (int y = max.y() + 1; y >= min.y() - 1; y--) {
                    region.setBlock(x, y, z, preparing ? LocalMaterials.STRUCTURE_VOID : LocalMaterials.AIR);
                }
            }
        }
    }

    /** Whether the selection is larger than the maximum footprint of the object type (32x32 BO3, 16x16 BO4). */
    static boolean isOutsideBounds(RegionCommand.Region region, ObjectType type) {
        Corner min = region.getMin();
        Corner max = region.getMax();
        int xlen = Math.abs(max.x() - min.x());
        int zlen = Math.abs(max.z() - min.z());
        return switch (type) {
            case BO3 -> xlen > 31 || zlen > 31;
            case BO4 -> xlen > 15 || zlen > 15;
            default -> false;
        };
    }

    /**
     * Builds a fresh selection large enough to hold {@code object}, placed a few blocks away from the
     * player and clamped into the world's height bounds.
     */
    static RegionCommand.Region getRegionFromObject(BlockPos playerPos, StructuredCustomObject object,
                                                    int worldMinY, int worldMaxY) {
        RegionCommand.Region region = new RegionCommand.Region();
        BoundingBox box = object.getBoundingBox(Rotation.NORTH);

        // Keep the object from spawning on top of the player.
        BlockPos pos = playerPos.offset(3, 0, 3);

        int lowestElevation = pos.getY() + box.getMinY();
        int highestElevation = pos.getY() + box.getMinY() + box.getHeight();

        int yShift = 0;
        if (lowestElevation <= worldMinY + 1) {
            yShift = (worldMinY + 2) - lowestElevation;
        } else if (highestElevation >= worldMaxY) {
            yShift = highestElevation - worldMaxY;
        }

        Corner center = new Corner(
            pos.getX() + 2 + (box.getWidth() / 2),
            pos.getY() + yShift,
            pos.getZ() + 2 + (box.getDepth() / 2));

        region.setPos1(new BlockPos(
            center.x() + box.getMinX(),
            lowestElevation + yShift,
            center.z() + box.getMinZ()));
        region.setPos2(new BlockPos(
            center.x() + box.getMinX() + box.getWidth(),
            highestElevation + yShift,
            center.z() + box.getMinZ() + box.getDepth()));
        region.setCenter(center);
        return region;
    }

    /** Looks up an object by name within a preset (or the default preset when {@code presetFolderName} is null). */
    static CustomObject getObject(String objectName, String presetFolderName) {
        if (presetFolderName == null) {
            presetFolderName = OTG.getEngine().getPresetLoader().getDefaultPresetFolderName();
        }
        return OTG.getEngine().getCustomObjectManager().getGlobalObjects().getObjectByName(
            objectName,
            presetFolderName,
            OTG.getEngine().getOTGRootFolder());
    }

    /** Wraps the current OTG world in a {@link FabricWorldGenRegion} so object code can read/write blocks. */
    static FabricWorldGenRegion getWorldGenRegion(Preset preset, ServerLevel level, OTGFabricChunkGenerator gen) {
        ChunkAccess chunk = level.getChunk(0, 0);
        OTGWorldInfo worldInfo = new OTGWorldInfo(level.getMinBuildHeight(), level.getMaxBuildHeight());
        return new FabricWorldGenRegion(
            preset.getFolderName(),
            OTG.getEngine().getPluginConfig(),
            preset.getPresetConfig(),
            worldInfo,
            level,
            chunk,
            gen);
    }

    private ObjectUtils() {}
}
