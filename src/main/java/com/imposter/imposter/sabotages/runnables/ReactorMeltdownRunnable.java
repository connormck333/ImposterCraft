package com.imposter.imposter.sabotages.runnables;

import com.imposter.imposter.ImposterCraft;
import com.imposter.imposter.instances.Arena;
import com.imposter.imposter.sabotages.guis.ReactorMeltdownGui;
import com.imposter.imposter.utils.ConfigManager;
import com.imposter.imposter.utils.GameState;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class ReactorMeltdownRunnable extends BukkitRunnable {

    private ImposterCraft imposterCraft;
    private Arena arena;
    private int count;

    private final int MAX_COUNT;

    public ReactorMeltdownRunnable(ImposterCraft imposterCraft, Arena arena) {
        this.imposterCraft = imposterCraft;
        this.arena = arena;
        count = 1;
        MAX_COUNT = ConfigManager.getSabotageSecondsBeforeEndGame();
    }

    public void start() {
        arena.sendMessage(ChatColor.RED + "You have " + MAX_COUNT + " seconds to stop the reactor melting down!");
        runTaskTimer(this.imposterCraft, 0, 20);
    }

    @Override
    public void run() {
        if (!arena.getReactorManager().isActive()) {
            cancel();
            List<ReactorMeltdownGui> guis = arena.getReactorManager().getGuisOpen();
            for (ReactorMeltdownGui gui : guis) {
                gui.setGreen();
                Bukkit.getScheduler().runTaskLater(imposterCraft, gui::complete, 20L);
            }
            arena.getReactorManager().clearGuis();
            return;
        } else if (count == MAX_COUNT) {
            cancel();
            arena.endGame(true);
            return;
        }

        if (arena.getState() == GameState.LIVE) {
            if (count % 5 == 0 || MAX_COUNT - count <= 5) {
                arena.sendMessage(ChatColor.RED + "Reactor meltdown in " + (MAX_COUNT - count) + " seconds!");
            }

            count++;
        } else if (arena.getState() == GameState.END_GAME) {
            cancel();
        } else {
            count = 1;
        }
    }
}
