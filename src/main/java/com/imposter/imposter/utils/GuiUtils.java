package com.imposter.imposter.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static com.imposter.imposter.utils.Colors.getColoredConcrete;
import static com.imposter.imposter.utils.Constants.*;

public class GuiUtils {

    private static final Material[] WOOL_MATERIALS = {
            Material.WHITE_WOOL, Material.ORANGE_WOOL, Material.MAGENTA_WOOL,
            Material.LIGHT_BLUE_WOOL, Material.YELLOW_WOOL, Material.LIME_WOOL,
            Material.PINK_WOOL, Material.GRAY_WOOL, Material.LIGHT_GRAY_WOOL,
            Material.CYAN_WOOL, Material.PURPLE_WOOL, Material.BLUE_WOOL,
            Material.BROWN_WOOL, Material.GREEN_WOOL, Material.RED_WOOL,
            Material.BLACK_WOOL
    };

    public static ItemStack getExitButton() {
        ItemStack item = new ItemStack(Material.BARRIER);
        item.setItemMeta(getMeta(item, EXIT_BTN_TITLE));

        return item;
    }

    public static ItemStack getInfoButton(String title, String info) {
        ItemStack item = new ItemStack(Material.BOOK);
        item.setItemMeta(getMeta(
                item,
                ChatColor.YELLOW + ChatColor.BOLD.toString() + title,
                ChatColor.GOLD + info
        ));

        return item;
    }

    public static ItemStack getRandomWool(String title, String lore) {
        return getRandomWool(title, Collections.singletonList(lore));
    }

    public static ItemStack getRandomWool(String title, List<String> lore) {
        Random random = new Random();
        ItemStack item = new ItemStack(WOOL_MATERIALS[random.nextInt(WOOL_MATERIALS.length)]);
        item.setItemMeta(getMeta(item, title, lore));

        return item;
    }

    public static boolean isWool(Material material) {
        for (Material wool : WOOL_MATERIALS) {
            if (material == wool) {
                return true;
            }
        }

        return false;
    }

    public static ItemStack getNextVentItem() {
        ItemStack item = new ItemStack(Material.ENDER_PEARL);
        item.setItemMeta(getMeta(item, VENT_NEXT_ITEM_TITLE, ChatColor.LIGHT_PURPLE + "Click to see next vent"));

        return item;
    }

    public static ItemStack getExitVentItem() {
        ItemStack item = new ItemStack(Material.IRON_TRAPDOOR);
        item.setItemMeta(getMeta(item, VENT_EXIT_ITEM_TITLE, ChatColor.LIGHT_PURPLE + "Click to exit the vent"));

        return item;
    }

    public static ItemStack getPlayerHead(Player p, Colors color) {
        ItemStack item = new ItemStack(getColoredConcrete(color));
        item.setItemMeta(getMeta(item, p.getDisplayName()));

        return item;
    }

    public static ItemStack getSkipButton() {
        ItemStack item = new ItemStack(Material.ARROW);
        item.setItemMeta(getMeta(item, SKIP_BTN_TITLE));

        return item;
    }

    public static ItemMeta getMeta(ItemStack item, String title) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(title);
        return meta;
    }

    public static ItemMeta getMeta(ItemStack item, String title, String info) {
        ItemMeta meta = getMeta(item, title);
        meta.setLore(Collections.singletonList(info));
        return meta;
    }

    public static ItemMeta getMeta(ItemStack item, String title, List<String> lore) {
        ItemMeta meta = getMeta(item, title);
        meta.setLore(lore);
        return meta;
    }

}
