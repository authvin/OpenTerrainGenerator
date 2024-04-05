package com.pg85.otg.config.settings.preset;

import com.pg85.otg.config.io.SettingsMap;
import com.pg85.otg.config.standard.PresetStandardValues;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GameRuleSettings {
    private final boolean overrideGameRules;
    private final boolean doFireTick;
    private final boolean mobGriefing;
    private final boolean keepInventory;
    private final boolean doMobSpawning;
    private final boolean doMobLoot;
    private final boolean doTileDrops;
    private final boolean doEntityDrops;
    private final boolean commandBlockOutput;
    private final boolean naturalRegeneration;
    private final boolean doDaylightCycle;
    private final boolean logAdminCommands;
    private final boolean showDeathMessages;
    private final int randomTickSpeed;
    private final boolean sendCommandFeedback;
    private final boolean spectatorsGenerateChunks;
    private final int spawnRadius;
    private final boolean disableElytraMovementCheck;
    private final int maxEntityCramming;
    private final boolean doWeatherCycle;
    private final boolean doLimitedCrafting;
    private final int maxCommandChainLength;
    private final boolean announceAdvancements;
    private final boolean disableRaids;
    private final boolean doInsomnia;
    private final boolean drowningDamage;
    private final boolean fallDamage;
    private final boolean fireDamage;
    private final boolean doPatrolSpawning;
    private final boolean doTraderSpawning;
    private final boolean forgiveDeadPlayers;
    private final boolean universalAnger;

    public static GameRuleSettings getGameRuleSettings(SettingsMap reader) {
        var gameRuleSettingsBuilder = builder();

        gameRuleSettingsBuilder.overrideGameRules(reader.getSetting(PresetStandardValues.OVERRIDE_GAME_RULES));
        gameRuleSettingsBuilder.doFireTick(reader.getSetting(PresetStandardValues.DO_FIRE_TICK));
        gameRuleSettingsBuilder.mobGriefing(reader.getSetting(PresetStandardValues.MOB_GRIEFING));
        gameRuleSettingsBuilder.keepInventory(reader.getSetting(PresetStandardValues.KEEP_INVENTORY));
        gameRuleSettingsBuilder.doMobSpawning(reader.getSetting(PresetStandardValues.DO_MOB_SPAWNING));
        gameRuleSettingsBuilder.doMobLoot(reader.getSetting(PresetStandardValues.DO_MOB_LOOT));
        gameRuleSettingsBuilder.doTileDrops(reader.getSetting(PresetStandardValues.DO_TILE_DROPS));
        gameRuleSettingsBuilder.doEntityDrops(reader.getSetting(PresetStandardValues.DO_ENTITY_DROPS));
        gameRuleSettingsBuilder.commandBlockOutput(reader.getSetting(PresetStandardValues.COMMAND_BLOCK_OUTPUT));
        gameRuleSettingsBuilder.naturalRegeneration(reader.getSetting(PresetStandardValues.NATURAL_REGENERATION));
        gameRuleSettingsBuilder.doDaylightCycle(reader.getSetting(PresetStandardValues.DO_DAY_LIGHT_CYCLE));
        gameRuleSettingsBuilder.logAdminCommands(reader.getSetting(PresetStandardValues.LOG_ADMIN_COMMANDS));
        gameRuleSettingsBuilder.showDeathMessages(reader.getSetting(PresetStandardValues.SHOW_DEATH_MESSAGES));
        gameRuleSettingsBuilder.randomTickSpeed(reader.getSetting(PresetStandardValues.RANDOM_TICK_SPEED));
        gameRuleSettingsBuilder.sendCommandFeedback(reader.getSetting(PresetStandardValues.SEND_COMMAND_FEEDBACK));
        gameRuleSettingsBuilder.spectatorsGenerateChunks(reader.getSetting(PresetStandardValues.SPECTATORS_GENERATE_CHUNKS));
        gameRuleSettingsBuilder.spawnRadius(reader.getSetting(PresetStandardValues.SPAWN_RADIUS));
        gameRuleSettingsBuilder.disableElytraMovementCheck(reader.getSetting(PresetStandardValues.DISABLE_ELYTRA_MOVEMENT_CHECK));
        gameRuleSettingsBuilder.maxEntityCramming(reader.getSetting(PresetStandardValues.MAX_ENTITY_CRAMMING));
        gameRuleSettingsBuilder.doWeatherCycle(reader.getSetting(PresetStandardValues.DO_WEATHER_CYCLE));
        gameRuleSettingsBuilder.doLimitedCrafting(reader.getSetting(PresetStandardValues.DO_LIMITED_CRAFTING));
        gameRuleSettingsBuilder.maxCommandChainLength(reader.getSetting(PresetStandardValues.MAX_COMMAND_CHAIN_LENGTH));
        gameRuleSettingsBuilder.announceAdvancements(reader.getSetting(PresetStandardValues.ANNOUNCE_ADVANCEMENTS));
        gameRuleSettingsBuilder.disableRaids(reader.getSetting(PresetStandardValues.DISABLE_RAIDS));
        gameRuleSettingsBuilder.doInsomnia(reader.getSetting(PresetStandardValues.DO_INSOMNIA));
        gameRuleSettingsBuilder.drowningDamage(reader.getSetting(PresetStandardValues.DROWNING_DAMAGE));
        gameRuleSettingsBuilder.fallDamage(reader.getSetting(PresetStandardValues.FALL_DAMAGE));
        gameRuleSettingsBuilder.fireDamage(reader.getSetting(PresetStandardValues.FIRE_DAMAGE));
        gameRuleSettingsBuilder.doPatrolSpawning(reader.getSetting(PresetStandardValues.DO_PATROL_SPAWNING));
        gameRuleSettingsBuilder.doTraderSpawning(reader.getSetting(PresetStandardValues.DO_TRADER_SPAWNING));
        gameRuleSettingsBuilder.forgiveDeadPlayers(reader.getSetting(PresetStandardValues.FORGIVE_DEAD_PLAYERS));
        gameRuleSettingsBuilder.universalAnger(reader.getSetting(PresetStandardValues.UNIVERSAL_ANGER));
        return gameRuleSettingsBuilder.build();
    }
}