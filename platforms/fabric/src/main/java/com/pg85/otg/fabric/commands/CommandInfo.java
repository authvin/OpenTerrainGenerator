package com.pg85.otg.fabric.commands;

/**
 * Self-describing metadata for an OTG subcommand. Kept local to each command class
 * (as a {@code static final INFO} field) so the same text drives both the help
 * listing and the usage shown when a command is invoked incorrectly.
 */
record CommandInfo(String name, String description, String usage) {}
