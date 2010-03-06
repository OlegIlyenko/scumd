package com.asolutions.scmsshd.util;

/**
 * @author Oleg Ilyenko
 */
public class SpringUtil {

    public static String fixConfigLocation(String configLocation) {
        if (configLocation.startsWith("/")) {
            return "file:" + configLocation;
        } else {
            return configLocation;
        }
    }

}
