package com.imposter.imposter.utils;

import com.imposter.imposter.ImposterCraft;
import com.imposter.imposter.instances.Arena;
import com.imposter.imposter.instances.corpse_entities.CorpseEntity;
import com.imposter.imposter.instances.corpse_entities.CorpseEntityV1_21_1;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionUtils {

    public static boolean isVersion(int major, int minor) {
        String version = Bukkit.getVersion();

        String regex = "MC: (\\d+)\\.(\\d+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(version);

        if (matcher.find()) {
            int currentMajor = Integer.parseInt(matcher.group(1));
            int currentMinor = Integer.parseInt(matcher.group(2));

            System.out.println(currentMajor + " " + major);
            System.out.println(currentMinor + " " + minor);

            return currentMajor == major && currentMinor == minor;
        }

        return false;
    }

    public static boolean isVersionAtLeast(int major, int minor) {
        String version = Bukkit.getVersion();

        String regex = "MC: (\\d+)\\.(\\d+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(version);

        if (matcher.find()) {
            int currentMajor = Integer.parseInt(matcher.group(1));
            int currentMinor = Integer.parseInt(matcher.group(2));

            return (currentMajor > major) || (currentMajor == major && currentMinor >= minor);
        }

        return false;
    }

    public static void setEnchantmentGlintOverride(ItemMeta meta) {
        if (isVersionAtLeast(1, 21)) {
            try {
                Method method = meta.getClass().getMethod("setEnchantmentGlintOverride", boolean.class);
                method.invoke(meta, true);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static CorpseEntity createCorpseEntityByVersion(ImposterCraft imposterCraft, Arena arena, Player player, Location deathLocation, boolean isPlayerOnCameras) {
//        if (isVersion(1, 21)) {
//            return new CorpseEntityV1_21_1(imposterCraft, arena, player, deathLocation, isPlayerOnCameras);
//        } else if (isVersion(1, 20)) {
//            System.out.println("1, 20");
//            return new CorpseEntityV1_20_2(imposterCraft, arena, player, deathLocation, isPlayerOnCameras);
//        }

        return new CorpseEntityV1_21_1(imposterCraft, arena, player, deathLocation, isPlayerOnCameras);
    }
}
