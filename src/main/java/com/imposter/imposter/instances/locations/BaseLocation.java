package com.imposter.imposter.instances.locations;

import org.bukkit.Location;

import static com.imposter.imposter.utils.Utils.locationEquals;

public abstract class BaseLocation {

    public abstract int getId();

    public abstract Location getLocation();

    public boolean equals(Location loc1) {
        Location loc2 = getLocation();
        return locationEquals(loc1, loc2);
    }
}
