package com.imposter.imposter.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import static com.imposter.imposter.utils.Messages.sendInvalidPermsMessageToPlayer;

public class PermissionUtils {

    private static final String ADMIN_PERMISSIONS = "imposter.admin";

    public static boolean doesPlayerHavePermissions(Player player) {
        if (player.isOp() || player.hasPermission(ADMIN_PERMISSIONS)) {
            return true;
        }

        sendInvalidPermsMessageToPlayer(player);
        return false;
    }

    public static boolean doesSenderHavePermissions(CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) {
            return true;
        }

        if (sender instanceof Player player) {
            return doesPlayerHavePermissions(player);
        }

        return false;
    }
}
