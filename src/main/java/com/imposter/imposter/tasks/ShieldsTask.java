package com.imposter.imposter.tasks;

import com.imposter.imposter.utils.Tasks;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ShieldsTask extends PlayerTask {

    public static final String SHIELDS_TASK_TITLE = ChatColor.RED.toString() + ChatColor.BOLD + "Fix the Shields";
    public static final int[] SHIELDS_BUTTONS = { 4, 11, 29, 40, 22, 15, 33 };
    private final Map<Integer, Boolean> activatedShields;

    public ShieldsTask(Player player) {
        super(player, SHIELDS_TASK_TITLE, "Shields Activated", Tasks.SHIELDS);
        activatedShields = new HashMap<>();
        populateInitialItems();
        super.addEmptySlots();
    }

    private void populateInitialItems() {
        ItemStack disarmedShield = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta disarmedMeta = disarmedShield.getItemMeta();
        disarmedMeta.setDisplayName(ChatColor.RED + ChatColor.BOLD.toString() + "Disarmed Shield");
        disarmedMeta.setLore(Collections.singletonList(ChatColor.GREEN + "Click to arm the shield"));
        disarmedShield.setItemMeta(disarmedMeta);
        for (int slot : SHIELDS_BUTTONS) {
            super.getGui().setItem(slot, disarmedShield);
            super.getGui().setItem(slot + 1, disarmedShield);
            super.getGui().setItem(slot + 9, disarmedShield);
            super.getGui().setItem(slot + 10, disarmedShield);
            activatedShields.put(slot, false);
        }
    }

    public void setShieldActivated(int slot) {
        ItemStack armedShield = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta armedMeta = armedShield.getItemMeta();
        armedMeta.setDisplayName(ChatColor.RED + ChatColor.BOLD.toString() + "Armed Shield");
        armedMeta.setLore(Collections.singletonList(ChatColor.GREEN + "This shield has been armed"));
        armedShield.setItemMeta(armedMeta);

        super.getGui().setItem(slot, armedShield);
        super.getGui().setItem(slot + 1, armedShield);
        super.getGui().setItem(slot + 9, armedShield);
        super.getGui().setItem(slot + 10, armedShield);

        activatedShields.put(slot, true);
    }

    public boolean areAllShieldsActivated() {
        for (int i : SHIELDS_BUTTONS) {
            if (!activatedShields.get(i)) {
                return false;
            }
        }

        return true;
    }
}
