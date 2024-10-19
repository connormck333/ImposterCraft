package com.imposter.imposter.tasks;

import com.imposter.imposter.ImposterCraft;
import com.imposter.imposter.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Random;

import static com.imposter.imposter.utils.Constants.BOLD_GREEN;
import static com.imposter.imposter.utils.Constants.BOLD_RED;
import static com.imposter.imposter.utils.GuiUtils.getMeta;

public class DivertPowerTask extends PlayerTask {

    private ImposterCraft imposterCraft;
    private int[] switchSlots = { 28, 30, 32, 34 };
    private HashMap<Integer, Boolean> enabledSwitches;

    private ItemStack switchItem;
    private ItemStack shadeItem;
    private ItemStack greenStatusItem;
    private ItemStack redStatusItem;

    public static final String DIVERT_POWER_TASK_TITLE = BOLD_RED + "Divert the power";
    public static final String FIX_LIGHTS_TASK_TITLE = BOLD_RED + "Fix the lights!";

    public DivertPowerTask(ImposterCraft imposterCraft, Player player) {
        super(player, DIVERT_POWER_TASK_TITLE, "Power diverted!", Tasks.DIVERT_POWER);
        init(imposterCraft);
    }

    public DivertPowerTask(ImposterCraft imposterCraft, Player player, boolean fixLights) {
        super(player, fixLights ? FIX_LIGHTS_TASK_TITLE : DIVERT_POWER_TASK_TITLE, fixLights ? "Lights fixed!" : "Power diverted!",  fixLights ? Tasks.FIX_LIGHTS : Tasks.DIVERT_POWER);
        init(imposterCraft);
    }

    public void handleSwitchClick(int slot) {
        for (int switchSlot : this.switchSlots) {
            if (slot == switchSlot || slot == switchSlot + 9) {
                if (enabledSwitches.get(switchSlot)) {
                    if (super.getTaskType() == Tasks.FIX_LIGHTS) {
                        disableSwitch(switchSlot);
                    }
                    continue;
                }
                enableSwitch(switchSlot);
                break;
            }
        }
    }

    public boolean allSwitchesEnabled() {
        for (int slot : this.switchSlots) {
            if (!enabledSwitches.get(slot)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void complete() {
        Bukkit.getScheduler().runTaskLater(imposterCraft, DivertPowerTask.super::complete, 20L);

        if (super.getTaskType() == Tasks.FIX_LIGHTS) {
            imposterCraft.getArenaManager().getArena(super.getPlayer()).getLightsManager().end();
        }
    }

    private void init(ImposterCraft imposterCraft) {
        this.imposterCraft = imposterCraft;
        this.enabledSwitches = new HashMap<>();

        this.switchItem = getSwitchItem(Material.RED_STAINED_GLASS_PANE);
        this.shadeItem = getSwitchItem(Material.BLACK_STAINED_GLASS_PANE);
        this.greenStatusItem = getGreenStatusItem();
        this.redStatusItem = getRedStatusItem();

        setupGui();
        super.addEmptySlots();
    }

    private void setupGui() {
        Random random = new Random();

        boolean allSwitchesEnabled = true;
        for (int slot : this.switchSlots) {
            if (random.nextBoolean()) {
                // Switch already enabled
                enableSwitch(slot);
            } else {
                // Switch not enabled
                super.getGui().setItem(slot, shadeItem);
                super.getGui().setItem(slot + 9, switchItem);
                this.enabledSwitches.put(slot, false);

                int statusSlot = slot - 18;
                super.getGui().setItem(statusSlot, getRedStatusItem());

                allSwitchesEnabled = false;
            }
        }

        if (allSwitchesEnabled) {
            int slot = this.switchSlots[random.nextInt(this.switchSlots.length)];
            super.getGui().setItem(slot, shadeItem);
            super.getGui().setItem(slot + 9, switchItem);
            super.getGui().setItem(slot - 18, getRedStatusItem());
            enabledSwitches.put(slot, false);
        }
    }

    private void enableSwitch(int slot) {
        super.getGui().setItem(slot, switchItem);
        super.getGui().setItem(slot + 9, shadeItem);
        this.enabledSwitches.put(slot, true);

        int statusSlot = slot - 18;
        super.getGui().setItem(statusSlot, greenStatusItem);
    }

    private void disableSwitch(int slot) {
        super.getGui().setItem(slot, shadeItem);
        super.getGui().setItem(slot + 9, switchItem);
        this.enabledSwitches.put(slot, false);

        int statusSlot = slot - 18;
        super.getGui().setItem(statusSlot, redStatusItem);
    }

    private ItemStack getSwitchItem(Material material) {
        ItemStack item = new ItemStack(material);
        item.setItemMeta(getMeta(item, BOLD_RED + "Power switch", ChatColor.GOLD + "Turn all switches on"));

        return item;
    }

    private ItemStack getGreenStatusItem() {
        ItemStack item = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        item.setItemMeta(getMeta(item, BOLD_GREEN + "On"));

        return item;
    }

    private ItemStack getRedStatusItem() {
        ItemStack item = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        item.setItemMeta(getMeta(item, BOLD_RED + "Off"));

        return item;
    }
}
