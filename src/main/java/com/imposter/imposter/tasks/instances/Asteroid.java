package com.imposter.imposter.tasks.instances;

import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class Asteroid {

    private UUID id;
    private final ItemStack color;
    private int slot;
    private boolean active = true;

    public Asteroid(ItemStack color, int slot) {
        this.color = color;
        this.slot = slot;
        this.id = UUID.randomUUID();
    }

    public ItemStack getColor() {
        return this.color;
    }

    public int[] getSlots() {
        return new int[] { this.slot, this.slot + 1, this.slot + 9, this.slot + 10 };
    }

    public int getSlot() {
        return this.slot;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public void setInactive() {
        this.active = false;
    }
}
