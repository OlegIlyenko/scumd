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

    public static String produce(String s, int n) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < n; i++) {
            sb.append(s);
        }

        return sb.toString();
    }

}
