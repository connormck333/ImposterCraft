package com.imposter.imposter.managers.sabotages;

import com.imposter.imposter.ImposterCraft;
import com.imposter.imposter.instances.Arena;
import com.imposter.imposter.utils.ConfigManager;
import lombok.Getter;

public class SabotageManager {

    @Getter
    private final DoorManager doorManager;
    @Getter
    private final ReactorManager reactorManager;
    @Getter
    private final OxygenManager oxygenManager;
    @Getter
    private final LightsManager lightsManager;

    @Getter
    private final int sabotageCooldown;
    private Long sabotageLastUse;

    public SabotageManager(ImposterCraft imposterCraft, Arena arena) {
        this.doorManager = new DoorManager(arena.getId());
        this.reactorManager = new ReactorManager(imposterCraft, arena);
        this.oxygenManager = new OxygenManager(imposterCraft, arena);
        this.lightsManager = new LightsManager(arena);

        this.sabotageCooldown = ConfigManager.getSabotageCooldown() * 1000;
        this.sabotageLastUse = 0L;
    }

    public void setSabotageLastUse() {
        this.sabotageLastUse = System.currentTimeMillis();
    }

    public boolean isSabotageAvailable() {
        return isDoorSabotageAvailable() && doorManager.areAllDoorsClosed() && System.currentTimeMillis() - sabotageLastUse > sabotageCooldown;
    }

    public boolean isDoorSabotageAvailable() {
        return !lightsManager.isActive() && !reactorManager.isActive() && !oxygenManager.isActive();
    }

    public void cancelAllSabotages() {
        oxygenManager.cancel();
        reactorManager.cancel();
    }
}
