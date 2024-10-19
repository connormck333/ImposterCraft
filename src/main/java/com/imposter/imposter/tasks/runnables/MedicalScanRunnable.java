package com.imposter.imposter.tasks.runnables;

import com.imposter.imposter.ImposterCraft;
import com.imposter.imposter.tasks.MedicalScanTask;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class MedicalScanRunnable extends BukkitRunnable {

    private ImposterCraft imposterCraft;
    private MedicalScanTask task;

    public MedicalScanRunnable(ImposterCraft imposterCraft, MedicalScanTask task) {
        this.imposterCraft = imposterCraft;
        this.task = task;
    }

    public void start() {
        runTaskTimer(imposterCraft, 0, 20);
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

        task.updateProgress();
    }
}
