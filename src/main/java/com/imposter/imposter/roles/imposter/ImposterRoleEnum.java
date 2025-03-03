package com.imposter.imposter.roles.imposter;

import lombok.Getter;

@Getter
public enum ImposterRoleEnum {
    BOMBER("bomber"),
    JANITOR("janitor"),
    CAMOUFLAGER("camouflager"),
    BOUNTY_HUNTER("bounty_hunter"),
    GUESSER("guesser");

    private final String role;

    ImposterRoleEnum(String role) {
        this.role = role;
    }

    public static ImposterRoleEnum fromString(String role) {
        for (ImposterRoleEnum imposterRoleEnum : ImposterRoleEnum.values()) {
            if (imposterRoleEnum.getRole().equalsIgnoreCase(role)) {
                return imposterRoleEnum;
            }
        }
        throw new IllegalArgumentException("No enum constant for role: " + role);
    }
}
