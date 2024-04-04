package com.pg85.otg.settings.preset;

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
}