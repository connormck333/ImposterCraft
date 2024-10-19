package com.imposter.imposter.tasks;

import com.imposter.imposter.utils.Tasks;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class NumberTask extends PlayerTask {

    private int[] numberSlots = { 20, 30, 33, 23, 29, 22, 24, 32, 21, 31 };
    private int currentNum;

    public static final String ORDER_NUMBERS_TASK_TITLE = ChatColor.RED + ChatColor.BOLD.toString() + "Order the numbers";

    public NumberTask(Player player) {
        super(player, ORDER_NUMBERS_TASK_TITLE, "Task complete", Tasks.NUMBERS);
        this.currentNum = 0;
        setupGui();
    }

    public void handleNumberClick(int slot) {
        if (numberSlots[currentNum] == slot) {
            currentNum++;
            super.getGui().setItem(slot, getGreenItem(currentNum));
        } else {
            setupGui();
            currentNum = 0;
        }
    }

    public boolean isTaskComplete() {
        return currentNum == 10;
    }

    private void setupGui() {
        for (int i = 0; i < numberSlots.length; i++) {
            int slot = numberSlots[i];
            super.getGui().setItem(slot, getRedItem(i + 1));
        }

        super.setInfoButton("Click the numbers in ascending order to complete the task");
        super.addEmptySlots();
    }

    private ItemStack getRedItem(int num) {
        ItemStack item = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RED + ChatColor.BOLD.toString() + num);
        item.setItemMeta(meta);

        return item;
    }

    private ItemStack getGreenItem(int num) {
        ItemStack item = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + ChatColor.BOLD.toString() + num);
        item.setItemMeta(meta);

        return item;
    }
}
