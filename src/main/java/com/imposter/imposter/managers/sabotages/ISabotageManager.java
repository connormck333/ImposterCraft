package com.imposter.imposter.managers.sabotages;

import com.imposter.imposter.tasks.PlayerTask;
import org.bukkit.entity.Player;

public interface ISabotageManager {

    public void start();

    public void end();

    public void startTask(Player player, PlayerTask playerTask);

    public boolean isActive();
}
