package com.imposter.imposter.managers.players.roles;

import com.imposter.imposter.instances.Arena;
import com.imposter.imposter.roles.*;
import com.imposter.imposter.roles.crewmate.*;

import java.util.*;

import static com.imposter.imposter.utils.ConfigManager.getEnabledCrewmateRoles;

public class CrewmateRolesManager extends RolesManager {

    private final List<CrewmateRoleEnum> enabledRoles;
    private final List<CrewmateRoleEnum> availableCrewmateRoles;

    public CrewmateRolesManager(Arena arena) {
        super(arena);

        this.enabledRoles = getEnabledCrewmateRoles();
        this.availableCrewmateRoles = new ArrayList<>(enabledRoles);
    }

    public Protector protector() {
        return (Protector) getRole(Protector.class);
    }

    public Sheriff sheriff() {
        return (Sheriff) getRole(Sheriff.class);
    }

    public Engineer engineer() {
        return (Engineer) getRole(Engineer.class);
    }

    public Deputy deputy() {
        return (Deputy) getRole(Deputy.class);
    }

    public Mayor mayor() {
        return (Mayor) getRole(Mayor.class);
    }

    public Lighter lighter() {
        return (Lighter) getRole(Lighter.class);
    }

    public void assignPlayerRole(UUID uuid) {
        Random rand = new Random();

        if (!arena().isPlayerImposter(uuid) && !availableCrewmateRoles.isEmpty()) {
            int roleIndex = rand.nextInt(availableCrewmateRoles.size());
            CrewmateRoleEnum role = availableCrewmateRoles.get(roleIndex);
            availableCrewmateRoles.remove(roleIndex);

            Role newRole = createPlayerRole(uuid, role);
            sendPlayerRoleMessage(uuid, newRole, false);
            setupInventoryForRole(uuid);
        }
    }

    public void setupInventoryForRole(UUID uuid) {
        getRole(uuid).setup();
    }

    private Role createPlayerRole(UUID uuid, CrewmateRoleEnum role) {
        Role newRole = null;
        switch (role) {
            case PROTECTOR -> newRole = new Protector(arena(), uuid);
            case SHERIFF -> newRole = new Sheriff(uuid);
            case ENGINEER -> newRole = new Engineer(uuid);
            case DEPUTY -> newRole = new Deputy(arena(), uuid);
            case MAYOR -> newRole = new Mayor(uuid);
            case LIGHTER -> newRole = new Lighter(uuid);
        }

        setRole(uuid, newRole);
        return newRole;
    }
}
