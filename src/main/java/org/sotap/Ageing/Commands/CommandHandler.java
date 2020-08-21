package org.sotap.Ageing.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.sotap.Ageing.Ageing;
import org.sotap.Ageing.Utils.Functions;
import org.sotap.Ageing.Utils.LogUtil;

import java.util.Objects;

public final class CommandHandler implements CommandExecutor {
    public Ageing plug;

    public CommandHandler(Ageing plug) {
        this.plug = plug;
    }

    public static void noPermission(Player p) {
        LogUtil.warn("你没有执行该指令的权限。", p);
    }

    public static void playerOnlyWarning() {
        LogUtil.failed("该指令只能被玩家执行。");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("ageing")) {
            if (args.length > 0) {
                String arg = args[0];
                String playername = null;
                String playerUUID = null;
                Player p = sender instanceof Player ? (Player) sender : null;

                if (p != null) {
                    if (!p.hasPermission("ageing." + arg)) {
                        noPermission(p);
                        return true;
                    }
                }

                if (args.length > 1) {
                    playername = args[1];
                    try {
                        playerUUID = Objects.requireNonNull(Bukkit.getPlayer(playername)).getUniqueId().toString();
                    } catch (NullPointerException npe) {
                        LogUtil.failed("指定的玩家&c不在线&r或者&c不存在&r。", p);
                        return true;
                    }

                    if (!plug.ageData.contains(playerUUID)) {
                        LogUtil.failed("找不到 &c" + playername + "&r 的数据。", p);
                        return true;
                    }
                }

                FileConfiguration config = plug.getConfig();

                if (args.length == 3) {
                    if (!Functions.isStringIntegerNatural(args[2])) {
                        LogUtil.failed("参数必须为&c自然数&r。", p);
                        return true;
                    }
                }

                switch (arg) {
                    case "set": {
                        if (args.length != 3) {
                            LogUtil.failed("无效参数。", p);
                            return true;
                        }
                        int newAge = Integer.parseInt(args[2]);
                        if (newAge > config.getInt("max_age")) {
                            LogUtil.failed("目标年龄必须&c小于&r最高年龄 (&e"
                                    + config.getInt("max_age") + "&r)。", p);
                            return true;
                        }
                        plug.controller.updateAge(playername, newAge);
                        plug.saveData();
                        LogUtil.success("成功将 &a" + playername + "&r 的年龄设置为了 &a"
                                + args[2] + "&r.", p);
                        break;
                    }

                    case "get": {
                        if (args.length != 2) {
                            LogUtil.failed("无效参数。", p);
                            return true;
                        }
                        int age = plug.ageData.getInt(playerUUID + ".age");
                        LogUtil.info("&a" + playername + "&r 的年龄为 &a"
                                + age + "&r。", p);
                        break;
                    }

                    case "add": {
                        if (args.length != 2 && args.length != 3) {
                            LogUtil.failed("无效参数。", p);
                            return true;
                        }
                        int maxAge = config.getInt("max_age");
                        int oldAge = plug.ageData.getInt(playerUUID + ".age");
                        int addend = args.length == 2 ? 1 : Integer.parseInt(args[2]);
                        int result = oldAge + addend;
                        if (maxAge == oldAge) {
                            LogUtil.failed("该玩家的年龄已经是&c最大值&r。", p);
                            return true;
                        }
                        if (result > maxAge) {
                            LogUtil.failed(
                                    "无效的加数，必须&c小于或等于&r &e"
                                            + (maxAge - oldAge) + "&r。",
                                    p);
                            return true;
                        }
                        plug.controller.updateAge(playername, result);
                        plug.saveData();
                        LogUtil.success("成功将 &a" + playername + "&r 的年龄设置为了 &a"
                                + result + "&r。", p);
                        break;
                    }

                    case "sub": {
                        if (args.length != 2 && args.length != 3) {
                            LogUtil.failed("无效参数。");
                            return true;
                        }
                        int oldAge = plug.ageData.getInt(playerUUID + ".age");
                        int subtrahend = args.length == 2 ? 1 : Integer.parseInt(args[2]);
                        int result = oldAge - subtrahend;
                        if (oldAge == 0) {
                            LogUtil.failed("该玩家的年龄为 &e0&r，不可再减。",
                                    p);
                            return true;
                        }
                        if (result < 0) {
                            LogUtil.failed(
                                    "减数必须&c小于 &e" + oldAge + "&r。",
                                    p);
                            return true;
                        }
                        plug.controller.updateAge(playername, result);
                        plug.saveData();
                        LogUtil.success("成功将 &a" + playername + "&r 的年龄设置为了 &a"
                        + result + "&r。", p);
                        break;
                    }

                    case "setexp": {
                        if (args.length != 3) {
                            LogUtil.failed("无效参数。", p);
                            return true;
                        }
                        Integer newExp = Integer.parseInt(args[2]);
                        if (!plug.controller.updateExperience(playername, newExp)) {
                            LogUtil.failed(
                                    "发生严重错误，请检查该玩家是否在线，提供的数值是否正确。",
                                    p);
                            return true;
                        }
                        LogUtil.success("成功将 &a" + playername + "&r 的经验值设为了 &a"
                                + args[2] + "&r。", p);
                        break;
                    }

                    case "addexp": {
                        if (args.length != 3) {
                            LogUtil.failed("无效参数。", p);
                            return true;
                        }
                        int oldExp = plug.ageData.getInt(playerUUID + ".exp");
                        Integer newExp = oldExp + Integer.parseInt(args[2]);
                        if (!plug.controller.updateExperience(playername, newExp)) {
                            LogUtil.failed(
                                    "发生严重错误，请检查该玩家是否在线，提供的数值是否正确。",
                                    p);
                            return true;
                        }
                        LogUtil.success("成功将 &a" + args[2] + " 经验值&r给予了 &a"
                                + playername + "&r。", p);
                        break;
                    }

                    case "subexp": {
                        if (args.length != 3) {
                            LogUtil.failed("无效参数。", p);
                            return true;
                        }
                        int oldExp = plug.ageData.getInt(playerUUID + ".exp");
                        Integer newExp = oldExp - Integer.parseInt(args[2]);
                        if (!plug.controller.updateExperience(playername, newExp)) {
                            LogUtil.failed(
                                    "发生严重错误，请检查该玩家是否在线，提供的数值是否正确。",
                                    p);
                            return true;
                        }
                        LogUtil.success("成功从 &a" + playername + " &r的数据中取走了 &a"
                                + args[2] + " 经验值&r。", p);
                        break;
                    }

                    case "getexp": {
                        if (args.length != 2) {
                            LogUtil.failed("无效参数。", p);
                            return true;
                        }
                        int exp = plug.ageData.getInt(playerUUID + ".exp");
                        LogUtil.info("&a" + playername + "&r 的总经验值为 &a"
                                + exp + "&r。", p);
                        break;
                    }

                    case "me": {
                        if (p == null) {
                            playerOnlyWarning();
                            break;
                        }
                        LogUtil.info("您的 Ageing 年龄： &a" + plug.ageData.getInt(p.getUniqueId() + ".age"), p);
                        LogUtil.info("您的 Ageing 总经验值： &a" + plug.ageData.getInt(p.getUniqueId() + ".exp"), p);
                        break;
                    }

                    case "reload": {
                        plug.reloadConfig();
                        plug.reloadData();
                        LogUtil.success(
                                "成功重载配置文件与年龄数据。", p);
                        break;
                    }
                }
                return true;
            }
        }
        return false;
    }
}
