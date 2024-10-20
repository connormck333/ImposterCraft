package com.imposter.imposter.instances;

import com.imposter.imposter.utils.Colors;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import static com.imposter.imposter.utils.Colors.getColor;
import static com.imposter.imposter.utils.Colors.getColoredConcrete;
import static com.imposter.imposter.utils.VersionUtils.setEnchantmentGlintOverride;

public class Outfit {

    private final ItemStack helmet;
    private final ItemStack chestplate;
    private final ItemStack leggings;
    private final ItemStack boots;
    private final Colors color;

    public Outfit(Colors color) {
        this.color = color;
        this.helmet = getConcrete();
        this.chestplate = getItem(Material.LEATHER_CHESTPLATE);
        this.leggings = getItem(Material.LEATHER_LEGGINGS);
        this.boots = getItem(Material.LEATHER_BOOTS);
    }

    public ItemStack[] getArmor() {
        return new ItemStack[]{ this.boots, this.leggings, this.chestplate, this.helmet };
    }

    private ItemStack getConcrete() {
        ItemStack item = new ItemStack(getColoredConcrete(this.color));
        ItemMeta meta = item.getItemMeta();
        item.setItemMeta(getMeta(meta));

        return item;
    }

    private ItemStack getItem(Material armor) {
        ItemStack item = new ItemStack(armor);
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        item.setItemMeta(getMeta(meta));

        return item;
    }

    private LeatherArmorMeta getMeta(LeatherArmorMeta meta) {
        meta = (LeatherArmorMeta) getMeta((ItemMeta) meta);
        meta.setColor(getColor(color));

        return meta;
    }

    private ItemMeta getMeta(ItemMeta meta) {
        meta.addEnchant(Enchantment.BINDING_CURSE, 1, false);
        meta.setUnbreakable(true);
        setEnchantmentGlintOverride(meta);

        return meta;
    }
}
