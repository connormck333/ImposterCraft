package com.imposter.imposter.roles.imposter;

import com.imposter.imposter.roles.Role;

import java.util.UUID;

public class ImposterRole extends Role {

    private ImposterRoleEnum imposterRole;

    public ImposterRole(UUID uuid, String title, String description, ImposterRoleEnum imposterRole) {
        super(uuid, title, description);
        this.imposterRole = imposterRole;
    }

    public ImposterRoleEnum getImposterRole() {
        return imposterRole;
    }
}
