package com.imposter.imposter.sabotages.guis;

import com.imposter.imposter.sabotages.ReactorGuiStatus;
import com.imposter.imposter.tasks.PlayerTask;
import com.imposter.imposter.utils.Tasks;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static com.imposter.imposter.utils.Constants.BOLD_RED;
import static com.imposter.imposter.utils.Constants.BOLD_YELLOW;
import static com.imposter.imposter.utils.GuiUtils.getMeta;

public class ReactorMeltdownGui extends PlayerTask {

    private ReactorGuiStatus status;
    private final Location location;
    private final int[] slots = { 12, 13, 14, 20, 21, 22, 23, 24, 29, 30, 32, 32, 33, 38, 39, 40, 41, 42, 48, 49, 50 };

    public static final String REACTOR_MELTDOWN_TASK_TITLE = BOLD_RED + "Reactor Meltdown";

    public ReactorMeltdownGui(Player player, Location location) {
        super(player, REACTOR_MELTDOWN_TASK_TITLE, null, Tasks.REACTOR_MELTDOWN);
        this.status = ReactorGuiStatus.RED;
        this.location = location;

        setupGui();
    }

    public boolean handleButtonClick(int slot) {
        if (status != ReactorGuiStatus.RED) {
            return false;
        }

        for (int buttonSlot : slots) {
            if (slot == buttonSlot) {
                setYellow();
                return true;
            }
        }

        return false;
    }

    public Location getLocation() {
        return this.location;
    }

    public boolean isComplete() {
        return status == ReactorGuiStatus.YELLOW;
    }

    public void setGreen() {
        ItemStack greenItem = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        greenItem.setItemMeta(getMeta(greenItem, BOLD_YELLOW + "Complete"));

        for (int slot : slots) {
            getGui().setItem(slot, greenItem);
        }

        status = ReactorGuiStatus.GREEN;
    }

    private void setYellow() {
        ItemStack yellowItem = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
        yellowItem.setItemMeta(getMeta(yellowItem, BOLD_YELLOW + "Waiting...", ChatColor.LIGHT_PURPLE + "Waiting on other player. Do not exit!"));

        for (int slot : slots) {
            getGui().setItem(slot, yellowItem);
        }

        status = ReactorGuiStatus.YELLOW;
    }

    private void setupGui() {
        ItemStack redItem = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        redItem.setItemMeta(getMeta(redItem, BOLD_RED + "STOP MELTDOWN", ChatColor.LIGHT_PURPLE + "Click to stop the meltdown"));

        for (int slot : slots) {
            getGui().setItem(slot, redItem);
        }

        addEmptySlots();
    }
}
