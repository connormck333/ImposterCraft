package com.imposter.imposter.instances;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class TaskBar {

    private final Arena arena;

    private final BossBar taskBar;

    public TaskBar(Arena arena) {
        this.arena = arena;
        this.taskBar = Bukkit.createBossBar(
                ChatColor.GREEN + ChatColor.BOLD.toString() + "Tasks completed",
                BarColor.GREEN,
                BarStyle.SOLID
        );
        this.taskBar.setProgress(0);
    }

    public void updateTaskBar() {
        double crewmateCount = arena.getPlayers().size() - arena.getImposters().size();
        double totalNumTasks = crewmateCount * arena.getTaskManager().getNumTasksPerPlayer();
        double progress = (totalNumTasks - arena.getTaskManager().getCompletedTasksCount()) / totalNumTasks;
        if (progress <= 1 && progress >= 0) {
            this.taskBar.setProgress(1 - progress);
        }
    }

    public void removePlayerFromTaskBar(Player player) {
        this.taskBar.removePlayer(player);
    }

    public void addPlayer(Player player) {
        this.taskBar.addPlayer(player);
    }
}
