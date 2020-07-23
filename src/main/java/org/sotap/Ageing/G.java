package org.sotap.Ageing;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;

public class G {
    public static String success = "&r[&aSUCCESS&r] ";
    public static String warn = "&r[&eWARN&r] ";
    public static String failed = "&r[&cFAILED&r] ";
    public static String info = "&r[&bINFO&r] ";

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
            return Integer.parseInt(str) > 0;
        }
        return false;
    }
}