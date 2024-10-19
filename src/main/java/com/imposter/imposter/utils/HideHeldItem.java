package com.imposter.imposter.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class HideHeldItem {

    public static void hideHeldItem(ProtocolManager protocolManager, Player player, List<UUID> arenaPlayers) {
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);

        packet.getIntegers().write(0, player.getEntityId());
        packet.getSlotStackPairLists().write(0,
            Collections.singletonList(new Pair<>(EnumWrappers.ItemSlot.MAINHAND, new ItemStack(Material.AIR)))
        );

        for (UUID uuid : arenaPlayers) {
            Player arenaPlayer = Bukkit.getPlayer(uuid);
            if (arenaPlayer != player) {
                try {
                    ProtocolLibrary.getProtocolManager().sendServerPacket(arenaPlayer, packet);
                } catch (Exception e) {}
            }
        }
    }
}
