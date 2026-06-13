package com.pg85.otg.fabric.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * Lists every registered OTG subcommand, or detailed help for one of them.
 * Pulls its text from each command's local {@link CommandInfo}, so descriptions
 * stay next to the command they describe.
 */
final class HelpCommand {

    static final CommandInfo INFO = new CommandInfo(
        "help",
        "Lists all OTG commands, or shows detailed help for one.",
        "/otg help [command]");

    static LiteralArgumentBuilder<CommandSourceStack> register(List<CommandInfo> commands) {
        return Commands.literal("help")
            .executes(ctx -> showAll(ctx, commands))
            .then(Commands.argument("command", StringArgumentType.word())
                .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(
                    commands.stream().map(CommandInfo::name), builder))
                .executes(ctx -> showOne(ctx, commands,
                    StringArgumentType.getString(ctx, "command"))));
    }

    static int showAll(CommandContext<CommandSourceStack> ctx, List<CommandInfo> commands) {
        CommandSourceStack src = ctx.getSource();
        src.sendSuccess(() -> Component.literal("OTG commands:"), false);
        for (CommandInfo info : commands) {
            src.sendSuccess(() -> Component.literal("/otg " + info.name() + " - " + info.description()), false);
        }
        src.sendSuccess(() -> Component.literal("Use /otg help <command> for usage."), false);
        return 1;
    }

    private static int showOne(CommandContext<CommandSourceStack> ctx, List<CommandInfo> commands, String name) {
        CommandSourceStack src = ctx.getSource();
        CommandInfo info = commands.stream()
            .filter(c -> c.name().equalsIgnoreCase(name))
            .findFirst().orElse(null);
        if (info == null) {
            src.sendFailure(Component.literal("Unknown command: \"" + name + "\". Use /otg help to list commands."));
            return 0;
        }
        src.sendSuccess(() -> Component.literal("/otg " + info.name() + ": " + info.description()), false);
        src.sendSuccess(() -> Component.literal("usage: " + info.usage()), false);
        return 1;
    }

    private HelpCommand() {}
}
