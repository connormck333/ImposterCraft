package com.imposter.imposter.tasks.runnables;

import com.imposter.imposter.ImposterCraft;
import com.imposter.imposter.tasks.CalibrateDistributorTask;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CalibratorRunnable extends BukkitRunnable {

    private ImposterCraft imposterCraft;
    private CalibrateDistributorTask task;

    public CalibratorRunnable(ImposterCraft imposterCraft, CalibrateDistributorTask task) {
        this.imposterCraft = imposterCraft;
        this.task = task;
    }

    public void start() {
        runTaskTimer(imposterCraft, 0, 10);
    }

    @Override
    public void run() {
        if (task.isTaskComplete()) {
            cancel();
            Player player = task.getPlayer();
            imposterCraft.getArenaManager().getArena(player).getTaskManager().completeTask(player.getUniqueId(), task.getTaskType());
            task.complete();
            return;
        }

        task.changePosition();
    }
}
