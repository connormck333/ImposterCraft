package com.imposter.imposter.managers.sabotages;

import com.imposter.imposter.ImposterCraft;
import com.imposter.imposter.instances.Arena;
import com.imposter.imposter.sabotages.guis.OxygenSabotageGui;
import com.imposter.imposter.sabotages.runnables.OxygenRunnable;
import com.imposter.imposter.tasks.PlayerTask;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static com.imposter.imposter.utils.Messages.sendUnableMessageToPlayer;
import static com.imposter.imposter.utils.Utils.locationEquals;

public class OxygenManager implements ISabotageManager, ISabotageGuiManager {

    private ImposterCraft imposterCraft;
    private Arena arena;

    private final OxygenRunnable runnable;
    private final List<OxygenSabotageGui> oxygenGuisOpen;
    private boolean oxygenDepleting = false;

    public OxygenManager(ImposterCraft imposterCraft, Arena arena) {
        this.imposterCraft = imposterCraft;
        this.arena = arena;

        this.runnable = new OxygenRunnable(imposterCraft, arena);
        this.oxygenGuisOpen = new ArrayList<>();
    }

    @Override
    public void start() {
        this.oxygenDepleting = true;
        arena.sendMessage(ChatColor.RED + "The oxygen supply is depleting!");
        runnable.start();
    }

    @Override
    public boolean isFixed() {
        if (oxygenGuisOpen.size() < 2) {
            return false;
        }

        for (int i = 0; i < oxygenGuisOpen.size(); i++) {
            OxygenSabotageGui task1 = oxygenGuisOpen.get(i);
            for (int j = i + 1; j < oxygenGuisOpen.size(); j++) {
                OxygenSabotageGui task2 = oxygenGuisOpen.get(j);
                if (
                        !locationEquals(task1.getLocation(), task2.getLocation())
                                && task1.isComplete() && task2.isComplete()
                ) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void startTask(Player player, PlayerTask task) {
        if (oxygenDepleting) {
            arena.getTaskManager().setPlayerOpenTask(player.getUniqueId(), task);
            oxygenGuisOpen.add((OxygenSabotageGui) task);
            task.openGui();
        } else {
            sendUnableMessageToPlayer(player);
        }
    }

    @Override
    public void end() {
        this.oxygenDepleting = false;
        arena.getSabotageManager().setSabotageLastUse();
        arena.sendMessage(ChatColor.GREEN + "Oxygen restored!");
    }

    @Override
    public void clearGuis() {
        this.oxygenGuisOpen.clear();
    }

    @Override
    public boolean isActive() {
        return this.oxygenDepleting;
    }

    public void cancel() {
        if (oxygenDepleting) {
            this.runnable.cancel();
            oxygenDepleting = false;
        }
    }
}
