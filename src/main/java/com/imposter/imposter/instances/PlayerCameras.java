package com.imposter.imposter.instances;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

@Getter
public class PlayerCameras {

    private final Location locationBeforeCameras;
    private final CorpseEntity corpseEntity;
    @Setter
    private int currentCameraIndex;

    public PlayerCameras(Location locationBeforeCameras, CorpseEntity corpseEntity) {
        this.locationBeforeCameras = locationBeforeCameras;
        this.currentCameraIndex = 0;
        this.corpseEntity = corpseEntity;
    }

}
