package com.imposter.imposter.managers.sabotages;

import com.imposter.imposter.ImposterCraft;
import com.imposter.imposter.instances.Arena;
import com.imposter.imposter.sabotages.guis.ReactorMeltdownGui;
import com.imposter.imposter.sabotages.runnables.ReactorMeltdownRunnable;
import com.imposter.imposter.tasks.PlayerTask;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static com.imposter.imposter.utils.Messages.sendUnableMessageToPlayer;
import static com.imposter.imposter.utils.Utils.locationEquals;

public class ReactorManager implements ISabotageManager, ISabotageGuiManager {

    private ImposterCraft imposterCraft;
    private Arena arena;

    private final ReactorMeltdownRunnable runnable;
    private final List<ReactorMeltdownGui> reactorGuisOpen;
    private boolean reactorMeltDown = false;

    public ReactorManager(ImposterCraft imposterCraft, Arena arena) {
        this.imposterCraft = imposterCraft;
        this.arena = arena;
        this.runnable = new ReactorMeltdownRunnable(imposterCraft, arena);
        this.reactorGuisOpen = new ArrayList<>();
    }

    @Override
    public void start() {
        this.reactorMeltDown = true;
        arena.sendMessage(ChatColor.RED + "The reactor is melting!");
        runnable.start();
    }

    @Override
    public boolean isFixed() {
        if (reactorGuisOpen.size() < 2) {
            return false;
        }

        for (int i = 0; i < reactorGuisOpen.size(); i++) {
            ReactorMeltdownGui task1 = reactorGuisOpen.get(i);
            for (int j = i + 1; j < reactorGuisOpen.size(); j++) {
                ReactorMeltdownGui task2 = reactorGuisOpen.get(j);
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
        if (isActive()) {
            arena.getTaskManager().setPlayerOpenTask(player.getUniqueId(), task);
            this.reactorGuisOpen.add((ReactorMeltdownGui) task);
            task.openGui();
        } else {
            sendUnableMessageToPlayer(player);
        }
    }

    public void removeGuiByPlayer(Player player) {
        int index = -1;
        for (int i = 0; i < reactorGuisOpen.size(); i++) {
            ReactorMeltdownGui gui = reactorGuisOpen.get(i);
            if (gui.getPlayer() == player) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            reactorGuisOpen.remove(index);
        }
    }

    @Override
    public void end() {
        this.reactorMeltDown = false;
        arena.getSabotageManager().setSabotageLastUse();
        arena.sendMessage(ChatColor.GREEN + "Reactor temperature neutralized!");
    }

    public List<ReactorMeltdownGui> getGuisOpen() {
        return this.reactorGuisOpen;
    }

    @Override
    public void clearGuis() {
        this.reactorGuisOpen.clear();
    }

    @Override
    public boolean isActive() {
        return this.reactorMeltDown;
    }

    public void cancel() {
        if (reactorMeltDown) {
            this.runnable.cancel();
            this.reactorMeltDown = false;
        }
    }
}
