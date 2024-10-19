package com.imposter.imposter.managers.sabotages;

import com.imposter.imposter.instances.Arena;
import com.imposter.imposter.roles.crewmate.Lighter;
import com.imposter.imposter.tasks.PlayerTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

import static com.imposter.imposter.utils.Messages.sendRedMessageToPlayer;

public class LightsManager implements ISabotageManager {

    private Arena arena;

    private boolean lightsOff = false;

    public LightsManager(Arena arena) {
        this.arena = arena;
    }

    @Override
    public void start() {
        PotionEffect effect = new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 1, false, false, false);
        for (UUID uuid : arena.getRemainingPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                continue;
            }
            Lighter lighter = arena.getGame().lighter();
            if (arena.isPlayerImposter(player) || (lighter != null && lighter.is(uuid))) {
                continue;
            }

            player.addPotionEffect(effect);
        }
        arena.sendMessage(ChatColor.RED + "The lights have been turned off!");
        this.lightsOff = true;
    }

    @Override
    public void startTask(Player player, PlayerTask task) {
        if (isActive()) {
            arena.getTaskManager().setPlayerOpenTask(player.getUniqueId(), task);
            task.openGui();
        } else {
            sendRedMessageToPlayer(player, "The lights do not need fixed right now!");
        }
    }

    @Override
    public void end() {
        for (UUID uuid : arena.getRemainingPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                continue;
            }

            player.removePotionEffect(PotionEffectType.BLINDNESS);
        }

        this.lightsOff = false;
        arena.getSabotageManager().setSabotageLastUse();
    }

    @Override
    public boolean isActive() {
        return this.lightsOff;
    }
}
