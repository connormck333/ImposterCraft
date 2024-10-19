package com.imposter.imposter.instances.locations;

import org.bukkit.Location;

public class VentLocation extends BaseLocation {

    private final Location location;
    private final String category;
    private int id;
    private int currentVentIndex;

    public VentLocation(Location location, String category) {
        this.location = location;
        this.category = category;
    }

    public VentLocation(Location location, String category, int id) {
        this(location, category);
        this.id = id;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    public String getCategory() {
        return this.category;
    }
}
