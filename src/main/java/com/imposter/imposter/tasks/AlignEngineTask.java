package com.imposter.imposter.tasks;

import com.imposter.imposter.utils.Tasks;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class AlignEngineTask extends PlayerTask {

    private final int CENTER_SLOT = 27;

    public static final String ALIGN_ENGINE_TASK_TITLE = ChatColor.RED + ChatColor.BOLD.toString() + "Align Engine";

    public AlignEngineTask(Player player) {
        super(player, ALIGN_ENGINE_TASK_TITLE, "Engine successfully aligned!", Tasks.ALIGN_ENGINE);

        setupGui();
        super.addEmptySlots();
    }

    public int getCenterSlot() {
        return this.CENTER_SLOT;
    }

    private void setupGui() {
        ArrayList<Integer> lineSlots = getMisalignmentSlots();
        ItemStack lineItem = createMisalignmentItem();
        for (int slot : lineSlots) {
            super.getGui().setItem(slot, lineItem);
        }

        int[] outlineSlots = { 17, 25, 34, 43, 53 };
        ItemStack outlineItem = createOutlineItem();
        for (int slot : outlineSlots) {
            super.getGui().setItem(slot, outlineItem);
        }

        ItemStack centerItem = createCenterItem();
        for (int i = 28; i < 33; i++) {
            super.getGui().setItem(i, centerItem);
        }

        super.getGui().setItem(CENTER_SLOT, createCenterEndItem());
    }

    private ArrayList<Integer> getMisalignmentSlots() {
        Random random = new Random();
        ArrayList<Integer> slots = new ArrayList<>();
        if (random.nextBoolean()) {
            slots.add(33);
            slots.add(23);
            slots.add(22);
            slots.add(21);
            slots.add(11);
            slots.add(10);
            slots.add(9);
        } else {
            slots.add(33);
            slots.add(41);
            slots.add(40);
            slots.add(39);
            slots.add(47);
            slots.add(46);
            slots.add(45);
        }

        return slots;
    }

    private ItemStack createMisalignmentItem() {
        ItemStack item = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RED + ChatColor.BOLD.toString() + "Engine alignment");
        meta.setLore(Collections.singletonList(ChatColor.GOLD + "Click the center to align the engine correctly."));
        item.setItemMeta(meta);

        return item;
    }

    private ItemStack createOutlineItem() {
        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + ChatColor.BOLD.toString() + "Engine");
        item.setItemMeta(meta);

        return item;
    }

    private ItemStack createCenterItem() {
        ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + ChatColor.BOLD.toString() + "Center alignment");
        meta.setLore(Collections.singletonList(ChatColor.GOLD + "Click the center to align the engine correctly."));
        item.setItemMeta(meta);

        return item;
    }

    private ItemStack createCenterEndItem() {
        ItemStack item = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + ChatColor.BOLD.toString() + "Center alignment");
        meta.setLore(Collections.singletonList(ChatColor.GOLD + "Click to center the engine alignment!"));
        item.setItemMeta(meta);

        return item;
    }
}
