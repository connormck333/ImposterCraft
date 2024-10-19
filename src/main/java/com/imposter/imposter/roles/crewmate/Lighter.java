package com.imposter.imposter.roles.crewmate;

import java.util.UUID;

public class Lighter extends CrewmateRole {

    private static final String TITLE = "Lighter";
    private static final String DESCRIPTION = "You can see in the dark!";

    public Lighter(UUID uuid) {
        super(uuid, TITLE, DESCRIPTION, CrewmateRoleEnum.LIGHTER);
    }
}
