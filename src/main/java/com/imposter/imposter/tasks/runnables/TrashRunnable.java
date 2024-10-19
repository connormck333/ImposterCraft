package com.imposter.imposter.tasks.runnables;

import com.imposter.imposter.ImposterCraft;
import com.imposter.imposter.tasks.TrashTask;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TrashRunnable extends BukkitRunnable {

    private ImposterCraft imposterCraft;
    private TrashTask task;

    private int slotsToFall = 6;

    public TrashRunnable(ImposterCraft imposterCraft, TrashTask task) {
        this.imposterCraft = imposterCraft;
        this.task = task;
    }

    public void start() {
        task.setStatusYellow();
        runTaskTimer(imposterCraft, 0, 10);
    }

    @Override
    public void run() {
        if (slotsToFall == 0) {
            cancel();
            task.setStatusGreen();
            Player player = task.getPlayer();
            imposterCraft.getArenaManager().getArena(player).getTaskManager().completeTask(player.getUniqueId(), task.getTaskType());
            task.complete();
            return;
        }

        task.dropSlots(7 - slotsToFall);

        slotsToFall--;
    }
}
