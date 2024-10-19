package com.imposter.imposter.tasks;

import com.imposter.imposter.ImposterCraft;
import com.imposter.imposter.tasks.runnables.MedicalScanRunnable;
import com.imposter.imposter.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import static com.imposter.imposter.utils.Messages.sendRedMessageToPlayer;

public class MedicalScanTask extends PlayerTask {

    private final ImposterCraft imposterCraft;
    private final BossBar statusBar;
    private MedicalScanRunnable runnable;
    private double progress;

    public static final String MEDICAL_TASK_TITLE = ChatColor.RED + ChatColor.BOLD.toString() + "Medical Scan";

    public MedicalScanTask(ImposterCraft imposterCraft, Player player) {
        super(player, MEDICAL_TASK_TITLE, "Scan complete", Tasks.MEDICAL_SCAN);
        this.imposterCraft = imposterCraft;

        statusBar = Bukkit.createBossBar(
                ChatColor.GREEN + ChatColor.BOLD.toString() + "Medical Scan",
                BarColor.GREEN,
                BarStyle.SOLID
        );
        statusBar.setProgress(progress);
        statusBar.addPlayer(player);
    }

    public void updateProgress() {
        progress += 0.1;
        statusBar.setProgress(progress);
    }

    public boolean isTaskComplete() {
        return progress >= 0.9;
    }

    @Override
    public void cancel() {
        runnable.cancel();
        statusBar.removeAll();
        sendRedMessageToPlayer(super.getPlayer(), "Could not complete medical scan. Do not move!");
    }

    @Override
    public void complete() {
        statusBar.removeAll();
        super.complete();
    }

    @Override
    public void openGui() {
        runnable = new MedicalScanRunnable(imposterCraft, this);
        runnable.start();
    }
}
