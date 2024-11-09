package com.imposter.imposter.managers.players;

import com.imposter.imposter.ImposterCraft;
import com.imposter.imposter.instances.Arena;
import com.imposter.imposter.instances.Outfit;
import com.imposter.imposter.utils.Colors;
import com.imposter.imposter.utils.GameState;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

import static com.imposter.imposter.utils.ConfigManager.getRequiredPlayers;
import static com.imposter.imposter.utils.Messages.sendTitleToPlayer;

public class PlayerManager {

    private final ImposterCraft imposterCraft;
    private final Arena arena;

    private final List<UUID> players;
    @Getter
    private List<UUID> imposters = new ArrayList<>();
    private List<UUID> playersRemaining;
    private final Map<UUID, Colors> playerColors;

    @Getter
    private int crewmateRemainingCount;
    @Getter
    private int imposterRemainingCount;

    public PlayerManager(ImposterCraft imposterCraft, Arena arena) {
        this.imposterCraft = imposterCraft;
        this.arena = arena;

        this.players = new ArrayList<>();
        this.playersRemaining = new ArrayList<>();
        this.playerColors = new HashMap<>();

        this.crewmateRemainingCount = 0;
        this.imposterRemainingCount = 0;
    }

    public boolean isPlayerImposter(UUID uuid) {
        return this.imposters.contains(uuid);
    }

    public void setImposters(List<UUID> imposters) {
        this.imposters = imposters;
        this.imposterRemainingCount = imposters.size();
        this.crewmateRemainingCount = players.size() - imposters.size();
    }

    public void setPlayersRemaining() {
        this.playersRemaining = new ArrayList<>(this.players);
    }

    public void addPlayer(Player player, Location arenaLobby) {
        UUID uuid = player.getUniqueId();
        players.add(uuid);
        playerColors.put(uuid, Colors.generateColor(players.size()));
        player.teleport(arenaLobby);

        if (arena.getState().equals(GameState.RECRUITING) && players.size() >= getRequiredPlayers()) {
            arena.startCountdown();
        }
    }

    public void removePlayer(Player player) {
        resetPlayer(player);
        players.remove(player.getUniqueId());
        removeRemainingPlayer(player.getUniqueId());

        if (arena.getState() == GameState.COUNTDOWN && players.size() < getRequiredPlayers()) {
            arena.sendMessage(ChatColor.RED + "There are not enough players. Countdown stopped.");
            arena.reset(false);
        }

        if (arena.getGame().isGameOver()) {
            arena.getGame().endGame(imposterRemainingCount != 0);
        }
    }

    public void removeRemainingPlayer(UUID uuid) {
        this.playersRemaining.remove(uuid);
        if (arena.isPlayerImposter(uuid)) {
            imposterRemainingCount--;
        } else {
            crewmateRemainingCount--;
        }
    }

    public void setPlayerInvisible(Player player, boolean invisible) {
        for (UUID uuid : players) {
            Player p = Bukkit.getPlayer(uuid);
            if (p == null) {
                continue;
            }

            if (invisible) {
                p.hidePlayer(imposterCraft, player);
            } else {
                p.showPlayer(imposterCraft, player);
            }
        }
    }

    public void clearHotbar(Player player) {
        ItemStack air = new ItemStack(Material.AIR);
        for (int i = 0; i < 9; i++) {
            player.getInventory().setItem(i, air);
        }
    }

    public List<UUID> players() {
        return players;
    }

    public List<UUID> playersRemaining() {
        return playersRemaining;
    }

    public Colors getPlayerColor(UUID uuid) {
        return playerColors.get(uuid);
    }

    public UUID getPlayerByColor(Colors color) {
        for (UUID key : playerColors.keySet()) {
            if (playerColors.get(key) == color) {
                return key;
            }
        }

        return null;
    }

    public UUID getRandomCrewmate() {
        if (playersRemaining.isEmpty() || crewmateRemainingCount == 0) {
            return null;
        }
        Random random = new Random();
        UUID uuid;
        do {
            int index = random.nextInt(playersRemaining.size());
            uuid = playersRemaining.get(index);
        } while (isPlayerImposter(uuid));

        return uuid;
    }

    public void clear() {
        this.players.clear();
        this.playersRemaining.clear();
    }

    public void removePlayerArmor(Player player) {
        player.getInventory().setArmorContents(new ItemStack[]{});
    }

    public void returnPlayerArmorAndWeapons(Player player) {
        Colors color = arena.getPlayerManager().getPlayerColor(player.getUniqueId());
        Outfit armor = new Outfit(color);
        player.getInventory().clear();
        player.getInventory().setArmorContents(armor.getArmor());
        if (arena.isPlayerImposter(player)) {
            arena.getGame().getImposterManager().giveImposterWeapons(player);
        }
    }

    public void resetPlayer(Player player) {
        player.getInventory().clear();
        player.setScoreboard(Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard());
        player.setGameMode(GameMode.SURVIVAL);
        player.setDisplayName(player.getName());
        player.removePotionEffect(PotionEffectType.BLINDNESS);
        arena.getGame().getTaskBar().removePlayerFromTaskBar(player);
        sendTitleToPlayer(player, "", "", 0);
    }
}
