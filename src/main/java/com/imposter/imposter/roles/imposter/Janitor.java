package com.imposter.imposter.roles.imposter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

import static com.imposter.imposter.utils.Constants.BRUSH_ITEM;
import static com.imposter.imposter.utils.GuiUtils.getMeta;

public class Janitor extends ImposterRole {

    private static final String TITLE = "Guesser";
    private static final String DESCRIPTION = "Clean up dead bodies!";

    public Janitor(UUID janitor) {
        super(janitor, TITLE, DESCRIPTION, ImposterRoleEnum.JANITOR);
    }

    @Override
    public void setup() {
        Player player = Bukkit.getPlayer(getPlayer());
        if (player == null) {
            return;
        }

        ItemStack brushItem = new ItemStack(Material.BRUSH);
        brushItem.setItemMeta(getMeta(brushItem, BRUSH_ITEM, ChatColor.LIGHT_PURPLE + "Right click bodies to hide them!"));

        player.getInventory().setItem(1, brushItem);
    }
}
