package com.imposter.imposter.managers.mechanics;

import com.imposter.imposter.instances.Arena;
import com.imposter.imposter.instances.CorpseEntity;
import com.imposter.imposter.instances.PlayerCameras;
import com.imposter.imposter.managers.players.PlayerManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static com.imposter.imposter.utils.ConfigManager.getArenaCameraLocations;
import static com.imposter.imposter.utils.ConfigManager.getCameraJoinLocation;
import static com.imposter.imposter.utils.Constants.CAMERAS_EXIT_TITLE;
import static com.imposter.imposter.utils.Constants.CAMERAS_NEXT_TITLE;
import static com.imposter.imposter.utils.GuiUtils.getMeta;
import static com.imposter.imposter.utils.Utils.locationEquals;

public class CamerasManager {

    private final Arena arena;

    private Location camerasJoinLocation;
    private final List<Location> camerasLocations;
    private final Map<UUID, PlayerCameras> playersOnCameras;
    private final List<CorpseEntity> corpsesOnCameras;

    public CamerasManager(Arena arena) {
        this.arena = arena;

        this.camerasJoinLocation = getCameraJoinLocation(arena.getId());
        this.camerasLocations = getArenaCameraLocations(arena.getId());
        this.playersOnCameras = new HashMap<>();
        this.corpsesOnCameras = new ArrayList<>();
    }

    public void playerEnterCameras(Player player) {
        PlayerManager playerManager = arena.getPlayerManager();
        playerManager.setPlayerInvisible(player, true);
        playerManager.removePlayerArmor(player);
        playerManager.clearHotbar(player);
        arena.getCamerasManager().givePlayerCameraItems(player);

        Location playerLocation = player.getLocation();
        CorpseEntity corpse = arena.getCorpseManager().createCorpse(player, playerLocation, true);
        PlayerCameras playerCameras = new PlayerCameras(playerLocation, corpse);
        playersOnCameras.put(player.getUniqueId(), playerCameras);

        Location cameraLocation = camerasLocations.getFirst();
        player.teleport(cameraLocation);
    }

    public void playerExitCameras(Player player) {
        arena.getPlayerManager().setPlayerInvisible(player, false);
        arena.getPlayerManager().returnPlayerArmorAndWeapons(player);

        // Remove fake player from camera room
        UUID uuid = player.getUniqueId();
        PlayerCameras playerCameras = playersOnCameras.get(uuid);
        CorpseEntity corpseEntity = playerCameras.getCorpseEntity();
        arena.getCorpseManager().removeCorpse(corpseEntity.getId());

        corpsesOnCameras.remove(corpseEntity);
        playersOnCameras.remove(uuid);

        player.teleport(playerCameras.getLocationBeforeCameras());
    }

    public void playerNextCamera(Player player) {
        PlayerCameras playerCameras = playersOnCameras.get(player.getUniqueId());
        int currentIndex = playerCameras.getCurrentCameraIndex();

        int nextIndex;
        if (currentIndex == camerasLocations.size() - 1) {
            nextIndex = 0;
        } else {
            nextIndex = currentIndex + 1;
        }

        Location nextLocation = camerasLocations.get(nextIndex);
        playerCameras.setCurrentCameraIndex(nextIndex);
        player.teleport(nextLocation);
    }

    public void addCameraJoinLocation(Location location) {
        camerasJoinLocation = location;
    }

    public void addCameraLocation(Location location) {
        camerasLocations.add(location);
    }

    public void addCorpseOnCameras(CorpseEntity corpseEntity) {
        corpsesOnCameras.add(corpseEntity);
    }

    public boolean isPlayerOnCameras(UUID uuid) {
        return playersOnCameras.get(uuid) != null;
    }

    public boolean isLocationCamerasJoinLocation(Location location) {
        return camerasJoinLocation != null && locationEquals(location, camerasJoinLocation);
    }

    public CorpseEntity getEntityOnCameras(int entityId) {
        for (CorpseEntity corpse : this.corpsesOnCameras) {
            if (corpse.getId() == entityId) {
                return corpse;
            }
        }

        return null;
    }

    public void givePlayerCameraItems(Player player) {
        player.getInventory().setItem(0, createCameraNextItem());
        player.getInventory().setItem(1, createCameraExitItem());
    }

    private ItemStack createCameraNextItem() {
        ItemStack item = new ItemStack(Material.ENDER_PEARL);
        item.setItemMeta(getMeta(item, CAMERAS_NEXT_TITLE));

        return item;
    }

    private ItemStack createCameraExitItem() {
        ItemStack item = new ItemStack(Material.IRON_TRAPDOOR);
        item.setItemMeta(getMeta(item, CAMERAS_EXIT_TITLE));

        return item;
    }
}
