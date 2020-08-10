package org.sotap.Ageing.Utils;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.sotap.Ageing.Ageing;

public final class G {
    public final static String SUCCESS = "&r[&aSUCCESS&r] ";
    public final static String WARN = "&r[&eWARN&r] ";
    public final static String FAILED = "&r[&cFAILED&r] ";
    public final static String INFO = "&r[&bINFO&r] ";

    /**
     * 调用 ChatColor 对 '&' 进行转义
     * 
     * @param message 要转义的字符串
     * @return 转义后的字符串
     */
    public static String translateColor(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * 判断一个字符串是否为数字
     * 
     * @param str 字符串
     * @return boolean
     */
    public static boolean isStringNumeric(String str) {
        try {
            @SuppressWarnings("unused")
            String big = new BigDecimal(str).toString();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 判断一个字符串数字是否为整数
     * 
     * @param str 字符串数字
     * @return boolean
     */
    public static boolean isStringInteger(String str) {
        Matcher mer = Pattern.compile("^[+-]?[0-9]+$").matcher(str);
        return mer.find();
    }

    /**
     * 判断一个字符串数字是否为自然数
     * 
     * @param str 字符串数字
     * @return boolean
     */
    public static boolean isStringIntegerNatural(String str) {
        if (G.isStringInteger(str)) {
            return Integer.parseInt(str) >= 0;
        }
        return false;
    }

    /**
     * 判断一个字符串数字是否为正整数
     * 
     * @param str 字符串数字
     * @return boolean
     */
    public static boolean isStringIntegerPositive(String str) {
        if (G.isStringIntegerNatural(str)) {
            return Integer.parseInt(str) > 0;
        }
        return false;
    }

    /**
     * 获取指定玩家的数据 ConfigurationSection 在使用之前请验证该玩家是否在线
     * 
     * @param plugin     插件实例
     * @param playername 玩家名称
     * @return ConfigurationSection
     */
    public static ConfigurationSection getDataOf(Ageing plugin, String playername) {
        return plugin.ageData
                .getConfigurationSection(Bukkit.getPlayer(playername).getUniqueId().toString());
    }

    public static void dispatchCommand(String command, String playername, String uuid) {
        Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),
                command.replace("%playername%", playername).replace("%uuid%", uuid));
    }
}
