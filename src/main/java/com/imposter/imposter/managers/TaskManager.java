package com.imposter.imposter.managers;

import com.imposter.imposter.ImposterCraft;
import com.imposter.imposter.instances.Arena;
import com.imposter.imposter.instances.locations.TaskLocation;
import com.imposter.imposter.sabotages.guis.OxygenSabotageGui;
import com.imposter.imposter.sabotages.guis.ReactorMeltdownGui;
import com.imposter.imposter.tasks.*;
import com.imposter.imposter.tasks.instances.AssignedTask;
import com.imposter.imposter.utils.ConfigManager;
import com.imposter.imposter.utils.Tasks;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

import static com.imposter.imposter.utils.ConfigManager.getTaskLocations;
import static com.imposter.imposter.utils.Messages.sendRedMessageToPlayer;
import static com.imposter.imposter.utils.Tasks.*;
import static com.imposter.imposter.utils.Tasks.OXYGEN_DEPLETION;

public class TaskManager {

    private final ImposterCraft imposterCraft;
    private final Arena arena;

    private final Map<UUID, List<AssignedTask>> playerTasks;
    private final Map<Location, String> taskLocations;
    private final Map<UUID, PlayerTask> openTasks;
    @Getter
    private final int numTasksPerPlayer;
    @Getter
    private int completedTasksCount;

    public TaskManager(ImposterCraft imposterCraft, Arena arena) {
        this.imposterCraft = imposterCraft;
        this.arena = arena;

        this.playerTasks = new HashMap<>();
        this.taskLocations = createTaskLocationsMap();
        this.openTasks = new HashMap<>();
        this.numTasksPerPlayer = ConfigManager.getNumTasksPerPlayer();
        this.completedTasksCount = 0;
    }

    public PlayerTask getPlayerOpenTask(UUID uuid) {
        return this.openTasks.get(uuid);
    }

    public void setPlayerOpenTask(UUID uuid, PlayerTask task) {
        this.openTasks.put(uuid, task);
    }

    public void setPlayerTasks(UUID uuid, List<AssignedTask> tasks) {
        this.playerTasks.put(uuid, tasks);
    }

    public void completeTask(UUID uuid, Tasks task) {
        this.openTasks.remove(uuid);
        completePlayerTask(uuid, task);

        if (areAllTasksCompleted()) {
            arena.endGame(false);
        }
    }

    public void closeTask(UUID uuid) {
        PlayerTask task = openTasks.get(uuid);
        if (task != null) {
            task.cancel();
            openTasks.remove(uuid);
        }
    }

    public void clearPlayerTasks(Player player) {
        player.closeInventory();
        UUID uuid = player.getUniqueId();
        if (openTasks.get(uuid) != null) {
            openTasks.remove(uuid);
        }
    }

    public void addTaskLocation(Location blockLocation, String command) {
        taskLocations.put(blockLocation, command);
    }

    public String getTaskFromLocation(Location location) {
        return taskLocations.get(location);
    }

    public boolean areAllTaskLocationsSet() {
        Set<Tasks> existingTasks = new HashSet<>();
        int reactorCount = 0;
        int oxygenCount = 0;

        for (Location location : taskLocations.keySet()) {
            try {
                Tasks task = Tasks.valueOf(taskLocations.get(location).toUpperCase());
                existingTasks.add(task);
                if (task == REACTOR_MELTDOWN) {
                    reactorCount++;
                } else if (task == OXYGEN_DEPLETION) {
                    oxygenCount++;
                }
            } catch (Exception ignore) {}
        }

        return existingTasks.containsAll(List.of(Tasks.values())) && reactorCount > 1 && oxygenCount > 1;
    }

    public boolean areAllTasksCompleted() {
        return completedTasksCount == ((arena.getPlayers().size() - arena.getImposters().size()) * numTasksPerPlayer);
    }

    public AssignedTask generateRandomTask(Player player, List<AssignedTask> tasks) {
        Tasks randomTask = Tasks.getRandomTask();
        while (arena.getTaskManager().playerAlreadyAssignedTaskAndNotComplete(tasks, randomTask) || randomTask.isSabotage()) {
            randomTask = Tasks.getRandomTask();
        }

        return new AssignedTask(player, randomTask);
    }

    public void selectTask(Player player, String command, Location taskLocation) {
        if (openTasks.get(player.getUniqueId()) != null) {
            return;
        }

        if (command.equalsIgnoreCase(String.valueOf(UPLOAD))) {
            UploadTask task = new UploadTask(imposterCraft, player, false);
            startTask(player, task);
        } else if (command.equalsIgnoreCase(String.valueOf(DOWNLOAD))) {
            UploadTask task = new UploadTask(imposterCraft, player, true);
            startTask(player, task);
        } else if (command.equalsIgnoreCase(String.valueOf(SHIELDS))) {
            ShieldsTask task = new ShieldsTask(player);
            startTask(player, task);
        } else if (command.equalsIgnoreCase(String.valueOf(EMPTY_TRASH))) {
            TrashTask task = new TrashTask(imposterCraft, player);
            startTask(player, task);
        } else if (command.equalsIgnoreCase(String.valueOf(CLEAR_VENT))) {
            VentTask task = new VentTask(player);
            startTask(player, task);
        } else if (command.equalsIgnoreCase(String.valueOf(NAVIGATION))) {
            NavigationTask task = new NavigationTask(player);
            startTask(player, task);
        } else if (command.equalsIgnoreCase(String.valueOf(ASTEROIDS))) {
            AsteroidsTask task = new AsteroidsTask(imposterCraft, player);
            startTask(player, task);
        } else if (command.equalsIgnoreCase(String.valueOf(ALIGN_ENGINE))) {
            AlignEngineTask task = new AlignEngineTask(player);
            startTask(player, task);
        } else if (command.equalsIgnoreCase(String.valueOf(NUMBERS))) {
            NumberTask task = new NumberTask(player);
            startTask(player, task);
        } else if (command.equalsIgnoreCase(String.valueOf(ENABLE_POWER))) {
            EnablePowerTask task = new EnablePowerTask(imposterCraft, player);
            startTask(player, task);
        } else if (command.equalsIgnoreCase(String.valueOf(DIVERT_POWER))) {
            DivertPowerTask task = new DivertPowerTask(imposterCraft, player);
            startTask(player, task);
        } else if (command.equalsIgnoreCase(String.valueOf(CALIBRATE_DISTRIBUTOR))) {
            CalibrateDistributorTask task = new CalibrateDistributorTask(imposterCraft, player);
            startTask(player, task);
        } else if (command.equalsIgnoreCase(String.valueOf(MEDICAL_SCAN))) {
            MedicalScanTask task = new MedicalScanTask(imposterCraft, player);
            startTask(player, task);
        } else if (command.equalsIgnoreCase(String.valueOf(FIX_LIGHTS))) {
            DivertPowerTask task = new DivertPowerTask(imposterCraft, player, true);
            arena.getLightsManager().startTask(player, task);
        } else if (command.equalsIgnoreCase(String.valueOf(REACTOR_MELTDOWN))) {
            ReactorMeltdownGui task = new ReactorMeltdownGui(player, taskLocation);
            arena.getReactorManager().startTask(player, task);
        } else if (command.equalsIgnoreCase(String.valueOf(OXYGEN_DEPLETION))) {
            OxygenSabotageGui task = new OxygenSabotageGui(player, taskLocation);
            arena.getOxygenManager().startTask(player, task);
        }
    }

    private void startTask(Player player, PlayerTask task) {
        if (arena.isPlayerImposter(player)) {
            sendRedMessageToPlayer(player, "Imposters cannot do tasks!");
            return;
        } else if (!doesPlayerHaveTaskAndNotComplete(player.getUniqueId(), task.getTaskType())) {
            sendRedMessageToPlayer(player, "You have already completed this task or it has not been assigned to you!");
            return;
        } else if (openTasks.get(player.getUniqueId()) != null) {
            sendRedMessageToPlayer(player, "You cannot do this right now. Please try again in a few seconds...");
            return;
        }

        setPlayerOpenTask(player.getUniqueId(), task);
        task.openGui();
    }

    private void completePlayerTask(UUID uuid, Tasks task) {
        for (AssignedTask assignedTask : this.playerTasks.get(uuid)) {
            if (assignedTask.getTaskType() == task) {
                assignedTask.setComplete();
                arena.getGame().getGameScoreboard().resetPlayerScoreboardTask(uuid, task);
                completedTasksCount++;
                break;
            }
        }
    }

    private boolean doesPlayerHaveTaskAndNotComplete(UUID uuid, Tasks task) {
        return playerAlreadyAssignedTaskAndNotComplete(this.playerTasks.get(uuid), task);
    }

    public boolean playerAlreadyAssignedTaskAndNotComplete(List<AssignedTask> assignedTasks, Tasks task) {
        for (AssignedTask assignedTask : assignedTasks) {
            if (assignedTask.getTaskType() == task) {
                return !assignedTask.isComplete();
            }
        }

        return false;
    }

    private Map<Location, String> createTaskLocationsMap() {
        List<TaskLocation> list = getTaskLocations(arena.getId());
        Map<Location, String> map = new HashMap<>();

        for (TaskLocation taskLocation : list) {
            map.put(taskLocation.getLocation(), taskLocation.getCommand());
        }

        return map;
    }
}
