package com.imposter.imposter.tasks;

import com.imposter.imposter.ImposterCraft;
import com.imposter.imposter.tasks.runnables.UploadRunnable;
import com.imposter.imposter.utils.Tasks;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static com.imposter.imposter.utils.Constants.FINAL_SLOT_INDEX;

public class UploadTask extends PlayerTask {

    private ImposterCraft imposterCraft;

    private final int[] FILE_SLOTS = { 11, 12, 20, 21, 22, 23, 24, 29, 30, 31, 32, 32, 33 };
    private final boolean isDownload;
    private final String UPLOAD = "Upload";
    private final String DOWNLOAD = "Download";
    private UploadRunnable runnable;

    public static final String UPLOAD_TASK_TITLE = ChatColor.RED.toString() + ChatColor.BOLD + "Upload file";
    public static final String DOWNLOAD_TASK_TITLE = ChatColor.RED.toString() + ChatColor.BOLD + "Download file";
    public static final int[] UPLOAD_BTN_SLOTS = { 48, 49, 50 };

    public UploadTask(ImposterCraft imposterCraft, Player player, boolean isDownload) {
        super(player, UPLOAD_TASK_TITLE, (isDownload ? "Download" : "Upload") + " complete", (isDownload ? Tasks.DOWNLOAD : Tasks.UPLOAD));
        this.imposterCraft = imposterCraft;
        this.isDownload = isDownload;

        createFile();
        createButton();
        addEmptySlots();
    }

    private void createFile() {
        ItemStack fileGlass = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
        ItemMeta fileMeta = fileGlass.getItemMeta();
        fileMeta.setDisplayName(ChatColor.YELLOW + ChatColor.BOLD.toString() + "File");
        fileGlass.setItemMeta(fileMeta);
        for (int slot : FILE_SLOTS) {
            super.getGui().setItem(slot, fileGlass);
        }
    }

    private void createButton() {
        ItemStack uploadButton = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta btnMeta = uploadButton.getItemMeta();
        btnMeta.setDisplayName(ChatColor.GREEN + ChatColor.BOLD.toString() + (this.isDownload ? DOWNLOAD : UPLOAD));
        uploadButton.setItemMeta(btnMeta);
        for (int slot : UPLOAD_BTN_SLOTS) {
            super.getGui().setItem(slot, uploadButton);
        }
    }

    public void startUpload() {
        if (runnable != null) {
            return;
        }
        ItemStack uploadBarItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        for (int i = 45; i < 54; i++) {
            super.getGui().setItem(i, uploadBarItem);
        }

        runnable = new UploadRunnable(imposterCraft, this);
        runnable.start();
    }

    public void fillUploadSlot(int index) {
        ItemStack item = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        super.getGui().setItem((FINAL_SLOT_INDEX + 1) - index, item);
    }

    @Override
    public void cancel() {
        if (runnable != null) {
            runnable.cancel();
        }
    }
}
