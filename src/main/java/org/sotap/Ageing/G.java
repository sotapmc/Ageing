package org.sotap.Ageing;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;

public final class G {
    public final static String SUCCESS = "&r[&aSUCCESS&r] ";
    public final static String WARN = "&r[&eWARN&r] ";
    public final static String FAILED = "&r[&cFAILED&r] ";
    public final static String INFO = "&r[&bINFO&r] ";

    public static String translateColor(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static Boolean isStringNumeric(String str) {
        try {
            @SuppressWarnings("unused")
            String big = new BigDecimal(str).toString();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static Boolean isStringInteger(String str) {
        Matcher mer = Pattern.compile("^[+-]?[0-9]+$").matcher(str);  
        return mer.find();  
    }

    public static Boolean isStringIntegerNatural(String str) {
        if (G.isStringInteger(str)) {
            return Integer.parseInt(str) >= 0;
        }
        return false;
    }

    public static Boolean isStringIntegerPositive(String str) {
        if (G.isStringIntegerNatural(str)) {
            return Integer.parseInt(str) > 0;
        }
        return false;
    }
}