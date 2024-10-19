package com.imposter.imposter.roles.imposter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

import static com.imposter.imposter.utils.Constants.BOMB_ITEM;
import static com.imposter.imposter.utils.GuiUtils.getMeta;

public class Bomber extends ImposterRole {

    private static final String TITLE = "Bomber";
    private static final String DESCRIPTION = "Blow crewmates up!";

    public Bomber(UUID bomber) {
        super(bomber, TITLE, DESCRIPTION, ImposterRoleEnum.BOMBER);
    }

    @Override
    public void setup() {
        Player player = Bukkit.getPlayer(getPlayer());
        if (player == null) {
            return;
        }

        ItemStack explosiveItem = new ItemStack(Material.TNT);
        explosiveItem.setItemMeta(getMeta(explosiveItem, BOMB_ITEM, ChatColor.LIGHT_PURPLE + "Place this to explode crewmates"));

        player.getInventory().setItem(1, explosiveItem);
    }
}
