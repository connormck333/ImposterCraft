package com.imposter.imposter.managers;

import com.imposter.imposter.ImposterCraft;
import com.imposter.imposter.instances.Arena;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static com.imposter.imposter.utils.ConfigManager.*;
import static com.imposter.imposter.utils.Messages.sendGreenMessageToPlayer;
import static com.imposter.imposter.utils.Messages.sendInvalidArenaIdMessage;

public class ArenaManager {

    private final ImposterCraft imposterCraft;
    private final List<Arena> arenas = new ArrayList<>();
    private final int DOORS_SHUT_TIME;

    public ArenaManager(ImposterCraft imposterCraft) {
        this.imposterCraft = imposterCraft;
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

    public List<Arena> getArenas() {
        return arenas;
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

    public void resetArena(Player player, int arenaId) {
        if (!doesArenaExist(arenaId)) {
            sendInvalidArenaIdMessage(player);
            return;
        }

        Arena arena = getArena(arenaId);
        arena.reset(true);

        sendGreenMessageToPlayer(player, "Arena " + arenaId + " reset!");
    }
}
