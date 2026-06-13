package com.pg85.otg.fabric.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.pg85.otg.customobject.util.Corner;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.joml.Vector3f;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Per-player cuboid selection used by {@link ExportCommand} and {@link EditCommand}. Selections are
 * drawn back to their owner as a particle outline (box edges + centre marker) so it's clear in-world
 * where the region actually is — see {@link #onServerTick}.
 */
final class RegionCommand {

    static final CommandInfo INFO = new CommandInfo(
        "region",
        "Marks and modifies the selection used by /otg export and /otg edit.",
        "/otg region <mark [1|2|center] | clear | expand <dir> <n> | shrink <dir> <n> | show [on|off]>");

    private static final List<String> DIRECTIONS = List.of("north", "south", "east", "west", "up", "down");
    private static final List<String> MARK_POINTS = List.of("1", "2", "center");

    /** Keyed by player UUID so selections survive entity reloads and don't leak entity references. */
    private static final ConcurrentHashMap<UUID, Region> SELECTIONS = new ConcurrentHashMap<>();

    // --- Visualization tuning ---
    private static final int DRAW_INTERVAL_TICKS = 8;
    private static final double STEP = 0.5;          // particle spacing along edges, in blocks
    private static final int MAX_POINTS_PER_EDGE = 64;
    private static final DustParticleOptions BOX_PARTICLE =
        new DustParticleOptions(new Vector3f(0.25f, 0.85f, 1.0f), 0.75f);   // cyan
    private static final DustParticleOptions CENTER_PARTICLE =
        new DustParticleOptions(new Vector3f(1.0f, 0.85f, 0.1f), 1.0f);      // amber

    private static final AtomicInteger TICK = new AtomicInteger();

    // -------------------------------------------------------------------------
    // Selection access (used by export/edit)
    // -------------------------------------------------------------------------

    /** The player's selection, creating an empty one if none exists. */
    static Region getOrCreate(UUID playerId) {
        return SELECTIONS.computeIfAbsent(playerId, k -> new Region());
    }

    /** The player's selection, or null if they have none. */
    static Region peek(UUID playerId) {
        return SELECTIONS.get(playerId);
    }

    static void put(UUID playerId, Region region) {
        SELECTIONS.put(playerId, region);
    }

    // -------------------------------------------------------------------------
    // Command tree
    // -------------------------------------------------------------------------

    static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("region")
            .executes(ctx -> CommandHelper.showUsage(ctx, INFO))
            .then(Commands.literal("mark")
                .executes(RegionCommand::markAlternating)
                .then(Commands.argument("point", StringArgumentType.word())
                    .suggests((ctx, b) -> SharedSuggestionProvider.suggest(MARK_POINTS, b))
                    .executes(ctx -> markPoint(ctx, StringArgumentType.getString(ctx, "point")))))
            .then(Commands.literal("clear").executes(RegionCommand::clear))
            .then(Commands.literal("expand")
                .then(Commands.argument("direction", StringArgumentType.word())
                    .suggests((ctx, b) -> SharedSuggestionProvider.suggest(DIRECTIONS, b))
                    .then(Commands.argument("value", IntegerArgumentType.integer())
                        .executes(ctx -> resize(ctx,
                            StringArgumentType.getString(ctx, "direction"),
                            IntegerArgumentType.getInteger(ctx, "value"))))))
            .then(Commands.literal("shrink")
                .then(Commands.argument("direction", StringArgumentType.word())
                    .suggests((ctx, b) -> SharedSuggestionProvider.suggest(DIRECTIONS, b))
                    .then(Commands.argument("value", IntegerArgumentType.integer())
                        .executes(ctx -> resize(ctx,
                            StringArgumentType.getString(ctx, "direction"),
                            -IntegerArgumentType.getInteger(ctx, "value"))))))
            .then(Commands.literal("show")
                .executes(ctx -> setVisible(ctx, null))
                .then(Commands.literal("on").executes(ctx -> setVisible(ctx, true)))
                .then(Commands.literal("off").executes(ctx -> setVisible(ctx, false))));
    }

    // -------------------------------------------------------------------------
    // Subcommands
    // -------------------------------------------------------------------------

    private static int markAlternating(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        Region region = getOrCreate(player.getUUID());
        boolean setFirst = region.setPos(player.blockPosition());
        ctx.getSource().sendSuccess(() -> Component.literal(setFirst ? "Point 1 marked." : "Point 2 marked."), false);
        return 1;
    }

    private static int markPoint(CommandContext<CommandSourceStack> ctx, String point) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        Region region = getOrCreate(player.getUUID());
        BlockPos pos = player.blockPosition();
        switch (point.toLowerCase()) {
            case "1", "min", "pos1" -> {
                region.setPos1(pos);
                ctx.getSource().sendSuccess(() -> Component.literal("Point 1 marked."), false);
            }
            case "2", "max", "pos2" -> {
                region.setPos2(pos);
                ctx.getSource().sendSuccess(() -> Component.literal("Point 2 marked."), false);
            }
            case "center" -> {
                region.setCenter(new Corner(pos.getX(), pos.getY(), pos.getZ()));
                ctx.getSource().sendSuccess(() -> Component.literal("Center marked."), false);
            }
            default -> ctx.getSource().sendFailure(Component.literal(
                "Unknown point \"" + point + "\". Use 1, 2 or center."));
        }
        return 1;
    }

    private static int clear(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        getOrCreate(player.getUUID()).clear();
        ctx.getSource().sendSuccess(() -> Component.literal("Selection cleared."), false);
        return 1;
    }

    private static int resize(CommandContext<CommandSourceStack> ctx, String direction, int value) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        Region region = getOrCreate(player.getUUID());
        if (region.getMin() == null) {
            ctx.getSource().sendFailure(Component.literal("Mark two points before resizing the region."));
            return 0;
        }

        // Grow/shrink whichever bound currently sits on the chosen side so the box stays well-formed.
        switch (direction.toLowerCase()) {
            case "south" -> { // +Z
                if (region.pos2.getZ() >= region.pos1.getZ()) region.pos2 = region.pos2.south(value);
                else region.pos1 = region.pos1.south(value);
            }
            case "north" -> { // -Z
                if (region.pos2.getZ() < region.pos1.getZ()) region.pos2 = region.pos2.north(value);
                else region.pos1 = region.pos1.north(value);
            }
            case "east" -> { // +X
                if (region.pos2.getX() >= region.pos1.getX()) region.pos2 = region.pos2.east(value);
                else region.pos1 = region.pos1.east(value);
            }
            case "west" -> { // -X
                if (region.pos2.getX() < region.pos1.getX()) region.pos2 = region.pos2.west(value);
                else region.pos1 = region.pos1.west(value);
            }
            case "up" -> { // +Y
                if (region.pos2.getY() >= region.pos1.getY()) region.pos2 = region.pos2.above(value);
                else region.pos1 = region.pos1.above(value);
            }
            case "down" -> { // -Y
                if (region.pos2.getY() < region.pos1.getY()) region.pos2 = region.pos2.below(value);
                else region.pos1 = region.pos1.below(value);
            }
            default -> {
                ctx.getSource().sendFailure(Component.literal("Unknown direction \"" + direction + "\"."));
                return 0;
            }
        }
        ctx.getSource().sendSuccess(() -> Component.literal("Region resized."), false);
        return 1;
    }

    /** Toggles or sets whether the player's selection outline is drawn. {@code state} null = toggle. */
    private static int setVisible(CommandContext<CommandSourceStack> ctx, Boolean state) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        Region region = getOrCreate(player.getUUID());
        region.visible = state != null ? state : !region.visible;
        boolean shown = region.visible;
        ctx.getSource().sendSuccess(() -> Component.literal("Region outline " + (shown ? "shown." : "hidden.")), false);
        return 1;
    }

    // -------------------------------------------------------------------------
    // Visualization — runs every server tick, draws only on a fixed interval.
    // -------------------------------------------------------------------------

    static void onServerTick(MinecraftServer server) {
        if (SELECTIONS.isEmpty()) return;
        if (TICK.incrementAndGet() % DRAW_INTERVAL_TICKS != 0) return;

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            Region region = SELECTIONS.get(player.getUUID());
            if (region == null || !region.visible) continue;
            draw(player, region);
        }
    }

    private static void draw(ServerPlayer player, Region region) {
        ServerLevel level = player.serverLevel();
        Corner min = region.getMin();
        Corner max = region.getMax();

        if (min != null && max != null) {
            // Outline the full block volume: blocks min..max inclusive => world box min .. max+1.
            double x0 = min.x(), y0 = min.y(), z0 = min.z();
            double x1 = max.x() + 1.0, y1 = max.y() + 1.0, z1 = max.z() + 1.0;

            // Bottom rectangle
            line(level, player, BOX_PARTICLE, x0, y0, z0, x1, y0, z0);
            line(level, player, BOX_PARTICLE, x0, y0, z1, x1, y0, z1);
            line(level, player, BOX_PARTICLE, x0, y0, z0, x0, y0, z1);
            line(level, player, BOX_PARTICLE, x1, y0, z0, x1, y0, z1);
            // Top rectangle
            line(level, player, BOX_PARTICLE, x0, y1, z0, x1, y1, z0);
            line(level, player, BOX_PARTICLE, x0, y1, z1, x1, y1, z1);
            line(level, player, BOX_PARTICLE, x0, y1, z0, x0, y1, z1);
            line(level, player, BOX_PARTICLE, x1, y1, z0, x1, y1, z1);
            // Vertical pillars
            line(level, player, BOX_PARTICLE, x0, y0, z0, x0, y1, z0);
            line(level, player, BOX_PARTICLE, x1, y0, z0, x1, y1, z0);
            line(level, player, BOX_PARTICLE, x0, y0, z1, x0, y1, z1);
            line(level, player, BOX_PARTICLE, x1, y0, z1, x1, y1, z1);
        }

        Corner center = region.getCenter();
        if (center != null) {
            level.sendParticles(player, CENTER_PARTICLE, true,
                center.x() + 0.5, center.y() + 0.5, center.z() + 0.5, 1, 0, 0, 0, 0);
        }
    }

    private static void line(ServerLevel level, ServerPlayer player, DustParticleOptions particle,
                             double x1, double y1, double z1, double x2, double y2, double z2) {
        double dx = x2 - x1, dy = y2 - y1, dz = z2 - z1;
        double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
        int points = Math.min(MAX_POINTS_PER_EDGE, Math.max(1, (int) Math.round(length / STEP)));
        for (int i = 0; i <= points; i++) {
            double t = (double) i / points;
            level.sendParticles(player, particle, true,
                x1 + dx * t, y1 + dy * t, z1 + dz * t, 1, 0, 0, 0, 0);
        }
    }

    // -------------------------------------------------------------------------
    // Selection state
    // -------------------------------------------------------------------------

    /** A cuboid selection: two corner positions, an optional explicit centre, plus outline visibility. */
    static final class Region {
        BlockPos pos1;
        BlockPos pos2;
        private Corner center;
        private boolean flip = true;   // true => the next alternating mark sets pos1
        boolean visible = true;

        /** Sets the next point, alternating pos1/pos2. Returns true if pos1 was set. */
        boolean setPos(BlockPos pos) {
            if (flip) pos1 = pos; else pos2 = pos;
            flip = !flip;
            return !flip;
        }

        void setPos1(BlockPos pos) { flip = false; this.pos1 = pos; }
        void setPos2(BlockPos pos) { flip = true;  this.pos2 = pos; }

        void setCenter(Corner center) { this.center = center; }
        Corner getCenter() { return center; }

        void clear() {
            pos1 = null;
            pos2 = null;
            center = null;
            flip = true;
        }

        Corner getMin() {
            if (pos1 == null || pos2 == null) return null;
            return new Corner(
                Math.min(pos1.getX(), pos2.getX()),
                Math.min(pos1.getY(), pos2.getY()),
                Math.min(pos1.getZ(), pos2.getZ()));
        }

        Corner getMax() {
            if (pos1 == null || pos2 == null) return null;
            return new Corner(
                Math.max(pos1.getX(), pos2.getX()),
                Math.max(pos1.getY(), pos2.getY()),
                Math.max(pos1.getZ(), pos2.getZ()));
        }
    }

    private RegionCommand() {}
}
