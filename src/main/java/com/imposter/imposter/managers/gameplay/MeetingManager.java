package com.imposter.imposter.managers.gameplay;

import com.imposter.imposter.ImposterCraft;
import com.imposter.imposter.instances.Arena;
import com.imposter.imposter.instances.Game;
import com.imposter.imposter.instances.MeetingCountdown;
import com.imposter.imposter.instances.VoteGui;
import com.imposter.imposter.roles.crewmate.Mayor;
import com.imposter.imposter.roles.imposter.Guesser;
import com.imposter.imposter.utils.GameState;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static com.imposter.imposter.utils.ConfigManager.getMeetingSignLocation;
import static com.imposter.imposter.utils.Constants.VOTE_BOOK_TITLE;
import static com.imposter.imposter.utils.GuiUtils.getMeta;
import static com.imposter.imposter.utils.Messages.sendRedMessageToPlayer;
import static com.imposter.imposter.utils.Messages.sendTitleToPlayer;
import static com.imposter.imposter.utils.Utils.locationEquals;

public class MeetingManager {

    private final ImposterCraft imposterCraft;
    private final Arena arena;

    private MeetingCountdown meetingCountdown;
    private Location emergencyMeetingSignLocation;

    private List<UUID> voters;
    private Map<UUID, UUID> roundVotes;
    private int skipVoteCount;
    private Set<UUID> playersUsedMeetings;
    private Long timeOfEndOfLastMeeting;

    public MeetingManager(ImposterCraft imposterCraft, Arena arena) {
        this.imposterCraft = imposterCraft;
        this.arena = arena;

        this.meetingCountdown = new MeetingCountdown(imposterCraft, arena);
        this.emergencyMeetingSignLocation = getMeetingSignLocation(arena.getId());

        this.voters = new ArrayList<>();
        this.roundVotes = new HashMap<>();
        this.skipVoteCount = 0;
        this.playersUsedMeetings = new HashSet<>();
    }

    public void startEmergencyMeeting(Player player, boolean bodyFound) {
        if (!arena.getSabotageManager().isDoorSabotageAvailable() && !bodyFound) {
            sendRedMessageToPlayer(player, "You cannot call a meeting during an emergency!");
            return;
        }
        arena.getSabotageManager().cancelAllSabotages();
        arena.setState(GameState.MEETING);
        arena.getCorpseManager().clearCorpses();

        String name = player.getName();
        ItemStack book = getMeetingBook();

        Game game = arena.getGame();
        List<UUID> players = arena.getPlayers();

        for (int i = 0; i < players.size(); i++) {
            Player p = Bukkit.getPlayer(players.get(i));
            if (p == null) {
                continue;
            }
            arena.getTaskManager().clearPlayerTasks(p);
            arena.getPlayerManager().clearHotbar(p);
            p.teleport(arena.getSpawnPoint(i));
            p.closeInventory();
            p.getInventory().setItem(0, book);
            Guesser guesser = game.guesser();
            if (guesser != null && guesser.is(p.getUniqueId())) {
                guesser.giveGuesserBook(p);
            }
            sendTitleToPlayer(
                    p,
                    ChatColor.RED + "Meeting",
                    ChatColor.DARK_RED + name + (bodyFound ? " found a body!" : " started a meeting!"),
                    60
            );
        }

        game.getTaskBar().updateTaskBar();
        meetingCountdown.start();
    }

    public void endEmergencyMeeting() {
        meetingCountdown.cancel();
        meetingCountdown = new MeetingCountdown(imposterCraft, arena);

        Game game = arena.getGame();
        Mayor mayor = arena.getGame().mayor();
        List<UUID> players = arena.getPlayers();
        Map<UUID, Integer> votes = new HashMap<>();
        for (UUID uuid : players) {
            votes.merge(roundVotes.get(uuid), (mayor != null && mayor.is(uuid)) ? 2 : 1, Integer::sum);
        }

        UUID mostVotedFor = null;
        int uuidVotes = 0;
        boolean tie = false;
        UUID tiedPlayer = null;
        for (UUID uuid : players) {
            int currentVotes = votes.getOrDefault(uuid, 0);
            if (currentVotes > uuidVotes) {
                mostVotedFor = uuid;
                uuidVotes = currentVotes;
                tie = false;
                tiedPlayer = null;
            } else if (currentVotes == uuidVotes && uuidVotes > 0) {
                tie = true;
                tiedPlayer = uuid;
            }

            Player p = Bukkit.getPlayer(uuid);
            arena.sendMessage(p.getDisplayName() + ChatColor.WHITE + " received " + currentVotes + " vote" + (currentVotes != 1 ? "s" : ""), true);

            arena.getPlayerManager().clearHotbar(p);
            p.closeInventory();
        }

        arena.sendMessage("There " + (skipVoteCount != 1 ? "were " : "was ") + skipVoteCount + " vote" + (skipVoteCount != 1 ? "s" : "") + " to skip", true);

        if (mostVotedFor != null && uuidVotes > skipVoteCount) {
            Player p = Bukkit.getPlayer(mostVotedFor);
            if (!tie) {
                ejectPlayer(p);
            } else {
                Player tiedP = Bukkit.getPlayer(tiedPlayer);
                arena.sendMessage(p.getDisplayName() + ChatColor.WHITE + " and " + tiedP.getDisplayName() + " tied! No one was ejected");
            }
        } else {
            arena.sendMessage(ChatColor.WHITE + "No one was ejected!");
        }

        if (game.isGameOver()) {
            arena.setState(GameState.END_GAME);
            arena.endGame();
        } else {
            arena.setState(GameState.LIVE);
            arena.sendMessage(ChatColor.GREEN + "Meeting has ended, you can now move around.");
            game.giveItems();
        }

        this.roundVotes.clear();
        this.voters.clear();
        this.skipVoteCount = 0;
        this.timeOfEndOfLastMeeting = System.currentTimeMillis();
    }

    public void voteForPlayer(UUID voter, UUID votedPlayer) {
        if (!arena.isMeetingActive() || hasPlayerVoted(voter)) {
            return;
        }

        this.roundVotes.put(voter, votedPlayer);
        this.voters.add(voter);
    }

    public void voteForSkip(UUID voter) {
        if (!arena.isMeetingActive() || hasPlayerVoted(voter)) {
            return;
        }
        Mayor mayor = arena.getGame().mayor();
        if (mayor != null && mayor.is(voter)) {
            this.skipVoteCount += 2;
        } else {
            this.skipVoteCount++;
        }
        this.voters.add(voter);
    }

    public boolean hasPlayerVoted(UUID uuid) {
        return this.voters.contains(uuid);
    }

    public boolean hasPlayerCalledMeeting(UUID uuid) {
        return playersUsedMeetings.contains(uuid);
    }

    public void playerCalledMeeting(UUID uuid) {
        playersUsedMeetings.add(uuid);
    }

    public boolean isMeetingSignLocationNotNull() {
        return emergencyMeetingSignLocation != null;
    }

    public void setEmergencyMeetingSignLocation(Location location) {
        emergencyMeetingSignLocation = location;
    }

    public void saveEndOfMeetingTime() {
        timeOfEndOfLastMeeting = System.currentTimeMillis();
    }

    public boolean hasAllPlayersVoted() {
        return roundVotes.keySet().size() >= arena.getRemainingPlayers().size();
    }

    public boolean isLocationMeetingSignLocation(Location location) {
        if (isMeetingSignLocationNotNull()) {
            return locationEquals(location, emergencyMeetingSignLocation);
        }

        return false;
    }

    public void openVoteGui(Player player) {
        if (hasPlayerVoted(player.getUniqueId())) {
            sendRedMessageToPlayer(player, "You have already voted!");
            return;
        }
        VoteGui vote = new VoteGui(arena, player);
        vote.openGui();
    }

    public Long getSpawnImmunityRemaining() {
        return System.currentTimeMillis() - timeOfEndOfLastMeeting;
    }

    public void close() {
        try {
            meetingCountdown.cancel();
            meetingCountdown = new MeetingCountdown(imposterCraft, arena);
        } catch (Exception ignored) {}
    }

    private void ejectPlayer(Player player) {
        arena.sendMessage(player.getDisplayName() + ChatColor.WHITE + " has been ejected!");
        sendTitleToPlayer(player, ChatColor.RED + "Ejected!", ChatColor.GREEN + "You are now a ghost!", 0);

        player.setGameMode(GameMode.SPECTATOR);

        arena.getPlayerManager().removeRemainingPlayer(player.getUniqueId());
    }

    private ItemStack getMeetingBook() {
        ItemStack item = new ItemStack(Material.BOOK);
        item.setItemMeta(getMeta(item, VOTE_BOOK_TITLE, ChatColor.GOLD + "Vote a player to be ejected"));

        return item;
    }
}
