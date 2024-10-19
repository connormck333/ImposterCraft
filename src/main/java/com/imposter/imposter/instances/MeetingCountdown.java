package com.imposter.imposter.instances;

import com.imposter.imposter.ImposterCraft;
import com.imposter.imposter.utils.ConfigManager;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class MeetingCountdown extends BukkitRunnable {

    private final ImposterCraft imposterCraft;
    private final Arena arena;
    private int countdownSeconds;
    private boolean active;

    public MeetingCountdown(ImposterCraft imposterCraft, Arena arena) {
        this.imposterCraft = imposterCraft;
        this.arena = arena;
        this.countdownSeconds = ConfigManager.getMeetingSeconds();
        this.active = false;
    }

    public void start() {
        if (this.active) {
            return;
        }
        this.active = true;
        runTaskTimer(imposterCraft, 0, 20);
    }

    @Override
    public void run() {
        if (countdownSeconds == 0) {
            cancel();
            this.active = false;
            arena.getMeetingManager().endEmergencyMeeting();
            return;
        }

        if (countdownSeconds <= 5 || countdownSeconds % 15 == 0) {
            arena.sendMessage(ChatColor.RED + "Meeting ends in " + countdownSeconds + " second" + (countdownSeconds == 1 ? "" : "s"));
        }

        countdownSeconds--;
    }
}
