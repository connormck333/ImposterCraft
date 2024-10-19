package com.imposter.imposter.sabotages.guis;

import com.imposter.imposter.tasks.PlayerTask;
import com.imposter.imposter.utils.Tasks;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static com.imposter.imposter.utils.Constants.*;
import static com.imposter.imposter.utils.GuiUtils.getMeta;
import static com.imposter.imposter.utils.Messages.sendGreenMessageToPlayer;
import static com.imposter.imposter.utils.Messages.sendRedMessageToPlayer;

public class OxygenSabotageGui extends PlayerTask {

    private Location location;
    private ArrayList<Integer> enteredCode;
    private final int[] code;
    private final int[] slots = { 29, 30, 31, 32, 33, 38, 39, 40, 41, 42 };
    private final int codeSlot = 13;
    private final int submitSlot = 43;

    private boolean codeCompleted;

    public static final String OXYGEN_SABOTAGE_TITLE = BOLD_RED + "Oxygen Depleting";

    public OxygenSabotageGui(Player player, Location location) {
        super(player, OXYGEN_SABOTAGE_TITLE, null, Tasks.OXYGEN_DEPLETION);
        this.location = location;
        this.code = generateCode();
        this.enteredCode = new ArrayList<>();
        this.codeCompleted = false;

        setupGui();
    }

    public void handleClick(int slot) {
        if (slot == submitSlot) {
            submitCode();
        }

        for (int i = 0; i < slots.length; i++) {
            if (slot == slots[i]) {
                enteredCode.add(i);
                return;
            }
        }
    }

    public boolean isComplete() {
        return codeCompleted;
    }

    public Location getLocation() {
        return location;
    }

    private void submitCode() {
        String strEnteredCode = enteredCode.toString().replaceAll("[\\[\\]]", "");
        String strExpectedCode = Arrays.toString(code).replaceAll("[\\[\\]]", "");
        if (strExpectedCode.equals(strEnteredCode)) {
            codeCompleted = true;
            getPlayer().closeInventory();
            sendGreenMessageToPlayer(getPlayer(), "Code complete!");
        } else {
            sendRedMessageToPlayer(getPlayer(), "Invalid code, try again!");
            enteredCode.clear();
        }
    }

    private int[] generateCode() {
        Random random = new Random();
        int codeSize = 5;
        int[] nums = new int[codeSize];
        for (int i = 0; i < codeSize; i++) {
            nums[i] = random.nextInt(9);
        }

        return nums;
    }

    private void setupGui() {
        super.getGui().setItem(codeSlot, getCodeItem());

        for (int i = 0; i < slots.length; i++) {
            ItemStack item = getNumberItem(i);
            int slot = slots[i];
            super.getGui().setItem(slot, item);
        }

        super.getGui().setItem(submitSlot, getSubmitButton());

        addEmptySlots();
        setInfoButton(ChatColor.LIGHT_PURPLE + "Enter the digits in order & press submit");
    }

    private ItemStack getNumberItem(int num) {
        ItemStack item = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        item.setItemMeta(getMeta(item, BOLD_GREEN + num));

        return item;
    }

    private ItemStack getSubmitButton() {
        ItemStack item = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        item.setItemMeta(getMeta(item, BOLD_YELLOW + "Submit"));

        return item;
    }

    private ItemStack getCodeItem() {
        ItemStack item = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
        item.setItemMeta(getMeta(item, BOLD_RED + Arrays.toString(code)));

        return item;
    }
}
