package com.pg85.otg.config.settings.preset;

import com.pg85.otg.config.io.SettingsMap;
import com.pg85.otg.config.settingType.Setting;
import com.pg85.otg.config.settingType.Settings;
import com.pg85.otg.config.settings.ConfigSection;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GameRuleSettings extends ConfigSection {
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

    public static final Setting<Boolean> OVERRIDE_GAME_RULES = Settings.booleanSetting(
            "OverrideGameRules", false,
            t -> ((GameRuleSettings) t).isOverrideGameRules()
    );
    public static final Setting<Boolean> DO_FIRE_TICK = Settings.booleanSetting(
            "DoFireTick", true,
            t -> ((GameRuleSettings) t).isDoFireTick()
    );
    public static final Setting<Boolean> MOB_GRIEFING = Settings.booleanSetting(
            "MobGriefing", true,
            t -> ((GameRuleSettings) t).isMobGriefing()
    );
    public static final Setting<Boolean> KEEP_INVENTORY = Settings.booleanSetting(
            "KeepInventory", false,
            t -> ((GameRuleSettings) t).isKeepInventory()
    );
    public static final Setting<Boolean> DO_MOB_SPAWNING = Settings.booleanSetting(
            "DoMobSpawning", true,
            t -> ((GameRuleSettings) t).isMobGriefing()
    );
    public static final Setting<Boolean> DO_MOB_LOOT = Settings.booleanSetting(
            "DoMobLoot", true,
            t -> ((GameRuleSettings) t).isDoMobLoot()
    );
    public static final Setting<Boolean> DO_TILE_DROPS = Settings.booleanSetting(
            "DoTileDrops", true,
            t -> ((GameRuleSettings) t).isDoTileDrops()
    );
    public static final Setting<Boolean> DO_ENTITY_DROPS = Settings.booleanSetting(
            "DoEntityDrops", true,
            t -> ((GameRuleSettings) t).isDoEntityDrops()
    );
    public static final Setting<Boolean> COMMAND_BLOCK_OUTPUT = Settings.booleanSetting(
            "CommandBlockOutput", true,
            t -> ((GameRuleSettings) t).isCommandBlockOutput()
    );
    public static final Setting<Boolean> NATURAL_REGENERATION = Settings.booleanSetting(
            "NaturalRegeneration", true,
            t -> ((GameRuleSettings) t).isNaturalRegeneration()
    );
    public static final Setting<Boolean> DO_DAY_LIGHT_CYCLE = Settings.booleanSetting(
            "DoDaylightCycle", true,
            t -> ((GameRuleSettings) t).isDoDaylightCycle()
    );
    public static final Setting<Boolean> LOG_ADMIN_COMMANDS = Settings.booleanSetting(
            "LogAdminCommands", true,
            t -> ((GameRuleSettings) t).isLogAdminCommands()
    );
    public static final Setting<Boolean> SHOW_DEATH_MESSAGES = Settings.booleanSetting(
            "ShowDeathMessages", true,
            t -> ((GameRuleSettings) t).isShowDeathMessages()
    );
    public static final Setting<Boolean> SEND_COMMAND_FEEDBACK = Settings.booleanSetting(
            "SendCommandFeedback", true,
            t -> ((GameRuleSettings) t).isSendCommandFeedback()
    );
    public static final Setting<Boolean> SPECTATORS_GENERATE_CHUNKS = Settings.booleanSetting(
            "SpectatorsGenerateChunks", true,
            t -> ((GameRuleSettings) t).isSpectatorsGenerateChunks()
    );
    public static final Setting<Boolean> DISABLE_ELYTRA_MOVEMENT_CHECK = Settings.booleanSetting(
            "DisableElytraMovementCheck", false,
            t -> ((GameRuleSettings) t).isDisableElytraMovementCheck()
    );
    public static final Setting<Boolean> DO_WEATHER_CYCLE = Settings.booleanSetting(
            "DoWeatherCycle", true,
            t -> ((GameRuleSettings) t).isDoWeatherCycle()
    );
    public static final Setting<Boolean> DO_LIMITED_CRAFTING = Settings.booleanSetting(
            "DoLimitedCrafting", false,
            t -> ((GameRuleSettings) t).isDoLimitedCrafting()
    );
    public static final Setting<Boolean> ANNOUNCE_ADVANCEMENTS = Settings.booleanSetting(
            "AnnounceAdvancements", true,
            t -> ((GameRuleSettings) t).isAnnounceAdvancements()
    );
    public static final Setting<Boolean> DISABLE_RAIDS = Settings.booleanSetting(
            "DisableRaids", false,
            t -> ((GameRuleSettings) t).isDisableRaids()
    );
    public static final Setting<Boolean> DO_INSOMNIA = Settings.booleanSetting(
            "DoInsomnia", true,
            t -> ((GameRuleSettings) t).isDoInsomnia()
    );
    public static final Setting<Boolean> DROWNING_DAMAGE = Settings.booleanSetting(
            "DrowningDamage", true,
            t -> ((GameRuleSettings) t).isDrowningDamage()
    );
    public static final Setting<Boolean> FALL_DAMAGE = Settings.booleanSetting(
            "FallDamage", true,
            t -> ((GameRuleSettings) t).isFallDamage()
    );
    public static final Setting<Boolean> FIRE_DAMAGE = Settings.booleanSetting(
            "FireDamage", true,
            t -> ((GameRuleSettings) t).isFireDamage()
    );
    public static final Setting<Boolean> DO_PATROL_SPAWNING = Settings.booleanSetting(
            "DoPatrolSpawning", true,
            t -> ((GameRuleSettings) t).isDoPatrolSpawning()
    );
    public static final Setting<Boolean> DO_TRADER_SPAWNING = Settings.booleanSetting(
            "DoTraderSpawning", true,
            t -> ((GameRuleSettings) t).isDoTraderSpawning()
    );
    public static final Setting<Boolean> FORGIVE_DEAD_PLAYERS = Settings.booleanSetting(
            "ForgiveDeadPlayers", true,
            t -> ((GameRuleSettings) t).isForgiveDeadPlayers()
    );
    public static final Setting<Boolean> UNIVERSAL_ANGER = Settings.booleanSetting(
            "UniversalAnger", false,
            t -> ((GameRuleSettings) t).isUniversalAnger()
    );
    public static final Setting<Integer> RANDOM_TICK_SPEED = Settings.intSetting(
            "RandomTickSpeed", 3, 0, Integer.MAX_VALUE,
            t -> ((GameRuleSettings) t).getRandomTickSpeed()
    );
    public static final Setting<Integer> SPAWN_RADIUS = Settings.intSetting(
            "SpawnRadius", 10, 0, Integer.MAX_VALUE,
            t -> ((GameRuleSettings) t).getSpawnRadius()
    );
    public static final Setting<Integer> MAX_ENTITY_CRAMMING = Settings.intSetting(
            "MaxEntityCramming", 24, 0, Integer.MAX_VALUE,
            t -> ((GameRuleSettings) t).getMaxEntityCramming()
    );
    public static final Setting<Integer> MAX_COMMAND_CHAIN_LENGTH = Settings.intSetting(
            "MaxCommandChainLength", 65536, 0, Integer.MAX_VALUE,
            t -> ((GameRuleSettings) t).getMaxCommandChainLength()
    );

    public static GameRuleSettings getGameRuleSettings(SettingsMap reader) {
        var gameRuleSettingsBuilder = builder();

        gameRuleSettingsBuilder.overrideGameRules(reader.getSetting(OVERRIDE_GAME_RULES));
        gameRuleSettingsBuilder.doFireTick(reader.getSetting(DO_FIRE_TICK));
        gameRuleSettingsBuilder.mobGriefing(reader.getSetting(MOB_GRIEFING));
        gameRuleSettingsBuilder.keepInventory(reader.getSetting(KEEP_INVENTORY));
        gameRuleSettingsBuilder.doMobSpawning(reader.getSetting(DO_MOB_SPAWNING));
        gameRuleSettingsBuilder.doMobLoot(reader.getSetting(DO_MOB_LOOT));
        gameRuleSettingsBuilder.doTileDrops(reader.getSetting(DO_TILE_DROPS));
        gameRuleSettingsBuilder.doEntityDrops(reader.getSetting(DO_ENTITY_DROPS));
        gameRuleSettingsBuilder.commandBlockOutput(reader.getSetting(COMMAND_BLOCK_OUTPUT));
        gameRuleSettingsBuilder.naturalRegeneration(reader.getSetting(NATURAL_REGENERATION));
        gameRuleSettingsBuilder.doDaylightCycle(reader.getSetting(DO_DAY_LIGHT_CYCLE));
        gameRuleSettingsBuilder.logAdminCommands(reader.getSetting(LOG_ADMIN_COMMANDS));
        gameRuleSettingsBuilder.showDeathMessages(reader.getSetting(SHOW_DEATH_MESSAGES));
        gameRuleSettingsBuilder.randomTickSpeed(reader.getSetting(RANDOM_TICK_SPEED));
        gameRuleSettingsBuilder.sendCommandFeedback(reader.getSetting(SEND_COMMAND_FEEDBACK));
        gameRuleSettingsBuilder.spectatorsGenerateChunks(reader.getSetting(SPECTATORS_GENERATE_CHUNKS));
        gameRuleSettingsBuilder.spawnRadius(reader.getSetting(SPAWN_RADIUS));
        gameRuleSettingsBuilder.disableElytraMovementCheck(reader.getSetting(DISABLE_ELYTRA_MOVEMENT_CHECK));
        gameRuleSettingsBuilder.maxEntityCramming(reader.getSetting(MAX_ENTITY_CRAMMING));
        gameRuleSettingsBuilder.doWeatherCycle(reader.getSetting(DO_WEATHER_CYCLE));
        gameRuleSettingsBuilder.doLimitedCrafting(reader.getSetting(DO_LIMITED_CRAFTING));
        gameRuleSettingsBuilder.maxCommandChainLength(reader.getSetting(MAX_COMMAND_CHAIN_LENGTH));
        gameRuleSettingsBuilder.announceAdvancements(reader.getSetting(ANNOUNCE_ADVANCEMENTS));
        gameRuleSettingsBuilder.disableRaids(reader.getSetting(DISABLE_RAIDS));
        gameRuleSettingsBuilder.doInsomnia(reader.getSetting(DO_INSOMNIA));
        gameRuleSettingsBuilder.drowningDamage(reader.getSetting(DROWNING_DAMAGE));
        gameRuleSettingsBuilder.fallDamage(reader.getSetting(FALL_DAMAGE));
        gameRuleSettingsBuilder.fireDamage(reader.getSetting(FIRE_DAMAGE));
        gameRuleSettingsBuilder.doPatrolSpawning(reader.getSetting(DO_PATROL_SPAWNING));
        gameRuleSettingsBuilder.doTraderSpawning(reader.getSetting(DO_TRADER_SPAWNING));
        gameRuleSettingsBuilder.forgiveDeadPlayers(reader.getSetting(FORGIVE_DEAD_PLAYERS));
        gameRuleSettingsBuilder.universalAnger(reader.getSetting(UNIVERSAL_ANGER));
        return gameRuleSettingsBuilder.build();
    }

    @Override
    public String getSectionName() {
        return "Game Rule Settings";
    }
}