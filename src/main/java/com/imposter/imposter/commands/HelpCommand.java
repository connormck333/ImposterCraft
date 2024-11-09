package com.imposter.imposter.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import static com.imposter.imposter.utils.PermissionUtils.doesSenderHavePermissions;

public class HelpCommand {

    private final CommandSender sender;

    public HelpCommand(CommandSender sender) {
        this.sender = sender;
    }

    private void sendTitle() {
        sender.sendMessage(ChatColor.YELLOW + "+------- " + ChatColor.WHITE + "Imposter Help" + ChatColor.YELLOW + " -------+");
    }

    private void sendLine(String command, String message) {
        sender.sendMessage(ChatColor.GOLD + "/imposter " + command + ": " + ChatColor.WHITE + message);
    }

    public void doCommand() {
        sendTitle();

        sendLine("list", "List all arenas");
        sendLine("join <arena Id>", "Join an arena");
        sendLine("leave", "Leave the arena you are currently in.");

        if (doesSenderHavePermissions(sender)) {
            sendLine("create", "Create a new arena");
            sendLine("lobby {arena Id / 'main'}", "Set the lobby spawn point for an arena");
            sendLine("spawn <arena Id>", "Set a spawn point for an arena");
            sendLine("reset <arena Id>", "Reset an arena");
            sendLine("task <task Id> <arena Id>", "Create a new task location");
            sendLine("meeting <arena Id>", "Create the emergency meeting button location");
            sendLine("door <item Id> <door category> <arena Id>", "Set a door location for an arena. Use /imposter wand to set the location");
            sendLine("vent <vent category> <arena Id>", "Create a new vent location");
            sendLine("cameras create <arena Id>", "Create the cameras join sign");
            sendLine("cameras set <arena Id>", "Set a camera location");
            sendLine("wand", "Get the ImposterCraft Wand");
        }
    }
}
