package com.imposter.imposter.roles.imposter;

import com.imposter.imposter.instances.Arena;
import com.imposter.imposter.roles.crewmate.CrewmateRole;
import com.imposter.imposter.roles.crewmate.CrewmateRoleEnum;
import com.imposter.imposter.roles.GuesserGui;
import com.imposter.imposter.roles.Role;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

import static com.imposter.imposter.utils.Constants.GUESSER_ITEM;
import static com.imposter.imposter.utils.GuiUtils.getMeta;

public class Guesser extends ImposterRole {

    private static final String TITLE = "Guesser";
    private static final String DESCRIPTION = "Guess the crewmates roles to kill them!";

    private final Arena arena;
    private GuesserGui gui;
    private UUID guessedPlayer;

    public Guesser(Arena arena, UUID guesser) {
        super(guesser, TITLE, DESCRIPTION, ImposterRoleEnum.GUESSER);
        this.arena = arena;
    }

    public void openGuessGui(Player player) {
        if (!is(player.getUniqueId())) {
            return;
        }
        gui = new GuesserGui(arena, player);
        gui.openGui();
    }

    public void setGuessedPlayer(UUID uuid) {
        this.guessedPlayer = uuid;
        gui.setupGuiForRoles();
    }

    public UUID getGuessedPlayer() {
        return guessedPlayer;
    }

    public GuesserGui getGui() {
        return gui;
    }

    public void guessPlayer(Player guesser, Player guessedPlayer, CrewmateRoleEnum guessedRole) {
        Role role = arena.getGame().getCrewmateManager().rolesManager().getRole(guessedPlayer.getUniqueId());
        CrewmateRoleEnum actualRole = ((CrewmateRole) role).getCrewmateRole();
        if (actualRole == guessedRole) {
            arena.getDeathManager().killPlayerWithoutBody(guessedPlayer);
            String displayName = guessedPlayer.getDisplayName();
            arena.sendMessage(displayName + " was killed!", true);
            arena.sendMessage(displayName + "'s role was guessed by the guesser!", true);
        } else {
            arena.getDeathManager().killPlayerWithoutBody(guesser);
            String displayName = guesser.getDisplayName();
            arena.sendMessage(displayName + " was killed!", true);
            arena.sendMessage(displayName + " guessed and got it wrong!", true);
        }
    }

    public void giveGuesserBook(Player player) {
        player.getInventory().setItem(1, getGuesserItem());
    }

    private static ItemStack getGuesserItem() {
        ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
        item.setItemMeta(getMeta(item, GUESSER_ITEM));

        return item;
    }
}
