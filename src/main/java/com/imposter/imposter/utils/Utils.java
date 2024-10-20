package com.imposter.imposter.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.lang.reflect.Method;

public class Utils {

    public static boolean locationEquals(Location loc1, Location loc2) {
        return loc1.getBlockX() == loc2.getBlockX()
                && loc1.getBlockY() == loc2.getBlockY()
                && loc1.getBlockZ() == loc2.getBlockZ();
    }

    public static String removeSquareBrackets(String str) {
        return str.replaceAll("[\\[\\]]", "");
    }

    public static int getRemainingCooldown(Long lastUse) {
        if (lastUse == null) {
            return Integer.MAX_VALUE;
        }
        return (int) (System.currentTimeMillis() - lastUse);
    }
}
