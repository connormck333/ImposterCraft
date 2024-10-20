package com.imposter.imposter.managers.gameplay;

import com.imposter.imposter.instances.Arena;
import com.imposter.imposter.instances.Game;
import com.imposter.imposter.roles.imposter.BountyHunter;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.imposter.imposter.utils.ConfigManager.getKillCooldown;
import static com.imposter.imposter.utils.Messages.*;

public class DeathManager {

    private final Arena arena;

    private final int killCooldown;
    private final Map<UUID, Long> playerKillCooldowns;

    public DeathManager(Arena arena) {
        this.arena = arena;

        this.killCooldown = getKillCooldown() * 1000;
        this.playerKillCooldowns = new HashMap<>();
    }

    public void killCrewmate(Player imposter, Player crewmate, Location deathLocation, boolean ignoreCooldown) {
        UUID imposterUUID = imposter.getUniqueId();
        UUID crewmateUUID = crewmate.getUniqueId();
        Game game = arena.getGame();
        if (arena.isPlayerImposter(crewmate) || !arena.isPlayerImposter(imposter)) {
            return;
        } else if (!ignoreCooldown && !canPlayerKill(imposterUUID)) {
            sendWaitForCooldownMessage(imposter, getPlayerCooldownRemaining(imposterUUID));
            return;
        } else if (game.deputy() != null && game.deputy().isPlayerHandcuffed(imposterUUID)) {
            sendHandcuffMessage(imposter);
            return;
        } else if (game.protector() != null && game.protector().isPlayerProtected(crewmateUUID)) {
            sendProtectedMessage(imposter);
            return;
        }

        this.playerKillCooldowns.put(imposterUUID, System.currentTimeMillis());

        BountyHunter bountyHunter = game.bountyHunter();
        if (bountyHunter != null && bountyHunter.is(imposterUUID)) {
            if (bountyHunter.didBountyHunterKillPlayer(crewmateUUID)) {
                bountyHunter.bountyHunterKill(imposter);
                return;
            }
        }

        killPlayer(crewmate, deathLocation);
    }

    public void killCrewmate(Player imposter, Player crewmate) {
        killCrewmate(imposter, crewmate, crewmate.getLocation(), false);
    }

    public void killPlayer(Player player, Location deathLocation) {
        arena.getPlayerManager().removeRemainingPlayer(player.getUniqueId());
        player.setGameMode(GameMode.SPECTATOR);
        player.getWorld().spawnParticle(Particle.DRAGON_BREATH, deathLocation, 20);

//        if (arena.getGame().isGameOver()) {
//            arena.endGame();
//        } else {
            arena.getCorpseManager().createCorpse(player, deathLocation, false);
//        }
    }

    public void killPlayerWithoutBody(Player player) {
        arena.getPlayerManager().removeRemainingPlayer(player.getUniqueId());
        player.setGameMode(GameMode.SPECTATOR);
    }

    public void sheriffKillPlayer(Player sheriff, Player hitPlayer) {
        if (arena.getGame().protector() != null && arena.getGame().protector().isPlayerProtected(hitPlayer.getUniqueId())) {
            sendProtectedMessage(sheriff);
            return;
        } else if (arena.getGame().deputy() != null && arena.getGame().deputy().isPlayerHandcuffed(sheriff.getUniqueId())) {
            sendHandcuffMessage(sheriff);
            return;
        } else if (!canPlayerKill(sheriff.getUniqueId())) {
            sendWaitForCooldownMessage(sheriff, getPlayerCooldownRemaining(sheriff.getUniqueId()));
            return;
        }

        if (arena.isPlayerImposter(hitPlayer)) {
            sendRedMessageToPlayer(hitPlayer, "The sheriff caught you! You are now a ghost!");
            killPlayer(hitPlayer, hitPlayer.getLocation());
            this.playerKillCooldowns.put(sheriff.getUniqueId(), System.currentTimeMillis());
        } else {
            sendRedMessageToPlayer(sheriff, "They were not the imposter! You are now a ghost!");
            killPlayer(sheriff, sheriff.getLocation());
        }

        if (arena.getGame().isGameOver()) {
            arena.endGame();
        }
    }

    public void restartPlayerKillCooldown(UUID uuid) {
        this.playerKillCooldowns.put(uuid, System.currentTimeMillis());
    }

    public boolean canPlayerKill(UUID player) {
        int remainingCooldown = getPlayerCooldownRemaining(player);
        if (remainingCooldown == Integer.MAX_VALUE) {
            return true;
        }

        return remainingCooldown <= 0 && !arena.getCamerasManager().isPlayerOnCameras(player) && !arena.getVentManager().isPlayerInVent(player);
    }

    public int getPlayerCooldownRemaining(UUID uuid) {
        Long spawnImmunity = arena.getMeetingManager().getSpawnImmunityRemaining();
        if (spawnImmunity < 10000) {
            return (int) (10000 - spawnImmunity);
        }

        Long timeOfLastKill = this.playerKillCooldowns.get(uuid);
        if (timeOfLastKill == null) {
            return Integer.MAX_VALUE;
        }

        return (int) (killCooldown - (System.currentTimeMillis() - timeOfLastKill));
    }
}
