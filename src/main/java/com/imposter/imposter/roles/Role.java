package com.imposter.imposter.roles;

import java.util.UUID;

public class Role {

    private final UUID uuid;

    private final String TITLE;
    private final String DESCRIPTION;


    public Role(UUID uuid, String title, String description) {
        this.uuid = uuid;

        this.TITLE = title;
        this.DESCRIPTION = description;
    }

    public boolean is(UUID playerUUID) {
        return playerUUID == uuid;
    }

    public String title() {
        return TITLE;
    }

    public String description() {
        return DESCRIPTION;
    }

    public UUID getPlayer() {
        return uuid;
    }

    public void setup() {}
}
