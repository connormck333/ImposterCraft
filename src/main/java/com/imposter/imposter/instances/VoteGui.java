package com.imposter.imposter.instances;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static com.imposter.imposter.utils.GuiUtils.getSkipButton;

public class VoteGui extends BaseVoteGui {

    private final String INFO_TEXT = "Skip or choose a player to eject!";
    public static final String VOTE_GUI_TITLE = ChatColor.RED + ChatColor.BOLD.toString() + "Vote";

    public VoteGui(Arena arena, Player player) {
        super(arena, player, VOTE_GUI_TITLE);
        setupGui("Vote", INFO_TEXT, false);

        ItemStack skipButton = getSkipButton();
        gui().setItem(40, skipButton);
    }
}
