package com.pg85.otg.fabric.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.pg85.otg.OTG;
import com.pg85.otg.customobject.CustomObject;
import com.pg85.otg.customobject.bo3.bo3function.BO3RandomBlockFunction;
import com.pg85.otg.customobject.bo4.bo4function.BO4RandomBlockFunction;
import com.pg85.otg.customobject.bofunctions.BlockFunction;
import com.pg85.otg.customobject.creator.ObjectCreator;
import com.pg85.otg.customobject.creator.ObjectType;
import com.pg85.otg.customobject.structures.StructuredCustomObject;
import com.pg85.otg.customobject.util.Corner;
import com.pg85.otg.fabric.gen.FabricWorldGenRegion;
import com.pg85.otg.fabric.gen.OTGFabricChunkGenerator;
import com.pg85.otg.fabric.materials.FabricMaterialData;
import com.pg85.otg.fabric.util.FabricNBTHelper;
import com.pg85.otg.presets.Preset;
import com.pg85.otg.util.OTGMaterialReader;
import com.pg85.otg.util.logging.LogCategory;
import com.pg85.otg.util.logging.LogLevel;
import com.pg85.otg.util.materials.LocalMaterials;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Spawns an existing BO3/BO4 into the world so it can be edited in place, then writes it back on
 * {@code /otg finishedit}. The working area uses the same {@link RegionCommand} selection so the
 * outline is visible while editing.
 */
final class EditCommand {

    static final CommandInfo INFO = new CommandInfo(
        "edit",
        "Spawns a BO3/BO4 to edit in-world; finish with /otg finishedit, abort with /otg canceledit.",
        "/otg edit <preset|global> <object> [-nofix -update -wrongleaves]");

    private static final ConcurrentHashMap<UUID, EditSession> SESSIONS = new ConcurrentHashMap<>();

    // -------------------------------------------------------------------------
    // Command tree
    // -------------------------------------------------------------------------

    static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("edit")
            .executes(ctx -> CommandHelper.showUsage(ctx, INFO))
            .then(Commands.argument("preset", StringArgumentType.string())
                .suggests(presetSuggestions())
                .then(Commands.argument("object", StringArgumentType.string())
                    .suggests(objectSuggestions())
                    .executes(ctx -> startEdit(ctx,
                        StringArgumentType.getString(ctx, "preset"),
                        StringArgumentType.getString(ctx, "object"), ""))
                    .then(Commands.argument("flags", StringArgumentType.greedyString())
                        .suggests((c, b) -> SharedSuggestionProvider.suggest(
                            java.util.List.of("-nofix", "-update", "-wrongleaves"), b))
                        .executes(ctx -> startEdit(ctx,
                            StringArgumentType.getString(ctx, "preset"),
                            StringArgumentType.getString(ctx, "object"),
                            StringArgumentType.getString(ctx, "flags"))))));
    }

    static LiteralArgumentBuilder<CommandSourceStack> registerFinish() {
        return Commands.literal("finishedit").executes(EditCommand::finishEdit);
    }

    static LiteralArgumentBuilder<CommandSourceStack> registerCancel() {
        return Commands.literal("canceledit").executes(EditCommand::cancelEdit);
    }

    // -------------------------------------------------------------------------
    // Start
    // -------------------------------------------------------------------------

    private static int startEdit(CommandContext<CommandSourceStack> ctx, String presetName, String objectName, String flags) {
        CommandSourceStack src = ctx.getSource();
        try {
            ServerPlayer player = src.getPlayerOrException();
            ServerLevel level = src.getLevel();
            OTGFabricChunkGenerator gen = CommandHelper.getGenerator(level);
            if (gen == null) {
                src.sendFailure(Component.literal("This command requires an OTG world."));
                return 0;
            }

            boolean immediate = flags.contains("-update");
            boolean doFixing = !flags.contains("-nofix");
            boolean leaveIllegalLeaves = flags.contains("-wrongleaves");

            boolean global = presetName.equalsIgnoreCase("global");
            String presetFolderName = global ? null : presetName;

            StructuredCustomObject inputObject = getStructuredObject(objectName, presetFolderName);
            if (inputObject == null) {
                src.sendFailure(Component.literal("Could not find an editable BO3/BO4 named \"" + objectName + "\"."));
                return 0;
            }
            ObjectType type = inputObject.getType();

            Preset preset = ObjectUtils.getPresetOrDefault(presetFolderName);
            if (preset == null) {
                src.sendFailure(Component.literal("Could not find preset \"" + presetName + "\"."));
                return 0;
            }

            FabricWorldGenRegion worldGenRegion = ObjectUtils.getWorldGenRegion(preset, level, gen);
            RegionCommand.Region region = ObjectUtils.getRegionFromObject(
                player.blockPosition(), inputObject, level.getMinBuildHeight(), level.getMaxBuildHeight());
            Corner center = region.getCenter();

            // Carve out empty space, then spawn the object into it.
            ObjectUtils.cleanArea(worldGenRegion, region.getMin(), region.getMax(), true);
            ArrayList<BlockFunction<?>> extraBlocks = spawnAndFixObject(
                center.x(), center.y(), center.z(), inputObject, worldGenRegion, doFixing, preset.getFolderName());

            if (immediate) {
                StructuredCustomObject result = export(type, region, center, inputObject, extraBlocks,
                    preset.getFolderName(), leaveIllegalLeaves, worldGenRegion);
                ObjectUtils.cleanArea(worldGenRegion, region.getMin(), region.getMax(), false);
                report(src, type, inputObject.getName(), result, preset.getFolderName());
                return result != null ? 1 : 0;
            }

            SESSIONS.put(player.getUUID(), new EditSession(
                type, worldGenRegion, inputObject, extraBlocks, preset.getFolderName(), center, leaveIllegalLeaves));
            RegionCommand.put(player.getUUID(), region);

            src.sendSuccess(() -> Component.literal("Editing \"" + inputObject.getName() + "\"."), false);
            src.sendSuccess(() -> Component.literal("Adjust the area with /otg region, then /otg finishedit (or /otg canceledit)."), false);
            if (!extraBlocks.isEmpty()) {
                src.sendSuccess(() -> Component.literal("Note: this object has NBT/random blocks, so its center cannot be moved."), false);
            }
            return 1;
        } catch (Exception e) {
            src.sendFailure(Component.literal("Edit failed, see the log for details."));
            OTG.getEngine().getLogger().log(LogLevel.ERROR, LogCategory.MAIN,
                "Edit command error: " + e.getClass().getName() + " - " + e.getMessage());
            OTG.getEngine().getLogger().printStackTrace(LogLevel.ERROR, LogCategory.MAIN, e);
            return 0;
        }
    }

    // -------------------------------------------------------------------------
    // Finish / cancel
    // -------------------------------------------------------------------------

    private static int finishEdit(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack src = ctx.getSource();
        try {
            ServerPlayer player = src.getPlayerOrException();
            EditSession session = SESSIONS.get(player.getUUID());
            RegionCommand.Region region = RegionCommand.peek(player.getUUID());
            if (session == null || region == null) {
                src.sendFailure(Component.literal("No active edit session. Start one with /otg edit."));
                return 0;
            }
            if (ObjectUtils.isOutsideBounds(region, session.type())) {
                src.sendFailure(Component.literal("Selection is too big — max 32x32 for BO3, 16x16 for BO4."));
                return 0;
            }

            // Complex objects (NBT/random blocks) keep their original centre.
            Corner center = session.extraBlocks().isEmpty() ? region.getCenter() : session.originalCenter();
            StructuredCustomObject result = export(session.type(), region, center, session.object(),
                session.extraBlocks(), session.presetFolderName(), session.leaveIllegalLeaves(), session.genRegion());

            ObjectUtils.cleanArea(session.genRegion(), region.getMin(), region.getMax(), false);
            SESSIONS.remove(player.getUUID());
            report(src, session.type(), session.object().getName(), result, session.presetFolderName());
            return result != null ? 1 : 0;
        } catch (Exception e) {
            src.sendFailure(Component.literal("Finishing edit failed, see the log for details."));
            OTG.getEngine().getLogger().printStackTrace(LogLevel.ERROR, LogCategory.MAIN, e);
            return 0;
        }
    }

    private static int cancelEdit(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack src = ctx.getSource();
        try {
            ServerPlayer player = src.getPlayerOrException();
            EditSession session = SESSIONS.remove(player.getUUID());
            RegionCommand.Region region = RegionCommand.peek(player.getUUID());
            if (session == null || region == null) {
                src.sendFailure(Component.literal("No active edit session to cancel."));
                return 0;
            }
            ObjectUtils.cleanArea(session.genRegion(), region.getMin(), region.getMax(), false);
            src.sendSuccess(() -> Component.literal("Edit session cancelled."), false);
            return 1;
        } catch (Exception e) {
            src.sendFailure(Component.literal("Cancelling edit failed, see the log for details."));
            OTG.getEngine().getLogger().printStackTrace(LogLevel.ERROR, LogCategory.MAIN, e);
            return 0;
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static StructuredCustomObject getStructuredObject(String objectName, String presetFolderName) {
        CustomObject object = ObjectUtils.getObject(objectName, presetFolderName);
        return object instanceof StructuredCustomObject structured ? structured : null;
    }

    private static StructuredCustomObject export(ObjectType type, RegionCommand.Region region, Corner center,
                                                 StructuredCustomObject object, ArrayList<BlockFunction<?>> extraBlocks,
                                                 String presetFolderName, boolean leaveIllegalLeaves,
                                                 FabricWorldGenRegion worldGenRegion) {
        return ObjectCreator.createObject(
            type, region.getMin(), region.getMax(), center, null, object.getName(),
            false, leaveIllegalLeaves, object.getConfig().getFile().getParentFile().toPath(),
            worldGenRegion, new FabricNBTHelper(), extraBlocks, object.getConfig(),
            presetFolderName, OTG.getEngine().getOTGRootFolder(),
            OTG.getEngine().getCustomObjectManager(), OTGMaterialReader.get(),
            OTG.getEngine().getCustomObjectResourcesManager(), OTG.getEngine().getModLoadedChecker());
    }

    private static void report(CommandSourceStack src, ObjectType type, String name,
                               StructuredCustomObject result, String presetFolderName) {
        if (result != null) {
            src.sendSuccess(() -> Component.literal("Saved " + type.getType() + " \"" + name + "\"."), false);
            OTG.getEngine().getCustomObjectManager().getGlobalObjects().addObjectToPreset(
                presetFolderName, result.getName().toLowerCase(Locale.ROOT), result.getConfig().getFile(), result);
        } else {
            src.sendFailure(Component.literal("Failed to save " + type.getType() + " \"" + name + "\"."));
        }
    }

    /**
     * Spawns the object's blocks into the world for editing. Returns the blocks that could not be
     * spawned directly (NBT, random blocks, blanks) so they can be re-attached on export. When
     * {@code fixObject}, connecting blocks (fences/walls/panes) get a neighbour-shape update and
     * leaves are kept from decaying.
     */
    private static ArrayList<BlockFunction<?>> spawnAndFixObject(int x, int y, int z, StructuredCustomObject object,
                                                                 FabricWorldGenRegion worldGenRegion, boolean fixObject,
                                                                 String presetFolderName) {
        BlockFunction<?>[] blocks = object.getConfig().getBlockFunctions(
            presetFolderName, OTG.getEngine().getOTGRootFolder(),
            OTG.getEngine().getCustomObjectManager(), OTGMaterialReader.get(),
            OTG.getEngine().getCustomObjectResourcesManager(), OTG.getEngine().getModLoadedChecker());

        Set<BlockPos> updates = new HashSet<>();
        Set<BlockPos> gravityBlocks = new HashSet<>();
        Random random = new Random();
        ArrayList<BlockFunction<?>> unspawned = new ArrayList<>();

        for (BlockFunction<?> block : blocks) {
            if (block.material == null || block.nbt != null
                || block instanceof BO3RandomBlockFunction || block instanceof BO4RandomBlockFunction
                || block.material.isBlank()) {
                unspawned.add(block);
                continue;
            }

            int bx = x + block.x, by = y + block.y, bz = z + block.z;

            if (fixObject) {
                ResourceLocation id = ResourceLocation.tryParse(block.material.getRegistryName());
                if (id != null && UPDATE_BLOCKS.contains(id)) {
                    updates.add(new BlockPos(bx, by, bz));
                }
            }
            if (GRAVITY_BLOCKS.contains(block.material.getRegistryName())) {
                gravityBlocks.add(new BlockPos(bx, by, bz));
            }
            // Keep leaves from decaying while the object sits in the world being edited.
            if (block.material.isLeaves()) {
                BlockState leaf = ((FabricMaterialData) block.material).getState()
                    .setValue(LeavesBlock.PERSISTENT, true).setValue(LeavesBlock.DISTANCE, 7);
                block.material = FabricMaterialData.ofBlockState(leaf);
            }
            block.spawn(worldGenRegion, random, bx, by, bz);
        }

        // Prop up sand/gravel so it doesn't fall into the structure void below.
        for (BlockPos pos : gravityBlocks) {
            if (worldGenRegion.getMaterial(pos.getX(), pos.getY() - 1, pos.getZ()).isMaterial(LocalMaterials.STRUCTURE_VOID)) {
                worldGenRegion.setBlock(pos.getX(), pos.getY() - 1, pos.getZ(), LocalMaterials.STRUCTURE_BLOCK);
            }
        }

        if (fixObject) {
            WorldGenLevel level = worldGenRegion.getInternal();
            for (BlockPos pos : updates) {
                BlockState state = level.getBlockState(pos);
                if (state.is(BlockTags.LEAVES)) {
                    level.scheduleTick(pos, state.getBlock(), 1);
                } else {
                    BlockState updated = Block.updateFromNeighbourShapes(state, level, pos);
                    level.setBlock(pos, updated, 20);
                }
            }
        }
        return unspawned;
    }

    // -------------------------------------------------------------------------
    // Suggestions
    // -------------------------------------------------------------------------

    private static SuggestionProvider<CommandSourceStack> presetSuggestions() {
        return (ctx, builder) -> {
            builder.suggest("global");
            return SharedSuggestionProvider.suggest(
                OTG.getEngine().getPresetLoader().getAllPresets().stream().map(Preset::getFolderName), builder);
        };
    }

    private static SuggestionProvider<CommandSourceStack> objectSuggestions() {
        return (ctx, builder) -> {
            String presetName = StringArgumentType.getString(ctx, "preset");
            String presetFolderName = presetName.equalsIgnoreCase("global")
                ? OTG.getEngine().getPresetLoader().getDefaultPresetFolderName() : presetName;
            ArrayList<String> names = OTG.getEngine().getCustomObjectManager().getGlobalObjects()
                .getAllBONamesForPreset(presetFolderName, OTG.getEngine().getOTGRootFolder());
            return names != null ? SharedSuggestionProvider.suggest(names, builder) : builder.buildFuture();
        };
    }

    // -------------------------------------------------------------------------

    private record EditSession(ObjectType type, FabricWorldGenRegion genRegion, StructuredCustomObject object,
                               ArrayList<BlockFunction<?>> extraBlocks, String presetFolderName,
                               Corner originalCenter, boolean leaveIllegalLeaves) {}

    /** Block ids whose connected state (fences, walls, panes, stairs, redstone, leaves) needs a post-spawn update. */
    private static final Set<ResourceLocation> UPDATE_BLOCKS = new HashSet<>();
    /** Registry names of blocks that fall under gravity and must be propped during editing. */
    private static final Set<String> GRAVITY_BLOCKS = Set.of(
        "minecraft:sand", "minecraft:red_sand", "minecraft:gravel");

    static {
        String[] names = {
            "oak_fence", "birch_fence", "nether_brick_fence", "spruce_fence", "jungle_fence", "acacia_fence", "dark_oak_fence",
            "iron_door", "oak_door", "spruce_door", "birch_door", "jungle_door", "acacia_door", "dark_oak_door",
            "glass_pane", "white_stained_glass_pane", "orange_stained_glass_pane", "magenta_stained_glass_pane",
            "light_blue_stained_glass_pane", "yellow_stained_glass_pane", "lime_stained_glass_pane", "pink_stained_glass_pane",
            "gray_stained_glass_pane", "light_gray_stained_glass_pane", "cyan_stained_glass_pane", "purple_stained_glass_pane",
            "blue_stained_glass_pane", "brown_stained_glass_pane", "green_stained_glass_pane", "red_stained_glass_pane",
            "black_stained_glass_pane", "purpur_stairs", "oak_stairs", "cobblestone_stairs", "brick_stairs",
            "stone_brick_stairs", "nether_brick_stairs", "spruce_stairs", "sandstone_stairs", "birch_stairs", "jungle_stairs",
            "quartz_stairs", "acacia_stairs", "dark_oak_stairs", "prismarine_stairs", "prismarine_brick_stairs",
            "dark_prismarine_stairs", "red_sandstone_stairs", "polished_granite_stairs", "smooth_red_sandstone_stairs",
            "mossy_stone_brick_stairs", "polished_diorite_stairs", "mossy_cobblestone_stairs", "end_stone_brick_stairs",
            "stone_stairs", "smooth_sandstone_stairs", "smooth_quartz_stairs", "granite_stairs", "andesite_stairs",
            "red_nether_brick_stairs", "polished_andesite_stairs", "diorite_stairs", "cobblestone_wall", "mossy_cobblestone_wall",
            "brick_wall", "prismarine_wall", "red_sandstone_wall", "mossy_stone_brick_wall", "granite_wall", "stone_brick_wall",
            "nether_brick_wall", "andesite_wall", "red_nether_brick_wall", "sandstone_wall", "end_stone_brick_wall",
            "diorite_wall", "iron_bars", "trapped_chest", "chest", "redstone_wire", "oak_leaves", "spruce_leaves",
            "birch_leaves", "jungle_leaves", "acacia_leaves", "dark_oak_leaves", "vine"
        };
        for (String name : names) {
            UPDATE_BLOCKS.add(new ResourceLocation(name));
        }
    }

    private EditCommand() {}
}
