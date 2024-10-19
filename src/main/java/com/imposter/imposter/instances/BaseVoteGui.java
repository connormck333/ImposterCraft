package com.imposter.imposter.instances;

import com.imposter.imposter.utils.Colors;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

import static com.imposter.imposter.utils.GuiUtils.*;

public class BaseVoteGui {

    private final Arena arena;
    private final Player player;
    private final Inventory gui;

    public BaseVoteGui(Arena arena, Player player, String guiTitle) {
        this.arena = arena;
        this.player = player;
        this.gui = Bukkit.createInventory(player, 54, guiTitle);
    }

    public void openGui() {
        player.openInventory(gui);
    }

    public Player getPlayer() {
        return player;
    }

    public Arena getArena() {
        return arena;
    }

    protected void setupGui(String infoTitle, String infoText, boolean ignoreOwner) {
        ItemStack exitButton = getExitButton();
        gui.setItem(0, exitButton);

        ItemStack infoButton = getInfoButton(infoTitle, infoText);
        gui.setItem(8, infoButton);

        int slotId = 19;
        for (UUID uuid : arena.getRemainingPlayers()) {
            Colors color = arena.getPlayerManager().getPlayerColor(uuid);
            Player p = Bukkit.getPlayer(uuid);
            if (ignoreOwner && player == p) {
                continue;
            }
            ItemStack head = getPlayerHead(p, color);

            gui.setItem(slotId, head);
            slotId++;
        }
    }

    protected Inventory gui() {
        return gui;
    }
}
