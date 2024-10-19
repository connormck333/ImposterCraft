package com.imposter.imposter.tasks;

import com.imposter.imposter.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static com.imposter.imposter.utils.GuiUtils.getExitButton;
import static com.imposter.imposter.utils.GuiUtils.getInfoButton;
import static com.imposter.imposter.utils.Messages.sendGreenMessageToPlayer;
import static com.imposter.imposter.utils.Messages.sendRedMessageToPlayer;

public class PlayerTask {

    private final Inventory gui;
    private final Player player;
    private final ItemStack emptySlotItem = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
    private final String COMPLETE_MESSAGE;
    private final Tasks TASK_TYPE;

    public PlayerTask(Player player, String title, String completeMessage, Tasks taskType) {
        this.player = player;
        this.gui = Bukkit.createInventory(player, 54, title);
        this.COMPLETE_MESSAGE = completeMessage;
        this.TASK_TYPE = taskType;
        setExitButton();
    }

    public void openGui() {
        this.player.openInventory(this.gui);
    }

    public Player getPlayer() {
        return this.player;
    }

    public void complete() {
        this.player.closeInventory();
        if (COMPLETE_MESSAGE != null) {
            sendGreenMessageToPlayer(this.player, COMPLETE_MESSAGE);
        }
    }

    public void fail() {
        this.player.closeInventory();
        sendRedMessageToPlayer(this.player, "You were too slow! Try again!");
    }

    public Tasks getTaskType() {
        return this.TASK_TYPE;
    }

    public void cancel() {}

    protected Inventory getGui() {
        return this.gui;
    }

    protected ItemStack getEmptySlotItem() {
        return this.emptySlotItem;
    }

    protected void addEmptySlots() {
        for (int i = 0; i < 54; i++) {
            if (gui.getItem(i) == null) {
                gui.setItem(i, this.emptySlotItem);
            }
        }
    }

    protected void setInfoButton(String info) {
        this.gui.setItem(8, getInfoButton("Info", info));
    }

    private void setExitButton() {
        this.gui.setItem(0, getExitButton());
    }
}
