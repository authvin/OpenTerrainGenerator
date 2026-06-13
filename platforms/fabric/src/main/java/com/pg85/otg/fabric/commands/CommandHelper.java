package com.pg85.otg.fabric.commands;

import com.mojang.brigadier.context.CommandContext;
import com.pg85.otg.fabric.gen.OTGFabricChunkGenerator;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import org.jetbrains.annotations.Nullable;

final class CommandHelper {

    @Nullable
    static OTGFabricChunkGenerator getGenerator(ServerLevel level) {
        ChunkGenerator gen = level.getChunkSource().getGenerator();
        return gen instanceof OTGFabricChunkGenerator otg ? otg : null;
    }

    /** Sends a failure message and returns false if the current world is not an OTG world. */
    static boolean requireOTGWorld(CommandContext<CommandSourceStack> ctx) {
        if (getGenerator(ctx.getSource().getLevel()) == null) {
            ctx.getSource().sendFailure(Component.literal("This command requires an OTG world."));
            return false;
        }
        return true;
    }

    /** Prints a command's own description and usage. Used as the base action when a command is invoked without a valid subcommand. */
    static int showUsage(CommandContext<CommandSourceStack> ctx, CommandInfo info) {
        ctx.getSource().sendSuccess(() -> Component.literal("/otg " + info.name() + ": " + info.description()), false);
        ctx.getSource().sendSuccess(() -> Component.literal("usage: " + info.usage()), false);
        return 1;
    }

    private CommandHelper() {}
}
