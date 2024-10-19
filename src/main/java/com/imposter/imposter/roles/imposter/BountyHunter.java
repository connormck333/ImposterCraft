package com.imposter.imposter.roles.imposter;

import com.imposter.imposter.instances.Arena;
import com.imposter.imposter.utils.Colors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;

import java.util.UUID;

public class BountyHunter extends ImposterRole {

    private static final String TITLE = "Bounty Hunter";
    private static final String DESCRIPTION = "Kill your bounties to win the game early!";

    private final Arena arena;

    private UUID bountyHunterPlayerToKill;
    private int bountyHunterKillCount;

    public BountyHunter(Arena arena, UUID bountyHunter) {
        super(bountyHunter, TITLE, DESCRIPTION, ImposterRoleEnum.BOUNTY_HUNTER);

        this.arena = arena;
        this.bountyHunterKillCount = 0;
    }

    public void bountyHunterKill(Player bountyHunter) {
        bountyHunterKillCount++;
        if (bountyHunterKillCount == 3) {
            arena.endGame(true);
        } else {
            Objective objective = bountyHunter.getScoreboard().getObjective(bountyHunter.getName() + "_tasks");
            if (objective != null) {
                setBountyHunterKill(objective);
            }
        }
    }

    public void setBountyHunterKill(Objective objective) {
        UUID randomUUID = arena.getPlayerManager().getRandomCrewmate();
        if (randomUUID != null) {
            Player randomCrewmate = Bukkit.getPlayer(randomUUID);
            Colors crewmateColor = arena.getPlayerManager().getPlayerColor(randomUUID);
            objective.getScore(ChatColor.RED + "Kill: " + crewmateColor.getChatColor() + randomCrewmate.getDisplayName()).setScore(18);
            bountyHunterPlayerToKill = randomUUID;
        }
    }

    public boolean didBountyHunterKillPlayer(UUID uuid) {
        return bountyHunterPlayerToKill == uuid;
    }
}
