package com.imposter.imposter.roles.crewmate;

import com.imposter.imposter.instances.Arena;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

import static com.imposter.imposter.utils.Constants.PROTECTOR_ITEM;
import static com.imposter.imposter.utils.Cooldowns.PROTECTION_DURATION_MILLIS;
import static com.imposter.imposter.utils.GuiUtils.getMeta;
import static com.imposter.imposter.utils.Messages.*;
import static com.imposter.imposter.utils.Utils.getRemainingCooldown;

public class Protector extends CrewmateRole {

    private static final String TITLE = "Protector";
    private static final String DESCRIPTION = "Shield players from being killed!";

    private final Arena arena;
    private final HashMap<UUID, Long> protectedPlayers;
    private Long protectorLastUse;

    public Protector(Arena arena, UUID protector) {
        super(protector, TITLE, DESCRIPTION, CrewmateRoleEnum.PROTECTOR);

        this.arena = arena;
        this.protectedPlayers = new HashMap<>();
    }

    public boolean isPlayerProtected(UUID uuid) {
        Long protectedTime = protectedPlayers.get(uuid);
        if (protectedTime == null) {
            return false;
        }

        return System.currentTimeMillis() - protectedTime <= PROTECTION_DURATION_MILLIS;
    }

    public void protectPlayer(UUID protector, UUID uuid) {
        if (!is(protector)) {
            return;
        }
        int protectorCooldownRemaining = getRemainingCooldown(protectorLastUse);
        if (protectorCooldownRemaining < arena.getSabotageManager().getSabotageCooldown()) {
            sendWaitForCooldownMessage(Bukkit.getPlayer(protector), protectorCooldownRemaining);
            return;
        }
        protectorLastUse = System.currentTimeMillis();

        protectedPlayers.put(uuid, System.currentTimeMillis());
        String protectedPlayer = Bukkit.getPlayer(uuid).getName();
        sendMessageToPlayer(protector, ChatColor.GREEN + "You protected " + arena.getPlayerManager().getPlayerColor(uuid).getChatColor() + protectedPlayer);
    }

    @Override
    public void setup() {
        Player player = Bukkit.getPlayer(getPlayer());
        if (player == null) {
            return;
        }

        ItemStack item = new ItemStack(Material.TURTLE_HELMET);
        item.setItemMeta(getMeta(item, PROTECTOR_ITEM, ChatColor.LIGHT_PURPLE + "Protect players to stop them being killed!"));

        player.getInventory().setItem(0, item);
    }
}
