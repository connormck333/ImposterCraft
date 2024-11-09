package com.imposter.imposter.utils;

import org.bukkit.Material;

public class SignUtils {

    public static boolean isSign(Material material) {
        return isOakSign(material) || isSpruceSign(material) || isBirchSign(material) || isAcaciaSign(material)
                || isBambooSign(material) || isDarkOakSign(material) || isMangroveSign(material) || isCherrySign(material)
                || isCrimsonSign(material) || isJungleSign(material) || isWarpedSign(material);
    }
    
    public static boolean isOakSign(Material material) {
        return material == Material.OAK_SIGN || material == Material.OAK_WALL_SIGN || material == Material.OAK_HANGING_SIGN || material == Material.OAK_WALL_HANGING_SIGN;
    }
    
    private static boolean isSpruceSign(Material material) {
        return material == Material.SPRUCE_SIGN || material == Material.SPRUCE_WALL_SIGN || material == Material.SPRUCE_HANGING_SIGN || material == Material.SPRUCE_WALL_HANGING_SIGN;
    }
    
    private static boolean isBirchSign(Material material) {
        return material == Material.BIRCH_SIGN || material == Material.BIRCH_WALL_SIGN || material == Material.BIRCH_HANGING_SIGN || material == Material.BIRCH_WALL_HANGING_SIGN;
    }

    private static boolean isAcaciaSign(Material material) {
        return material == Material.ACACIA_SIGN || material == Material.ACACIA_WALL_SIGN || material == Material.ACACIA_HANGING_SIGN || material == Material.ACACIA_WALL_HANGING_SIGN;
    }

    private static boolean isBambooSign(Material material) {
        return material == Material.BAMBOO_SIGN || material == Material.BAMBOO_WALL_SIGN || material == Material.BAMBOO_HANGING_SIGN || material == Material.BAMBOO_WALL_HANGING_SIGN;
    }

    private static boolean isDarkOakSign(Material material) {
        return material == Material.DARK_OAK_SIGN || material == Material.DARK_OAK_WALL_SIGN || material == Material.DARK_OAK_HANGING_SIGN || material == Material.DARK_OAK_WALL_HANGING_SIGN;
    }

    private static boolean isMangroveSign(Material material) {
        return material == Material.MANGROVE_SIGN || material == Material.MANGROVE_WALL_SIGN || material == Material.MANGROVE_HANGING_SIGN || material == Material.MANGROVE_WALL_HANGING_SIGN;
    }

    private static boolean isCherrySign(Material material) {
        return material == Material.CHERRY_SIGN || material == Material.CHERRY_WALL_SIGN || material == Material.CHERRY_HANGING_SIGN || material == Material.CHERRY_WALL_HANGING_SIGN;
    }

    private static boolean isCrimsonSign(Material material) {
        return material == Material.CRIMSON_SIGN || material == Material.CRIMSON_WALL_SIGN || material == Material.CRIMSON_HANGING_SIGN || material == Material.CRIMSON_WALL_HANGING_SIGN;
    }

    private static boolean isJungleSign(Material material) {
        return material == Material.JUNGLE_SIGN || material == Material.JUNGLE_WALL_SIGN || material == Material.JUNGLE_HANGING_SIGN || material == Material.JUNGLE_WALL_HANGING_SIGN;
    }

    private static boolean isWarpedSign(Material material) {
        return material == Material.WARPED_SIGN || material == Material.WARPED_WALL_SIGN || material == Material.WARPED_HANGING_SIGN || material == Material.WARPED_WALL_HANGING_SIGN;
    }
}
