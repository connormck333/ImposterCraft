package com.imposter.imposter.commands;

import com.imposter.imposter.ImposterCraft;
import com.imposter.imposter.instances.Arena;
import com.imposter.imposter.utils.ConfigManager;
import com.imposter.imposter.utils.Tasks;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static com.imposter.imposter.instances.Wand.*;
import static com.imposter.imposter.utils.ConfigManager.*;
import static com.imposter.imposter.utils.Constants.*;
import static com.imposter.imposter.utils.GuiUtils.getMeta;
import static com.imposter.imposter.utils.Messages.*;
import static com.imposter.imposter.utils.PermissionUtils.doesPlayerHavePermissions;

public class ImposterCommand implements CommandExecutor {

    private final ImposterCraft imposterCraft;

    public ImposterCommand(ImposterCraft imposterCraft) {
        this.imposterCraft = imposterCraft;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args[0].equalsIgnoreCase("help")) {
            new HelpCommand(sender).doCommand();
            return true;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("reset")) {
            if (sender instanceof Player player) {
                resetArena(player, args[1]);
            } else {
                int arenaId = -1;
                try {
                    arenaId = Integer.parseInt(args[1]);
                } catch (Exception ignored) {}
                imposterCraft.getArenaManager().resetArena(sender, arenaId);
            }

            return true;
        }

        if (sender instanceof Player player) {

            if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
                sendMessageToPlayer(player, ChatColor.GREEN + "These are the available arenas:");
                for (Arena arena : imposterCraft.getArenaManager().getArenas()) {
                    sendMessageToPlayer(player, ChatColor.GREEN + "- " + arena.getId() + "(" + arena.getState().name() + ")");
                }

            } else if (args.length == 1 && args[0].equalsIgnoreCase("leave")) {
                Arena arena = imposterCraft.getArenaManager().getArena(player);
                if (arena != null) {
                    sendMessageToPlayer(player, ChatColor.RED + "You left the arena.");
                    arena.getPlayerManager().removePlayer(player);
                } else {
                    sendMessageToPlayer(player, ChatColor.RED + "You are not in an arena!");
                }

            } else if (args.length == 2 && args[0].equalsIgnoreCase("join")) {
                int id;
                try {
                    id = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    sendMessageToPlayer(player, ChatColor.RED + "You specified an invalid arena ID!");
                    return true;
                }

                imposterCraft.getArenaManager().playerJoinArena(player, id);

            } else if (args.length == 1 && args[0].equalsIgnoreCase("create")) {
                createNewArena(player);
            } else if (args.length == 3 && args[0].equalsIgnoreCase("task")) {
                createTaskItem(player, args[1], args[2]);
            } else if (args.length == 2 && args[0].equalsIgnoreCase("spawn")) {
                saveArenaSpawn(player, args[1], false);
            } else if (args.length == 2 && args[0].equalsIgnoreCase("lobby")) {
                if (args[1].equalsIgnoreCase("main")) {
                    saveLobbySpawn(player);
                } else {
                    saveArenaSpawn(player, args[1], true);
                }
            } else if (args.length == 2 && args[0].equalsIgnoreCase("meeting")) {
                createMeetingItem(player, args[1]);
            } else if (args.length == 1 && args[0].equalsIgnoreCase("wand")) {
                givePlayerWand(player);
            } else if (args.length == 4 && args[0].equalsIgnoreCase("door")) {
                if (doesPlayerHavePermissions(player)) {
                    createDoor(player, args[1], args[3], args[2]);
                } else {
                    sendInvalidPermsMessageToPlayer(player);
                }
            } else if (args.length == 3 && args[0].equalsIgnoreCase("vent")) {
                createVentItem(player, args[1], args[2]);
            } else if (args.length == 3 && args[0].equalsIgnoreCase("cameras")) {
                if (args[1].equalsIgnoreCase("create")) {
                    createCameraJoinItem(player, args[2]);
                } else if (args[1].equalsIgnoreCase("set")) {
                    setCameraPosition(player, args[2]);
                } else {
                    invalidUsage(player);
                    return false;
                }
            } else {
                invalidUsage(player);
                return false;
            }
        }

        return true;
    }

    private void createNewArena(Player player) {
        if (!doesPlayerHavePermissions(player)) {
            sendInvalidPermsMessageToPlayer(player);
            return;
        }

        List<Arena> arenas = imposterCraft.getArenaManager().getArenas();
        int arenaId = 0;
        for (Arena arena : arenas) {
            if (arena.getId() > arenaId) {
                arenaId = arena.getId();
            }
        }
        arenaId++;

        ConfigManager.createNewArena(arenaId);
        imposterCraft.getArenaManager().createArena(arenaId, player.getLocation());

        sendMessageToPlayer(player, "Arena created! ID: " + arenaId);
    }

    private void createTaskItem(Player player, String command, String arenaId) {
        if (!doesPlayerHavePermissions(player)) {
            sendInvalidPermsMessageToPlayer(player);
            return;
        } else if (!imposterCraft.getArenaManager().doesArenaExist(Integer.parseInt(arenaId))) {
            sendInvalidArenaIdMessage(player);
            return;
        }

        Tasks commandTask;
        try {
            commandTask = Tasks.valueOf(command.toUpperCase());
        } catch (Exception e) {
            unknownTask(player);
            return;
        }

        ItemStack item = new ItemStack(Material.OAK_SIGN);
        item.setItemMeta(getMeta(item, TASK_SIGN_TITLE, commandTask + " " + arenaId));

        player.getInventory().addItem(item);
    }

    private void createMeetingItem(Player player, String arenaIdStr) {
        if (!doesPlayerHavePermissions(player)) {
            sendInvalidPermsMessageToPlayer(player);
        } else if (!imposterCraft.getArenaManager().doesArenaExist(Integer.parseInt(arenaIdStr))) {
            sendInvalidArenaIdMessage(player);
            return;
        }

        ItemStack item = new ItemStack(Material.OAK_SIGN);
        item.setItemMeta(getMeta(item, MEETING_SIGN_TITLE, arenaIdStr));
        player.getInventory().addItem(item);
    }

    private void createVentItem(Player player, String ventCategory, String arenaId) {
        if (!doesPlayerHavePermissions(player)) {
            sendInvalidPermsMessageToPlayer(player);
            return;
        } else if (!imposterCraft.getArenaManager().doesArenaExist(Integer.parseInt(arenaId))) {
            sendInvalidArenaIdMessage(player);
            return;
        }

        ItemStack item = new ItemStack(Material.IRON_TRAPDOOR);
        item.setItemMeta(getMeta(item, VENT_TRAPDOOR_TITLE, ventCategory.toLowerCase() + " " + arenaId));

        player.getInventory().addItem(item);
    }

    private void createCameraJoinItem(Player player, String arenaId) {
        if (!doesPlayerHavePermissions(player)) {
            sendInvalidPermsMessageToPlayer(player);
            return;
        } else if (!imposterCraft.getArenaManager().doesArenaExist(Integer.parseInt(arenaId))) {
            sendInvalidArenaIdMessage(player);
            return;
        }

        ItemStack item = new ItemStack(Material.OAK_SIGN);
        item.setItemMeta(getMeta(item, CAMERAS_ITEM_TITLE, arenaId));

        player.getInventory().addItem(item);
    }

    private void setCameraPosition(Player player, String arenaIdStr) {
        int arenaId;
        try {
            arenaId = Integer.parseInt(arenaIdStr);
        } catch (Exception e) {
            sendInvalidArenaIdMessage(player);
            return;
        }

        if (!doesPlayerHavePermissions(player)) {
            sendInvalidPermsMessageToPlayer(player);
            return;
        } else if (!imposterCraft.getArenaManager().doesArenaExist(arenaId)) {
            sendInvalidArenaIdMessage(player);
            return;
        }

        Location playerLocation = player.getLocation();
        imposterCraft.getArenaManager().getArena(arenaId).getCamerasManager().addCameraLocation(playerLocation);
        saveArenaCameraLocation(arenaId, playerLocation);

        sendGreenMessageToPlayer(player, "Camera position set! Make sure the player has something to stand on!");
    }

    private void saveArenaSpawn(Player player, String arenaIdStr, boolean lobbySpawn) {
        int arenaId;
        try {
            arenaId = Integer.parseInt(arenaIdStr);
        } catch (Exception e) {
            sendInvalidArenaIdMessage(player);
            return;
        }

        if (!doesPlayerHavePermissions(player)) {
            sendInvalidPermsMessageToPlayer(player);
            return;
        } else if (!imposterCraft.getArenaManager().doesArenaExist(arenaId)) {
            sendInvalidArenaIdMessage(player);
            return;
        }

        Location location = player.getLocation();
        if (lobbySpawn) {
            boolean success = saveArenaLobbyLocation(arenaId, location);
            if (!success) {
                sendRedMessageToPlayer(player, "There was an error saving spawn location.");
            } else {
                sendGreenMessageToPlayer(player, "Spawn location saved!");
                imposterCraft.getArenaManager().setLobbySpawn(arenaId, location);
            }
        } else {
            boolean success = saveArenaSpawnLocation(arenaId, location);
            if (!success) {
                sendRedMessageToPlayer(player, "There was an error saving spawn location.");
            } else {
                sendGreenMessageToPlayer(player, "Spawn location saved!");
                imposterCraft.getArenaManager().addSpawnToArena(arenaId, location);
            }
        }
    }

    private void saveLobbySpawn(Player player) {
        if (!doesPlayerHavePermissions(player)) {
            sendInvalidPermsMessageToPlayer(player);
            return;
        }

        Location location = player.getLocation();

        boolean success = saveMainLobbyLocation(location);
        if (!success) {
            sendRedMessageToPlayer(player, "There was an error saving main lobby location.");
            return;
        }

        sendGreenMessageToPlayer(player, "Main lobby location saved!");
        imposterCraft.getArenaManager().setMainLobbySpawn(location);
    }

    private void resetArena(Player player, String arenaIdStr) {
        int arenaId;
        try {
            arenaId = Integer.parseInt(arenaIdStr);
        } catch (Exception e) {
            sendInvalidArenaIdMessage(player);
            return;
        }

        if (!doesPlayerHavePermissions(player)) {
            sendInvalidPermsMessageToPlayer(player);
            return;
        }

        imposterCraft.getArenaManager().resetArena(player, arenaId);
    }

    private void givePlayerWand(Player player) {
        if (!doesPlayerHavePermissions(player)) {
            return;
        }

        player.getInventory().addItem(getWand());
    }

    private void invalidUsage(Player player) {
        sendRedMessageToPlayer(player, "Invalid usage!");
    }

    private void unknownTask(Player player) {
        sendRedMessageToPlayer(player, "Task does not exist!");
    }
}
