package com.imposter.imposter.roles.imposter;

import com.imposter.imposter.ImposterCraft;
import com.imposter.imposter.instances.Arena;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

import static com.imposter.imposter.utils.Constants.CAMOUFLAGE_ITEM;
import static com.imposter.imposter.utils.GuiUtils.getMeta;
import static com.imposter.imposter.utils.Messages.sendGreenMessageToPlayer;
import static com.imposter.imposter.utils.Messages.sendWaitForCooldownMessage;
import static com.imposter.imposter.utils.Utils.getRemainingCooldown;

public class Camouflager extends ImposterRole {

    private static final String TITLE = "Camouflager";
    private static final String DESCRIPTION = "Turn invisible for a limited amount of time!";

    private final ImposterCraft imposterCraft;
    private final Arena arena;
    private Long camouflagerLastUse;

    public Camouflager(ImposterCraft imposterCraft, Arena arena, UUID camouflager) {
        super(camouflager, TITLE, DESCRIPTION, ImposterRoleEnum.BOUNTY_HUNTER);

        this.imposterCraft = imposterCraft;
        this.arena = arena;
    }

    @Override
    public void setup() {
        Player player = Bukkit.getPlayer(getPlayer());
        if (player == null) {
            return;
        }

        ItemStack hideItem = new ItemStack(Material.ENDER_EYE);
        hideItem.setItemMeta(getMeta(hideItem, CAMOUFLAGE_ITEM, ChatColor.LIGHT_PURPLE + "Right click to turn invisible"));

        player.getInventory().setItem(1, hideItem);
    }

    public void camouflage(Player player) {
        if (!is(player.getUniqueId())) {
            return;
        }
        int camouflageCooldownRemaining = getRemainingCooldown(camouflagerLastUse);
        if (camouflageCooldownRemaining < arena.getSabotageManager().getSabotageCooldown()) {
            sendWaitForCooldownMessage(player, camouflageCooldownRemaining);
            return;
        }
        camouflagerLastUse = System.currentTimeMillis();

        arena.getPlayerManager().setPlayerInvisible(player, true);
        arena.getPlayerManager().removePlayerArmor(player);
        Bukkit.getScheduler().runTaskLater(imposterCraft, () -> {
            arena.getPlayerManager().setPlayerInvisible(player, false);
            arena.getPlayerManager().returnPlayerArmorAndWeapons(player);
        }, 200L);

        sendGreenMessageToPlayer(player, "You are invisible for 10 seconds!");
    }
}
