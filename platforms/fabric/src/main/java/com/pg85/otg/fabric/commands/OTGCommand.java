package com.pg85.otg.fabric.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.Comparator;
import java.util.List;

public final class OTGCommand {

    /** Local INFO of every subcommand, sorted by name. Drives /otg help and the bare-/otg listing. */
    private static final List<CommandInfo> COMMANDS = List.of(
        HelpCommand.INFO,
        BiomeCommand.INFO,
        TpCommand.INFO,
        MapCommand.INFO,
        PresetCommand.INFO,
        SettingsCommand.INFO,
        PackCommand.INFO,
        ProfileCommand.INFO,
        RegionCommand.INFO,
        ExportCommand.INFO,
        EditCommand.INFO
    ).stream().sorted(Comparator.comparing(CommandInfo::name)).toList();

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher,
                                CommandBuildContext buildContext,
                                Commands.CommandSelection selection) {
        dispatcher.register(
            Commands.literal("otg")
                .requires(src -> src.hasPermission(2))
                .executes(ctx -> HelpCommand.showAll(ctx, COMMANDS))
                .then(HelpCommand.register(COMMANDS))
                .then(BiomeCommand.register())
                .then(TpCommand.register())
                .then(MapCommand.register())
                .then(PresetCommand.register())
                .then(SettingsCommand.register())
                .then(PackCommand.register())
                .then(ProfileCommand.register())
                .then(RegionCommand.register())
                .then(ExportCommand.register())
                .then(EditCommand.register())
                .then(EditCommand.registerFinish())
                .then(EditCommand.registerCancel())
        );
    }

    /** Registers server-side tick events used by commands (the /otg region selection outline). */
    public static void registerEvents() {
        ServerTickEvents.END_SERVER_TICK.register(server -> RegionCommand.onServerTick(server));
    }

    private OTGCommand() {}
}
