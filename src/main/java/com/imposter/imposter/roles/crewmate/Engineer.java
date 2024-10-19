package com.imposter.imposter.roles.crewmate;

import java.util.UUID;

public class Engineer extends CrewmateRole {

    private static final String TITLE = "Engineer";
    private static final String DESCRIPTION = "You can vent!";

    public Engineer(UUID uuid) {
        super(uuid, TITLE, DESCRIPTION, CrewmateRoleEnum.ENGINEER);
    }
}
