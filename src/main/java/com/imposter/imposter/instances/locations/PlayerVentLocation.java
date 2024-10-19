package com.imposter.imposter.instances.locations;

public class PlayerVentLocation {

    private final VentLocation ventLocation;
    private int nextIndex;

    public PlayerVentLocation(VentLocation ventLocation, int nextIndex) {
        this.ventLocation = ventLocation;
        this.nextIndex = nextIndex;
    }

    public VentLocation getVentLocation() {
        return ventLocation;
    }

    public int getNextIndex() {
        return nextIndex;
    }

    public void setNextIndex(int nextIndex) {
        this.nextIndex = nextIndex;
    }
}
