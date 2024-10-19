package com.imposter.imposter.managers.players;

import com.imposter.imposter.ImposterCraft;
import com.imposter.imposter.instances.Arena;
import com.imposter.imposter.managers.players.roles.ImposterRolesManager;
import com.imposter.imposter.sabotages.ImposterItems;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class ImposterManager {

    private Arena arena;

    private final ImposterRolesManager rolesManager;
    private final ImposterItems imposterItems;

    public ImposterManager(ImposterCraft imposterCraft, Arena arena) {
        this.arena = arena;

        this.rolesManager = new ImposterRolesManager(imposterCraft, arena);
        this.imposterItems = new ImposterItems();
    }

    public List<UUID> selectImposters() {
        int numImposters = arena.calculateNumImposters();
        int size = arena.getPlayers().size();
        List<UUID> imposters = new ArrayList<>();

        for (int i = 0; i < numImposters; i++) {
            // Select imposter
            Random rand = new Random();
            UUID newImposter;
            do {
                newImposter = arena.getPlayers().get(rand.nextInt(size));
            } while (imposters.contains(newImposter));
            imposters.add(newImposter);

            rolesManager.assignPlayerRole(newImposter);
        }

        return imposters;
    }

    public void prepareImposters(boolean sendMessage) {
        for (UUID uuid : arena.getImposters()) {
            Player player = Bukkit.getPlayer(uuid);

            if (player == null) {
                continue;
            }

            if (sendMessage) {
                rolesManager.sendPlayerRoleMessage(player);
            }

            giveImposterWeapons(player);
        }
    }

    public void giveImposterWeapons(Player player) {
        player.getInventory().setItem(0, imposterItems.getWeapon());
        player.getInventory().setItem(3, imposterItems.getLightSwitch());
        player.getInventory().setItem(4, imposterItems.getOxygenTrigger());
        player.getInventory().setItem(5, imposterItems.getReactorTrigger());
        player.getInventory().setItem(8, imposterItems.getDoorBook());
        rolesManager.setupInventoryForRole(player.getUniqueId());
    }

    public ImposterRolesManager rolesManager() {
        return rolesManager;
    }
}
