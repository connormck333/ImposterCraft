package com.imposter.imposter.roles.crewmate;

import com.imposter.imposter.instances.Arena;
import com.imposter.imposter.utils.Colors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

import static com.imposter.imposter.utils.Constants.DEPUTY_ITEM;
import static com.imposter.imposter.utils.Cooldowns.HANDCUFF_DURATION_MILLIS;
import static com.imposter.imposter.utils.GuiUtils.getMeta;
import static com.imposter.imposter.utils.Messages.sendMessageToPlayer;
import static com.imposter.imposter.utils.Messages.sendWaitForCooldownMessage;
import static com.imposter.imposter.utils.Utils.getRemainingCooldown;

public class Deputy extends CrewmateRole {

    private static final String TITLE = "Deputy";
    private static final String DESCRIPTION = "Handcuff players to stop them killing!";

    private final Arena arena;

    private final HashMap<UUID, Long> handcuffedPlayers;
    private Long deputyLastUse;

    public Deputy(Arena arena, UUID deputy) {
        super(deputy, TITLE, DESCRIPTION, CrewmateRoleEnum.DEPUTY);

        this.arena = arena;

        this.handcuffedPlayers = new HashMap<>();
    }

    public boolean isPlayerHandcuffed(UUID uuid) {
        Long handcuffedTime = handcuffedPlayers.get(uuid);
        if (handcuffedTime == null) {
            return false;
        }
        return System.currentTimeMillis() - handcuffedTime <= HANDCUFF_DURATION_MILLIS;
    }

    public void handcuffPlayer(UUID deputy, UUID uuid) {
        if (!is(deputy)) {
            return;
        }
        int deputyCooldownRemaining = getRemainingCooldown(deputyLastUse);
        if (deputyCooldownRemaining < arena.getSabotageManager().getSabotageCooldown()) {
            sendWaitForCooldownMessage(Bukkit.getPlayer(deputy), deputyCooldownRemaining);
            return;
        }
        deputyLastUse = System.currentTimeMillis();

        handcuffedPlayers.put(uuid, System.currentTimeMillis());
        String handcuffedPlayer = Bukkit.getPlayer(uuid).getName();
        sendMessageToPlayer(deputy, ChatColor.GREEN + "You handcuffed " + arena.getPlayerManager().getPlayerColor(uuid).getChatColor() + handcuffedPlayer);
    }

    @Override
    public void setup() {
        Player player = Bukkit.getPlayer(getPlayer());
        if (player == null) {
            return;
        }

        ItemStack handcuff = new ItemStack(Material.CHAIN);
        handcuff.setItemMeta(getMeta(handcuff, DEPUTY_ITEM, ChatColor.LIGHT_PURPLE + "Handcuff players to stop them killing!"));

        player.getInventory().setItem(0, handcuff);
    }
}
