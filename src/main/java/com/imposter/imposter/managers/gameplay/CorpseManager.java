package com.imposter.imposter.managers.gameplay;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.imposter.imposter.ImposterCraft;
import com.imposter.imposter.instances.corpse_entities.CorpseEntity;
import com.imposter.imposter.instances.Arena;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.imposter.imposter.utils.VersionUtils.createCorpseEntityByVersion;
import static com.imposter.imposter.utils.VersionUtils.isVersion;

public class CorpseManager {

    private ImposterCraft imposterCraft;
    private Arena arena;

    private final List<CorpseEntity> corpses;

    public CorpseManager(ImposterCraft imposterCraft, Arena arena) {
        this.imposterCraft = imposterCraft;
        this.arena = arena;
        this.corpses = new ArrayList<>();
    }

    public CorpseEntity createCorpse(Player player, Location deathLocation, boolean cameras) {
        CorpseEntity corpse = createCorpseEntityByVersion(imposterCraft, arena, player, deathLocation, cameras);
        System.out.println(corpse);
        if (!cameras) {
            corpses.add(corpse);
        } else {
            arena.getCamerasManager().addCorpseOnCameras(corpse);
        }

        return corpse;
    }

    public void removeCorpses(IntList entityIds) {
        for (UUID uuid : arena.getPlayers()) {
            removeCorpsesForPlayer(entityIds, Bukkit.getPlayer(uuid));
        }
    }

    public void removeCorpsesForPlayer(IntList entityIds, Player player) {
        PacketContainer removeEntityPacket = imposterCraft.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_DESTROY);
        if (isVersion(1, 20)) {
            removeEntityPacket.getModifier().write(0, new IntArrayList(entityIds));
        } else {
            removeEntityPacket.getModifier().write(0, entityIds);
        }

        try {
            imposterCraft.getProtocolManager().sendServerPacket(player, removeEntityPacket);
        } catch (Exception ignored) {}
    }

    public void removeCorpsesForPlayer(Player player) {
        removeCorpsesForPlayer(getCorpseEntityIds(), player);
    }

    public void removeCorpse(int entityId) {
        removeCorpses(new IntArrayList(new int[] {entityId}));
    }

    public void clearCorpses() {
        removeCorpses(getCorpseEntityIds());
        corpses.clear();
    }

    public IntList getCorpseEntityIds() {
        IntList entityIds = new IntArrayList();
        for (CorpseEntity corpse : corpses) {
            entityIds.add(corpse.getId());
        }

        return entityIds;
    }

    public boolean isEntityCorpse(int entityId) {
        for (CorpseEntity corpse : this.corpses) {
            if (corpse.getId() == entityId) {
                return true;
            }
        }

        return false;
    }

    public void corpsesClearArray() {
        this.corpses.clear();
    }
}
