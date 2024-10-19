package com.imposter.imposter.utils;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;

public enum Colors {
    RED("RED"),
    ORANGE("ORANGE"),
    GREEN("GREEN"),
    BLUE("BLUE"),
    BLACK("BLACK"),
    PURPLE("PURPLE"),
    GRAY("GRAY"),
    AQUA("AQUA"),
    PINK("PINK"),
    DARK_BLUE("DARK_BLUE"),
    BROWN("BROWN"),
    WHITE("WHITE");

    private final String color;

    Colors(String color) {
        this.color = color;
    }

    public String getColor() {
        return this.color;
    }

    public ChatColor getChatColor() {
        return switch (this) {
            case RED -> ChatColor.RED;
            case BLUE -> ChatColor.BLUE;
            case BLACK -> ChatColor.DARK_GRAY;
            case GREEN -> ChatColor.GREEN;
            case ORANGE -> ChatColor.GOLD;
            case PURPLE -> ChatColor.DARK_PURPLE;
            case AQUA -> ChatColor.AQUA;
            case GRAY -> ChatColor.GRAY;
            case PINK -> ChatColor.LIGHT_PURPLE;
            case DARK_BLUE -> ChatColor.DARK_BLUE;
            case BROWN -> ChatColor.DARK_GREEN;
            case WHITE -> ChatColor.WHITE;
        };
    }

    public static Material getColoredConcrete(Colors color) {
        return switch (color) {
            case RED -> Material.RED_CONCRETE;
            case BLUE -> Material.LIGHT_BLUE_CONCRETE;
            case BLACK -> Material.BLACK_CONCRETE;
            case GREEN -> Material.GREEN_CONCRETE;
            case ORANGE -> Material.ORANGE_CONCRETE;
            case PURPLE -> Material.PURPLE_CONCRETE;
            case AQUA -> Material.CYAN_CONCRETE;
            case GRAY -> Material.GRAY_CONCRETE;
            case PINK -> Material.PINK_CONCRETE;
            case DARK_BLUE -> Material.BLUE_CONCRETE;
            case BROWN -> Material.BROWN_CONCRETE;
            case WHITE -> Material.WHITE_CONCRETE;
        };
    }

    public static Color getColor(Colors color) {
        return switch (color) {
            case RED -> Color.RED;
            case BLUE -> Color.BLUE;
            case BLACK -> Color.BLACK;
            case GREEN -> Color.GREEN;
            case ORANGE -> Color.ORANGE;
            case PURPLE -> Color.PURPLE;
            case AQUA -> Color.AQUA;
            case GRAY -> Color.GRAY;
            case PINK -> Color.fromBGR(255, 192, 203);
            case DARK_BLUE -> Color.fromBGR(139, 0, 0);
            case BROWN -> Color.fromBGR(222, 184, 135);
            case WHITE -> Color.WHITE;
        };
    }

    public static Colors generateColor(int index) {
        return switch (index) {
            case 1 -> Colors.RED;
            case 2 -> Colors.BLUE;
            case 3 -> Colors.BLACK;
            case 4 -> Colors.GREEN;
            case 5 -> Colors.ORANGE;
            case 6 -> Colors.PURPLE;
            case 7 -> Colors.AQUA;
            case 8 -> Colors.GRAY;
            case 9 -> Colors.PINK;
            case 10 -> Colors.DARK_BLUE;
            case 11 -> Colors.BROWN;
            case 12 -> Colors.WHITE;
            default -> throw new IllegalStateException("Unexpected value: " + index);
        };
    }

    public static Colors getColorByConcrete(Material material) {
        return switch (material) {
            case Material.RED_CONCRETE -> RED;
            case Material.LIGHT_BLUE_CONCRETE -> BLUE;
            case Material.BLACK_CONCRETE -> BLACK;
            case Material.GREEN_CONCRETE -> GREEN;
            case Material.ORANGE_CONCRETE -> ORANGE;
            case Material.PURPLE_CONCRETE -> PURPLE;
            case Material.CYAN_CONCRETE -> AQUA;
            case Material.GRAY_CONCRETE -> GRAY;
            case Material.PINK_CONCRETE -> PINK;
            case Material.BLUE_CONCRETE -> DARK_BLUE;
            case Material.BROWN_CONCRETE -> BROWN;
            case Material.WHITE_CONCRETE -> WHITE;
            default -> null;
        };
    }
}
