package com.thenodemc.specnik;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    static Pattern regex = Pattern.compile("(§x§([0-9a-fA-F]|§){11})");
    public static String parseLegacyToHex(String message) {

        Matcher regexMatcher = regex.matcher(message);

        // Requires Java 9+
        if (regexMatcher.find()) {
            message = regexMatcher.replaceAll(
                    m -> m.group().replaceAll("§x","&#").replaceAll("§","")
            );
        }
        message = message.replaceAll("&[0-9a-f]{1}","§");

        return message;
    }
}
