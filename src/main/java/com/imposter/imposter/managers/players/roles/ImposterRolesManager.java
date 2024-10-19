package com.imposter.imposter.managers.players.roles;

import com.imposter.imposter.ImposterCraft;
import com.imposter.imposter.instances.Arena;
import com.imposter.imposter.roles.Role;
import com.imposter.imposter.roles.imposter.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

import static com.imposter.imposter.utils.ConfigManager.getEnabledImposterRoles;
import static com.imposter.imposter.utils.Messages.sendTitleToPlayer;

public class ImposterRolesManager extends RolesManager {

    private final ImposterCraft imposterCraft;
    private final List<ImposterRoleEnum> enabledRoles;
    private final List<ImposterRoleEnum> availableImposterRoleEnums;

    public ImposterRolesManager(ImposterCraft imposterCraft, Arena arena) {
        super(arena);

        this.imposterCraft = imposterCraft;
        this.enabledRoles = getEnabledImposterRoles();
        this.availableImposterRoleEnums = new ArrayList<>(enabledRoles);
    }

    public BountyHunter bountyHunter() {
        return (BountyHunter) getRole(BountyHunter.class);
    }

    public Guesser guesser() {
        return (Guesser) getRole(Guesser.class);
    }

    public Bomber bomber() {
        return (Bomber) getRole(Bomber.class);
    }

    public Camouflager camouflager() {
        return (Camouflager) getRole(Camouflager.class);
    }

    public void assignPlayerRole(UUID uuid) {
        Random rand = new Random();

        if (!availableImposterRoleEnums.isEmpty()) {
            int roleIndex = rand.nextInt(availableImposterRoleEnums.size());
//            ImposterRoleEnum role = availableImposterRoleEnums.get(roleIndex);
            ImposterRoleEnum role = ImposterRoleEnum.GUESSER;
            availableImposterRoleEnums.remove(roleIndex);

            createPlayerRole(uuid, role);
        }
    }

    public void sendPlayerRoleMessage(Player player) {
        UUID uuid = player.getUniqueId();
        sendTitleToPlayer(player, ChatColor.RED + "Imposter", ChatColor.GRAY + "You are the imposter! Eliminate crewmates to win the game.", 60);
        Role role = getRole(uuid);
        if (role != null) {
            sendPlayerRoleMessage(uuid, role, true);
        }
    }

    public void setupInventoryForRole(UUID uuid) {
        getRole(uuid).setup();
    }

    private void createPlayerRole(UUID uuid, ImposterRoleEnum role) {
        switch (role) {
            case GUESSER -> setRole(uuid, new Guesser(arena(), uuid));
            case BOUNTY_HUNTER -> setRole(uuid, new BountyHunter(arena(), uuid));
            case BOMBER -> setRole(uuid, new Bomber(uuid));
            case JANITOR -> setRole(uuid, new Janitor(uuid));
            case CAMOUFLAGER -> setRole(uuid, new Camouflager(imposterCraft, arena(), uuid));
        }
    }
}
