package com.imposter.imposter;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import com.imposter.imposter.commands.ImposterCommand;
import com.imposter.imposter.instances.Arena;
import com.imposter.imposter.instances.CorpseEntity;
import com.imposter.imposter.instances.Wand;
import com.imposter.imposter.listeners.ChatListener;
import com.imposter.imposter.listeners.ConnectListener;
import com.imposter.imposter.listeners.GameListener;
import com.imposter.imposter.managers.ArenaManager;
import com.imposter.imposter.roles.crewmate.Deputy;
import com.imposter.imposter.utils.ConfigManager;
import com.imposter.imposter.utils.GameState;
import com.imposter.imposter.utils.Messages;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

import static com.imposter.imposter.utils.Constants.BRUSH_ITEM;
import static com.imposter.imposter.utils.Messages.sendHandcuffMessage;
import static com.imposter.imposter.utils.Messages.sendWaitForCooldownMessage;

public final class ImposterCraft extends JavaPlugin {

    @Getter
    private ArenaManager arenaManager;
    @Getter
    private ProtocolManager protocolManager;
    @Getter
    private String messagePrefix;

    private Map<UUID, Long> entityInteractions;

    @Override
    public void onEnable() {
        ConfigManager.setupConfig(this);
        Wand.setupWand(this);
        Messages.setup(this);
        this.arenaManager = new ArenaManager(this);
        this.protocolManager = ProtocolLibrary.getProtocolManager();
        this.entityInteractions = new HashMap<>();
        this.messagePrefix = ConfigManager.getMessagePrefix();

        // Listeners setup
        createEquipmentPacketListener();
        createUseEntityPacketListener();
        createCancelAnimationPacketListener();
        Bukkit.getPluginManager().registerEvents(new ConnectListener(this), this);
        Bukkit.getPluginManager().registerEvents(new GameListener(this, this.arenaManager), this);
        Bukkit.getPluginManager().registerEvents(new ChatListener(this), this);

        String commandTitle = "impostercraft";
        getCommand(commandTitle).setExecutor(new ImposterCommand(this));
    }

    private void createEquipmentPacketListener() {
        protocolManager.addPacketListener(new PacketAdapter(this, PacketType.Play.Server.ENTITY_EQUIPMENT) {
            @Override
            public void onPacketSending(PacketEvent e) {
                Player player = e.getPlayer();
                Arena playerArena = arenaManager.getArena(player);

                if (playerArena == null) {
                    return;
                }

                PacketContainer packet = e.getPacket();
                int entityId = packet.getIntegers().read(0);

                if (entityId == player.getEntityId()) {
                    return;
                }

                List<Pair<EnumWrappers.ItemSlot, ItemStack>> equipment = new ArrayList<>(packet.getSlotStackPairLists().read(0));

                for (int i = 0; i < equipment.size(); i++) {
                    if (equipment.get(i).getFirst() == EnumWrappers.ItemSlot.MAINHAND) {
                        equipment.set(i, new Pair<>(EnumWrappers.ItemSlot.MAINHAND, new ItemStack(Material.AIR)));
                        break;
                    }
                }

                packet.getSlotStackPairLists().write(0, equipment);
                e.setPacket(packet);
            }
        });
    }

    private void createUseEntityPacketListener() {
        protocolManager.addPacketListener(new PacketAdapter(this, PacketType.Play.Client.USE_ENTITY) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                Player player = event.getPlayer();
                Arena arena = arenaManager.getArena(player);

                if (arena == null) {
                    return;
                }

                Long lastInteraction = entityInteractions.get(player.getUniqueId());
                if (lastInteraction != null && System.currentTimeMillis() - lastInteraction < 100) {
                    return;
                } else {
                    entityInteractions.put(player.getUniqueId(), System.currentTimeMillis());
                }

                int entityId = packet.getIntegers().read(0);

                // Check if entity is player on cameras
                CorpseEntity corpseEntity = arena.getCamerasManager().getEntityOnCameras(entityId);
                if (corpseEntity != null) {
                    Player camerasPlayer = Bukkit.getPlayer(corpseEntity.getPlayer());
                    if (camerasPlayer != null) {
                        Bukkit.getScheduler().runTask(ImposterCraft.this, () -> {
                            Location corpseLocation = corpseEntity.getLocation();
                            if (arena.getGame().sheriff() != null && arena.getGame().sheriff().is(player.getUniqueId())) {
                                arena.getDeathManager().sheriffKillPlayer(player, camerasPlayer);
                            }
                            if (arena.isPlayerImposter(player)) {
                                arena.getDeathManager().killCrewmate(player, camerasPlayer, corpseLocation, false);
                                arena.getCamerasManager().playerExitCameras(camerasPlayer);
                            }
                        });
                    }
                }

                // Check for janitor cleaning
                ItemStack item = player.getInventory().getItemInMainHand();
                if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {

                    String displayName = item.getItemMeta().getDisplayName();
                    if (displayName.equals(BRUSH_ITEM) && arena.getCorpseManager().isEntityCorpse(entityId)) {

                        EnumWrappers.EntityUseAction action = event.getPacket().getEnumEntityUseActions().read(0).getAction();
                        if (action != EnumWrappers.EntityUseAction.INTERACT_AT) {
                            return;
                        }

                        Deputy deputy = arena.getGame().deputy();
                        if (deputy != null && deputy.isPlayerHandcuffed(player.getUniqueId())) {
                            sendHandcuffMessage(player);
                            return;
                        } else if (arena.getDeathManager().canPlayerKill(player.getUniqueId())) {
                            arena.getCorpseManager().removeCorpse(entityId);
                            arena.getDeathManager().restartPlayerKillCooldown(player.getUniqueId());
                        } else {
                            sendWaitForCooldownMessage(player, arena.getDeathManager().getPlayerCooldownRemaining(player.getUniqueId()));
                        }
                        return;
                    }
                }

                // Report body & start meeting
                if (arena.getCorpseManager().isEntityCorpse(entityId) && arena.getState() != GameState.MEETING) {
                    Bukkit.getScheduler().runTask(ImposterCraft.this, () -> arena.getMeetingManager().startEmergencyMeeting(player, true));
                }
            }
        });
    }

    private void createCancelAnimationPacketListener() {
        protocolManager.addPacketListener(new PacketAdapter(this, PacketType.Play.Client.ARM_ANIMATION) {
            @Override
            public void onPacketReceiving(PacketEvent e) {
                e.setCancelled(true);
            }
        });
    }
}
