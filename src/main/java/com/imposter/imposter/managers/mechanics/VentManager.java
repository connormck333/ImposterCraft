package com.imposter.imposter.managers.mechanics;

import com.imposter.imposter.instances.Arena;
import com.imposter.imposter.instances.locations.PlayerVentLocation;
import com.imposter.imposter.instances.locations.VentLocation;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

import static com.imposter.imposter.utils.ConfigManager.getArenaVentLocations;
import static com.imposter.imposter.utils.GuiUtils.getExitVentItem;
import static com.imposter.imposter.utils.GuiUtils.getNextVentItem;
import static com.imposter.imposter.utils.Utils.locationEquals;

public class VentManager {

    private final Arena arena;

    private final Map<String, ArrayList<VentLocation>> ventLocations;
    private final Map<UUID, PlayerVentLocation> currentlyVentedPlayers;

    public VentManager(Arena arena) {
        this.arena = arena;
        this.ventLocations = getArenaVentLocations(arena.getId());
        this.currentlyVentedPlayers = new HashMap<>();
    }

    public void addVentLocation(String key, VentLocation ventLocation) {
        if (this.ventLocations.containsKey(key)) {
            this.ventLocations.get(key).add(ventLocation);
        } else {
            ArrayList<VentLocation> ventList = new ArrayList<>();
            ventList.add(ventLocation);
            this.ventLocations.put(key, ventList);
        }
    }

    public void handleVentClick(Player player, Location ventLocation) {
        if (arena.isPlayerImposter(player) || (arena.getGame().engineer() != null && arena.getGame().engineer().is(player.getUniqueId()))) {
            String category = getVentCategoryFromLocation(ventLocation);
            if (category != null) {
                imposterVent(player, category, ventLocation);
            }
        }
    }

    private void imposterVent(Player player, String category, Location clickedVentLocation) {
        // Save vent category to player
        int index = getIndexOfVentLocationInCategory(category, clickedVentLocation);
        VentLocation ventLocation = ventLocations.get(category).get(index);
        currentlyVentedPlayers.put(player.getUniqueId(), new PlayerVentLocation(ventLocation, index));

        // Make player invisible
        arena.getPlayerManager().setPlayerInvisible(player, true);
        arena.getPlayerManager().removePlayerArmor(player);

        // Hover player over vent
        player.teleport(clickedVentLocation);

        // Clear inventory and give vent item
        player.getInventory().clear();
        player.getInventory().addItem(getNextVentItem());
        player.getInventory().addItem(getExitVentItem());
    }

    private String getVentCategoryFromLocation(Location location) {
        for (String key : ventLocations.keySet()) {
            for (VentLocation ventLocation : ventLocations.get(key)) {
                if (locationEquals(ventLocation.getLocation(), location)) {
                    return key;
                }
            }
        }

        return null;
    }

    private int getIndexOfVentLocationInCategory(String category, Location location) {
        for (int i = 0; i < ventLocations.get(category).size(); i++) {
            VentLocation ventLocation = ventLocations.get(category).get(i);
            if (ventLocation.equals(location)) {
                return i;
            }
        }

        return -1;
    }

    public boolean isPlayerInVent(UUID uuid) {
        return currentlyVentedPlayers.get(uuid) != null;
    }

    public PlayerVentLocation getPlayerVentCategory(UUID uuid) {
        return currentlyVentedPlayers.get(uuid);
    }

    public void playerExitVent(Player player) {
        currentlyVentedPlayers.remove(player.getUniqueId());
        arena.getPlayerManager().setPlayerInvisible(player, false);
        arena.getPlayerManager().returnPlayerArmorAndWeapons(player);
    }

    public List<VentLocation> getVentLocationsByCategory(String category) {
        return ventLocations.get(category);
    }

    public void removePlayerFromVent(UUID uuid) {
        currentlyVentedPlayers.remove(uuid);
    }
}
