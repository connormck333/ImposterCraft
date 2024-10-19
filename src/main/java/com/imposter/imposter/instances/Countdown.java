package com.imposter.imposter.instances;

import com.imposter.imposter.ImposterCraft;
import com.imposter.imposter.utils.ConfigManager;
import com.imposter.imposter.utils.GameState;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class Countdown extends BukkitRunnable {

    private ImposterCraft imposterCraft;
    private Arena arena;
    private int countdownSeconds;

    public Countdown(ImposterCraft imposterCraft, Arena arena) {
        this.imposterCraft = imposterCraft;
        this.arena = arena;
        this.countdownSeconds = ConfigManager.getCountdownSeconds();
    }

    public void start() {
        arena.setState(GameState.COUNTDOWN);
        runTaskTimer(imposterCraft, 0, 20);
    }

    @Override
    public void run() {
        if (countdownSeconds == 0) {
            cancel();
            arena.start();
            return;
        }

        if (countdownSeconds <= 10 || countdownSeconds % 15 == 0) {
            arena.sendMessage(ChatColor.GREEN + "Game will start in " + countdownSeconds + " second" + (countdownSeconds == 1 ? "" : "s"));
        }

        arena.sendTitle(ChatColor.GREEN.toString() + countdownSeconds + " second"+ (countdownSeconds == 1 ? "" : "s"), ChatColor.GRAY + "until game starts", 20);

        countdownSeconds--;
    }
}
