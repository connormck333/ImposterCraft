package com.imposter.imposter.instances;

import com.imposter.imposter.ImposterCraft;
import com.imposter.imposter.managers.*;
import com.imposter.imposter.managers.gameplay.CorpseManager;
import com.imposter.imposter.managers.gameplay.DeathManager;
import com.imposter.imposter.managers.gameplay.MeetingManager;
import com.imposter.imposter.managers.mechanics.CamerasManager;
import com.imposter.imposter.managers.mechanics.VentManager;
import com.imposter.imposter.managers.players.PlayerManager;
import com.imposter.imposter.managers.sabotages.*;
import com.imposter.imposter.utils.GameState;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.*;

import static com.imposter.imposter.utils.ConfigManager.*;
import static com.imposter.imposter.utils.Messages.*;

public class Arena {

    private final ImposterCraft imposterCraft;

    private final int id;
    private final Location arenaLobby;
    private final List<Location> spawns;

    private GameState state;
    private Countdown countdown;
    private Game game;

    private PlayerManager playerManager;
    private TaskManager taskManager;
    private MeetingManager meetingManager;
    private VentManager ventManager;
    private CamerasManager camerasManager;
    private CorpseManager corpseManager;
    private DeathManager deathManager;
    private SabotageManager sabotageManager;

    private final int numImposters;

    public Arena(ImposterCraft imposterCraft, int id, Location lobby, List<Location> spawns) {
        this.imposterCraft = imposterCraft;

        this.id = id;
        this.arenaLobby = lobby;
        this.spawns = spawns;

        this.countdown = new Countdown(imposterCraft, this);
        this.game = new Game(imposterCraft, this);
        this.state = GameState.RECRUITING;

        this.numImposters = getNumImposters();

        initManagers();
    }

    public boolean isReady() {
        if (spawns.isEmpty() || arenaLobby == null || !meetingManager.isMeetingSignLocationNotNull()) {
            return false;
        }

        return taskManager.areAllTaskLocationsSet();
    }

    public void sendMessage(String message, boolean ignoreGameState) {

        // Cannot send a message during gameplay
        if (state == GameState.LIVE && !ignoreGameState) {
            return;
        }

        for (UUID uuid : playerManager.players()) {
            sendMessageToPlayer(uuid, message);
        }
    }

    public void sendMessage(String message) {
        sendMessage(message, true);
    }

    public void sendTitle(String title, String subtitle, int duration) {
        for (UUID uuid : playerManager.players()) {
            sendTitleToPlayer(uuid, title, subtitle, duration);
        }
    }

    public void start() {
        this.state = GameState.LIVE;
        playerManager.setPlayersRemaining();
        meetingManager.saveEndOfMeetingTime();
        game.start();
    }

    public void startCountdown() {
        countdown.start();
    }

    public void endGame(boolean impostersWin) {
        game.endGame(impostersWin);
    }

    public void endGame() {
        game.endGame(playerManager.getCrewmateRemainingCount() <= playerManager.getImposterRemainingCount());
    }

    public void reset(boolean kickPlayers) {
        Location location = getLobbySpawn();
        for (UUID uuid : playerManager.players()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                if (kickPlayers) {
                    player.teleport(location);
                }
                playerManager.resetPlayer(player);
                corpseManager.removeCorpsesForPlayer(player);
            }
        }
        if (kickPlayers) {
            playerManager.clear();
        }
        corpseManager.corpsesClearArray();

        sendTitle("", "", 0);
        state = GameState.RECRUITING;

        try {
            countdown.cancel();
            countdown = new Countdown(imposterCraft, this);
        } catch (Exception ignored) {}
        meetingManager.close();
        game = new Game(imposterCraft, this);
        initManagers();
    }

    public void addPlayer(Player player) {
        playerManager.addPlayer(player, arenaLobby);
    }

    public void handleSignClick(Player player, Location signLocation) {
        if (meetingManager.isLocationMeetingSignLocation(signLocation)) {
            UUID uuid = player.getUniqueId();
            if (!meetingManager.hasPlayerCalledMeeting(uuid)) {
                meetingManager.startEmergencyMeeting(player, false);
                meetingManager.playerCalledMeeting(uuid);
            } else {
                sendRedMessageToPlayer(player, "You have already used your 1 emergency meeting call!");
            }
        } else if (camerasManager.isLocationCamerasJoinLocation(signLocation)) {
            camerasManager.playerEnterCameras(player);
        } else {
            String command = taskManager.getTaskFromLocation(signLocation);

            if (command != null) {
                taskManager.selectTask(player, command, signLocation);
            }
        }
    }

    public int getId() {
        return this.id;
    }

    public List<UUID> getPlayers() {
        return playerManager.players();
    }

    public List<UUID> getRemainingPlayers() {
        return playerManager.playersRemaining();
    }

    public GameState getState() {
        return this.state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public Game getGame() {
        return game;
    }

    public Location getSpawnPoint(int index) {
        Location spawn = spawns.get(index);
        if (spawn == null) {
            Random random = new Random();
            spawn = spawns.get(random.nextInt(spawns.size()));
        }

        return spawn;
    }

    public boolean isChatEnabled() {
        return state != GameState.LIVE;
    }

    public List<UUID> getImposters() {
        return playerManager.getImposters();
    }

    public boolean isPlayerImposter(Player player) {
        return playerManager.isPlayerImposter(player.getUniqueId());
    }

    public boolean isPlayerImposter(UUID uuid) {
        return playerManager.isPlayerImposter(uuid);
    }

    public int calculateNumImposters() {
        return playerManager.players().size() > (this.numImposters * 2) + 2 ? this.numImposters : 1;
    }

    public boolean isMeetingActive() {
        return this.state == GameState.MEETING;
    }

    public void addSpawnLocation(Location location) {
        this.spawns.add(location);
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public MeetingManager getMeetingManager() {
        return meetingManager;
    }

    public VentManager getVentManager() {
        return ventManager;
    }

    public CamerasManager getCamerasManager() {
        return camerasManager;
    }

    public CorpseManager getCorpseManager() {
        return corpseManager;
    }

    public DeathManager getDeathManager() {
        return deathManager;
    }

    public SabotageManager getSabotageManager() {
        return sabotageManager;
    }

    public DoorManager getDoorManager() {
        return sabotageManager.getDoorManager();
    }

    public ReactorManager getReactorManager() {
        return sabotageManager.getReactorManager();
    }

    public OxygenManager getOxygenManager() {
        return sabotageManager.getOxygenManager();
    }

    public LightsManager getLightsManager() {
        return sabotageManager.getLightsManager();
    }

    private void initManagers() {
        this.playerManager = new PlayerManager(imposterCraft, this);
        this.taskManager = new TaskManager(imposterCraft, this);
        this.meetingManager = new MeetingManager(imposterCraft, this);
        this.ventManager = new VentManager(this);
        this.camerasManager = new CamerasManager(this);
        this.corpseManager = new CorpseManager(imposterCraft, this);
        this.deathManager = new DeathManager(this);
        this.sabotageManager = new SabotageManager(imposterCraft, this);
    }
}
