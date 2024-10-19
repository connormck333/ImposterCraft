package com.imposter.imposter.managers.players;

import com.imposter.imposter.instances.Arena;
import com.imposter.imposter.managers.players.roles.CrewmateRolesManager;

public class CrewmateManager {

    private final CrewmateRolesManager rolesManager;

    public CrewmateManager(Arena arena) {
        this.rolesManager = new CrewmateRolesManager(arena);
    }

    public CrewmateRolesManager rolesManager() {
        return rolesManager;
    }
}
