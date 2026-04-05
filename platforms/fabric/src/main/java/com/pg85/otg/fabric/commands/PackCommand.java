package com.pg85.otg.fabric.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.pg85.otg.OTG;
import com.pg85.otg.customobject.BOPackExporter;
import com.pg85.otg.customobject.structures.CustomStructureCache;
import com.pg85.otg.fabric.gen.FabricWorldGenRegion;
import com.pg85.otg.fabric.gen.OTGFabricChunkGenerator;
import com.pg85.otg.presets.Preset;
import com.pg85.otg.util.gen.OTGWorldInfo;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.storage.LevelResource;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

final class PackCommand
{
	private static final AtomicBoolean isRunning = new AtomicBoolean(false);
	private static final AtomicBoolean isDone = new AtomicBoolean(false);
	private static final AtomicInteger current = new AtomicInteger(0);
	private static final AtomicInteger total = new AtomicInteger(0);
	private static final AtomicReference<String> currentName = new AtomicReference<>("");

	static LiteralArgumentBuilder<CommandSourceStack> register()
	{
		return Commands.literal("pack")
			.executes(PackCommand::execute);
	}

	private static int execute(CommandContext<CommandSourceStack> ctx)
	{
		CommandSourceStack src = ctx.getSource();

		if(!CommandHelper.requireOTGWorld(ctx))
		{
			return 0;
		}

		OTGFabricChunkGenerator gen = CommandHelper.getGenerator(src.getLevel());
		if(gen == null)
		{
			return 0;
		}

		Preset preset = gen.getPreset();

		if(isRunning.get())
		{
			if(isDone.get())
			{
				isRunning.set(false);
				isDone.set(false);
				src.sendSuccess(() -> Component.literal("OTG pack completed."), false);
			} else {
				int c = current.get();
				int t = total.get();
				String name = currentName.get();
				src.sendSuccess(
					() -> Component.literal("OTG pack is running: "
						+ (c == 0 ? "initializing structure starts (" + name + ")" : c + "/" + t + " — " + name)),
					false);
			}
			return 1;
		}

		isRunning.set(true);
		isDone.set(false);
		current.set(0);
		total.set(0);
		currentName.set("");

		src.sendSuccess(() -> Component.literal("Exporting .bopack files — run this command again to check progress."), false);

		ServerLevel level = src.getLevel();
		Path worldSaveFolder = level.getServer().getWorldPath(LevelResource.PLAYER_DATA_DIR).getParent();

		// Capture a world gen region at chunk (0,0) for structure start initialization.
		// minimumSize calculations only need the seed and world borders — no actual block reads happen.
		ChunkAccess chunk = level.getChunk(0, 0);
		OTGWorldInfo otgWorldInfo = new OTGWorldInfo(level.getMinBuildHeight(), level.getMaxBuildHeight());
		FabricWorldGenRegion worldGenRegion = new FabricWorldGenRegion(
			preset.getFolderName(),
			OTG.getEngine().getPluginConfig(),
			preset.getPresetConfig(),
			otgWorldInfo,
			level,
			chunk,
			gen
		);
		CustomStructureCache structureCache = gen.getStructureCache(worldSaveFolder);

		new Thread(() ->
		{
			BOPackExporter.exportPreset(
				preset,
				structureCache,
				worldGenRegion,
				OTG.getEngine().getOTGRootFolder(),
					(c, t, name) ->
				{
					current.set(c);
					total.set(t);
					currentName.set(name);
				}
			);
			isDone.set(true);
		}, "OTGPack").start();

		return 1;
	}
}
