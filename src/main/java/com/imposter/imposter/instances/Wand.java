package com.imposter.imposter.instances;

import com.imposter.imposter.ImposterCraft;
import com.imposter.imposter.instances.locations.DoorLocation;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.imposter.imposter.utils.ConfigManager.saveArenaDoorLocation;
import static com.imposter.imposter.utils.Messages.*;

public class Wand {

    private static ImposterCraft imposterCraft;
    private static HashMap<UUID, List<Location>> playerWandClicks;

    public static final String WAND_TITLE = ChatColor.LIGHT_PURPLE + "Wand";

    public static void setupWand(ImposterCraft imposterCraft) {
        Wand.imposterCraft = imposterCraft;
        Wand.playerWandClicks = new HashMap<>();
    }

    public static ItemStack getWand() {
        ItemStack wand = new ItemStack(Material.WOODEN_HOE);
        ItemMeta meta = wand.getItemMeta();
        meta.setDisplayName(WAND_TITLE);
        wand.setItemMeta(meta);

        return wand;
    }

    public static void handleWandRightClick(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) {
            return;
        }
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        Location location = e.getClickedBlock().getLocation();

        List<Location> locations = playerWandClicks.get(uuid);
        if (locations == null || locations.size() >= 2) {
            locations = new ArrayList<>();
        }
        locations.add(location);
        playerWandClicks.put(uuid, locations);
        sendGreenMessageToPlayer(player, "Location " + (locations.size()) + " set.");
    }

    public static void createDoor(Player player, String item, String arenaIdStr, String title) {
        int arenaId;
        try {
            arenaId = Integer.parseInt(arenaIdStr);
        } catch (Exception e) {
            sendInvalidArenaIdMessage(player);
            return;
        }

        Material material = Material.matchMaterial(item.toUpperCase());
        if (material == null) {
            sendRedMessageToPlayer(player, "Unknown item type");
            return;
        } else if (!material.isBlock()) {
            sendRedMessageToPlayer(player, "You must select a valid block!");
            return;
        }

        if (!imposterCraft.getArenaManager().doesArenaExist(arenaId)) {
            sendInvalidArenaIdMessage(player);
            return;
        }

        List<Location> doorLocations = playerWandClicks.get(player.getUniqueId());
        if (doorLocations == null || doorLocations.size() != 2) {
            sendRedMessageToPlayer(player, "You have not set 2 locations using the wand! Try /imposter wand");
            return;
        }

        int id = saveArenaDoorLocation(arenaId, doorLocations.get(0), doorLocations.get(1), item, title);
        if (id != -1) {
            imposterCraft.getArenaManager().getArena(arenaId).getDoorManager().addDoor(title, new DoorLocation(doorLocations.get(0), doorLocations.get(1), id, item, title));
            sendGreenMessageToPlayer(player, "Door" + id + "created in " + title + ".");
        } else {
            sendRedMessageToPlayer(player, "There was an error creating this door.");
        }
    }
}
