package org.sotap.Ageing.Utils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.sotap.Ageing.Ageing;

public final class Functions {
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
        if (Functions.isStringInteger(str)) {
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
        if (Functions.isStringIntegerNatural(str)) {
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
        Player p = Objects.requireNonNull(Bukkit.getPlayer(playername));
        return plugin.ageData
                .getConfigurationSection(p.getUniqueId().toString());
    }

    public static void dispatchCommands(List<String> commands, String playername, String uuid) {
        for (String command : commands) {
            Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),
                command.replace("%playername%", playername).replace("%uuid%", uuid));
        }
    }

    public static boolean eq(Object a, Object b) {
        return Objects.equals(a, b);
    }
}
