package com.imposter.imposter.tasks;

import com.imposter.imposter.utils.Tasks;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

public class VentTask extends PlayerTask {

    private int[] itemSlots;
    private HashMap<Integer, Boolean> clearedSlots;
    private final int MAX_ITEMS = 9;
    public static final String VENT_TASK_TITLE = ChatColor.RED + ChatColor.BOLD.toString() + "Clear the Vent";

    public VentTask(Player player) {
        super(player, VENT_TASK_TITLE, "Vent cleared!", Tasks.CLEAR_VENT);
        this.itemSlots = new int[MAX_ITEMS];
        setupGui();
    }

    public void clearItem(int slot) {
        super.getGui().setItem(slot, super.getEmptySlotItem());
        this.clearedSlots.put(slot, true);
    }

    public int[] getItemSlots() {
        return this.itemSlots;
    }

    public boolean isVentClear() {
        for (int slot : itemSlots) {
            if (!clearedSlots.get(slot)) {
                return false;
            }
        }

        return true;
    }

    private void setupGui() {
        int count = 0;
        Material[] items = { Material.PUFFERFISH, Material.COD, Material.SPONGE, Material.COBWEB, Material.DEAD_BUSH };
        this.clearedSlots = new HashMap<>();
        Random random = new Random();
        while (count < MAX_ITEMS) {
            ItemStack ventItem = new ItemStack(items[random.nextInt(items.length)]);
            ventItem.setItemMeta(getMeta(ventItem));

            int slot = random.nextInt(53) + 1;
            if (super.getGui().getItem(slot) != null) {
                continue;
            }
            super.getGui().setItem(slot, ventItem);
            this.itemSlots[count] = slot;
            this.clearedSlots.put(slot, false);

            count++;
        }

        super.addEmptySlots();
    }

    private ItemMeta getMeta(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RED + ChatColor.BOLD.toString() + "Trash");
        meta.setLore(Collections.singletonList(ChatColor.GOLD + "Click to remove trash"));

        return meta;
    }
}
