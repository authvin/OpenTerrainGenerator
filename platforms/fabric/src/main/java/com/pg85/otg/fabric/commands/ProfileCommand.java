package com.pg85.otg.fabric.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.pg85.otg.OTG;
import com.pg85.otg.util.logging.LogCategory;
import com.pg85.otg.util.profiling.GenProfiler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

/**
 * Inspects the worldgen profiler ({@link GenProfiler}). Data is collected globally across all
 * OTG worlds and generation threads since startup or the last reset.
 */
final class ProfileCommand {

    private static final int CHAT_TOP_SECTIONS = 15;

    static final CommandInfo INFO = new CommandInfo(
        "profile",
        "Shows worldgen profiling data; toggle, reset or dump it to log.",
        "/otg profile [full|log|reset|on|off]");

    static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("profile")
            .executes(ctx -> show(ctx, CHAT_TOP_SECTIONS))
            .then(Commands.literal("full").executes(ctx -> show(ctx, Integer.MAX_VALUE)))
            .then(Commands.literal("log").executes(ProfileCommand::dumpToLog))
            .then(Commands.literal("reset").executes(ProfileCommand::reset))
            .then(Commands.literal("on").executes(ctx -> setEnabled(ctx, true)))
            .then(Commands.literal("off").executes(ctx -> setEnabled(ctx, false)));
    }

    /** /otg profile [full] — print the profile to chat (column-aligned in console/log). */
    private static int show(CommandContext<CommandSourceStack> ctx, int topN) {
        if (!GenProfiler.hasData()) {
            ctx.getSource().sendSuccess(() -> Component.literal(
                "No worldgen profiling data collected yet"
                + (GenProfiler.isEnabled() ? "." : " (collection is off, use /otg profile on).")
            ), false);
            return 0;
        }
        String report = GenProfiler.report(topN);
        ctx.getSource().sendSuccess(() -> Component.literal(report), false);
        return 1;
    }

    /** /otg profile log — write the full profile to the server log. */
    private static int dumpToLog(CommandContext<CommandSourceStack> ctx) {
        OTG.getEngine().getLogger().info(LogCategory.MAIN, "%s", GenProfiler.report());
        ctx.getSource().sendSuccess(() -> Component.literal("Full worldgen profile written to the server log."), false);
        return 1;
    }

    private static int reset(CommandContext<CommandSourceStack> ctx) {
        GenProfiler.reset();
        ctx.getSource().sendSuccess(() -> Component.literal("Worldgen profiling data reset."), false);
        return 1;
    }

    private static int setEnabled(CommandContext<CommandSourceStack> ctx, boolean enabled) {
        GenProfiler.setEnabled(enabled);
        ctx.getSource().sendSuccess(() -> Component.literal(
            "Worldgen profiling collection " + (enabled ? "enabled." : "disabled.")
        ), false);
        return 1;
    }

    private ProfileCommand() {}
}
