package com.imposter.imposter.instances;

import com.imposter.imposter.managers.players.CrewmateManager;
import com.imposter.imposter.managers.players.ImposterManager;
import com.imposter.imposter.roles.Role;
import com.imposter.imposter.roles.imposter.BountyHunter;
import com.imposter.imposter.roles.imposter.ImposterRole;
import com.imposter.imposter.tasks.instances.AssignedTask;
import com.imposter.imposter.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GameScoreboard {

    private final Arena arena;
    private final ScoreboardManager scoreboardManager;

    public GameScoreboard(Arena arena) {
        this.arena = arena;
        this.scoreboardManager = Bukkit.getScoreboardManager();
    }

    public void resetPlayerScoreboardTask(UUID uuid, Tasks task) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return;
        }
        Scoreboard board = player.getScoreboard();
        Objective objective = board.getObjective(player.getName() + "_tasks");
        if (objective != null) {
            String taskName = task.getString();
            int score = objective.getScore(ChatColor.RED + taskName).getScore();
            board.resetScores(ChatColor.RED + taskName);
            Score completedTask = objective.getScore(ChatColor.GREEN + taskName);
            completedTask.setScore(score);
        }
    }

    public Scoreboard createScoreboard(Player player, boolean isImposter) {
        Scoreboard board = scoreboardManager.getNewScoreboard();
        Objective objective = board.registerNewObjective(player.getName() + "_tasks", Criteria.DUMMY, ChatColor.GREEN + ChatColor.BOLD.toString() + (isImposter ? "Fake " : "") + "Tasks");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        String divider = ChatColor.YELLOW + "+-----------------+";
        objective.getScore(divider).setScore(20);

        List<AssignedTask> tasks = new ArrayList<>();
        int numTasksPerPlayer = arena.getTaskManager().getNumTasksPerPlayer();
        for (int i = 0; i < numTasksPerPlayer; i++) {
            AssignedTask assignedTask = arena.getTaskManager().generateRandomTask(player, tasks);
            tasks.add(assignedTask);

            objective.getScore(ChatColor.RED + assignedTask.getTaskType().getString()).setScore(numTasksPerPlayer - i);
        }

        UUID uuid = player.getUniqueId();
        String roleString = ChatColor.YELLOW + "You are the ";
        if (isImposter) {
            ImposterManager imposterManager = arena.getGame().getImposterManager();
            Role role = imposterManager.rolesManager().getRole(uuid);
            if (role != null) {
                objective.getScore(roleString + ChatColor.RED + role.title()).setScore(19);
                BountyHunter bountyHunter = imposterManager.rolesManager().bountyHunter();
                if (bountyHunter != null && bountyHunter.is(uuid)) {
                    bountyHunter.setBountyHunterKill(objective);
                }
            } else {
                objective.getScore(ChatColor.YELLOW + "You are " + ChatColor.RED + "Imposter").setScore(19);
            }
        } else {
            CrewmateManager crewmateManager = arena.getGame().getCrewmateManager();
            Role role = crewmateManager.rolesManager().getRole(uuid);
            if (role != null) {
                objective.getScore(roleString + ChatColor.GREEN + role.title()).setScore(19);
            } else {
                objective.getScore(ChatColor.YELLOW + "You are " + ChatColor.GREEN + "Crewmate").setScore(19);
            }
        }

        objective.getScore(ChatColor.GREEN + divider).setScore(17);
        arena.getTaskManager().setPlayerTasks(uuid, tasks);
        return board;
    }
}
