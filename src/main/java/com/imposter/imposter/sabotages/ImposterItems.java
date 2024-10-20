package com.imposter.imposter.sabotages;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.imposter.imposter.utils.Constants.*;
import static com.imposter.imposter.utils.Constants.DOOR_SHUT_BOOK;
import static com.imposter.imposter.utils.VersionUtils.isVersionAtLeast;
import static com.imposter.imposter.utils.VersionUtils.setEnchantmentGlintOverride;

public class ImposterItems {

    private ItemStack weapon;
    private ItemStack lightSwitch;
    private ItemStack oxygenTrigger;
    private ItemStack reactorTrigger;
    private ItemStack doorBook;

    public ImposterItems() {
        weapon = new ItemStack(Material.BLAZE_ROD, 1);
        lightSwitch = new ItemStack(Material.LANTERN, 1);
        oxygenTrigger = new ItemStack(Material.HEART_OF_THE_SEA, 1);
        reactorTrigger = new ItemStack(Material.FIRE_CHARGE, 1);
        doorBook = new ItemStack(Material.BOOK, 1);

        weapon.setItemMeta(getMeta(weapon, "Weapon"));
        lightSwitch.setItemMeta(getMeta(lightSwitch, TURN_OFF_LIGHTS_ITEM_TITLE));
        oxygenTrigger.setItemMeta(getMeta(oxygenTrigger, DEPLETE_O2_ITEM_TITLE));
        reactorTrigger.setItemMeta(getMeta(reactorTrigger, REACTOR_ITEM_TITLE));
        doorBook.setItemMeta(getMeta(doorBook, DOOR_SHUT_BOOK));
    }

    public ItemStack getWeapon() {
        return weapon;
    }

    public ItemStack getLightSwitch() {
        return lightSwitch;
    }

    public ItemStack getOxygenTrigger() {
        return oxygenTrigger;
    }

    public ItemStack getReactorTrigger() {
        return reactorTrigger;
    }

    public ItemStack getDoorBook() {
        return doorBook;
    }

    private ItemMeta getMeta(ItemStack item, String title) {
        ItemMeta meta = item.getItemMeta();
        setEnchantmentGlintOverride(meta);
        meta.setDisplayName(title);

        return meta;
    }
}
