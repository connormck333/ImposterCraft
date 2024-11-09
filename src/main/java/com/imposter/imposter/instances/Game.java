package com.imposter.imposter.instances;

import com.imposter.imposter.ImposterCraft;
import com.imposter.imposter.managers.players.CrewmateManager;
import com.imposter.imposter.managers.players.ImposterManager;
import com.imposter.imposter.roles.crewmate.*;
import com.imposter.imposter.roles.imposter.Bomber;
import com.imposter.imposter.roles.imposter.BountyHunter;
import com.imposter.imposter.roles.imposter.Camouflager;
import com.imposter.imposter.roles.imposter.Guesser;
import com.imposter.imposter.utils.Colors;
import com.imposter.imposter.utils.GameState;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.*;

import java.util.*;

import static com.imposter.imposter.utils.ConfigManager.*;
import static com.imposter.imposter.utils.Constants.*;

public class Game {

    private final ImposterCraft imposterCraft;
    private final Arena arena;

    @Getter
    private final ImposterManager imposterManager;
    @Getter
    private final CrewmateManager crewmateManager;

    @Getter
    private final TaskBar taskBar;
    @Getter
    private final GameScoreboard gameScoreboard;

    public Game(ImposterCraft imposterCraft, Arena arena) {
        this.imposterCraft = imposterCraft;
        this.arena = arena;

        this.imposterManager = new ImposterManager(imposterCraft, arena);
        this.crewmateManager = new CrewmateManager(arena);

        this.taskBar = new TaskBar(arena);
        this.gameScoreboard = new GameScoreboard(arena);
    }

    public void start() {
        arena.setState(GameState.LIVE);
        arena.sendMessage(ChatColor.GREEN + "Game has started! There is an imposter among us!");
        arena.getPlayerManager().setImposters(imposterManager.selectImposters());

        for (int i = 0; i < arena.getPlayers().size(); i++) {
            UUID uuid = arena.getPlayers().get(i);
            Player player = Bukkit.getPlayer(uuid);

            if (player == null) {
                continue;
            }

            crewmateManager.rolesManager().assignPlayerRole(uuid);
            preparePlayer(player);

            // Teleport player
            player.teleport(arena.getSpawnPoint(i));
        }

        imposterManager.prepareImposters(true);
    }

    public void endGame(boolean impostersWin) {
        arena.sendMessage(BOLD_YELLOW + "Game Over");
        if (impostersWin) {
            arena.sendMessage(BOLD_RED + "Imposters Win!");
        } else {
            arena.sendMessage(BOLD_GREEN + "Crewmates Win!");
        }

        for (UUID uuid : arena.getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.setGameMode(GameMode.SURVIVAL);
                player.getInventory().clear();
                player.teleport(getLobbySpawn());
            }
        }

        arena.setState(GameState.END_GAME);
        Bukkit.getScheduler().runTaskLater(imposterCraft, () -> arena.reset(true), 200L);
    }

    public void giveItems() {
        for (UUID uuid : arena.getRemainingPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                continue;
            }

            if (!arena.isPlayerImposter(uuid)) {
                crewmateManager.rolesManager().setupInventoryForRole(uuid);
            } else {
                imposterManager.giveImposterWeapons(player);
            }
        }
    }

    public Sheriff sheriff() {
        return crewmateManager.rolesManager().sheriff();
    }

    public Protector protector() {
        return crewmateManager.rolesManager().protector();
    }

    public Engineer engineer() {
        return crewmateManager.rolesManager().engineer();
    }

    public Mayor mayor() {
        return crewmateManager.rolesManager().mayor();
    }

    public Lighter lighter() {
        return crewmateManager.rolesManager().lighter();
    }

    public Deputy deputy() {
        return crewmateManager.rolesManager().deputy();
    }

    public Camouflager camouflager() {
        return imposterManager.rolesManager().camouflager();
    }

    public Guesser guesser() {
        return imposterManager.rolesManager().guesser();
    }

    public Bomber bomber() {
        return imposterManager.rolesManager().bomber();
    }

    public BountyHunter bountyHunter() {
        return imposterManager.rolesManager().bountyHunter();
    }

    public boolean isGameOver() {
        if (arena.getRemainingPlayers().size() < 3) {
            return true;
        }

        GameState state = arena.getState();
        if (state == GameState.LIVE || state == GameState.MEETING) {
            int crewmateRemainingCount = arena.getPlayerManager().getCrewmateRemainingCount();
            int imposterRemainingCount = arena.getPlayerManager().getImposterRemainingCount();
            return crewmateRemainingCount <= imposterRemainingCount || imposterRemainingCount == 0;
        }

        return false;
    }

    private void preparePlayer(Player player) {
        Colors color = arena.getPlayerManager().getPlayerColor(player.getUniqueId());
        ItemStack[] armor = new Outfit(color).getArmor();
        player.getInventory().setArmorContents(armor);

        String displayName = color.getChatColor() + player.getName() + ChatColor.WHITE;
        player.setDisplayName(displayName);
        player.setPlayerListName(displayName);

        boolean isImposter = arena.isPlayerImposter(player);
        Scoreboard board = gameScoreboard.createScoreboard(player, isImposter);
        Team team = board.registerNewTeam("hide_names");
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        for (UUID uuid : arena.getPlayers()) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) {
                team.addEntry(p.getName());
            }
        }
        player.setScoreboard(board);

        taskBar.addPlayer(player);
    }
}
