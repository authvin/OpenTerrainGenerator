package com.pg85.otg.forge.event;

import com.pg85.otg.config.dimensions.DimensionConfig;
import com.pg85.otg.constants.Constants;
import com.pg85.otg.forge.gen.OTGNoiseChunkGenerator;
import com.pg85.otg.config.settings.preset.PresetSettings;

import com.pg85.otg.config.settings.preset.GameRuleSettings;
import com.pg85.otg.config.settings.preset.SpawnSettings;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Dimension;
import net.minecraft.world.GameRules;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.world.SleepFinishedTimeEvent;
import net.minecraftforge.event.world.WorldEvent.CreateSpawnPosition;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

// Used for:
// - Allowing sleeping in OTG dimensions.
// - Setting overworld spawn point from PresetConfig.
// - Applying GameRules from dimensionconfig or worldconfig. 
@EventBusSubscriber(modid = Constants.MOD_ID_SHORT)
public class WorldHandler
{
	@SubscribeEvent
	public static void onSetSpawn(CreateSpawnPosition event)
	{		
		if(event.getWorld() instanceof ServerWorld)
		{
			// If a fixed spawn point is configured in the PresetConfig, apply it.
			PresetSettings presetConfig = null;
			if(((ServerWorld)event.getWorld()).getWorldServer().getChunkSource().generator instanceof OTGNoiseChunkGenerator)
			{
				presetConfig = ((OTGNoiseChunkGenerator)((ServerWorld)event.getWorld()).getWorldServer().getChunkSource().generator).getPreset().getPresetConfig(); 
				if(presetConfig.getSpawnSettings().isSpawnPointSet())
				{
					event.setCanceled(true);
					SpawnSettings spawnSettings = presetConfig.getSpawnSettings();
					((ServerWorld)event.getWorld()).getWorldServer().setDefaultSpawnPos(new BlockPos(spawnSettings.getSpawnPointX(), spawnSettings.getSpawnPointY(), spawnSettings.getSpawnPointZ()), spawnSettings.getSpawnPointAngle());
				}
			}
		
			// If a modpack config is being used, apply the configured gamerules (if any).
			// TODO: What about non-modpack dimension configs?
			DimensionConfig modpackConfig = DimensionConfig.fromDisk(Constants.MODPACK_CONFIG_NAME);
			if(modpackConfig != null && modpackConfig.GameRules != null)
			{
				GameRules gameRules = ((ServerWorld)event.getWorld()).getGameRules();
				// TODO: doImmediateRespawn
				gameRules.getRule(GameRules.RULE_DOFIRETICK).set(modpackConfig.GameRules.DoFireTick, (MinecraftServer)null);
				gameRules.getRule(GameRules.RULE_MOBGRIEFING).set(modpackConfig.GameRules.MobGriefing, (MinecraftServer)null);
				gameRules.getRule(GameRules.RULE_KEEPINVENTORY).set(modpackConfig.GameRules.KeepInventory, (MinecraftServer)null);
				gameRules.getRule(GameRules.RULE_DOMOBSPAWNING).set(modpackConfig.GameRules.DoMobSpawning, (MinecraftServer)null);
				gameRules.getRule(GameRules.RULE_DOMOBLOOT).set(modpackConfig.GameRules.DoMobLoot, (MinecraftServer)null);
				gameRules.getRule(GameRules.RULE_DOBLOCKDROPS).set(modpackConfig.GameRules.DoTileDrops, (MinecraftServer)null);
				gameRules.getRule(GameRules.RULE_DOENTITYDROPS).set(modpackConfig.GameRules.DoEntityDrops, (MinecraftServer)null);
				gameRules.getRule(GameRules.RULE_COMMANDBLOCKOUTPUT).set(modpackConfig.GameRules.CommandBlockOutput, (MinecraftServer)null);
				gameRules.getRule(GameRules.RULE_NATURAL_REGENERATION).set(modpackConfig.GameRules.NaturalRegeneration, (MinecraftServer)null);
				gameRules.getRule(GameRules.RULE_DAYLIGHT).set(modpackConfig.GameRules.DoDaylightCycle, (MinecraftServer)null);
				gameRules.getRule(GameRules.RULE_LOGADMINCOMMANDS).set(modpackConfig.GameRules.LogAdminCommands, (MinecraftServer)null);
				gameRules.getRule(GameRules.RULE_SHOWDEATHMESSAGES).set(modpackConfig.GameRules.ShowDeathMessages, (MinecraftServer)null);
				gameRules.getRule(GameRules.RULE_RANDOMTICKING).value = modpackConfig.GameRules.RandomTickSpeed;
				gameRules.getRule(GameRules.RULE_SENDCOMMANDFEEDBACK).set(modpackConfig.GameRules.SendCommandFeedback, (MinecraftServer)null);
				gameRules.getRule(GameRules.RULE_SPECTATORSGENERATECHUNKS).set(modpackConfig.GameRules.SpectatorsGenerateChunks, (MinecraftServer)null);
				gameRules.getRule(GameRules.RULE_SPAWN_RADIUS).value = modpackConfig.GameRules.SpawnRadius;
				gameRules.getRule(GameRules.RULE_DISABLE_ELYTRA_MOVEMENT_CHECK).set(modpackConfig.GameRules.DisableElytraMovementCheck, (MinecraftServer)null);
				gameRules.getRule(GameRules.RULE_MAX_ENTITY_CRAMMING).value = modpackConfig.GameRules.MaxEntityCramming;
				gameRules.getRule(GameRules.RULE_WEATHER_CYCLE).set(modpackConfig.GameRules.DoWeatherCycle, (MinecraftServer)null);
				gameRules.getRule(GameRules.RULE_LIMITED_CRAFTING).set(modpackConfig.GameRules.DoLimitedCrafting, (MinecraftServer)null);
				gameRules.getRule(GameRules.RULE_MAX_COMMAND_CHAIN_LENGTH).value = modpackConfig.GameRules.MaxCommandChainLength;
				gameRules.getRule(GameRules.RULE_ANNOUNCE_ADVANCEMENTS).set(modpackConfig.GameRules.AnnounceAdvancements, (MinecraftServer)null);
				gameRules.getRule(GameRules.RULE_DISABLE_RAIDS).set(modpackConfig.GameRules.DisableRaids, (MinecraftServer)null);
				gameRules.getRule(GameRules.RULE_DOINSOMNIA).set(modpackConfig.GameRules.DoInsomnia, (MinecraftServer)null);
				gameRules.getRule(GameRules.RULE_DROWNING_DAMAGE).set(modpackConfig.GameRules.DrowningDamage, (MinecraftServer)null);
				gameRules.getRule(GameRules.RULE_FALL_DAMAGE).set(modpackConfig.GameRules.FallDamage, (MinecraftServer)null);
				gameRules.getRule(GameRules.RULE_FIRE_DAMAGE).set(modpackConfig.GameRules.FireDamage, (MinecraftServer)null);
				gameRules.getRule(GameRules.RULE_DO_PATROL_SPAWNING).set(modpackConfig.GameRules.DoPatrolSpawning, (MinecraftServer)null);
				gameRules.getRule(GameRules.RULE_DO_TRADER_SPAWNING).set(modpackConfig.GameRules.DoTraderSpawning, (MinecraftServer)null);
				gameRules.getRule(GameRules.RULE_FORGIVE_DEAD_PLAYERS).set(modpackConfig.GameRules.ForgiveDeadPlayers, (MinecraftServer)null);
				gameRules.getRule(GameRules.RULE_UNIVERSAL_ANGER).set(modpackConfig.GameRules.UniversalAnger, (MinecraftServer)null);
			}
			else if(presetConfig != null && presetConfig.getGameRuleSettings().isOverrideGameRules())
			{
				GameRules gameRules = ((ServerWorld)event.getWorld()).getGameRules();
				// TODO: doImmediateRespawn
				GameRuleSettings gameRuleSettings = presetConfig.getGameRuleSettings();
				gameRules.getRule(GameRules.RULE_DOFIRETICK).set(gameRuleSettings.isDoFireTick(), (MinecraftServer)null);
				gameRules.getRule(GameRules.RULE_MOBGRIEFING).set(gameRuleSettings.isMobGriefing(), (MinecraftServer)null);
				gameRules.getRule(GameRules.RULE_KEEPINVENTORY).set(gameRuleSettings.isKeepInventory(), (MinecraftServer)null);
				gameRules.getRule(GameRules.RULE_DOMOBSPAWNING).set(gameRuleSettings.isDoMobSpawning(), (MinecraftServer)null);
				gameRules.getRule(GameRules.RULE_DOMOBLOOT).set(gameRuleSettings.isDoMobLoot(), (MinecraftServer)null);
				gameRules.getRule(GameRules.RULE_DOBLOCKDROPS).set(gameRuleSettings.isDoTileDrops(), (MinecraftServer)null);
				gameRules.getRule(GameRules.RULE_DOENTITYDROPS).set(gameRuleSettings.isDoEntityDrops(), (MinecraftServer)null);
				gameRules.getRule(GameRules.RULE_COMMANDBLOCKOUTPUT).set(gameRuleSettings.isCommandBlockOutput(), (MinecraftServer)null);
				gameRules.getRule(GameRules.RULE_NATURAL_REGENERATION).set(gameRuleSettings.isNaturalRegeneration(), (MinecraftServer)null);
				gameRules.getRule(GameRules.RULE_DAYLIGHT).set(gameRuleSettings.isDoDaylightCycle(), (MinecraftServer)null);
				gameRules.getRule(GameRules.RULE_LOGADMINCOMMANDS).set(gameRuleSettings.isLogAdminCommands(), (MinecraftServer)null);
				gameRules.getRule(GameRules.RULE_SHOWDEATHMESSAGES).set(gameRuleSettings.isShowDeathMessages(), (MinecraftServer)null);
				gameRules.getRule(GameRules.RULE_RANDOMTICKING).value = gameRuleSettings.getRandomTickSpeed();
				gameRules.getRule(GameRules.RULE_SENDCOMMANDFEEDBACK).set(gameRuleSettings.isSendCommandFeedback(), (MinecraftServer)null);
				gameRules.getRule(GameRules.RULE_SPECTATORSGENERATECHUNKS).set(gameRuleSettings.isSpectatorsGenerateChunks(), (MinecraftServer)null);
				gameRules.getRule(GameRules.RULE_SPAWN_RADIUS).value = gameRuleSettings.getSpawnRadius();
				gameRules.getRule(GameRules.RULE_DISABLE_ELYTRA_MOVEMENT_CHECK).set(gameRuleSettings.isDisableElytraMovementCheck(), (MinecraftServer)null);
				gameRules.getRule(GameRules.RULE_MAX_ENTITY_CRAMMING).value = gameRuleSettings.getMaxEntityCramming();
				gameRules.getRule(GameRules.RULE_WEATHER_CYCLE).set(gameRuleSettings.isDoWeatherCycle(), (MinecraftServer)null);
				gameRules.getRule(GameRules.RULE_LIMITED_CRAFTING).set(gameRuleSettings.isDoLimitedCrafting(), (MinecraftServer)null);
				gameRules.getRule(GameRules.RULE_MAX_COMMAND_CHAIN_LENGTH).value = gameRuleSettings.getMaxCommandChainLength();
				gameRules.getRule(GameRules.RULE_ANNOUNCE_ADVANCEMENTS).set(gameRuleSettings.isAnnounceAdvancements(), (MinecraftServer)null);
				gameRules.getRule(GameRules.RULE_DISABLE_RAIDS).set(gameRuleSettings.isDisableRaids(), (MinecraftServer)null);
				gameRules.getRule(GameRules.RULE_DOINSOMNIA).set(gameRuleSettings.isDoInsomnia(), (MinecraftServer)null);
				gameRules.getRule(GameRules.RULE_DROWNING_DAMAGE).set(gameRuleSettings.isDrowningDamage(), (MinecraftServer)null);
				gameRules.getRule(GameRules.RULE_FALL_DAMAGE).set(gameRuleSettings.isFallDamage(), (MinecraftServer)null);
				gameRules.getRule(GameRules.RULE_FIRE_DAMAGE).set(gameRuleSettings.isFireDamage(), (MinecraftServer)null);
				gameRules.getRule(GameRules.RULE_DO_PATROL_SPAWNING).set(gameRuleSettings.isDoPatrolSpawning(), (MinecraftServer)null);
				gameRules.getRule(GameRules.RULE_DO_TRADER_SPAWNING).set(gameRuleSettings.isDoTraderSpawning(), (MinecraftServer)null);
				gameRules.getRule(GameRules.RULE_FORGIVE_DEAD_PLAYERS).set(gameRuleSettings.isForgiveDeadPlayers(), (MinecraftServer)null);
				gameRules.getRule(GameRules.RULE_UNIVERSAL_ANGER).set(gameRuleSettings.isUniversalAnger(), (MinecraftServer)null);
			}
		}
	}

	// Beds don't work in non-overworld dimensions since DerivedWorldInfo doesn't implement 
	// setDayTime (time is shared with the overworld, so can't tick more than one dim).
	// For non-overworld OTG dims, when players finish sleeping apply the new time to the 
	// overworld. 
	// TODO: Improve dimensions implementation, allow separate time/weather/gamerules per dim.
	@SubscribeEvent
	public static void onSleepFinished(SleepFinishedTimeEvent event)
	{
		if(event.getWorld() instanceof ServerWorld)
		{
			if(!((ServerWorld)event.getWorld()).dimension().location().equals(Dimension.OVERWORLD.location()))
			{
				ChunkGenerator chunkGenerator = ((ServerWorld)event.getWorld()).getChunkSource().generator;
				if(chunkGenerator instanceof OTGNoiseChunkGenerator)
				{
					((ServerWorld)event.getWorld()).getServer().overworld().setDayTime(event.getNewTime());
				}
			}
		}
	}
}
