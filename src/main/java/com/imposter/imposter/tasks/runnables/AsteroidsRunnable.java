package com.imposter.imposter.tasks.runnables;

import com.imposter.imposter.ImposterCraft;
import com.imposter.imposter.tasks.AsteroidsTask;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class AsteroidsRunnable extends BukkitRunnable {

    private ImposterCraft imposterCraft;
    private AsteroidsTask task;
    private int count;

    private final int MAX_COUNT = 10;

    public AsteroidsRunnable(ImposterCraft imposterCraft, AsteroidsTask task) {
        this.imposterCraft = imposterCraft;
        this.task = task;
        count = 0;
    }

    public void start() {
        runTaskTimer(this.imposterCraft, 0 ,20);
    }

    @Override
    public void run() {
        if (task.isTaskComplete()) {
            cancel();
            Player player = task.getPlayer();
            imposterCraft.getArenaManager().getArena(player).getTaskManager().completeTask(player.getUniqueId(), task.getTaskType());
            task.complete();
            return;
        } else if (count >= MAX_COUNT) {
            cancel();
            task.fail();
            return;
        }

        task.dropSlots();
        count++;
    }
}
