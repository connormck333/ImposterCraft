package com.imposter.imposter.tasks.instances;

import com.imposter.imposter.utils.Tasks;
import org.bukkit.entity.Player;

public class AssignedTask {

    private final Player player;
    private final Tasks taskType;
    private boolean complete;

    public AssignedTask(Player player, Tasks taskType) {
        this.player = player;
        this.taskType = taskType;
        this.complete = false;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Tasks getTaskType() {
        return this.taskType;
    }

    public boolean isComplete() {
        return this.complete;
    }

    public void setComplete() {
        this.complete = true;
    }
}
