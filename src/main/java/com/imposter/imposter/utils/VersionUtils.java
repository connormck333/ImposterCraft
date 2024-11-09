package com.imposter.imposter.utils;

import org.bukkit.Bukkit;
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

            return currentMajor == major && currentMinor == minor;
        }

        return false;
    }

    public static boolean isVersionAtLeast(String versionToCheck) {
        String currentVersion = Bukkit.getVersion();

        // Extract major and minor versions from the current version
        String regex = "MC: 1\\.(\\d+)(?:\\.(\\d+))?";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(currentVersion);

        if (matcher.find()) {
            int currentMajor = Integer.parseInt(matcher.group(1));
            int currentMinor = matcher.group(2) != null ? Integer.parseInt(matcher.group(2)) : 0;

            // Extract major and minor from the versionToCheck
            String[] versionParts = versionToCheck.split("\\.");
            if (versionParts.length < 2) {
                return false; // Invalid version to check
            }

            int checkMajor = Integer.parseInt(versionParts[0]);
            int checkMinor = Integer.parseInt(versionParts[1]);

            // Compare versions: check if the current version is at least the version to check
            return (currentMajor > checkMajor) || (currentMajor == checkMajor && currentMinor >= checkMinor);
        }

        return false;
    }

    public static void setEnchantmentGlintOverride(ItemMeta meta) {
        if (isVersionAtLeast("21.0")) {
            try {
                Method method = meta.getClass().getMethod("setEnchantmentGlintOverride", boolean.class);
                method.invoke(meta, true);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {}
        }
    }
}
