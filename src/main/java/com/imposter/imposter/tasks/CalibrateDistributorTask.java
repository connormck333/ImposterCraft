package com.imposter.imposter.tasks;

import com.imposter.imposter.ImposterCraft;
import com.imposter.imposter.tasks.instances.Calibrator;
import com.imposter.imposter.tasks.runnables.CalibratorRunnable;
import com.imposter.imposter.utils.Tasks;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;

public class CalibrateDistributorTask extends PlayerTask {

    private ImposterCraft imposterCraft;
    private Calibrator[] calibrators;
    private int currentCalibratorIndex;
    private ItemStack centerItem;
    private CalibratorRunnable runnable;

    public static final String CALIBRATE_TASK_TITLE = ChatColor.RED + ChatColor.BOLD.toString() + "Calibrate distributors";

    public CalibrateDistributorTask(ImposterCraft imposterCraft, Player player) {
        super(player, CALIBRATE_TASK_TITLE, "Distributors calibrated", Tasks.CALIBRATE_DISTRIBUTOR);
        this.imposterCraft = imposterCraft;

        ArrayList<Integer> calibrator1Slots = new ArrayList<>();
        calibrator1Slots.add(1);
        calibrator1Slots.add(10);
        calibrator1Slots.add(19);
        calibrator1Slots.add(20);
        calibrator1Slots.add(21);
        calibrator1Slots.add(12);
        calibrator1Slots.add(3);
        calibrator1Slots.add(2);
        int[] button1Slots = { 15, 16 };
        Calibrator calibrator1 = new Calibrator(calibrator1Slots, button1Slots, Material.BLUE_STAINED_GLASS_PANE, 12);

        ArrayList<Integer> calibrator2Slots = new ArrayList<>();
        calibrator2Slots.add(28);
        calibrator2Slots.add(37);
        calibrator2Slots.add(46);
        calibrator2Slots.add(47);
        calibrator2Slots.add(48);
        calibrator2Slots.add(39);
        calibrator2Slots.add(30);
        calibrator2Slots.add(29);
        int[] button2Slots = { 42, 43 };
        Calibrator calibrator2 = new Calibrator(calibrator2Slots, button2Slots, Material.ORANGE_STAINED_GLASS_PANE, 39);

        this.calibrators = new Calibrator[] {calibrator1, calibrator2};
        this.currentCalibratorIndex = 0;

        this.centerItem = getCenterItem();

        setupGui();
        super.addEmptySlots();
    }

    @Override
    public void openGui() {
        super.openGui();
        runnable = new CalibratorRunnable(imposterCraft, this);
        runnable.start();
    }

    @Override
    public void cancel() {
        if (runnable != null) {
            runnable.cancel();
        }
    }

    public void handleButtonClick(int slot) {
        Calibrator calibrator = calibrators[currentCalibratorIndex];
        for (int btnSlot : calibrator.getButtonSlots()) {
            if (slot == btnSlot && calibrator.isCenterSlot()) {
                completeCalibrator();
                break;
            }
        }
    }

    public void changePosition() {
        Calibrator calibrator = calibrators[currentCalibratorIndex];
        calibrator.incrementCurrentSlot();
        super.getGui().setItem(calibrator.getCurrentSlot(), centerItem);
        super.getGui().setItem(calibrator.getPreviousSlot(), calibrator.getItem());
    }

    public boolean isTaskComplete() {
        return this.currentCalibratorIndex > 1;
    }

    private void setupGui() {
        ItemStack buttonItem = getButtonItem();

        for (Calibrator calibrator : this.calibrators) {
            ItemStack item1 = calibrator.getItem();
            for (int slot : calibrator.getSlots()) {
                super.getGui().setItem(slot, item1);
            }

            super.getGui().setItem(calibrator.getCenterSlot() + 1, centerItem);

            for (int slot : calibrator.getButtonSlots()) {
                super.getGui().setItem(slot, buttonItem);
            }
        }
    }

    private void completeCalibrator() {
        Calibrator calibrator = calibrators[currentCalibratorIndex];
        for (int slot : calibrator.getSlots()) {
            super.getGui().setItem(slot, calibrator.getItem());
        }

        super.getGui().setItem(calibrator.getCenterSlot(), getCenterItem());

        calibrator.setComplete();
        this.currentCalibratorIndex++;
    }

    private ItemStack getCenterItem() {
        ItemStack item = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + ChatColor.BOLD.toString() + "Center");
        meta.setLore(Collections.singletonList(ChatColor.GOLD + "Stop the distributor when it reaches the green!"));
        item.setItemMeta(meta);

        return item;
    }

    private ItemStack getButtonItem() {
        ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RED + ChatColor.BOLD.toString() + "STOP");
        item.setItemMeta(meta);

        return item;
    }
}
