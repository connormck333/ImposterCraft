package com.imposter.imposter.roles;

import com.imposter.imposter.instances.Arena;
import com.imposter.imposter.instances.BaseVoteGui;
import com.imposter.imposter.roles.crewmate.CrewmateRoleEnum;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.imposter.imposter.utils.Constants.BOLD_GREEN;
import static com.imposter.imposter.utils.Constants.BOLD_RED;
import static com.imposter.imposter.utils.GuiUtils.getRandomWool;

public class GuesserGui extends BaseVoteGui {

    private final Map<Integer, CrewmateRoleEnum> slots;

    public static final String GUESSER_GUI_TITLE = BOLD_RED + "Guess roles";

    public GuesserGui(Arena arena, Player player) {
        super(arena, player, GUESSER_GUI_TITLE);
        this.slots = new HashMap<>();
        setupGui("Guess Role", "Guess a player's role to kill them! Guess wrong and you will die!", true);
    }

    public CrewmateRoleEnum getRoleBySlot(int slotId) {
        return slots.get(slotId);
    }

    public void setupGuiForRoles() {
        int slotId = 19;
        CrewmateRoleEnum[] roles = CrewmateRoleEnum.values();
        Player guessedP = Bukkit.getPlayer(getArena().getGame().guesser().getGuessedPlayer());
        for (CrewmateRoleEnum role : roles) {
            String roleTitle = role.toString();
            ItemStack item = getRandomWool(BOLD_GREEN + roleTitle,  "Guess " + guessedP.getDisplayName() + " is " + roleTitle);

            // Avoid side slots
            if (slotId % 9 == 0) {
                slotId++;
            } else if (slotId % 9 == 8) {
                slotId += 2;
            }

            gui().setItem(slotId, item);
            slots.put(slotId, role);
            slotId++;
        }

        openGui();
    }
}
