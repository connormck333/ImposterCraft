package com.imposter.imposter.managers.players.roles;

import com.imposter.imposter.instances.Arena;
import com.imposter.imposter.roles.Role;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.UUID;

import static com.imposter.imposter.utils.Messages.sendMessageToPlayer;

public class RolesManager {

    private final Arena arena;
    private final HashMap<UUID, Role> roles;

    public RolesManager(Arena arena) {
        this.arena = arena;
        this.roles = new HashMap<>();
    }

    public Role getRole(UUID uuid) {
        return roles.get(uuid);
    }

    protected Arena arena() {
        return arena;
    }

    protected Role getRole(Class<? extends Role> roleClass) {
        for (UUID uuid : roles.keySet()) {
            Role role = getRole(uuid);
            if (roleClass.isInstance(role)) {
                return role;
            }
        }

        return null;
    }

    protected void setRole(UUID uuid, Role role) {
        if (role != null) {
            roles.put(uuid, role);
        }
    }

    protected void sendPlayerRoleMessage(UUID uuid, Role role, boolean imposter) {
        sendMessageToPlayer(uuid, "You are the " + getChatColor(imposter) + role.title());
        sendMessageToPlayer(uuid, role.description());
    }

    private ChatColor getChatColor(boolean imposter) {
        return imposter ? ChatColor.RED : ChatColor.GREEN;
    }
}
