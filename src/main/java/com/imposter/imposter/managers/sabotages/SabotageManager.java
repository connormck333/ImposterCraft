package com.imposter.imposter.managers.sabotages;

import com.imposter.imposter.ImposterCraft;
import com.imposter.imposter.instances.Arena;

public class SabotageManager {

    private Arena arena;

    private DoorManager doorManager;
    private final ReactorManager reactorManager;
    private final OxygenManager oxygenManager;
    private final LightsManager lightsManager;

    private final int sabotageCooldown;
    private Long sabotageLastUse;

    public SabotageManager(ImposterCraft imposterCraft, Arena arena) {
        this.arena = arena;

        this.doorManager = new DoorManager(arena.getId());
        this.reactorManager = new ReactorManager(imposterCraft, arena);
        this.oxygenManager = new OxygenManager(imposterCraft, arena);
        this.lightsManager = new LightsManager(arena);

        this.sabotageCooldown = getSabotageCooldown() * 1000;
        this.sabotageLastUse = 0L;
    }

    public DoorManager getDoorManager() {
        return doorManager;
    }

    public ReactorManager getReactorManager() {
        return reactorManager;
    }

    public OxygenManager getOxygenManager() {
        return oxygenManager;
    }

    public LightsManager getLightsManager() {
        return lightsManager;
    }

    public void setSabotageLastUse() {
        this.sabotageLastUse = System.currentTimeMillis();
    }

    public int getSabotageCooldown() {
        return sabotageCooldown;
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
