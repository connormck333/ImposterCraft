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

    private ImposterCraft imposterCraft;
    private Location lobbySpawn;

    public ConnectListener(ImposterCraft imposterCraft) {
        this.imposterCraft = imposterCraft;
        this.lobbySpawn = ConfigManager.getLobbySpawn();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (this.lobbySpawn != null) {
            player.teleport(this.lobbySpawn);
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
