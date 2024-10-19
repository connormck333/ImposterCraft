package com.imposter.imposter.roles.crewmate;

public enum CrewmateRoleEnum {
    MAYOR("mayor"),
    ENGINEER("engineer"),
    SHERIFF("sheriff"),
    DEPUTY("deputy"),
    LIGHTER("lighter"),
    PROTECTOR("protector");

    private final String role;

    CrewmateRoleEnum(String role) {
        this.role = role;
    }

    public String getRole() {
        return this.role;
    }

    public static CrewmateRoleEnum fromString(String role) {
        for (CrewmateRoleEnum crewmateRole : CrewmateRoleEnum.values()) {
            if (crewmateRole.getRole().equalsIgnoreCase(role)) {
                return crewmateRole;
            }
        }
        throw new IllegalArgumentException("No enum constant for role: " + role);
    }
}
