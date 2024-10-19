package com.imposter.imposter.tasks;

import com.imposter.imposter.utils.Tasks;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.Random;

public class NavigationTask extends PlayerTask {

    private int centerSlot = 31;
    private int[] circleSlots = { 12, 13, 14, 20, 29, 38, 48, 49, 50, 24, 33, 42 };

    public static final String NAVIGATION_TASK_TITLE = ChatColor.RED + ChatColor.BOLD.toString() + "Center navigation route";

    public NavigationTask(Player player) {
        super(player, NAVIGATION_TASK_TITLE, "Navigation route centered!", Tasks.NAVIGATION);
        setupGUI();
        super.addEmptySlots();
    }

    public int getCenterSlot() {
        return this.centerSlot;
    }

    private void setupGUI() {
        ItemStack currentRouteCenter = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
        currentRouteCenter.setItemMeta(getRouteMeta(currentRouteCenter));

        Random random = new Random();
        int middleSlot;
        do {
            middleSlot = random.nextInt(44 - 10) + 10;
        } while (middleSlot % 9 == 0 || middleSlot % 9 == 8 || middleSlot == centerSlot);

        ItemStack currentRouteItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        currentRouteItem.setItemMeta(getRouteMeta(currentRouteItem));
        int diff = middleSlot % 9;
        for (int i = diff; i > 0; i--) {
            super.getGui().setItem(middleSlot - i, currentRouteItem);
        }

        for (int i = middleSlot + 1; i < middleSlot + (9 - diff); i++) {
            super.getGui().setItem(i, currentRouteItem);
        }

        diff = middleSlot / 9;
        for (int i = diff; i > 0; i--) {
            super.getGui().setItem(middleSlot - (i * 9), currentRouteItem);
        }

        for (int i = 1; i < 6 - diff; i++) {
            super.getGui().setItem(middleSlot + (i * 9), currentRouteItem);
        }

        ItemStack centerItem = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta centerMeta = centerItem.getItemMeta();
        centerMeta.setDisplayName(ChatColor.GREEN + ChatColor.BOLD.toString() + "Center");
        centerMeta.setLore(Collections.singletonList(ChatColor.GOLD + "Click here to center the route!"));
        centerItem.setItemMeta(centerMeta);
        super.getGui().setItem(31, centerItem);

        setupCircle();

        super.getGui().setItem(middleSlot, currentRouteCenter);
    }

    private void setupCircle() {
        ItemStack circleItem = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta circleMeta = circleItem.getItemMeta();
        circleMeta.setDisplayName(ChatColor.RED + ChatColor.BOLD.toString() + "Route");
        circleItem.setItemMeta(circleMeta);

        for (int slot : circleSlots) {
            super.getGui().setItem(slot, circleItem);
        }
    }

    private ItemMeta getRouteMeta(ItemStack item) {
        ItemMeta routeMeta = item.getItemMeta();
        routeMeta.setDisplayName(ChatColor.RED + ChatColor.BOLD.toString() + "Current route");
        routeMeta.setLore(Collections.singletonList(ChatColor.GOLD + "Redirect the route by pressing the center button"));

        return routeMeta;
    }
}
