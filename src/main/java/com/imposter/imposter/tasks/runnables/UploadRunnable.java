package com.imposter.imposter.tasks.runnables;

import com.imposter.imposter.ImposterCraft;
import com.imposter.imposter.tasks.UploadTask;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class UploadRunnable extends BukkitRunnable {

    private final ImposterCraft imposterCraft;
    private final UploadTask task;

    private int slotsRemaining = 9;

    public UploadRunnable(ImposterCraft imposterCraft, UploadTask task) {
        this.imposterCraft = imposterCraft;
        this.task = task;
    }

    public void start() {
        runTaskTimer(imposterCraft, 0, 10);
    }

    @Override
    public void run() {
        if (slotsRemaining == 0) {
            cancel();
            Player player = task.getPlayer();
            imposterCraft.getArenaManager().getArena(player).getTaskManager().completeTask(player.getUniqueId(), task.getTaskType());
            task.complete();
            return;
        }

        task.fillUploadSlot(slotsRemaining);
        slotsRemaining--;
    }

}
