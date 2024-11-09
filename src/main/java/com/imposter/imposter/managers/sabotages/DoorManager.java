package com.imposter.imposter.managers.sabotages;

import com.imposter.imposter.instances.locations.DoorLocation;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.imposter.imposter.utils.ConfigManager.getArenaDoorLocations;

public class DoorManager {

    @Getter
    private final Map<String, ArrayList<DoorLocation>> doorLocations;
    private final List<String> closedDoors;

    public DoorManager(int arenaId) {
        this.doorLocations = getArenaDoorLocations(arenaId);
        this.closedDoors = new ArrayList<>();
    }

    public void addDoor(String title, DoorLocation doorLocation) {
        if (this.doorLocations.containsKey(title)) {
            this.doorLocations.get(title).add(doorLocation);
        } else {
            ArrayList<DoorLocation> doorList = new ArrayList<>();
            doorList.add(doorLocation);
            this.doorLocations.put(title, doorList);
        }
    }

    public boolean closeDoor(String id) {
        if (closedDoors.contains(id) || !doorLocations.containsKey(id)) {
            return false;
        }

        for (DoorLocation doorLocation : doorLocations.get(id)) {
            fillDoor(doorLocation, false);
        }

        closedDoors.add(id);
        return true;
    }

    public void openDoor(String id) {
        for (DoorLocation doorLocation : doorLocations.get(id)) {
            fillDoor(doorLocation, true);
        }
        closedDoors.remove(id);
    }

    public boolean areAllDoorsClosed() {
        return closedDoors.isEmpty();
    }

    private void fillDoor(DoorLocation doorLocation, boolean openDoor) {
        Location loc1 = doorLocation.getPos1();
        Location loc2 = doorLocation.getPos2();
        Material material = openDoor ? Material.AIR : doorLocation.getMaterial();

        if (!Objects.equals(loc1.getWorld(), loc2.getWorld())) {
            throw new IllegalArgumentException("Locations must be in the same world!");
        }

        World world = loc1.getWorld();

        // Get the min and max x, y, z coordinates
        int minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());

        int maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
        int maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());

        // Loop through the coordinates within the bounding box
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    block.setType(material); // Set the block to the specified material
                }
            }
        }
    }
}
