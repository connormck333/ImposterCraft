package com.imposter.imposter.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Messages {

    public static void sendMessageToPlayer(Player player, String message) {
        player.sendMessage(message);
    }

    public static void sendMessageToPlayer(UUID uuid, String message) {
        Bukkit.getPlayer(uuid).sendMessage(message);
    }

    public static void sendRedMessageToPlayer(Player player, String message) {
        sendMessageToPlayer(player,ChatColor.RED + message);
    }
    public static void sendGreenMessageToPlayer(Player player, String message) {
        sendMessageToPlayer(player,ChatColor.GREEN + message);
    }

    public static void sendTitleToPlayer(Player player, String title, String subtitle, int duration) {
        player.sendTitle(title, subtitle, 4, duration, 4);
    }

    public static void sendTitleToPlayer(UUID uuid, String title, String subtitle, int duration) {
        Bukkit.getPlayer(uuid).sendTitle(title, subtitle, 4, duration, 4);
    }

    public static void sendInvalidPermsMessageToPlayer(Player player) {
        sendRedMessageToPlayer(player, "You do not have permissions to use this command!");
    }

    public static void sendInvalidArenaIdMessage(Player player) {
        sendRedMessageToPlayer(player, "Invalid arena id!");
    }

    public static void sendUnableMessageToPlayer(Player player) {
        sendRedMessageToPlayer(player, "You cannot do this now!");
    }

    public static void sendSabotageNotAvailableMessage(Player player) {
        sendRedMessageToPlayer(player, "Sabotage is unavailable right now!");
    }

    public static void sendHandcuffMessage(Player player) {
        sendRedMessageToPlayer(player, "You were handcuffed!");
    }

    public static void sendProtectedMessage(Player player) {
        sendRedMessageToPlayer(player, "This player was protected, you could not kill them!");
    }

    public static void sendWaitForCooldownMessage(Player player, int millisecondsRemaining) {
        int secondsRemaining = millisecondsRemaining / 1000;
        sendRedMessageToPlayer(player, "You must wait " + secondsRemaining + " second" + (secondsRemaining != 1 ? "s" : "") + " before trying this!");
    }

}
