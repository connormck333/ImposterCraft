package com.imposter.imposter.instances;

import org.bukkit.Location;

public class PlayerCameras {

    private Location locationBeforeCameras;
    private CorpseEntity corpseEntity;
    private int currentCameraIndex;

    public PlayerCameras(Location locationBeforeCameras, CorpseEntity corpseEntity) {
        this.locationBeforeCameras = locationBeforeCameras;
        this.currentCameraIndex = 0;
        this.corpseEntity = corpseEntity;
    }

    public int getCurrentCameraIndex() {
        return currentCameraIndex;
    }

    public Location getLocationBeforeCameras() {
        return locationBeforeCameras;
    }

    public CorpseEntity getCorpseEntity() {
        return corpseEntity;
    }

    public void setCurrentCameraIndex(int nextIndex) {
        this.currentCameraIndex = nextIndex;
    }
}
