package com.imposter.imposter.instances.corpse_entities;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
import com.imposter.imposter.ImposterCraft;
import com.imposter.imposter.instances.Arena;
import com.imposter.imposter.instances.Outfit;
import net.minecraft.world.entity.EntityPose;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class CorpseEntity {

    private final ImposterCraft imposterCraft;
    private final Arena arena;

    private final Player player;
    private final int id;
    private final Location location;

    public CorpseEntity(ImposterCraft imposterCraft, Arena arena, Player player, Location deathLocation) {
        this.imposterCraft = imposterCraft;
        this.arena = arena;
        this.player = player;
        this.location = deathLocation;
        this.id = (int) (Math.random() * Integer.MAX_VALUE);
    }

    public int getId() {
        return this.id;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Location getLocation() {
        return this.location;
    }

    protected ImposterCraft getImposterCraft() {
        return this.imposterCraft;
    }

    protected Arena getArena() {
        return this.arena;
    }

    protected abstract PacketContainer createCorpse();

    protected abstract PacketContainer spawnCorpse();

    protected abstract PacketContainer getMetadataPacket();

    protected abstract PacketContainer getArmorPacket(EnumWrappers.ItemSlot slot, ItemStack item);

    protected abstract void sendPackets(PacketContainer playerInfoPacket, PacketContainer spawnEntityPacket, PacketContainer metadataPacket);

}
