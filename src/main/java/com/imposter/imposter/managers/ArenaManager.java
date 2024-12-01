package com.imposter.imposter.managers;

import com.imposter.imposter.ImposterCraft;
import com.imposter.imposter.instances.Arena;
import com.imposter.imposter.utils.GameState;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static com.imposter.imposter.utils.ConfigManager.*;
import static com.imposter.imposter.utils.Messages.*;
import static com.imposter.imposter.utils.Messages.sendMessageToPlayer;

public class ArenaManager {

    private final ImposterCraft imposterCraft;

    @Getter
    @Setter
    private Location mainLobbySpawn;

    @Getter
    private final List<Arena> arenas = new ArrayList<>();
    private final int DOORS_SHUT_TIME;

    public ArenaManager(ImposterCraft imposterCraft) {
        this.imposterCraft = imposterCraft;
        this.mainLobbySpawn = getLobbySpawn();

        FileConfiguration config = imposterCraft.getConfig();
        ConfigurationSection section = config.getConfigurationSection("arenas.");
        if (section != null) {
            for (String arenaIdStr : section.getKeys(false)) {
                int arenaId = Integer.parseInt(arenaIdStr);
                List<Location> spawns = getArenaSpawns(arenaId);
                Location arenaLobby = getArenaLobbySpawn(arenaId);
                arenas.add(new Arena(imposterCraft, arenaId, arenaLobby, spawns));
            }
        }

        this.DOORS_SHUT_TIME = getDoorsShutSeconds();
    }

    public Arena getArena(Player player) {
        for (Arena arena : arenas) {
            if (arena.getPlayers().contains(player.getUniqueId())) {
                return arena;
            }
        }

        return null;
    }

    public Arena getArena(int id) {
        for (Arena arena : arenas) {
            if (arena.getId() == id) {
                return arena;
            }
        }

        return null;
    }

    public void addSpawnToArena(int arenaId, Location location) {
        if (!doesArenaExist(arenaId)) {
            return;
        }

        getArena(arenaId).addSpawnLocation(location);
    }

    public void setLobbySpawn(int arenaId, Location lobbySpawn) {
        if (!doesArenaExist(arenaId)) {
            return;
        }

        getArena(arenaId).setArenaLobby(lobbySpawn);
    }

    public boolean doesArenaExist(int arenaId) {
        for (Arena arena : arenas) {
            if (arenaId == arena.getId()) {
                return true;
            }
        }

        return false;
    }

    public int getDoorsShutTime() {
        return DOORS_SHUT_TIME;
    }

    public void createArena(int arenaId, Location lobbyLocation) {
        Arena newArena = new Arena(imposterCraft, arenaId, lobbyLocation, new ArrayList<>());
        arenas.add(newArena);
    }

    public void resetArena(CommandSender player, int arenaId) {
        if (!doesArenaExist(arenaId)) {
            sendInvalidArenaIdMessage(player);
            return;
        }

        Arena arena = getArena(arenaId);
        arena.reset(true);

        sendGreenMessageToPlayer(player, "Arena " + arenaId + " reset!");
    }

    public void playerJoinArena(Player player, int arenaId) {
        if (getArena(player) != null) {
            sendMessageToPlayer(player, ChatColor.RED + "You are already in an arena!");
            return;
        } else if (!doesArenaExist(arenaId)) {
            sendInvalidArenaIdMessage(player);
        }

        if (arenaId >= 0 && imposterCraft.getArenaManager().doesArenaExist(arenaId)) {
            Arena arena = imposterCraft.getArenaManager().getArena(arenaId);
            if (!arena.isReady()) {
                sendRedMessageToPlayer(player, "This arena has not finished setup.");
            } else if (arena.getPlayers().size() >= 12) {
                sendRedMessageToPlayer(player, "You cannot join this arena. This arena is full.");
            } else if (arena.getState() == GameState.RECRUITING || arena.getState() == GameState.COUNTDOWN) {
                sendMessageToPlayer(player, ChatColor.GREEN + "You have joined arena " + arenaId);
                arena.addPlayer(player);
            } else {
                sendMessageToPlayer(player, ChatColor.RED + "You cannot join this arena right now.");
            }
        } else {
            sendMessageToPlayer(player, ChatColor.RED + "You specified an invalid arena ID!");
        }
    }
}
