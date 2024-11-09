package com.imposter.imposter.listeners;

import com.imposter.imposter.ImposterCraft;
import com.imposter.imposter.instances.Arena;
import com.imposter.imposter.utils.ConfigManager;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;

public class ConnectListener implements Listener {

    private final ImposterCraft imposterCraft;

    public ConnectListener(ImposterCraft imposterCraft) {
        this.imposterCraft = imposterCraft;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        Location mainLobbySpawn = imposterCraft.getArenaManager().getMainLobbySpawn();
        if (mainLobbySpawn != null) {
            player.teleport(mainLobbySpawn);
        }
        player.setInvisible(false);
        player.getInventory().clear();
        player.removePotionEffect(PotionEffectType.BLINDNESS);
        player.setGameMode(GameMode.SURVIVAL);
        player.setDisplayName(player.getName());

        Arena arena = imposterCraft.getArenaManager().getArena(player);
        if (arena != null) {
            arena.getPlayerManager().setPlayerInvisible(player, false);
        }
    }

    @EventHandler void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        Arena arena = imposterCraft.getArenaManager().getArena(player);
        if (arena != null) {
            arena.getPlayerManager().removePlayer(player);
        }
    }
}
