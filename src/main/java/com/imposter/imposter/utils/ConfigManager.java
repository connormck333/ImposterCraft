package com.imposter.imposter.utils;

import com.imposter.imposter.ImposterCraft;
import com.imposter.imposter.instances.locations.DoorLocation;
import com.imposter.imposter.instances.locations.VentLocation;
import com.imposter.imposter.instances.locations.TaskLocation;
import com.imposter.imposter.roles.crewmate.CrewmateRoleEnum;
import com.imposter.imposter.roles.imposter.ImposterRoleEnum;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

import static com.imposter.imposter.utils.Messages.sendRedMessageToPlayer;
import static com.imposter.imposter.utils.Utils.locationEquals;

public class ConfigManager {

    private static ImposterCraft imposterCraft;
    private static FileConfiguration config;

    public static void setupConfig(ImposterCraft imposterCraft) {
        ConfigManager.config = imposterCraft.getConfig();
        ConfigManager.imposterCraft = imposterCraft;
        imposterCraft.saveDefaultConfig();
    }

    public static int getRequiredPlayers() {
        return config.getInt("required-players");
    }

    public static int getCountdownSeconds() {
        return config.getInt("countdown-seconds");
    }

    public static int getMeetingSeconds() {
        return config.getInt("meeting-seconds");
    }

    public static Location getLobbySpawn() {
        ConfigurationSection section = config.getConfigurationSection("lobby-spawn");
        return loadLocation(section, "");
    }

    public static List<Location> getArenaSpawns(int arenaId) {
        List<Location> spawns = new ArrayList<>();
        String path = "arenas." + arenaId + ".spawns.";
        ConfigurationSection section = config.getConfigurationSection(path);
        if (section == null) {
            return spawns;
        }
        for (String spawn : section.getKeys(false)) {
            spawns.add(loadLocation(section, spawn + "."));
        }

        return spawns;
    }

    public static Location getArenaLobbySpawn(int arenaId) {
        String path = "arenas." + arenaId + ".arena-lobby";
        ConfigurationSection section = config.getConfigurationSection(path);
        return loadLocation(section, "");
    }

    public static boolean canChatInLobby() {
        return config.getBoolean("main-lobby-chat-enabled");
    }

    public static int getNumImposters() {
        return config.getBoolean("two-imposters-enabled") ? 2 : 1;
    }

    public static int getKillCooldown() {
        return config.getInt("imposter-kill-cooldown-seconds");
    }

    public static int getSabotageCooldown() {
        return config.getInt("imposter-sabotage-cooldown-seconds");
    }

    public static int getSabotageSecondsBeforeEndGame() {
        return config.getInt("sabotage-seconds-before-end-game");
    }

    public static int getNumTasksPerPlayer() {
        return Math.min(config.getInt("num-tasks-per-player"), 10);
    }

    public static int getDoorsShutSeconds() {
        return config.getInt("doors-shut-seconds") * 20;
    }

    public static List<ImposterRoleEnum> getEnabledImposterRoles() {
        List<ImposterRoleEnum> enabledRoles = new ArrayList<>();
        if (!config.getBoolean("roles-enabled")) {
            return enabledRoles;
        }

        ConfigurationSection section = config.getConfigurationSection("imposter-roles");
        if (section != null) {
            for (String role : section.getKeys(false)) {
                if (section.getBoolean(role)) {
                    try {
                        enabledRoles.add(ImposterRoleEnum.fromString(role));
                    } catch (IllegalArgumentException ignored) {
                    }
                }
            }
        }

        return enabledRoles;
    }

    public static List<CrewmateRoleEnum> getEnabledCrewmateRoles() {
        List<CrewmateRoleEnum> enabledRoles = new ArrayList<>();
        if (!config.getBoolean("roles-enabled")) {
            return enabledRoles;
        }

        ConfigurationSection section = config.getConfigurationSection("crewmate-roles");
        if (section != null) {
            for (String role : section.getKeys(false)) {
                if (section.getBoolean(role)) {
                    try {
                        enabledRoles.add(CrewmateRoleEnum.fromString(role));
                    } catch (IllegalArgumentException ignored) {}
                }
            }
        }

        return enabledRoles;
    }

    public static List<Integer> getArenaIds() {
        ConfigurationSection arenasSection = config.getConfigurationSection("arenas");

        if (arenasSection == null) {
            return Collections.emptyList();
        }

        return arenasSection.getKeys(false).stream()
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    public static Location getCameraJoinLocation(int arenaId) {
        String path = "arenas." + arenaId + ".camera-join-location";
        ConfigurationSection section = config.getConfigurationSection(path);
        if (section != null) {
            return loadLocation(section, "");
        }

        return null;
    }

    public static List<Location> getArenaCameraLocations(int arenaId) {
        List<Location> cameraLocations = new ArrayList<>();
        String path = "arenas." + arenaId + ".camera-locations";
        ConfigurationSection section = config.getConfigurationSection(path);
        if (section != null) {
            for (String key : section.getKeys(false)) {
                cameraLocations.add(loadLocation(section.getConfigurationSection(key), ""));
            }
        }

        return cameraLocations;
    }

    public static List<TaskLocation> getTaskLocations(int arenaId) {
        List<TaskLocation> taskLocations = new ArrayList<>();
        String path = "arenas." + arenaId + ".task-locations";
        ConfigurationSection section = config.getConfigurationSection(path);
        if (section != null) {
            for (String key : section.getKeys(false)) {
                TaskLocation taskLocation = loadTaskLocation(section.getConfigurationSection(key), Integer.parseInt(key));
                taskLocations.add(taskLocation);
            }
        }

        return taskLocations;
    }

    public static Location getMeetingSignLocation(int arenaId) {
        String path = "arenas." + arenaId + ".meeting-sign-location";
        ConfigurationSection section = config.getConfigurationSection(path);

        return loadLocation(section, "");
    }

    public static void removeTaskLocationFromConfig(Player player, Location location) {
        for (int arenaId : getArenaIds()) {
            Location meetingLocation = getMeetingSignLocation(arenaId);
            if (meetingLocation != null && locationEquals(location, meetingLocation)) {
                deleteMeetingLocation(arenaId);
                sendRedMessageToPlayer(player, "Deleted meeting start location.");
            }

            Location camerasLocation = getCameraJoinLocation(arenaId);
            if (camerasLocation != null && locationEquals(location, camerasLocation)) {
                deleteCamerasJoinLocation(arenaId);
                sendRedMessageToPlayer(player, "Deleted cameras join location.");
            }

            List<TaskLocation> taskLocations = getTaskLocations(arenaId);
            for (TaskLocation taskLocation : taskLocations) {
                if (taskLocation.equals(location)) {
                    deleteTaskLocation(arenaId, taskLocation.getId());
                    sendRedMessageToPlayer(player, "Deleted task location.");
                    break;
                }
            }
        }
    }

    public static void removeVentLocationFromConfig(Player player, Location location) {
        for (int arenaId : getArenaIds()) {
            Map<String, ArrayList<VentLocation>> ventLocations = getArenaVentLocations(arenaId);
            for (String key : ventLocations.keySet()) {
                for (VentLocation ventLocation : ventLocations.get(key)) {
                    if (ventLocation.getLocation().equals(location)) {
                        deleteVentLocation(arenaId, ventLocation.getId());
                        sendRedMessageToPlayer(player, "Deleted vent location.");
                        break;
                    }
                }
            }
        }
    }

    public static Map<String, ArrayList<VentLocation>> getArenaVentLocations(int arenaId) {
        Map<String, ArrayList<VentLocation>> ventLocations = new HashMap<>();
        String path = "arenas." + arenaId + ".vent-locations";
        ConfigurationSection section = config.getConfigurationSection(path);
        if (section != null) {
            for (String key : section.getKeys(false)) {
                VentLocation ventLocation = loadVentLocation(section.getConfigurationSection(key), Integer.parseInt(key));
                if (ventLocations.get(ventLocation.getCategory()) != null) {
                    ventLocations.get(ventLocation.getCategory()).add(ventLocation);
                } else {
                    ArrayList<VentLocation> ventList = new ArrayList<>();
                    ventList.add(ventLocation);
                    ventLocations.put(ventLocation.getCategory(), ventList);
                }
            }
        }

        return ventLocations;
    }

    public static Map<String, ArrayList<DoorLocation>> getArenaDoorLocations(int arenaId) {
        Map<String, ArrayList<DoorLocation>> doorLocations = new HashMap<>();
        String path = "arenas." + arenaId + ".doors";
        ConfigurationSection section = config.getConfigurationSection(path);
        if (section != null) {
            for (String key : section.getKeys(false)) {
                DoorLocation doorLocation = loadDoorLocation(section.getConfigurationSection(key), Integer.parseInt(key));
                if (doorLocations.get(doorLocation.getTitle()) != null) {
                    doorLocations.get(doorLocation.getTitle()).add(doorLocation);
                } else {
                    ArrayList<DoorLocation> doorList = new ArrayList<>();
                    doorList.add(doorLocation);
                    doorLocations.put(doorLocation.getTitle(), doorList);
                }

            }
        }

        return doorLocations;
    }

    /*
        Save methods
    */

    public static void createNewArena(int arenaId) {
        String path = "arenas";
        ConfigurationSection section = config.getConfigurationSection(path);
        if (section == null) {
            section = config.createSection(path);
        }

        section.createSection(String.valueOf(arenaId));
        imposterCraft.saveConfig();
    }

    public static void saveArenaCameraJoinLocation(int arenaId, Location location) {
        String path = "arenas." + arenaId + ".camera-join-location";
        ConfigurationSection section = config.getConfigurationSection(path);
        if (section == null) {
            section = config.createSection(path);
        }

        saveLocation(section, location);
        imposterCraft.saveConfig();
    }

    public static void saveArenaCameraLocation(int arenaId, Location location) {
        String path = "arenas." + arenaId + ".camera-locations";
        ConfigurationSection section = config.getConfigurationSection(path);
        if (section == null) {
            section = config.createSection(path);
        }

        int id = getArenaCameraLocations(arenaId).size();
        section = section.createSection(String.valueOf(id));
        saveLocation(section, location);

        imposterCraft.saveConfig();
    }

    public static void saveTaskLocation(int arenaId, TaskLocation taskLocation) {
        String path = "arenas." + arenaId + ".task-locations";
        ConfigurationSection section = config.getConfigurationSection(path);
        if (section == null) {
            section = config.createSection(path);
        }

        int id = 0;
        for (TaskLocation task : getTaskLocations(arenaId)) {
            if (task.getId() > id) {
                id = task.getId();
            }
        }
        id++;
        section = section.createSection(String.valueOf(id));

        Location location = taskLocation.getLocation();
        section.set("command", taskLocation.getCommand());
        section = section.createSection("location");
        saveLocation(section, location, true);

        imposterCraft.saveConfig();
    }

    public static void saveArenaMeetingSignLocation(int arenaId, Location location) {
        String path = "arenas." + arenaId + ".meeting-sign-location";
        ConfigurationSection section = config.getConfigurationSection(path);
        if (section == null) {
            section = config.createSection(path);
        }

        saveLocation(section, location, true);
        imposterCraft.saveConfig();
    }

    public static boolean saveArenaSpawnLocation(int arenaId, Location location) {
        if (!imposterCraft.getArenaManager().doesArenaExist(arenaId)) {
            return false;
        }

        try {
            String path = "arenas." + arenaId + ".spawns";
            ConfigurationSection section = config.getConfigurationSection(path);
            if (section == null) {
                section = config.createSection(path);
            }

            int id = getArenaSpawns(arenaId).size();
            section = section.createSection(String.valueOf(id));

            saveLocation(section, location);
            imposterCraft.saveConfig();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean saveMainLobbyLocation(Location location) {
        try {
            String path = "lobby-spawn";
            ConfigurationSection section = config.getConfigurationSection(path);
            if (section == null) {
                section = config.createSection(path);
            }

            saveLocation(section, location);
            imposterCraft.saveConfig();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean saveArenaLobbyLocation(int arenaId, Location location) {
        if (!imposterCraft.getArenaManager().doesArenaExist(arenaId)) {
            return false;
        }

        try {
            String path = "arenas." + arenaId + ".arena-lobby";
            ConfigurationSection section = config.getConfigurationSection(path);
            if (section == null) {
                section = config.createSection(path);
            }

            saveLocation(section, location);
            imposterCraft.saveConfig();
        } catch (Exception ignored) {
            return false;
        }

        return true;
    }

    public static int saveArenaDoorLocation(int arenaId, Location pos1, Location pos2, String item, String title) {
        if (!imposterCraft.getArenaManager().doesArenaExist(arenaId)) {
            return -1;
        }

        String path = "arenas." + arenaId + ".doors";
        try {
            ConfigurationSection section = config.getConfigurationSection(path);
            if (section == null) {
                section = config.createSection(path);
            }

            String id = String.valueOf(getTotalDoorLocations(getArenaDoorLocations(arenaId)));
            section = section.createSection(id);
            section.set("item_type", item);
            section.set("title", title);
            section.createSection("pos1");
            section = section.createSection("pos2");
            ConfigurationSection pos1Section = config.getConfigurationSection(path + "." + id + "." + ".pos1");
            saveLocation(pos1Section, pos1, true);
            saveLocation(section, pos2, true);
            imposterCraft.saveConfig();
            return Integer.parseInt(id);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    public static int saveVentLocation(int arenaId, Location location, String category) {
        if (!imposterCraft.getArenaManager().doesArenaExist(arenaId)) {
            return -1;
        }

        String path = "arenas." + arenaId + ".vent-locations";
        try {
            ConfigurationSection section = config.getConfigurationSection(path);
            if (section == null) {
                section = config.createSection(path);
            }

            String id = String.valueOf(getTotalVentLocations(getArenaVentLocations(arenaId)));
            section = section.createSection(id);
            section.set("category", category);
            section = section.createSection("location");
            saveLocation(section, location);
            imposterCraft.saveConfig();
            return Integer.parseInt(id);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    /*
        Delete methods
    */

    private static void deleteTaskLocation(int arenaId, int taskId) {
        String path = "arenas." + arenaId + ".task-locations";
        deleteLocation(path, taskId);
    }

    private static void deleteVentLocation(int arenaId, int ventId) {
        String path = "arenas." + arenaId + ".vent-locations";
        deleteLocation(path, ventId);
    }

    private static void deleteMeetingLocation(int arenaId) {
        String key = "meeting-sign-location";
        deleteLocation(arenaId, key);
    }

    private static void deleteCamerasJoinLocation(int arenaId) {
        String key = "camera-join-location";
        deleteLocation(arenaId, key);
    }

    private static void deleteLocation(String path, int locationId) {
        ConfigurationSection section = config.getConfigurationSection(path);

        if (section == null) {
            return;
        }

        section.set(String.valueOf(locationId), null);
        imposterCraft.saveConfig();
    }

    private static void deleteLocation(int arenaId, String key) {
        ConfigurationSection section = config.getConfigurationSection("arenas." + arenaId);

        if (section == null) {
            return;
        }

        section.set(key, null);
        imposterCraft.saveConfig();
    }

    /*
        Private methods
    */

    private static TaskLocation loadTaskLocation(ConfigurationSection section, int key) {
        String command = section.getString("command");
        Location location = loadLocation(section, "location.");

        return new TaskLocation(location, command, key);
    }

    private static DoorLocation loadDoorLocation(ConfigurationSection section, int id) {
        Location pos1 = loadLocation(section, "pos1.");
        Location pos2 = loadLocation(section, "pos2.");
        String material = section.getString("item_type");
        String title = section.getString("title");

        return new DoorLocation(pos1, pos2, id, material, title);
    }

    private static VentLocation loadVentLocation(ConfigurationSection section, int id) {
        Location location = loadLocation(section, "location.");
        String category = section.getString("category");

        return new VentLocation(location, category, id);
    }

    private static Location loadLocation(ConfigurationSection section, String key) {
        if (section == null) {
            return null;
        }
        return new Location(
                org.bukkit.Bukkit.getWorld(section.getString(key + "world")),
                section.getDouble(key + "x"),
                section.getDouble(key + "y"),
                section.getDouble(key + "z"),
                (float) section.getDouble(key + "yaw"),
                (float) section.getDouble(key + "pitch")
        );
    }

    private static void saveLocation(ConfigurationSection section, Location location, boolean saveByBlock) {
        section.set("world", location.getWorld().getName());
        section.set("x", saveByBlock ? location.getBlockX() : location.getX());
        section.set("y", saveByBlock ? location.getBlockY() : location.getY());
        section.set("z", saveByBlock ? location.getBlockZ() : location.getZ());
        section.set("yaw", location.getYaw());
        section.set("pitch", location.getPitch());
    }

    private static void saveLocation(ConfigurationSection section, Location location) {
        saveLocation(section, location, false);
    }

    private static int getTotalDoorLocations(Map<String, ArrayList<DoorLocation>> doorMap) {
        int totalSize = 0;

        for (ArrayList<DoorLocation> doorLocations : doorMap.values()) {
            totalSize += doorLocations.size();
        }

        return totalSize;
    }

    private static int getTotalVentLocations(Map<String, ArrayList<VentLocation>> ventMap) {
        int totalSize = 0;

        for (ArrayList<VentLocation> locations : ventMap.values()) {
            totalSize += locations.size();
        }

        return totalSize;
    }
}
