package org.sotap.Ageing;

import org.bukkit.ChatColor;

public class G {
    public static String success = "&r[&aSUCCESS&r] ";
    public static String warn = "&r[&eWARN&r] ";
    public static String failed = "&r[&cFAILED&r] ";
    public static String info = "&r[&bINFO&r] ";

    public static String translateColor(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}