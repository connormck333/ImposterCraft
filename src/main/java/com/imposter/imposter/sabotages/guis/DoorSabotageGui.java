package com.imposter.imposter.sabotages.guis;

import com.imposter.imposter.instances.Arena;
import com.imposter.imposter.instances.locations.DoorLocation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Map;

import static com.imposter.imposter.utils.Constants.BOLD_RED;
import static com.imposter.imposter.utils.GuiUtils.*;
import static com.imposter.imposter.utils.StringUtils.makeTitle;

public class DoorSabotageGui {

    private Arena arena;
    private Player player;
    private Inventory gui;

    public static final String DOOR_SABOTAGE_TITLE = BOLD_RED + "Shut Doors";

    public DoorSabotageGui(Arena arena, Player player) {
        this.arena = arena;
        this.player = player;
        this.gui = Bukkit.createInventory(player, 54, DOOR_SABOTAGE_TITLE);
        setupGui();
    }

    public void openGui() {
        player.openInventory(gui);
    }

    private void setupGui() {
        this.gui.setItem(0, getExitButton());
        this.gui.setItem(8, getInfoButton("Shut Doors", "Trap players by shutting the doors!"));

        int currentSlot = 19;
        Map<String, ArrayList<DoorLocation>> doorLocations = arena.getDoorManager().getDoorLocations();
        for (String key : doorLocations.keySet()) {
            if (currentSlot % 9 == 0 || currentSlot % 9 == 8) {
                currentSlot++;
            }
            this.gui.setItem(currentSlot, getRandomWool(
                    BOLD_RED + makeTitle(key),
                    String.valueOf(key)
            ));

            currentSlot++;
        }
    }
}
