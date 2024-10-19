package com.imposter.imposter.sabotages.runnables;

import com.imposter.imposter.ImposterCraft;
import com.imposter.imposter.instances.Arena;
import com.imposter.imposter.utils.ConfigManager;
import com.imposter.imposter.utils.GameState;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class OxygenRunnable extends BukkitRunnable {

    private ImposterCraft imposterCraft;
    private Arena arena;
    private int count;

    private final int MAX_COUNT;

    public OxygenRunnable(ImposterCraft imposterCraft, Arena arena) {
        this.imposterCraft = imposterCraft;
        this.arena = arena;
        count = 1;
        MAX_COUNT = ConfigManager.getSabotageSecondsBeforeEndGame();
    }

    public void start() {
        arena.sendMessage(ChatColor.RED + "You have " + MAX_COUNT + " seconds to stop the oxygen from running out!");
        runTaskTimer(this.imposterCraft, 0, 20);
    }

    @Override
    public void run() {
        if (!arena.getOxygenManager().isActive()) {
            cancel();
            arena.getOxygenManager().clearGuis();
            return;
        } else if (count == MAX_COUNT) {
            cancel();
            arena.endGame(true);
            return;
        }

        if (arena.getState() == GameState.LIVE) {
            if (count % 5 == 0 || MAX_COUNT - count <= 5) {
                arena.sendMessage(ChatColor.RED + "Oxygen depleting in " + (MAX_COUNT - count) + " seconds!");
            }

            count++;
        } else if (arena.getState() == GameState.END_GAME) {
            cancel();
        } else {
            count = 1;
        }
    }
}
