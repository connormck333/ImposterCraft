package com.imposter.imposter.instances;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
import com.imposter.imposter.ImposterCraft;
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

public class CorpseEntity {

    private final ImposterCraft imposterCraft;
    private final Arena arena;

    private final Player player;
    private WrappedGameProfile corpse;
    private int id;
    private final EntityPose pose;
    private final Location location;

    public CorpseEntity(ImposterCraft imposterCraft, Arena arena, Player player, Location deathLocation, boolean isPlayerOnCameras) {
        this.imposterCraft = imposterCraft;
        this.arena = arena;

        this.player = player;
        this.pose = isPlayerOnCameras ? EntityPose.a : EntityPose.b;
        this.location = deathLocation;

        PacketContainer playerInfoPacket = createCorpse();
        PacketContainer spawnEntityPacket = spawnCorpse(deathLocation);
        PacketContainer metadataPacket = getMetadataPacket();

        sendPackets(playerInfoPacket, spawnEntityPacket, metadataPacket);
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

    private PacketContainer createCorpse() {
        this.corpse = new WrappedGameProfile(UUID.randomUUID(), player.getName());
        this.id = (int) (Math.random() * Integer.MAX_VALUE);

        PlayerInfoData playerInfoData = new PlayerInfoData(
                corpse,
                0,
                EnumWrappers.NativeGameMode.SURVIVAL,
                null
        );

        PacketContainer playerInfoPacket = imposterCraft.getProtocolManager().createPacket(PacketType.Play.Server.PLAYER_INFO);
        playerInfoPacket.getPlayerInfoActions().write(0, Collections.singleton(EnumWrappers.PlayerInfoAction.ADD_PLAYER));
        playerInfoPacket.getPlayerInfoDataLists().write(1, Collections.singletonList(playerInfoData));

        return playerInfoPacket;
    }

    private PacketContainer spawnCorpse(Location deathLocation) {
        PacketContainer spawnEntityPacket = imposterCraft.getProtocolManager().createPacket(PacketType.Play.Server.SPAWN_ENTITY);
        spawnEntityPacket.getUUIDs().write(0, corpse.getUUID());
        spawnEntityPacket.getIntegers().write(0, id);
        spawnEntityPacket.getDoubles()
                .write(0, deathLocation.getX())
                .write(1, deathLocation.getY())
                .write(2, deathLocation.getZ());
        spawnEntityPacket.getBytes()
                .write(0, (byte) ((deathLocation.getYaw() * 256.0F) / 360.0F))
                .write(1, (byte) -90);
        spawnEntityPacket.getEntityTypeModifier().write(0, EntityType.PLAYER);

        return spawnEntityPacket;
    }

    private PacketContainer getMetadataPacket() {
        WrappedDataWatcher dataWatcher = new WrappedDataWatcher();
        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(6, WrappedDataWatcher.Registry.get(EntityPose.class)), this.pose);

        // Prepare the metadata packet
        PacketContainer metadataPacket = imposterCraft.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_METADATA);
        List<WrappedDataValue> wrappedDataValues = dataWatcher.getWatchableObjects().stream()
                .map(watchableObject -> new WrappedDataValue(watchableObject.getIndex(), watchableObject.getWatcherObject().getSerializer(), watchableObject.getValue()))
                .collect(Collectors.toList());

        // Write the entity ID and data
        metadataPacket.getIntegers().write(0, id);
        metadataPacket.getDataValueCollectionModifier().write(0, wrappedDataValues);

        return metadataPacket;
    }

    private PacketContainer getArmorPacket(EnumWrappers.ItemSlot slot, ItemStack item) {
        PacketContainer packet = imposterCraft.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);

        packet.getIntegers().write(0, id);
        Pair<EnumWrappers.ItemSlot, ItemStack> pair = new Pair<>(slot, item);
        List<Pair<EnumWrappers.ItemSlot, ItemStack>> equipmentList = List.of(pair);
        packet.getSlotStackPairLists().write(0, equipmentList);

        return packet;
    }

    private void sendPackets(PacketContainer playerInfoPacket, PacketContainer spawnEntityPacket, PacketContainer metadataPacket) {
        ItemStack[] armor = new Outfit(arena.getPlayerManager().getPlayerColor(player.getUniqueId())).getArmor();
        EnumWrappers.ItemSlot[] slots = {
                EnumWrappers.ItemSlot.FEET,
                EnumWrappers.ItemSlot.LEGS,
                EnumWrappers.ItemSlot.CHEST,
                EnumWrappers.ItemSlot.HEAD
        };

        for (UUID uuid : arena.getPlayers()) {
            Player p = Bukkit.getPlayer(uuid);
            try {
                imposterCraft.getProtocolManager().sendServerPacket(p, playerInfoPacket);
                imposterCraft.getProtocolManager().sendServerPacket(p, spawnEntityPacket);
                imposterCraft.getProtocolManager().sendServerPacket(p, metadataPacket);
                for (int i = 0; i < slots.length; i++) {
                    imposterCraft.getProtocolManager().sendServerPacket(p, getArmorPacket(slots[i], armor[i]));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
