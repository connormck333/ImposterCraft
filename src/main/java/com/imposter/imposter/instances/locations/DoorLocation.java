package com.imposter.imposter.instances.locations;

import org.bukkit.Location;
import org.bukkit.Material;

public class DoorLocation {

    private final Location pos1;
    private final Location pos2;
    private final int id;
    private final Material material;
    private final String title;

    public DoorLocation(Location pos1, Location pos2, int id, String material, String title) {
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.id = id;
        this.material = Material.matchMaterial(material);
        this.title = title;
    }

    public Location getPos1() {
        return pos1;
    }

    public Location getPos2() {
        return pos2;
    }

    public int getId() {
        return id;
    }

    public Material getMaterial() {
        return material;
    }

    public String getTitle() {
        return title;
    }
}
