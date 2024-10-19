package com.imposter.imposter.utils;

public class StringUtils {

    public static String makeTitle(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}
