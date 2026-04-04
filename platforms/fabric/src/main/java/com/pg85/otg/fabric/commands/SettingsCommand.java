package com.pg85.otg.fabric.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.pg85.otg.OTG;
import com.pg85.otg.config.biome.BiomeConfig;
import com.pg85.otg.config.io.FileSettingsWriter;
import com.pg85.otg.config.io.SimpleSettingsMap;
import com.pg85.otg.config.settings.biome.BiomeSettings;
import com.pg85.otg.constants.settings.ConfigMode;
import com.pg85.otg.fabric.gen.OTGFabricChunkGenerator;
import com.pg85.otg.presets.Preset;
import com.pg85.otg.util.biome.OTGBiomeID;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

final class SettingsCommand {

    static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("settings")
            // /otg settings biome  (uses biome at current position)
            // /otg settings biome <name>  (uses current world's preset)
            .then(Commands.literal("biome")
                .executes(SettingsCommand::executeBiomeAtPosition)
                .then(Commands.argument("name", StringArgumentType.greedyString())
                    .suggests(currentPresetBiomeSuggestions())
                    .executes(ctx -> executeBiome(ctx,
                        StringArgumentType.getString(ctx, "name"),
                        null)))
            )
            // /otg settings preset  (current world's preset)
            .then(Commands.literal("preset")
                .executes(SettingsCommand::executePreset)
                // /otg settings preset <name>
                .then(Commands.argument("name", StringArgumentType.greedyString())
                    .suggests(presetSuggestions())
                    .executes(ctx -> executePresetByName(ctx,
                        StringArgumentType.getString(ctx, "name")))));
    }

    // -------------------------------------------------------------------------

    private static int executeBiome(CommandContext<CommandSourceStack> ctx, String biomeName, String presetName) {
        CommandSourceStack src = ctx.getSource();

        Preset preset = resolvePreset(ctx, presetName);
        if (preset == null) return 0;

        BiomeSettings bs = preset.getBiomeConfig(biomeName);
        if (bs == null) {
            src.sendFailure(Component.literal("Biome \"" + biomeName + "\" not found in preset \"" + preset.getFolderName() + "\"."));
            return 0;
        }
        if (!(bs instanceof BiomeConfig bc)) {
            src.sendFailure(Component.literal("Internal error: biome config is not a BiomeConfig instance."));
            return 0;
        }

        try {
            Path outFile = prepareOutputFile(preset.getFolderName(), biomeName.replace(' ', '_') + ".bc");
            SimpleSettingsMap map = new SimpleSettingsMap(biomeName, outFile);
            bc.writeConfigSettings(map);
            FileSettingsWriter.writeToFile(map, outFile.toFile(), ConfigMode.WriteAll);
            src.sendSuccess(() -> Component.literal("Biome settings written to: " + outFile), false);
            return 1;
        } catch (IOException e) {
            src.sendFailure(Component.literal("Failed to write settings: " + e.getMessage()));
            return 0;
        }
    }

    private static int executePreset(CommandContext<CommandSourceStack> ctx) {
        if (!CommandHelper.requireOTGWorld(ctx)) return 0;
        OTGFabricChunkGenerator gen = CommandHelper.getGenerator(ctx.getSource().getLevel());
        if (gen == null) {
            return 0;
        }
        return dumpPreset(ctx, gen.getPreset());
    }

    private static int executePresetByName(CommandContext<CommandSourceStack> ctx, String presetName) {
        Preset preset = resolvePreset(ctx, presetName);
        if (preset == null) return 0;
        return dumpPreset(ctx, preset);
    }

    private static int dumpPreset(CommandContext<CommandSourceStack> ctx, Preset preset) {
        CommandSourceStack src = ctx.getSource();
        try {
            Path outFile = prepareOutputFile(preset.getFolderName(), "PresetConfig.ini");
            SimpleSettingsMap map = new SimpleSettingsMap(preset.getFolderName(), outFile);
            preset.getPresetConfig().writeConfigSettings(map);
            FileSettingsWriter.writeToFile(map, outFile.toFile(), ConfigMode.WriteAll);
            src.sendSuccess(() -> Component.literal("Preset settings written to: " + outFile), false);
            return 1;
        } catch (IOException e) {
            src.sendFailure(Component.literal("Failed to write settings: " + e.getMessage()));
            return 0;
        }
    }

    private static int executeBiomeAtPosition(CommandContext<CommandSourceStack> ctx) {
        if (!CommandHelper.requireOTGWorld(ctx)) return 0;
        CommandSourceStack src = ctx.getSource();
        ServerLevel level = src.getLevel();
        OTGFabricChunkGenerator gen = CommandHelper.getGenerator(level);
        if (gen == null) return 0;

        BlockPos pos = BlockPos.containing(src.getPosition());
        Holder<Biome> biomeHolder = level.getBiome(pos);
        Optional<ResourceKey<Biome>> biomeKey = biomeHolder.unwrapKey();
        if (biomeKey.isEmpty()) {
            src.sendFailure(Component.literal("Could not determine biome at current position."));
            return 0;
        }

        String registryName = biomeKey.get().location().toString();
        OTGBiomeID otgBiomeID = gen.getPreset().getBiomeIDByRegistryName(registryName);
        if (otgBiomeID == null) {
            src.sendFailure(Component.literal("Current biome (" + registryName + ") is not an OTG biome."));
            return 0;
        }

        return executeBiome(ctx, otgBiomeID.biomeName(), null);
    }

    // -------------------------------------------------------------------------

    private static Preset resolvePreset(CommandContext<CommandSourceStack> ctx, String presetName) {
        if (presetName != null) {
            Preset preset = OTG.getEngine().getPresetLoader().getPresetByShortNameOrFolderName(presetName);
            if (preset == null) {
                ctx.getSource().sendFailure(Component.literal("Unknown preset: \"" + presetName + "\"."));
            }
            return preset;
        }
        if (!CommandHelper.requireOTGWorld(ctx)) return null;
        return CommandHelper.getGenerator(ctx.getSource().getLevel()).getPreset();
    }

    private static Path prepareOutputFile(String presetFolder, String filename) throws IOException {
        Path dir = OTG.getEngine().getOTGRootFolder().resolve("output").resolve(presetFolder);
        Files.createDirectories(dir);
        return dir.resolve(filename);
    }

    // -------------------------------------------------------------------------

    private static SuggestionProvider<CommandSourceStack> currentPresetBiomeSuggestions() {
        return (ctx, builder) -> {
            OTGFabricChunkGenerator gen = CommandHelper.getGenerator(ctx.getSource().getLevel());
            if (gen != null) {
                return SharedSuggestionProvider.suggest(gen.getPreset().getAllBiomeNames(), builder);
            }
            return builder.buildFuture();
        };
    }

    private static SuggestionProvider<CommandSourceStack> namedPresetBiomeSuggestions() {
        return (ctx, builder) -> {
            try {
                String presetName = StringArgumentType.getString(ctx, "preset");
                Preset preset = OTG.getEngine().getPresetLoader().getPresetByShortNameOrFolderName(presetName);
                if (preset != null) {
                    return SharedSuggestionProvider.suggest(preset.getAllBiomeNames(), builder);
                }
            } catch (Exception ignored) {}
            return builder.buildFuture();
        };
    }

    private static SuggestionProvider<CommandSourceStack> presetSuggestions() {
        return (ctx, builder) -> SharedSuggestionProvider.suggest(
            OTG.getEngine().getPresetLoader().getAllPresets().stream()
                .map(Preset::getFolderName)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()),
            builder
        );
    }
}
