package com.imposter.imposter.listeners;

import com.imposter.imposter.ImposterCraft;
import com.imposter.imposter.instances.Arena;
import com.imposter.imposter.utils.Colors;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import static com.imposter.imposter.utils.ConfigManager.canChatInLobby;
import static com.imposter.imposter.utils.Messages.sendRedMessageToPlayer;

public class ChatListener implements Listener {

    private final ImposterCraft imposterCraft;
    private boolean canChatInMainLobby;

    public ChatListener(ImposterCraft imposterCraft) {
        this.imposterCraft = imposterCraft;
        this.canChatInMainLobby = canChatInLobby();
    }

    @EventHandler
    public void onPlayerWrite(AsyncPlayerChatEvent e) {
        e.setCancelled(true);
        Player player = e.getPlayer();
        Arena arena = imposterCraft.getArenaManager().getArena(player);
        if (arena == null) {
            if (!canChatInMainLobby) {
                sendRedMessageToPlayer(player,"You cannot chat here!");
            }
        } else {
            if (arena.isChatEnabled()) {
                arena.sendMessage(formatPlayerNameForMessage(player) + e.getMessage(), false);
            } else {
                sendRedMessageToPlayer(player,"You cannot chat right now!");
            }
        }
    }

    private String formatPlayerNameForMessage(Player player) {
        Colors playerColor = imposterCraft.getArenaManager().getArena(player).getPlayerManager().getPlayerColor(player.getUniqueId());
        return ChatColor.DARK_GRAY + "[" + playerColor.getChatColor() + playerColor.getColor() +
                ChatColor.DARK_GRAY + "] " + player.getDisplayName() + ChatColor.DARK_GRAY + ": " + ChatColor.WHITE;
    }
}
