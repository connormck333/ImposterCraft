package com.imposter.imposter.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import static com.imposter.imposter.utils.PermissionUtils.doesSenderHavePermissions;

public class HelpCommand {

    private final CommandSender sender;

    public HelpCommand(CommandSender sender) {
        this.sender = sender;
    }

    private void sendTitle(int pageNum) {
        sender.sendMessage(ChatColor.YELLOW + "+------- " + ChatColor.WHITE + "Imposter Help: " + pageNum + ChatColor.YELLOW + " -------+");
    }

    private void sendLine(String command, String message) {
        sender.sendMessage(ChatColor.GOLD + "/imposter " + command + ": " + ChatColor.WHITE + message);
    }

    private void sendAdminPage1() {
        sendLine("create", "Create a new arena");
        sendLine("start", "Start the arena you are currently in");
        sendLine("start <arena Id>", "Start game for an arena");
    }

    private void sendAdminPage2() {
        sendLine("end <arena Id>", "End the game for an arena");
        sendLine("lobby {arena Id / 'main'}", "Set the lobby spawn point for an arena");
        sendLine("spawn <arena Id>", "Set a spawn point for an arena");
        sendLine("reset <arena Id>", "Reset an arena");
        sendLine("task <task Id> <arena Id>", "Create a new task location");
        sendLine("meeting <arena Id>", "Create the emergency meeting button location");
    }

    private void sendAdminPage3() {
        sendLine("door <item Id> <door category> <arena Id>", "Set a door location for an arena. Use /imposter wand to set the location");
        sendLine("vent <vent category> <arena Id>", "Create a new vent location");
        sendLine("cameras create <arena Id>", "Create the cameras join sign");
        sendLine("cameras set <arena Id>", "Set a camera location");
        sendLine("wand", "Get the ImposterCraft Wand");
    }

    public void doCommand(String pageNumStr) {
        int pageNum = getPageNum(pageNumStr);
        sendTitle(pageNum);

        if (pageNum == 1) {
            sendLine("list", "List all arenas");
            sendLine("join <arena Id>", "Join an arena");
            sendLine("leave", "Leave the arena you are currently in.");
        }

        if (doesSenderHavePermissions(sender)) {
            switch (pageNum) {
                case 1:
                    sendAdminPage1();
                    break;
                case 2:
                    sendAdminPage2();
                    break;
                case 3:
                    sendAdminPage3();
                    break;
                default:
                    break;
            }
        }
    }

    private int getPageNum(String pageNum) {
        try {
            return Integer.parseInt(pageNum);
        } catch (Exception e) {
            return 1;
        }
    }
}
