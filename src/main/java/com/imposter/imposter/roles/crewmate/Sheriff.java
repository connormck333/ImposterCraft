package com.imposter.imposter.roles.crewmate;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

import static com.imposter.imposter.utils.Constants.SHERIFF_ITEM;
import static com.imposter.imposter.utils.GuiUtils.getMeta;

public class Sheriff extends CrewmateRole {

    private static final String TITLE = "Sheriff";
    private static final String DESCRIPTION = "Shoot the imposter. Be careful not to shoot a crewmate!";

    public Sheriff(UUID sheriff) {
        super(sheriff, TITLE, DESCRIPTION, CrewmateRoleEnum.SHERIFF);
    }

    @Override
    public void setup() {
        Player player = Bukkit.getPlayer(getPlayer());
        if (player == null) {
            return;
        }

        ItemStack weapon = new ItemStack(Material.BLAZE_ROD);
        weapon.setItemMeta(getMeta(weapon, SHERIFF_ITEM, ChatColor.LIGHT_PURPLE + "Kill the imposters to win!"));

        player.getInventory().setItem(0, weapon);
    }
}
