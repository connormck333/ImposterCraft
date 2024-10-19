package com.imposter.imposter.roles.crewmate;

import java.util.UUID;

public class Mayor extends CrewmateRole {

    private static final String TITLE = "Mayor";
    private static final String DESCRIPTION = "Your vote counts twice!";

    public Mayor(UUID mayor) {
        super(mayor, TITLE, DESCRIPTION, CrewmateRoleEnum.MAYOR);
    }
}
