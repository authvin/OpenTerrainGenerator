package com.pg85.otg.fabric.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.pg85.otg.OTG;

import com.pg85.otg.customobject.CustomObjectLoader;
import com.pg85.otg.customobject.creator.ObjectCreator;
import com.pg85.otg.customobject.creator.ObjectType;
import com.pg85.otg.customobject.structures.StructuredCustomObject;
import com.pg85.otg.customobject.util.Corner;
import com.pg85.otg.fabric.gen.FabricWorldGenRegion;
import com.pg85.otg.fabric.gen.OTGFabricChunkGenerator;
import com.pg85.otg.fabric.util.FabricNBTHelper;
import com.pg85.otg.presets.Preset;
import com.pg85.otg.util.OTGMaterialReader;
import com.pg85.otg.util.logging.LogCategory;
import com.pg85.otg.util.logging.LogLevel;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.io.File;
import java.nio.file.Path;
import java.util.Locale;

/**
 * Exports the player's {@link RegionCommand} selection as a BO3 or BO4. The selection must be marked
 * first ({@code /otg region mark}); flags toggle air inclusion, structure splitting and overwrite.
 */
final class ExportCommand {

    static final CommandInfo INFO = new CommandInfo(
        "export",
        "Exports the marked region as a BO3 or BO4 object.",
        "/otg export <name> [BO3|BO4] [preset] [template] [flags: -a air -b branches -o overwrite]");

    static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("export")
            .executes(ctx -> CommandHelper.showUsage(ctx, INFO))
            .then(Commands.argument("name", StringArgumentType.string())
                .executes(ctx -> run(ctx, "BO3", null, "default", ""))
                .then(Commands.argument("type", StringArgumentType.word())
                    .suggests((c, b) -> SharedSuggestionProvider.suggest(java.util.List.of("BO3", "BO4"), b))
                    .executes(ctx -> run(ctx, get(ctx, "type"), null, "default", ""))
                    .then(Commands.argument("preset", StringArgumentType.string())
                        .suggests(presetSuggestions())
                        .executes(ctx -> run(ctx, get(ctx, "type"), get(ctx, "preset"), "default", ""))
                        .then(Commands.argument("template", StringArgumentType.string())
                            .executes(ctx -> run(ctx, get(ctx, "type"), get(ctx, "preset"), get(ctx, "template"), ""))
                            .then(Commands.argument("flags", StringArgumentType.greedyString())
                                .executes(ctx -> run(ctx, get(ctx, "type"), get(ctx, "preset"),
                                    get(ctx, "template"), get(ctx, "flags"))))))));
    }

    private static String get(CommandContext<CommandSourceStack> ctx, String name) {
        return StringArgumentType.getString(ctx, name);
    }

    private static int run(CommandContext<CommandSourceStack> ctx, String typeName,
                           String presetName, String templateName, String flags) {
        CommandSourceStack src = ctx.getSource();
        try {
            ServerPlayer player = src.getPlayerOrException();
            ServerLevel level = src.getLevel();
            OTGFabricChunkGenerator gen = CommandHelper.getGenerator(level);
            if (gen == null) {
                src.sendFailure(Component.literal("This command requires an OTG world."));
                return 0;
            }

            String objectName = get(ctx, "name");
            ObjectType type;
            try {
                type = ObjectType.valueOf(typeName.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                src.sendFailure(Component.literal("Unknown object type \"" + typeName + "\". Use BO3 or BO4."));
                return 0;
            }
            if (type == ObjectType.BO2) {
                src.sendFailure(Component.literal("BO2 objects cannot be exported."));
                return 0;
            }

            boolean overwrite = flags.contains("o");
            boolean includeAir = flags.contains("a");
            boolean isStructure = flags.contains("b");

            // Default to the current world's preset when none is given.
            boolean global = presetName != null && presetName.equalsIgnoreCase("global");
            Preset preset = global ? ObjectUtils.getPresetOrDefault(null)
                : (presetName != null ? ObjectUtils.getPresetOrDefault(presetName) : gen.getPreset());
            if (preset == null) {
                src.sendFailure(Component.literal("Could not find preset \"" + presetName + "\"."));
                return 0;
            }

            RegionCommand.Region region = RegionCommand.peek(player.getUUID());
            if (region == null || region.getMin() == null || region.getMax() == null) {
                src.sendFailure(Component.literal("Mark a region first with /otg region mark."));
                return 0;
            }
            if (ObjectUtils.isOutsideBounds(region, type)) {
                isStructure = true; // too big for a single object — split into a structure
            }

            Path objectPath = ObjectUtils.getObjectFolderPath(global ? null : preset.getPresetFolder());
            File existing = type.getObjectFilePathFromName(objectName, objectPath).toFile();
            if (!overwrite && existing.exists()) {
                src.sendFailure(Component.literal("\"" + objectName + "." + type.getType()
                    + "\" already exists. Add the -o flag to overwrite."));
                return 0;
            }

            StructuredCustomObject template = loadTemplate(type, preset.getFolderName(), templateName);
            if (template == null) {
                src.sendFailure(Component.literal("Failed to load template \"" + templateName + "\"."));
                return 0;
            }

            Corner min = region.getMin();
            Corner max = region.getMax();
            Corner center = region.getCenter() != null ? region.getCenter()
                : new Corner((max.x() - min.x()) / 2 + min.x(), Math.min(min.y(), max.y()), (max.z() - min.z()) / 2 + min.z());

            FabricWorldGenRegion worldGenRegion = ObjectUtils.getWorldGenRegion(preset, level, gen);

            StructuredCustomObject object = ObjectCreator.create(
                type, min, max, center, null, objectName, includeAir, isStructure, false, objectPath,
                worldGenRegion, new FabricNBTHelper(), null, template.getConfig(),
                preset.getFolderName(), OTG.getEngine().getOTGRootFolder(),
                OTG.getEngine().getCustomObjectManager(), OTGMaterialReader.get(),
                OTG.getEngine().getCustomObjectResourcesManager(), OTG.getEngine().getModLoadedChecker());

            if (object == null) {
                src.sendFailure(Component.literal("Failed to create " + type.getType() + " \"" + objectName + "\"."));
                return 0;
            }

            // Register so it can be used immediately without a reload.
            if (global) {
                OTG.getEngine().getCustomObjectManager().registerGlobalObject(object, object.getConfig().getFile());
            } else {
                OTG.getEngine().getCustomObjectManager().getGlobalObjects().addObjectToPreset(
                    preset.getFolderName(), object.getName().toLowerCase(Locale.ROOT),
                    object.getConfig().getFile(), object);
            }
            src.sendSuccess(() -> Component.literal("Exported " + type.getType() + " \"" + objectName
                + "\" to " + object.getConfig().getFile().getPath()), false);
            return 1;
        } catch (Exception e) {
            src.sendFailure(Component.literal("Export failed, see the log for details."));
            OTG.getEngine().getLogger().log(LogLevel.ERROR, LogCategory.MAIN,
                "Export command error: " + e.getClass().getName() + " - " + e.getMessage());
            OTG.getEngine().getLogger().printStackTrace(LogLevel.ERROR, LogCategory.MAIN, e);
            return 0;
        }
    }

    /** Loads and enables the named template (its config supplies settings for the exported object). */
    private static StructuredCustomObject loadTemplate(ObjectType type, String presetFolderName, String templateName) {
        File templateFile = OTG.getEngine().getCustomObjectManager().getGlobalObjects()
            .getTemplateFileForPreset(presetFolderName, templateName, OTG.getEngine().getOTGRootFolder());

        CustomObjectLoader loader = OTG.getEngine().getCustomObjectManager()
            .getObjectLoaders().get(type.getType().toLowerCase(Locale.ROOT));
        if (loader == null) return null;

        StructuredCustomObject template = (StructuredCustomObject) loader.loadFromFile(
            templateName,
            templateFile != null ? templateFile : new File(type.getFileNameForTemplate(templateName)));

        if (template == null
            || !template.onEnable(presetFolderName, OTG.getEngine().getOTGRootFolder())) {
            return null;
        }
        return template;
    }

    private static SuggestionProvider<CommandSourceStack> presetSuggestions() {
        return (ctx, builder) -> {
            builder.suggest("global");
            return SharedSuggestionProvider.suggest(
                OTG.getEngine().getPresetLoader().getAllPresets().stream()
                    .map(Preset::getFolderName), builder);
        };
    }

    private ExportCommand() {}
}
