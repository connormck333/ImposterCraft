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

public class CorpseEntityV1_20_1 extends CorpseEntity {

    private WrappedGameProfile corpse;
    private final EntityPose pose;

    public CorpseEntityV1_20_1(ImposterCraft imposterCraft, Arena arena, Player player, Location deathLocation, boolean isPlayerOnCameras) {
        super(imposterCraft, arena, player, deathLocation);
        this.pose = isPlayerOnCameras ? EntityPose.a : EntityPose.b;

        PacketContainer playerInfoPacket = createCorpse();
        PacketContainer spawnEntityPacket = spawnCorpse();
        PacketContainer metadataPacket = getMetadataPacket();

        sendPackets(playerInfoPacket, spawnEntityPacket, metadataPacket);
    }

    protected PacketContainer createCorpse() {
        this.corpse = new WrappedGameProfile(UUID.randomUUID(), getPlayer().getName());

        PlayerInfoData playerInfoData = new PlayerInfoData(
                corpse,
                0,
                EnumWrappers.NativeGameMode.SURVIVAL,
                null
        );

        PacketContainer playerInfoPacket = getImposterCraft().getProtocolManager().createPacket(PacketType.Play.Server.PLAYER_INFO);
        playerInfoPacket.getPlayerInfoActions().write(0, Collections.singleton(EnumWrappers.PlayerInfoAction.ADD_PLAYER));
        playerInfoPacket.getPlayerInfoDataLists().write(1, Collections.singletonList(playerInfoData));

        return playerInfoPacket;
    }

    protected PacketContainer spawnCorpse() {
        PacketContainer spawnEntityPacket = getImposterCraft().getProtocolManager().createPacket(PacketType.Play.Server.NAMED_ENTITY_SPAWN);
        spawnEntityPacket.getUUIDs().write(0, corpse.getUUID());
        spawnEntityPacket.getIntegers().write(0, getId());

        Location deathLocation = getLocation();
        spawnEntityPacket.getDoubles()
                .write(0, deathLocation.getX())
                .write(1, deathLocation.getY())
                .write(2, deathLocation.getZ());
        spawnEntityPacket.getBytes()
                .write(0, (byte) ((deathLocation.getYaw() * 256.0F) / 360.0F))
                .write(1, (byte) -90);

        return spawnEntityPacket;
    }

    protected PacketContainer getMetadataPacket() {
        WrappedDataWatcher dataWatcher = new WrappedDataWatcher();
        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(6, WrappedDataWatcher.Registry.get(EntityPose.class)), this.pose);

        // Prepare the metadata packet
        PacketContainer metadataPacket = getImposterCraft().getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_METADATA);
        List<WrappedDataValue> wrappedDataValues = dataWatcher.getWatchableObjects().stream()
                .map(watchableObject -> new WrappedDataValue(watchableObject.getIndex(), watchableObject.getWatcherObject().getSerializer(), watchableObject.getValue()))
                .collect(Collectors.toList());

        // Write the entity ID and data
        metadataPacket.getIntegers().write(0, getId());
        metadataPacket.getDataValueCollectionModifier().write(0, wrappedDataValues);

        return metadataPacket;
    }

    protected PacketContainer getArmorPacket(EnumWrappers.ItemSlot slot, ItemStack item) {
        PacketContainer packet = getImposterCraft().getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);

        packet.getIntegers().write(0, getId());
        Pair<EnumWrappers.ItemSlot, ItemStack> pair = new Pair<>(slot, item);
        List<Pair<EnumWrappers.ItemSlot, ItemStack>> equipmentList = List.of(pair);
        packet.getSlotStackPairLists().write(0, equipmentList);

        return packet;
    }

    protected void sendPackets(PacketContainer playerInfoPacket, PacketContainer spawnEntityPacket, PacketContainer metadataPacket) {
        ItemStack[] armor = new Outfit(getArena().getPlayerManager().getPlayerColor(getPlayer().getUniqueId())).getArmor();
        EnumWrappers.ItemSlot[] slots = {
                EnumWrappers.ItemSlot.FEET,
                EnumWrappers.ItemSlot.LEGS,
                EnumWrappers.ItemSlot.CHEST,
                EnumWrappers.ItemSlot.HEAD
        };

        for (UUID uuid : getArena().getPlayers()) {
            Player p = Bukkit.getPlayer(uuid);
            try {
                getImposterCraft().getProtocolManager().sendServerPacket(p, playerInfoPacket);
                getImposterCraft().getProtocolManager().sendServerPacket(p, spawnEntityPacket);
                getImposterCraft().getProtocolManager().sendServerPacket(p, metadataPacket);
                for (int i = 0; i < slots.length; i++) {
                    getImposterCraft().getProtocolManager().sendServerPacket(p, getArmorPacket(slots[i], armor[i]));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
