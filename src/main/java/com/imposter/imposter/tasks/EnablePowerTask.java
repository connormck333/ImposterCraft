package com.imposter.imposter.tasks;

import com.imposter.imposter.ImposterCraft;
import com.imposter.imposter.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;

public class EnablePowerTask extends PlayerTask {

    private ImposterCraft imposterCraft;
    private ArrayList<Integer> switchSlots;

    public static final String ENABLE_POWER_TASK_TITLE = ChatColor.RED + ChatColor.BOLD.toString() + "Enable power";

    public EnablePowerTask(ImposterCraft imposterCraft, Player player) {
        super(player, ENABLE_POWER_TASK_TITLE, "Power enabled!", Tasks.ENABLE_POWER);
        this.imposterCraft = imposterCraft;

        this.switchSlots = new ArrayList<>();
        this.switchSlots.add(22);
        this.switchSlots.add(31);
        this.switchSlots.add(40);

        setupGui();
    }

    public boolean didClickSwitch(int slot) {
        if (switchSlots.contains(slot)) {
            setSwitchActive();
            Bukkit.getScheduler().runTaskLater(imposterCraft, new Runnable() {
                @Override
                public void run() {
                    EnablePowerTask.super.complete();
                }
            }, 20L);

            return true;
        }

        return false;
    }

    private void setupGui() {
        int[] wireSlots = { 27, 28, 29, 33, 34, 35 };
        ItemStack wireItem = getWireItem();
        for (int slot : wireSlots) {
            super.getGui().setItem(slot, wireItem);
        }

        ItemStack switchItem = getSwitchItem();
        for (int slot : this.switchSlots) {
            super.getGui().setItem(slot, switchItem);
        }

        super.setInfoButton("Click the switch to enable the power!");
        super.addEmptySlots();
    }

    private void setSwitchActive() {
        ItemStack whiteGlass = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
        for (int slot : this.switchSlots) {
            super.getGui().setItem(slot, whiteGlass);
        }

        int[] slots = { 30, 31, 32 };
        ItemStack item = getGreenSwitchItem();
        for (int slot : slots) {
            super.getGui().setItem(slot, item);
        }
    }

    private ItemStack getWireItem() {
        ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RED + ChatColor.BOLD.toString() + "Powerline");
        meta.setLore(Collections.singletonList(ChatColor.GOLD + "Click the switch to enable the power!"));
        item.setItemMeta(meta);

        return item;
    }

    private ItemStack getSwitchItem() {
        ItemStack item = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RED + ChatColor.BOLD.toString() + "Switch");
        meta.setLore(Collections.singletonList(ChatColor.GOLD + "Click to enable the power!"));
        item.setItemMeta(meta);

        return item;
    }

    private ItemStack getGreenSwitchItem() {
        ItemStack item = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RED + ChatColor.BOLD.toString() + "Switch");
        meta.setLore(Collections.singletonList(ChatColor.GOLD + "Power enabled!"));
        item.setItemMeta(meta);

        return item;
    }
}
