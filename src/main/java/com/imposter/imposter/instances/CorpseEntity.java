package com.imposter.imposter.instances;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
import com.imposter.imposter.ImposterCraft;
import lombok.Getter;
import net.minecraft.world.entity.EntityPose;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.imposter.imposter.utils.VersionUtils.isVersionAtLeast;

public class CorpseEntity {

    private final ImposterCraft imposterCraft;

    @Getter
    private final int id;

    @Getter
    private final Arena arena;

    @Getter
    private final UUID player;

    @Getter
    private final Location location;

    @Getter
    private final Long timestamp;

    private WrappedGameProfile corpse;
    private final EntityPose pose;
    private final ArrayList<PacketContainer> packets;

    public CorpseEntity(ImposterCraft imposterCraft, Arena arena, UUID player, Location deathLocation, boolean isPlayerOnCameras) {
        this.imposterCraft = imposterCraft;

        this.id = (int) (Math.random() * Integer.MAX_VALUE);
        this.arena = arena;
        this.player = player;
        this.location = deathLocation;
        this.pose = isPlayerOnCameras ? EntityPose.a : EntityPose.b;
        this.packets = new ArrayList<>();

        this.packets.add(createCorpse());
        this.packets.add(spawnCorpse(isPlayerOnCameras));
        this.packets.add(getMetadataPacket());
        if (!isPlayerOnCameras) {
            this.packets.add(getRotationPacket());
        }

        this.timestamp = System.currentTimeMillis();

        sendPackets();
    }

    private PacketContainer createCorpse() {
        Player corpsePlayer =  Bukkit.getPlayer(player);
        if (corpsePlayer == null) {
            return null;
        }

        WrappedGameProfile corpseProfile = WrappedGameProfile.fromPlayer(corpsePlayer);
        WrappedSignedProperty textures = corpseProfile.getProperties().get("textures").stream().findFirst().orElse(null);

        this.corpse = new WrappedGameProfile(UUID.randomUUID(), corpsePlayer.getName());
        if (textures != null) {
            corpse.getProperties().put("textures", new WrappedSignedProperty("textures", textures.getValue(), textures.getSignature()));
        }

        PlayerInfoData playerInfoData = new PlayerInfoData(
                corpse.getUUID(),
                0,
                false,
                EnumWrappers.NativeGameMode.SURVIVAL,
                corpse,
                null
        );

        PacketContainer playerInfoPacket = imposterCraft.getProtocolManager().createPacket(PacketType.Play.Server.PLAYER_INFO);
        if (isVersionAtLeast("20.0")) {
            playerInfoPacket.getPlayerInfoActions().write(0, Collections.singleton(EnumWrappers.PlayerInfoAction.ADD_PLAYER));
        } else {
            playerInfoPacket.getPlayerInfoAction().write(0, EnumWrappers.PlayerInfoAction.ADD_PLAYER);
        }
        playerInfoPacket.getPlayerInfoDataLists().write(1, Collections.singletonList(playerInfoData));

        return playerInfoPacket;
    }

    private PacketContainer spawnCorpse(boolean isPlayerOnCameras) {
        boolean isVersionAbove20_2 = isVersionAtLeast("20.2");
        PacketContainer spawnEntityPacket = imposterCraft.getProtocolManager().createPacket(
                isVersionAbove20_2 ? PacketType.Play.Server.SPAWN_ENTITY : PacketType.Play.Server.NAMED_ENTITY_SPAWN
        );
        spawnEntityPacket.getUUIDs().write(0, corpse.getUUID());
        spawnEntityPacket.getIntegers().write(0, this.id);

        Location deathLocation = location;
        spawnEntityPacket.getDoubles()
                .write(0, deathLocation.getX())
                .write(1, getHighestBlock())
                .write(2, deathLocation.getZ());

        if (!isPlayerOnCameras) {
            spawnEntityPacket.getBytes()
                    .write(0, (byte) ((deathLocation.getYaw() * 256.0F) / 360.0F))
                    .write(1, (byte) -90);
        }

        if (isVersionAbove20_2) {
            spawnEntityPacket.getEntityTypeModifier().write(0, EntityType.PLAYER);
        }

        return spawnEntityPacket;
    }

    private PacketContainer getMetadataPacket() {
        if (isVersionAtLeast("20.1")) {
            WrappedDataWatcher dataWatcher = new WrappedDataWatcher();
            dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(6, WrappedDataWatcher.Registry.get(EntityPose.class)), this.pose);

            // Prepare the metadata packet
            PacketContainer metadataPacket = imposterCraft.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_METADATA);
            List<WrappedDataValue> wrappedDataValues = dataWatcher.getWatchableObjects().stream()
                    .map(watchableObject -> new WrappedDataValue(watchableObject.getIndex(), watchableObject.getWatcherObject().getSerializer(), watchableObject.getValue()))
                    .collect(Collectors.toList());

            // Write the entity ID and data
            metadataPacket.getIntegers().write(0, this.id);
            metadataPacket.getDataValueCollectionModifier().write(0, wrappedDataValues);

            return metadataPacket;
        } else {
            PacketContainer metadataPacket = imposterCraft.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_METADATA);
            metadataPacket.getIntegers().write(0, this.id);

            List<WrappedDataValue> wrappedDataValues = new ArrayList<>();
            WrappedDataWatcher.WrappedDataWatcherObject poseObject = new WrappedDataWatcher.WrappedDataWatcherObject(
                    6, WrappedDataWatcher.Registry.get(EntityPose.class));

            WrappedDataValue poseValue = new WrappedDataValue(poseObject.getIndex(), poseObject.getSerializer(), EntityPose.b);
            wrappedDataValues.add(poseValue);
            metadataPacket.getDataValueCollectionModifier().write(0, wrappedDataValues);

            return metadataPacket;
        }
    }

    private PacketContainer getRotationPacket() {
        PacketContainer rotationPacket = imposterCraft.getProtocolManager().createPacket(PacketType.Play.Server.REL_ENTITY_MOVE_LOOK);
        rotationPacket.getIntegers().write(0, id);
        rotationPacket.getBytes()
                .write(0, (byte) ((location.getYaw() * 256.0F) / 360.0F))
                .write(1, (byte) -90);
        rotationPacket.getBooleans().write(0, true);

        return rotationPacket;
    }

    private PacketContainer getArmorPacket(EnumWrappers.ItemSlot slot, ItemStack item) {
        PacketContainer packet = imposterCraft.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);

        packet.getIntegers().write(0, this.id);
        Pair<EnumWrappers.ItemSlot, ItemStack> pair = new Pair<>(slot, item);
        List<Pair<EnumWrappers.ItemSlot, ItemStack>> equipmentList = List.of(pair);
        packet.getSlotStackPairLists().write(0, equipmentList);

        return packet;
    }

    private double getHighestBlock() {
        Location highestBlockLocation = location.getWorld().getHighestBlockAt(location).getLocation();

        return highestBlockLocation.getBlockY() < location.getBlockY() ? highestBlockLocation.getBlockY() + 0.9 : location.getBlockY() - 0.1;
    }

    protected void sendPackets() {
        ItemStack[] armor = new Outfit(arena.getPlayerManager().getPlayerColor(player)).getArmor();
        EnumWrappers.ItemSlot[] slots = {
                EnumWrappers.ItemSlot.FEET,
                EnumWrappers.ItemSlot.LEGS,
                EnumWrappers.ItemSlot.CHEST,
                EnumWrappers.ItemSlot.HEAD
        };

        for (UUID uuid : getArena().getPlayers()) {
            Player p = Bukkit.getPlayer(uuid);
            for (PacketContainer packet : packets) {
                try {
                    imposterCraft.getProtocolManager().sendServerPacket(p, packet);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            try {
                for (int i = 0; i < slots.length; i++) {
                    imposterCraft.getProtocolManager().sendServerPacket(p, getArmorPacket(slots[i], armor[i]));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
