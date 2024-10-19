package com.imposter.imposter.tasks;

import com.imposter.imposter.ImposterCraft;
import com.imposter.imposter.tasks.instances.Asteroid;
import com.imposter.imposter.tasks.runnables.AsteroidsRunnable;
import com.imposter.imposter.utils.Tasks;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class AsteroidsTask extends PlayerTask {

    private ImposterCraft imposterCraft;
    private final int MAX_ASTEROIDS = 5;
    private int asteroidsDestroyed = 0;
    private ArrayList<Asteroid> asteroids;
    private final ArrayList<Material> ASTEROID_COLORS;
    private AsteroidsRunnable runnable;

    public static final String ASTEROIDS_TASK_TITLE = ChatColor.RED + ChatColor.BOLD.toString() + "Destroy the asteroids!";

    public AsteroidsTask(ImposterCraft imposterCraft, Player player) {
        super(player, ASTEROIDS_TASK_TITLE, "All asteroids destroyed!", Tasks.ASTEROIDS);
        this.imposterCraft = imposterCraft;
        this.asteroids = new ArrayList<>();
        this.ASTEROID_COLORS = createAsteroidColors();

        setupGui();
    }

    @Override
    public void openGui() {
        super.openGui();
        this.runnable = new AsteroidsRunnable(imposterCraft, this);
        this.runnable.start();
    }

    @Override
    public void cancel() {
        this.runnable.cancel();
    }

    public ArrayList<Material> getAsteroidColors() {
        return this.ASTEROID_COLORS;
    }

    public void asteroidHit(int slot) {
        Asteroid asteroidToDestroy = null;
        for (Asteroid asteroid : this.asteroids) {
            for (int asteroidSlot : asteroid.getSlots()) {
                if (slot == asteroidSlot) {
                    asteroid.setInactive();
                    asteroidToDestroy = asteroid;
                    break;
                }
            }
        }

        if (asteroidToDestroy != null) {
            ItemStack air = new ItemStack(Material.AIR);
            for (int destroySlot : asteroidToDestroy.getSlots()) {
                super.getGui().setItem(destroySlot, air);
            }
            this.asteroidsDestroyed++;
        }
    }

    public boolean isTaskComplete() {
        return asteroidsDestroyed >= MAX_ASTEROIDS;
    }

    public void dropSlots() {
        ItemStack air = new ItemStack(Material.AIR);
        List<Asteroid> asteroidsToRemove = new ArrayList<>();
        List<Asteroid> asteroidsToUpdate = new ArrayList<>();

        // Batch GUI updates
        Map<Integer, ItemStack> guiUpdates = new HashMap<>();

        for (Asteroid asteroid : this.asteroids) {
            int currentSlot = asteroid.getSlot();
            int[] slots = {currentSlot + 10, currentSlot + 9, currentSlot + 1, currentSlot};

            // Clear the current asteroid's slots
            for (int slot : slots) {
                int newSlot = slot + 9;
                if (slot < 54 && !didAsteroidHit(slot)) {
                    guiUpdates.put(slot, air);  // Queue for clearing
                }

                // Check if asteroid has hit or is inactive, skip updating in such cases
                if (didAsteroidHit(newSlot) || !asteroid.isActive()) {
                    continue;
                }
                guiUpdates.put(newSlot, asteroid.getColor());  // Queue for update
            }

            // Update the asteroid's position if it hasn't hit the defense line
            if (!didAsteroidHit(currentSlot + 9) && asteroid.isActive()) {
                asteroid.setSlot(currentSlot + 9);
                asteroidsToUpdate.add(asteroid);  // Queue for updating
            } else {
                asteroidsToRemove.add(asteroid);  // Queue for removal
            }
        }

        // Apply batched updates to the GUI
        for (Map.Entry<Integer, ItemStack> entry : guiUpdates.entrySet()) {
            super.getGui().setItem(entry.getKey(), entry.getValue());
        }

        // Update the asteroids list
        this.asteroids.removeAll(asteroidsToRemove);
        this.asteroids.addAll(asteroidsToUpdate);

        // Ensure empty slots are properly handled
        super.addEmptySlots();

        // Try to spawn a new asteroid if possible
        trySpawnNewAsteroid();
    }

    private void trySpawnNewAsteroid() {
        Random random = new Random(); // You could also move this to the class level for reuse
        int newAsteroidSlot = random.nextInt(6) + 1; // random slot between 1 and 7
        int[] slots = {newAsteroidSlot, newAsteroidSlot + 1, newAsteroidSlot + 9, newAsteroidSlot + 10};

        // Ensure the new asteroid can be placed
        for (int slot : slots) {
            if (!Objects.equals(super.getGui().getItem(slot), super.getEmptySlotItem())) {
                return;  // If any slot is occupied, abort spawning
            }
        }

        // Create and place a new asteroid
        Asteroid newAsteroid = new Asteroid(createAsteroid(), newAsteroidSlot);
        for (int slot : slots) {
            super.getGui().setItem(slot, newAsteroid.getColor());
        }

        this.asteroids.add(newAsteroid);  // Add the new asteroid to the list
    }

    private ItemStack createAsteroid() {
        Random random = new Random();
        ItemStack item = new ItemStack(ASTEROID_COLORS.get(random.nextInt(ASTEROID_COLORS.size())));
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RED + ChatColor.BOLD.toString() + "Asteroid");
        meta.setLore(Collections.singletonList(ChatColor.GOLD + "Click to destroy!"));
        item.setItemMeta(meta);

        return item;
    }

    private void setupGui() {
        ItemStack bottomBar = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = bottomBar.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + ChatColor.BOLD.toString() + "Defense");
        meta.setLore(Collections.singletonList(ChatColor.GOLD + "Destroy the asteroids before they hit the ship!"));
        bottomBar.setItemMeta(meta);

        for (int i = 45; i < 54; i++) {
            super.getGui().setItem(i, bottomBar);
        }

        super.getGui().setItem(39, bottomBar);
        super.getGui().setItem(41, bottomBar);
    }

    private ArrayList<Material> createAsteroidColors() {
        ArrayList<Material> list = new ArrayList<>();
        list.add(Material.RED_STAINED_GLASS_PANE);
        list.add(Material.YELLOW_STAINED_GLASS_PANE);
        list.add(Material.ORANGE_STAINED_GLASS_PANE);
        list.add(Material.GRAY_STAINED_GLASS_PANE);

        return list;
    }

    private boolean didAsteroidHit(int slot) {
        return slot >=45 || slot == 39 || slot == 41;
    }
}
