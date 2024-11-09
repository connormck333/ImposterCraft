package com.imposter.imposter.roles.imposter;

import com.imposter.imposter.roles.Role;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ImposterRole extends Role {

    private final ImposterRoleEnum imposterRole;

    public ImposterRole(UUID uuid, String title, String description, ImposterRoleEnum imposterRole) {
        super(uuid, title, description);
        this.imposterRole = imposterRole;
    }
}
