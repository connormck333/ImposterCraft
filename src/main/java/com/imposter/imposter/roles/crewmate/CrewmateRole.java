package com.imposter.imposter.roles.crewmate;

import com.imposter.imposter.roles.Role;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CrewmateRole extends Role {

    private final CrewmateRoleEnum crewmateRole;

    public CrewmateRole(UUID uuid, String title, String description, CrewmateRoleEnum crewmateRole) {
        super(uuid, title, description);
        this.crewmateRole = crewmateRole;
    }
}
