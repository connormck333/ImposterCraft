package com.imposter.imposter.tasks;

import com.imposter.imposter.ImposterCraft;
import com.imposter.imposter.tasks.runnables.TrashRunnable;
import com.imposter.imposter.utils.Tasks;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;

public class TrashTask extends PlayerTask {

    private ImposterCraft imposterCraft;
    private ArrayList<ArrayList<Integer>> trashSlots;
    private final Material[] TRASH_ITEMS = {
            Material.BROWN_STAINED_GLASS_PANE,
            Material.BLACK_STAINED_GLASS_PANE,
            Material.YELLOW_STAINED_GLASS_PANE,
            Material.BROWN_STAINED_GLASS_PANE,
            Material.BLACK_STAINED_GLASS_PANE
    };

    private ItemStack statusItem;
    private final int[] statusSlots = { 16, 17, 25, 26 };
    private ItemStack btnItem;
    private final int[] btnSlots = { 34, 35, 43, 44 };
    private TrashRunnable runnable;

    public static final String TRASH_TASK_TITLE = ChatColor.RED + ChatColor.BOLD.toString() + "Empty the trash";

    public TrashTask(ImposterCraft imposterCraft, Player player) {
        super(player, TRASH_TASK_TITLE, "Trash emptied!", Tasks.EMPTY_TRASH);
        this.imposterCraft = imposterCraft;
        this.trashSlots = new ArrayList<>();

        createTrashSlots();
        setupGui();
        super.addEmptySlots();
    }

    public void startEmptying() {
        if (runnable != null) {
            return;
        }
        runnable = new TrashRunnable(imposterCraft, this);
        runnable.start();
    }

    public int[] getBtnSlots() {
        return this.btnSlots;
    }

    public void setStatusYellow() {
        this.statusItem = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
        ItemMeta statusMeta = statusItem.getItemMeta();
        statusMeta.setDisplayName(ChatColor.YELLOW + ChatColor.BOLD.toString() + "Emptying...");
        statusMeta.setLore(Collections.singletonList(ChatColor.GOLD + "Status"));
        statusItem.setItemMeta(statusMeta);

        for (int slot : statusSlots) {
            super.getGui().setItem(slot, statusItem);
        }
    }

    public void setStatusGreen() {
        this.statusItem = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta statusMeta = statusItem.getItemMeta();
        statusMeta.setDisplayName(ChatColor.GREEN + ChatColor.BOLD.toString() + "Trash emptied!");
        statusMeta.setLore(Collections.singletonList(ChatColor.GOLD + "Status"));
        statusItem.setItemMeta(statusMeta);

        for (int slot : statusSlots) {
            super.getGui().setItem(slot, statusItem);
        }

        this.btnItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta btnMeta = btnItem.getItemMeta();
        btnMeta.setDisplayName(ChatColor.GREEN + ChatColor.BOLD.toString() + "Empty trash");
        btnMeta.setLore(Collections.singletonList(ChatColor.GOLD + "You have already emptied the trash!"));
        btnItem.setItemMeta(btnMeta);

        for (int slot : btnSlots) {
            super.getGui().setItem(slot, btnItem);
        }
    }

    public void dropSlots(int slotsToDropBy) {
        ItemStack air = new ItemStack(Material.AIR);
        for (int x = 0; x < trashSlots.size(); x++) {
            ArrayList<Integer> trash = trashSlots.get(x);
            for (int i = 0; i < trash.size(); i++) {
                int slot = trash.get(i);
                int newSlot = slot + (9 * slotsToDropBy);
                if (newSlot - 9 < 54) {
                    super.getGui().setItem(newSlot - 9, air);
                }
                if (newSlot >= 54) {
                    continue;
                }
                ItemStack item = getTrashItem(x);
                super.getGui().setItem(newSlot, item);
            }
        }

        super.addEmptySlots();
    }

    @Override
    public void cancel() {
        runnable.cancel();
    }

    private void setupGui() {
        for (int x = 0; x < trashSlots.size(); x++) {
            ArrayList<Integer> trash = trashSlots.get(x);
            for (int i = 0; i < trash.size(); i++) {
                int slot = trash.get(i);
                ItemStack item = getTrashItem(x);
                super.getGui().setItem(slot, item);
            }
        }

        this.statusItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta statusMeta = statusItem.getItemMeta();
        statusMeta.setDisplayName(ChatColor.RED + ChatColor.BOLD.toString() + "Trash is full!");
        statusMeta.setLore(Collections.singletonList(ChatColor.GOLD + "Status"));
        statusItem.setItemMeta(statusMeta);

        for (int slot : statusSlots) {
            super.getGui().setItem(slot, statusItem);
        }

        this.btnItem = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta btnMeta = btnItem.getItemMeta();
        btnMeta.setDisplayName(ChatColor.GREEN + ChatColor.BOLD.toString() + "Empty trash");
        btnMeta.setLore(Collections.singletonList(ChatColor.GOLD + "Click here to empty the trash"));
        btnItem.setItemMeta(btnMeta);

        for (int slot : btnSlots) {
            super.getGui().setItem(slot, btnItem);
        }
    }

    private ItemStack getTrashItem(int i) {
        ItemStack item = new ItemStack(TRASH_ITEMS[i]);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_GREEN + ChatColor.BOLD.toString() + "TRASH");
        item.setItemMeta(meta);

        return item;
    }

    private void createTrashSlots() {
        ArrayList<Integer> trash1 = new ArrayList<>();
        trash1.add(20);
        trash1.add(19);
        trash1.add(11);
        trash1.add(10);

        ArrayList<Integer> trash2 = new ArrayList<>();
        trash2.add(46);
        trash2.add(39);
        trash2.add(38);

        ArrayList<Integer> trash3 = new ArrayList<>();
        trash3.add(13);
        trash3.add(4);
        trash3.add(3);

        ArrayList<Integer> trash4 = new ArrayList<>();
        trash4.add(32);
        trash4.add(23);

        ArrayList<Integer> trash5 = new ArrayList<>();
        trash5.add(50);

        trashSlots.add(trash1);
        trashSlots.add(trash2);
        trashSlots.add(trash3);
        trashSlots.add(trash4);
        trashSlots.add(trash5);
    }
}
