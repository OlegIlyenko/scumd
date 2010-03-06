package com.asolutions.scmsshd.util;

/**
 * @author Oleg Ilyenko
 */
public abstract class StringUtil {

    public static boolean hasText(String str) {
        return str != null && str.trim().length() > 0;
    }

    public static String cleanString(String dirty) {
        StringBuilder builder = new StringBuilder();
        for (String line : dirty.split("\n")) {
            builder.append(line.trim()).append("\n");
        }

        return builder.toString().trim();
    }

}
