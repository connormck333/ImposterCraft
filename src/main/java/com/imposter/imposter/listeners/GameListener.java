package com.imposter.imposter.listeners;

import com.imposter.imposter.ImposterCraft;
import com.imposter.imposter.instances.Arena;
import com.imposter.imposter.instances.locations.PlayerVentLocation;
import com.imposter.imposter.instances.locations.VentLocation;
import com.imposter.imposter.managers.ArenaManager;
import com.imposter.imposter.roles.crewmate.CrewmateRoleEnum;
import com.imposter.imposter.roles.GuesserGui;
import com.imposter.imposter.roles.crewmate.Deputy;
import com.imposter.imposter.roles.crewmate.Protector;
import com.imposter.imposter.roles.crewmate.Sheriff;
import com.imposter.imposter.roles.imposter.Bomber;
import com.imposter.imposter.roles.imposter.Camouflager;
import com.imposter.imposter.roles.imposter.Guesser;
import com.imposter.imposter.sabotages.guis.DoorSabotageGui;
import com.imposter.imposter.sabotages.guis.OxygenSabotageGui;
import com.imposter.imposter.sabotages.guis.ReactorMeltdownGui;
import com.imposter.imposter.tasks.*;
import com.imposter.imposter.instances.locations.TaskLocation;
import com.imposter.imposter.utils.Colors;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

import static com.imposter.imposter.instances.VoteGui.VOTE_GUI_TITLE;
import static com.imposter.imposter.instances.Wand.WAND_TITLE;
import static com.imposter.imposter.instances.Wand.handleWandRightClick;
import static com.imposter.imposter.roles.GuesserGui.GUESSER_GUI_TITLE;
import static com.imposter.imposter.sabotages.guis.DoorSabotageGui.DOOR_SABOTAGE_TITLE;
import static com.imposter.imposter.sabotages.guis.OxygenSabotageGui.OXYGEN_SABOTAGE_TITLE;
import static com.imposter.imposter.sabotages.guis.ReactorMeltdownGui.REACTOR_MELTDOWN_TASK_TITLE;
import static com.imposter.imposter.tasks.AlignEngineTask.ALIGN_ENGINE_TASK_TITLE;
import static com.imposter.imposter.tasks.AsteroidsTask.ASTEROIDS_TASK_TITLE;
import static com.imposter.imposter.tasks.CalibrateDistributorTask.CALIBRATE_TASK_TITLE;
import static com.imposter.imposter.tasks.DivertPowerTask.DIVERT_POWER_TASK_TITLE;
import static com.imposter.imposter.tasks.DivertPowerTask.FIX_LIGHTS_TASK_TITLE;
import static com.imposter.imposter.tasks.EnablePowerTask.ENABLE_POWER_TASK_TITLE;
import static com.imposter.imposter.tasks.NavigationTask.NAVIGATION_TASK_TITLE;
import static com.imposter.imposter.tasks.NumberTask.ORDER_NUMBERS_TASK_TITLE;
import static com.imposter.imposter.tasks.ShieldsTask.SHIELDS_BUTTONS;
import static com.imposter.imposter.tasks.ShieldsTask.SHIELDS_TASK_TITLE;
import static com.imposter.imposter.tasks.TrashTask.TRASH_TASK_TITLE;
import static com.imposter.imposter.tasks.UploadTask.*;
import static com.imposter.imposter.tasks.VentTask.VENT_TASK_TITLE;
import static com.imposter.imposter.utils.ConfigManager.*;
import static com.imposter.imposter.utils.Constants.*;
import static com.imposter.imposter.utils.Cooldowns.*;
import static com.imposter.imposter.utils.GuiUtils.isWool;
import static com.imposter.imposter.utils.Messages.*;
import static com.imposter.imposter.utils.Utils.removeSquareBrackets;

public class GameListener implements Listener {

    private final ImposterCraft imposterCraft;
    private final ArenaManager arenaManager;

    private Map<UUID, Long> interactCooldowns;

    public GameListener(ImposterCraft imposterCraft, ArenaManager arenaManager) {
        this.imposterCraft = imposterCraft;
        this.arenaManager = arenaManager;

        this.interactCooldowns = new HashMap<>();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        ItemStack item = e.getItem();
        Player player = e.getPlayer();
        Arena arena = arenaManager.getArena(player);

        if (arena == null) {
            return;
        }

        boolean isPlayerOnCooldown = isOnCooldown(player);
        setCooldown(player);

        // Cancel ender pearl or ender eye throws
        if (item != null && (item.getType() == Material.ENDER_PEARL || item.getType() == Material.ENDER_EYE || item.getType() == Material.TURTLE_HELMET)) {
            e.setCancelled(true);
        }

        // Check for vent item interaction
        if (item != null) {

            if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equals(BRUSH_ITEM)) {
                return;
            }

            if (item.getType() == Material.ENDER_PEARL) {
                e.setCancelled(true);

                if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && !isPlayerOnCooldown) {
                    String displayName = item.getItemMeta().getDisplayName();
                    if (displayName.equals(VENT_NEXT_ITEM_TITLE)) {
                        playerNextVent(arena, player);
                    } else if (displayName.equals(CAMERAS_NEXT_TITLE)) {
                        arena.getCamerasManager().playerNextCamera(player);
                    }
                }
            } else if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && !isPlayerOnCooldown) {
                String displayName = item.getItemMeta().getDisplayName();
                if (isIronTrapdoor(item.getType())) {
                    if (displayName.equals(VENT_EXIT_ITEM_TITLE)) {
                        arena.getVentManager().playerExitVent(player);
                    } else if (displayName.equals(CAMERAS_EXIT_TITLE)) {
                        arena.getCamerasManager().playerExitCameras(player);
                    }
                }
            }
        }

        // Check for task, vent or meeting interaction
        Block clickedBlock = e.getClickedBlock();
        if (clickedBlock != null) {
            Material material = clickedBlock.getType();
            Location blockLocation = e.getClickedBlock().getLocation();
            if (isOakSign(material)) {
                if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    e.setCancelled(true);
                }
                arena.handleSignClick(player, blockLocation);
                return;
            } else if (isIronTrapdoor(material)) {
                arena.getVentManager().handleVentClick(player, blockLocation);
                return;
            }
        }

        // Check for wand interaction
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && item != null
                && item.getType() == Material.WOODEN_HOE) {
            if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()
                    && item.getItemMeta().getDisplayName().equals(WAND_TITLE)) {
                handleWandRightClick(e);
                return;
            }
        }

        if (arena.isPlayerImposter(player) && item != null) {

            if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
                return;
            }

            // Sabotages or role items
            String displayName = item.getItemMeta().getDisplayName();
            if (item.getType().equals(Material.FIRE_CHARGE)) {
                if (displayName.equals(REACTOR_ITEM_TITLE)) {
                    e.setCancelled(true);
                    if (arena.getSabotageManager().isSabotageAvailable()) {
                        arena.getReactorManager().start();
                        arena.getSabotageManager().setSabotageLastUse();
                    } else {
                        sendSabotageNotAvailableMessage(player);
                    }
                }
            } else if (item.getType().equals(Material.LANTERN)) {
                if (displayName.equals(TURN_OFF_LIGHTS_ITEM_TITLE)) {
                    if (arena.getSabotageManager().isSabotageAvailable()) {
                        arena.getLightsManager().start();
                        arena.getSabotageManager().setSabotageLastUse();
                    } else {
                        sendSabotageNotAvailableMessage(player);
                    }
                }
            } else if (item.getType().equals(Material.BOOK)) {
                if (displayName.equals(DOOR_SHUT_BOOK)) {
                    if (arena.getSabotageManager().isDoorSabotageAvailable()) {
                        DoorSabotageGui sabotageGui = new DoorSabotageGui(arenaManager.getArena(player), player);
                        sabotageGui.openGui();
                    } else {
                        sendSabotageNotAvailableMessage(player);
                    }
                }
            } else if (item.getType().equals(Material.HEART_OF_THE_SEA)) {
                if (displayName.equals(DEPLETE_O2_ITEM_TITLE)) {
                    e.setCancelled(true);
                    if (arena.getSabotageManager().isSabotageAvailable()) {
                        arena.getOxygenManager().start();
                        arena.getSabotageManager().setSabotageLastUse();
                    } else {
                        sendSabotageNotAvailableMessage(player);
                    }
                }
            } else if (item.getType().equals(Material.ENDER_EYE)) {
                if (displayName.equals(CAMOUFLAGE_ITEM)) {
                    Camouflager camouflager = arena.getGame().camouflager();
                    if (camouflager != null) {
                        arena.getGame().camouflager().camouflage(player);
                    }
                }
            } else if (item.getType().equals(Material.ENCHANTED_BOOK) && displayName.equals(GUESSER_ITEM)) {
                Guesser guesser = arena.getGame().guesser();
                if (guesser != null) {
                    arena.getGame().guesser().openGuessGui(player);
                }
            }
        }

        if (item != null && item.getType().equals(Material.BOOK) && item.hasItemMeta() && item.getItemMeta().getDisplayName().equals(VOTE_BOOK_TITLE)) {
            arena.getMeetingManager().openVoteGui(player);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {

        String viewTitle = e.getView().getTitle();

        // Check for GUI interactions
        if (e.getCurrentItem() != null) {
            Player player = (Player) e.getWhoClicked();

            if (!e.getCurrentItem().hasItemMeta()) {
                return;
            }

            if (e.getCurrentItem().getItemMeta().hasDisplayName() && e.getCurrentItem().getItemMeta().getDisplayName().equals(EXIT_BTN_TITLE)) {
                closeInventory(e);
                return;
            }

            if (arenaManager.getArena(player).isMeetingActive()) {
                if (viewTitle.equals(VOTE_GUI_TITLE)) {
                    voteForPlayer(e);
                    return;
                } else if (viewTitle.equals(GUESSER_GUI_TITLE)) {
                    manageGuess(e);
                    return;
                }
            }

            if ((viewTitle.equals(UPLOAD_TASK_TITLE) || viewTitle.equals(DOWNLOAD_TASK_TITLE))) {
                manageUploadClick(e);
            } else if (viewTitle.equals(SHIELDS_TASK_TITLE)) {
                manageShieldsClick(e);
            } else if (viewTitle.equals(TRASH_TASK_TITLE)) {
                manageTrashClick(e);
            } else if (viewTitle.equals(VENT_TASK_TITLE)) {
                manageVentTaskClick(e);
            } else if (viewTitle.equals(NAVIGATION_TASK_TITLE)) {
                manageNavigationClick(e);
            } else if (viewTitle.equals(ASTEROIDS_TASK_TITLE)) {
                manageAsteroidsClick(e);
            } else if (viewTitle.equals(ALIGN_ENGINE_TASK_TITLE)) {
                manageAlignEngineClick(e);
            } else if (viewTitle.equals(ORDER_NUMBERS_TASK_TITLE)) {
                manageOrderNumbersClick(e);
            } else if (viewTitle.equals(ENABLE_POWER_TASK_TITLE)) {
                manageEnablePowerClick(e);
            } else if (viewTitle.equals(DIVERT_POWER_TASK_TITLE) || viewTitle.equals(FIX_LIGHTS_TASK_TITLE)) {
                manageDivertPowerClick(e, viewTitle.equals(FIX_LIGHTS_TASK_TITLE));
            } else if (viewTitle.equals(CALIBRATE_TASK_TITLE)) {
                manageCalibrateTaskClick(e);
            } else if (viewTitle.equals(DOOR_SABOTAGE_TITLE)) {
                shutDoor(e);
            } else if (viewTitle.equals(REACTOR_MELTDOWN_TASK_TITLE)) {
                manageReactorMeltdownClick(e);
            } else if (viewTitle.equals(OXYGEN_SABOTAGE_TITLE)) {
                manageOxygenClick(e);
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        Arena arena = arenaManager.getArena(player);

        if (arena == null) {
            return;
        }

        if (arena.getCamerasManager().isPlayerOnCameras(player.getUniqueId())) {
            e.setCancelled(true);
            return;
        }

        if (arena.isMeetingActive() || arena.getVentManager().isPlayerInVent(player.getUniqueId())) {
            onlyCancelIfMovedBlock(e);
            return;
        }

        PlayerTask task = arena.getTaskManager().getPlayerOpenTask(player.getUniqueId());
        if (task != null) {
            if (task instanceof MedicalScanTask medicalScanTask) {
                if (e.getTo() == null) {
                    return;
                }
                if (didPlayerMove(e.getFrom(), e.getTo())) {
                    arena.getTaskManager().closeTask(player.getUniqueId());
                    medicalScanTask.cancel();
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        e.setCancelled(true);

        // Handle bomber kill
        if (e.getDamager() instanceof TNTPrimed && e.getEntity() instanceof Player crewmate) {
            Arena arena = arenaManager.getArena(crewmate);
            Bomber bomberClass = arena.getGame().bomber();
            if (bomberClass == null) {
                return;
            }
            UUID bomberUUID = arena.getGame().bomber().getPlayer();
            Player bomber = Bukkit.getPlayer(bomberUUID);
            if (bomber == null) {
                return;
            } else if (arena.getGame().protector() != null && arena.getGame().protector().isPlayerProtected(crewmate.getUniqueId())) {
                sendProtectedMessage(crewmate);
                return;
            }

            if (arena.isPlayerImposter(crewmate)) {
                arena.getDeathManager().killPlayer(crewmate, crewmate.getLocation());
            } else {
                arena.getDeathManager().killCrewmate(bomber, crewmate, crewmate.getLocation(), true);
                return;
            }

            arena.getDeathManager().restartPlayerKillCooldown(bomber.getUniqueId());
            return;
        }

        if (e.getDamager().getType() != EntityType.PLAYER) {
            return;
        } else if (e.getEntity().getType() != EntityType.PLAYER) {
            return;
        }

        Player imposter = (Player) e.getDamager();
        Player crewmate = (Player) e.getEntity();
        Arena imposterArena = arenaManager.getArena(imposter);
        Arena crewmateArena = arenaManager.getArena(crewmate);
        if (imposterArena != crewmateArena) {
            return;
        }

        ItemStack weapon = imposter.getInventory().getItemInMainHand();
        if (weapon.getType() == Material.BLAZE_ROD) {
            Sheriff sheriff = crewmateArena.getGame().sheriff();
            if (sheriff != null && sheriff.is(imposter.getUniqueId())) {
                imposterArena.getDeathManager().sheriffKillPlayer(imposter, crewmate);
            } else {
                imposterArena.getDeathManager().killCrewmate(imposter, crewmate);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        ItemStack item = e.getItemInHand();
        if (!isPermittedPlaceBlock(e.getBlockPlaced().getType())) {
            if (!e.getPlayer().isOp()) {
                e.setCancelled(true);
            }
        } else if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (!meta.hasDisplayName() || !meta.hasLore()) {
                return;
            }

            String displayName = meta.getDisplayName();
            if (displayName.equals(TASK_SIGN_TITLE)) {

                String[] split = getLoreSplit(meta.getLore());
                int arenaId = Integer.parseInt(split[1]);
                String taskCommand = split[0];

                Arena arena = imposterCraft.getArenaManager().getArena(arenaId);
                if (arena == null) {
                    sendInvalidArenaIdMessage(e.getPlayer());
                    return;
                }

                Location blockLocation = getBlockLocation(e);
                arena.getTaskManager().addTaskLocation(blockLocation, taskCommand);

                saveTaskLocation(arenaId, new TaskLocation(blockLocation, taskCommand));

            } else if (displayName.equals(MEETING_SIGN_TITLE)) {

                int arenaId = Integer.parseInt(removeSquareBrackets(meta.getLore().toString()));
                Arena arena = imposterCraft.getArenaManager().getArena(arenaId);
                if (arena == null) {
                    sendInvalidArenaIdMessage(e.getPlayer());
                    return;
                }

                Location meetingSignLocation = getBlockLocation(e);
                arena.getMeetingManager().setEmergencyMeetingSignLocation(meetingSignLocation);

                saveArenaMeetingSignLocation(arenaId, meetingSignLocation);

            } else if (displayName.equals(VENT_TRAPDOOR_TITLE)) {

                String[] split = getLoreSplit(meta.getLore());
                int arenaId = Integer.parseInt(split[1]);
                String ventCategory = split[0];

                Arena arena = imposterCraft.getArenaManager().getArena(arenaId);
                if (arena == null) {
                    sendInvalidArenaIdMessage(e.getPlayer());
                    return;
                }

                Location location = getBlockLocation(e);
                location.setX(location.getBlockX() + 0.5);
                location.setZ(location.getBlockZ() + 0.5);
                int id = saveVentLocation(arenaId, location, ventCategory);
                arena.getVentManager().addVentLocation(ventCategory, new VentLocation(location, ventCategory, id));

            } else if (displayName.equals(CAMERAS_ITEM_TITLE)) {

                int arenaId = Integer.parseInt(getLoreSplit(meta.getLore())[0]);
                Arena arena = imposterCraft.getArenaManager().getArena(arenaId);
                if (arena == null) {
                    sendInvalidArenaIdMessage(e.getPlayer());
                    return;
                }

                Location location = getBlockLocation(e);
                arena.getCamerasManager().addCameraJoinLocation(location);
                saveArenaCameraJoinLocation(arenaId, location);

            } else if (displayName.equals(BOMB_ITEM)) {

                if (item.getType() != Material.TNT) {
                    return;
                }

                e.setCancelled(true);

                Player player = e.getPlayer();
                UUID uuid = player.getUniqueId();
                Arena arena = imposterCraft.getArenaManager().getArena(player);
                if (!arena.getDeathManager().canPlayerKill(uuid)) {
                    sendWaitForCooldownMessage(player, arena.getDeathManager().getPlayerCooldownRemaining(uuid));
                    return;
                } else if (arena.getGame().deputy().isPlayerHandcuffed(uuid)) {
                    sendHandcuffMessage(player);
                    return;
                }

                arena.getDeathManager().restartPlayerKillCooldown(uuid);

                Block tntBlock = e.getBlock();
                World world = tntBlock.getWorld();
                Location location = tntBlock.getLocation();

                tntBlock.setType(Material.AIR);

                TNTPrimed primedTnt = world.spawn(location, TNTPrimed.class);
                primedTnt.setFuseTicks(80);

            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (!e.getPlayer().isOp()) {
            e.setCancelled(true);
        }

        Material material = e.getBlock().getType();
        Location blockLocation = e.getBlock().getLocation();

        if (isOakSign(material)) {
            removeTaskLocationFromConfig(blockLocation);
        } else if (isIronTrapdoor(material)) {
            removeVentLocationFromConfig(blockLocation);
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        if (e.getEntity() instanceof TNTPrimed) {
            e.blockList().clear();
        }
    }

    @EventHandler
    public void onPlayerCloseInventory(InventoryCloseEvent e) {
        String viewTitle = e.getView().getTitle();
        Player player = (Player) e.getPlayer();
        Arena arena = arenaManager.getArena(player);
        if (arena == null) {
            return;
        }

        if (viewTitle.equals(REACTOR_MELTDOWN_TASK_TITLE)) {
            arena.getReactorManager().removeGuiByPlayer(player);
        } else {
            arena.getTaskManager().closeTask(player.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
        if (!(e.getRightClicked() instanceof Player playerInteracted)) {
            return;
        }
        Arena arena = arenaManager.getArena(playerInteracted);
        Player player = e.getPlayer();
        if (arena != arenaManager.getArena(player)) {
            return;
        }
        ItemStack itemUsed = player.getInventory().getItemInMainHand();

        if (itemUsed.hasItemMeta() && itemUsed.getItemMeta().hasDisplayName()) {

            String displayName = itemUsed.getItemMeta().getDisplayName();
            if (displayName.equals(DEPUTY_ITEM)) {

                Deputy deputy = arena.getGame().deputy();
                if (deputy != null) {
                    deputy.handcuffPlayer(player.getUniqueId(), playerInteracted.getUniqueId());
                }

            } else if (displayName.equals(PROTECTOR_ITEM)) {

                Protector protector = arena.getGame().protector();
                if (protector != null) {
                    protector.protectPlayer(player.getUniqueId(), playerInteracted.getUniqueId());
                }

            }
        }
    }

    /*
        Cancelled Events
    */

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent e) {
        e.setCancelled(true);
    }

    private void onlyCancelIfMovedBlock(PlayerMoveEvent e) {
        if (e.getTo() != null) {
            if (didPlayerMove(e.getFrom(), e.getTo())) {
                e.setCancelled(true);
            }
        }
    }

    private void closeInventory(InventoryClickEvent e) {
        e.setCancelled(true);
        e.getWhoClicked().closeInventory();
    }

    private boolean didPlayerMove(Location from, Location to) {
        return from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ();
    }

    private boolean isOakSign(Material material) {
        return material == Material.OAK_SIGN || material == Material.OAK_WALL_SIGN || material == Material.OAK_HANGING_SIGN || material == Material.OAK_WALL_HANGING_SIGN;
    }

    private boolean isIronTrapdoor(Material material) {
        return material == Material.IRON_TRAPDOOR;
    }

    private boolean isOakSignOrIronTrapdoor(Material material) {
        return isIronTrapdoor(material) || isOakSign(material);
    }

    private boolean isPermittedPlaceBlock(Material material) {
        ArrayList<Material> permitted = new ArrayList<>(Arrays.asList(
                Material.TNT
        ));

        return permitted.contains(material) || isOakSignOrIronTrapdoor(material);
    }

    private String[] getLoreSplit(List<String> lore) {
        return lore.toArray(new String[0])[0].split("\\s+");
    }

    private Location getBlockLocation(BlockPlaceEvent e) {
        return e.getBlockPlaced().getLocation();
    }

    private boolean isOnCooldown(Player player) {
        UUID playerUUID = player.getUniqueId();

        if (!interactCooldowns.containsKey(playerUUID)) {
            return false;
        }

        long lastActionTime = interactCooldowns.get(playerUUID);

        if ((System.currentTimeMillis() - lastActionTime) >= PLAYER_INTERACT_COOLDOWN) {
            interactCooldowns.remove(playerUUID);
            return false;
        }

        return true;
    }

    private void setCooldown(Player player) {
        UUID playerUUID = player.getUniqueId();
        interactCooldowns.put(playerUUID, System.currentTimeMillis());
    }

    /*
        Task handlers
    */

    private void manageUploadClick(InventoryClickEvent e) {
        e.setCancelled(true);
        Player player = (Player) e.getWhoClicked();
        UploadTask task = (UploadTask) arenaManager.getArena(player).getTaskManager().getPlayerOpenTask(player.getUniqueId());
        int slot = e.getRawSlot();

        for (int index : UPLOAD_BTN_SLOTS) {
            if (slot == index) {
                task.startUpload();
            }
        }
    }

    private void manageShieldsClick(InventoryClickEvent e) {
        e.setCancelled(true);
        Player player = (Player) e.getWhoClicked();
        Arena arena = arenaManager.getArena(player);
        ShieldsTask task = (ShieldsTask) arena.getTaskManager().getPlayerOpenTask(player.getUniqueId());
        int slot = e.getRawSlot();

        for (int index : SHIELDS_BUTTONS) {
            if (slot == index || slot == index + 1 || slot == index + 9 || slot == index + 10) {
                task.setShieldActivated(index);
            }
        }

        if (task.areAllShieldsActivated()) {
            arena.getTaskManager().completeTask(player.getUniqueId(), task.getTaskType());
            task.complete();
        }
    }

    private void manageTrashClick(InventoryClickEvent e) {
        e.setCancelled(true);
        Player player = (Player) e.getWhoClicked();
        TrashTask task = (TrashTask) arenaManager.getArena(player).getTaskManager().getPlayerOpenTask(player.getUniqueId());
        int slot = e.getRawSlot();

        for (int index : task.getBtnSlots()) {
            if (slot == index) {
                task.startEmptying();
            }
        }
    }

    private void manageVentTaskClick(InventoryClickEvent e) {
        e.setCancelled(true);
        Player player = (Player) e.getWhoClicked();
        Arena arena = arenaManager.getArena(player);
        VentTask task = (VentTask) arena.getTaskManager().getPlayerOpenTask(player.getUniqueId());
        int slot = e.getRawSlot();

        for (int index : task.getItemSlots()) {
            if (slot == index) {
                task.clearItem(slot);
            }
        }

        if (task.isVentClear()) {
            arena.getTaskManager().completeTask(player.getUniqueId(), task.getTaskType());
            task.complete();
        }
    }

    private void manageNavigationClick(InventoryClickEvent e) {
        e.setCancelled(true);
        Player player = (Player) e.getWhoClicked();
        Arena arena = arenaManager.getArena(player);
        NavigationTask task = (NavigationTask) arena.getTaskManager().getPlayerOpenTask(player.getUniqueId());
        int slot = e.getRawSlot();

        if (slot == task.getCenterSlot()) {
            arena.getTaskManager().completeTask(player.getUniqueId(), task.getTaskType());
            task.complete();
        }
    }

    private void manageAsteroidsClick(InventoryClickEvent e) {
        e.setCancelled(true);
        Player player = (Player) e.getWhoClicked();
        AsteroidsTask task = (AsteroidsTask) arenaManager.getArena(player).getTaskManager().getPlayerOpenTask(player.getUniqueId());
        ItemStack item = e.getCurrentItem();

        if (item != null) {
            ArrayList<Material> asteroidMaterials = task.getAsteroidColors();
            if (asteroidMaterials.contains(item.getType())) {
                task.asteroidHit(e.getRawSlot());
            }
        }
    }

    private void manageAlignEngineClick(InventoryClickEvent e) {
        e.setCancelled(true);
        Player player = (Player) e.getWhoClicked();
        Arena arena = arenaManager.getArena(player);
        AlignEngineTask task = (AlignEngineTask) arena.getTaskManager().getPlayerOpenTask(player.getUniqueId());
        int slot = e.getRawSlot();

        if (slot == task.getCenterSlot()) {
            arena.getTaskManager().completeTask(player.getUniqueId(), task.getTaskType());
            task.complete();
        }
    }

    private void manageOrderNumbersClick(InventoryClickEvent e) {
        e.setCancelled(true);
        Player player = (Player) e.getWhoClicked();
        Arena arena = arenaManager.getArena(player);
        NumberTask task = (NumberTask) arena.getTaskManager().getPlayerOpenTask(player.getUniqueId());
        int slot = e.getRawSlot();

        task.handleNumberClick(slot);

        if (task.isTaskComplete()) {
            arena.getTaskManager().completeTask(player.getUniqueId(), task.getTaskType());
            task.complete();
        }
    }

    private void manageEnablePowerClick(InventoryClickEvent e) {
        e.setCancelled(true);
        Player player = (Player) e.getWhoClicked();
        Arena arena = arenaManager.getArena(player);
        EnablePowerTask task = (EnablePowerTask) arena.getTaskManager().getPlayerOpenTask(player.getUniqueId());
        int slot = e.getRawSlot();

        if (task.didClickSwitch(slot)) {
            arena.getTaskManager().completeTask(player.getUniqueId(), task.getTaskType());
        }
    }

    private void manageDivertPowerClick(InventoryClickEvent e, boolean fixLights) {
        e.setCancelled(true);
        Player player = (Player) e.getWhoClicked();
        Arena arena = arenaManager.getArena(player);
        DivertPowerTask task = (DivertPowerTask) arena.getTaskManager().getPlayerOpenTask(player.getUniqueId());
        int slot = e.getRawSlot();

        task.handleSwitchClick(slot);

        if (task.allSwitchesEnabled()) {
            if (!fixLights) {
                arena.getTaskManager().completeTask(player.getUniqueId(), task.getTaskType());
            }
            task.complete();
        }
    }

    private void manageCalibrateTaskClick(InventoryClickEvent e) {
        e.setCancelled(true);
        Player player = (Player) e.getWhoClicked();
        Arena arena = arenaManager.getArena(player);
        CalibrateDistributorTask task = (CalibrateDistributorTask) arena.getTaskManager().getPlayerOpenTask(player.getUniqueId());
        int slot = e.getRawSlot();

        task.handleButtonClick(slot);

        if (task.isTaskComplete()) {
            task.complete();
            arena.getTaskManager().completeTask(player.getUniqueId(), task.getTaskType());
        }
    }

    private void manageReactorMeltdownClick(InventoryClickEvent e) {
        e.setCancelled(true);
        Player player = (Player) e.getWhoClicked();
        Arena arena = arenaManager.getArena(player);
        ReactorMeltdownGui task = (ReactorMeltdownGui) arena.getTaskManager().getPlayerOpenTask(player.getUniqueId());
        int slot = e.getRawSlot();

        boolean buttonClicked = task.handleButtonClick(slot);

        if (buttonClicked && arena.getReactorManager().isFixed()) {
            arena.getReactorManager().end();
        }
    }

    private void manageOxygenClick(InventoryClickEvent e) {
        e.setCancelled(true);
        Player player = (Player) e.getWhoClicked();
        Arena arena = arenaManager.getArena(player);
        OxygenSabotageGui task = (OxygenSabotageGui) arena.getTaskManager().getPlayerOpenTask(player.getUniqueId());
        int slot = e.getRawSlot();

        task.handleClick(slot);

        if (arena.getOxygenManager().isFixed()) {
            arena.getOxygenManager().end();
        }
    }

    private void voteForPlayer(InventoryClickEvent e) {
        e.setCancelled(true);
        if (e.getCurrentItem() == null) {
            return;
        }

        Player player = (Player) e.getWhoClicked();
        Arena arena = arenaManager.getArena(player);
        if (arena == null) {
            return;
        }
        Material item = e.getCurrentItem().getType();
        Colors votedPlayerColor = Colors.getColorByConcrete(item);

        if (votedPlayerColor != null) {
            UUID votedUUID = arena.getPlayerManager().getPlayerByColor(votedPlayerColor);
            if (votedUUID != null) {
                    arena.getMeetingManager().voteForPlayer(player.getUniqueId(), votedUUID);
                    sendGreenMessageToPlayer(player, "You voted for " + Bukkit.getPlayer(votedUUID).getDisplayName() + "!");
                player.closeInventory();
            }
        } else if (item == Material.ARROW && e.getCurrentItem().getItemMeta().getDisplayName().equals(SKIP_BTN_TITLE)) {
            arena.getMeetingManager().voteForSkip(player.getUniqueId());
            sendGreenMessageToPlayer(player, "You voted to skip!");
            player.closeInventory();
        }

        if (arena.getMeetingManager().hasAllPlayersVoted()) {
            arena.getMeetingManager().endEmergencyMeeting();
        }
    }

    private void shutDoor(InventoryClickEvent e) {
        e.setCancelled(true);
        if (e.getCurrentItem() == null) {
            return;
        }

        Player player = (Player) e.getWhoClicked();
        Arena arena = arenaManager.getArena(player);
        if (!arena.isPlayerImposter(player)) {
            return;
        }

        ItemMeta meta = e.getCurrentItem().getItemMeta();
        if (meta == null || !meta.hasLore()) {
            return;
        }

        String doorId = removeSquareBrackets(String.valueOf(meta.getLore()));

        boolean didClose = arena.getDoorManager().closeDoor(doorId);
        if (didClose) {
            Bukkit.getScheduler().runTaskLater(imposterCraft, () -> arena.getDoorManager().openDoor(doorId), arenaManager.getDoorsShutTime());
            sendGreenMessageToPlayer(player, "Doors shut!");
        } else {
            sendRedMessageToPlayer(player, "You cannot shut this door now!");
        }
    }

    private void playerNextVent(Arena arena, Player player) {
        PlayerVentLocation ventLocation = arena.getVentManager().getPlayerVentCategory(player.getUniqueId());
        String category = ventLocation.getVentLocation().getCategory();
        List<VentLocation> ventLocations = arena.getVentManager().getVentLocationsByCategory(category);
        System.out.println("Size:" + ventLocations.size());

        int index = ventLocation.getNextIndex();
        VentLocation nextLocation;
        if (index == ventLocations.size() - 1) {
            System.out.println("back to first: " + index);
            nextLocation = ventLocations.getFirst();
            ventLocation.setNextIndex(0);
        } else {
            System.out.println("next: " + index);
            index++;
            nextLocation = ventLocations.get(index);
            ventLocation.setNextIndex(index);
        }

        player.teleport(nextLocation.getLocation());
    }

    private void manageGuess(InventoryClickEvent e) {
        e.setCancelled(true);
        if (e.getCurrentItem() == null) {
            return;
        }

        Player player = (Player) e.getWhoClicked();
        UUID uuid = player.getUniqueId();
        Arena arena = arenaManager.getArena(player);
        Guesser guesser = arena.getGame().guesser();
        ItemStack item = e.getCurrentItem();

        if (guesser == null) {
            return;
        }

        if (isWool(item.getType())) {
            CrewmateRoleEnum role = guesser.getGui().getRoleBySlot(e.getRawSlot());
            Player guessedPlayer = Bukkit.getPlayer(guesser.getGuessedPlayer());
            if (guessedPlayer == null) {
                return;
            }
            arena.getGame().guesser().guessPlayer(player, guessedPlayer, role);
            player.closeInventory();
        } else {
            Colors guessedPlayerColor = Colors.getColorByConcrete(item.getType());
            if (guessedPlayerColor == null) {
                return;
            }
            UUID guessedUUID = arena.getPlayerManager().getPlayerByColor(guessedPlayerColor);
            guesser.setGuessedPlayer(guessedUUID);
        }
    }
}
