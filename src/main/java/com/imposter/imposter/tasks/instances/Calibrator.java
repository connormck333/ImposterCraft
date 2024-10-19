package com.imposter.imposter.tasks.instances;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;

public class Calibrator {

    private ArrayList<Integer> slots;
    private int[] buttonSlots;
    private int currentSlotIndex;
    private int centerSlot;
    private ItemStack item;
    private boolean complete;

    public Calibrator(ArrayList<Integer> slots, int[] buttonSlots, Material material, int centerSlot) {
        this.slots = slots;
        this.buttonSlots = buttonSlots;
        this.currentSlotIndex = 0;
        this.centerSlot = centerSlot;
        this.item = createItem(material);
        this.complete = false;
    }

    public ItemStack getItem() {
        return this.item;
    }

    public ArrayList<Integer> getSlots() {
        return this.slots;
    }

    public int[] getButtonSlots() {
        return this.buttonSlots;
    }

    public int getCurrentSlot() {
        return this.slots.get(currentSlotIndex);
    }

    public int getPreviousSlot() {
        return this.slots.get(currentSlotIndex == 0 ? slots.size() - 1 : currentSlotIndex - 1);
    }

    public int getCenterSlot() {
        return this.centerSlot;
    }

    public boolean isComplete() {
        return this.complete;
    }

    public void incrementCurrentSlot() {
        this.currentSlotIndex++;
        if (currentSlotIndex >= slots.size()) {
            this.currentSlotIndex = 0;
        }
    }

    public boolean isCenterSlot() {
        return this.slots.get(currentSlotIndex) == this.centerSlot;
    }

    public void setComplete() {
        this.complete = true;
    }

    private ItemStack createItem(Material material) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RED + ChatColor.BOLD.toString() + "Calibrator");
        meta.setLore(Collections.singletonList(ChatColor.GOLD + "Stop the calibrator when it reaches the green!"));
        item.setItemMeta(meta);

        return item;
    }
}
