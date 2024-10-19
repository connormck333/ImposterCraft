package com.imposter.imposter.instances.locations;

import org.bukkit.Location;

public class TaskLocation extends BaseLocation {

    private final Location location;
    private final String command;
    private int id;

    public TaskLocation(Location location, String command) {
        this.location = location;
        this.command = command;
    }

    public TaskLocation(Location location, String command, int id) {
        this(location, command);
        this.id = id;
    }

    public String getCommand() {
        return this.command;
    }

    public Location getLocation() {
        return this.location;
    }

    @Override
    public int getId() {
        return this.id;
    }
}
