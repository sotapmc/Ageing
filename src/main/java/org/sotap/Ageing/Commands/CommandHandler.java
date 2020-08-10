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

public final class CommandHandler implements CommandExecutor {
    public Ageing plug;

    public CommandHandler(Ageing plug) {
        this.plug = plug;
    }

    public static void noPermission(Player p) {
        LogUtil.failed("你没有执行该指令的权限。");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("age")) {
            if (args.length > 0) {
                String arg = args[0];
                String playername = null;
                String playerUUID = null;
                Player p = (Player) sender;

                if (!p.hasPermission("ageing." + arg)) {
                    noPermission(p);
                    return true;
                }

                if (args.length > 1) {
                    playername = args[1];
                    try {
                        playerUUID = Bukkit.getPlayer(playername).getUniqueId().toString();
                    } catch (NullPointerException npe) {
                        LogUtil.failed("The player isn't &conline&r or does &cnot exist&r.", p);
                        return true;
                    }

                    if (!plug.ageData.contains(playerUUID)) {
                        LogUtil.failed("There is no data for &c" + playername + "&r.", p);
                        return true;
                    }
                }

                FileConfiguration config = plug.getConfig();

                if (args.length == 3) {
                    if (!Functions.isStringIntegerNatural(args[2])) {
                        LogUtil.failed("&cNatural integer &rrequired.", p);
                        return true;
                    }
                }

                switch (arg) {
                    case "set": {
                        if (args.length != 3) {
                            LogUtil.failed("Invalid argument list length.", p);
                            return true;
                        }
                        Integer newAge = Integer.parseInt(args[2]);
                        if (newAge > config.getInt("max_age")) {
                            LogUtil.failed("The age must be &csmaller&r than the maximum value (&e"
                                    + config.getInt("max_age") + "&r) defined in the config.", p);
                            return true;
                        }
                        plug.controller.updateAge(playername, newAge);
                        plug.saveData();
                        LogUtil.success("Successfully set &a" + playername + "&r's age to &a"
                                + args[2] + "&r.", p);
                        break;
                    }

                    case "get": {
                        if (args.length != 2) {
                            LogUtil.failed("Invalid argument list length", p);
                            return true;
                        }
                        Integer age = plug.ageData.getInt(playerUUID + ".age");
                        LogUtil.info("The age of &a" + playername + "&r is &a"
                                + Integer.toString(age) + "&r.", p);
                        break;
                    }

                    case "add": {
                        if (args.length != 2 && args.length != 3) {
                            LogUtil.failed("Invalid argument list length", p);
                            return true;
                        }
                        Integer maxAge = config.getInt("max_age");
                        Integer oldAge = plug.ageData.getInt(playerUUID + ".age");
                        Integer addend = args.length == 2 ? 1 : Integer.parseInt(args[2]);
                        Integer result = oldAge + addend;
                        if (maxAge == oldAge) {
                            LogUtil.failed("The age is already &cat maximum&r!", p);
                            return true;
                        }
                        if (result > maxAge) {
                            LogUtil.failed(
                                    "Invalid addend. (Must be &cequal&r to or &csmaller &rthan &e"
                                            + (maxAge - oldAge) + "&r)",
                                    p);
                            return true;
                        }
                        plug.controller.updateAge(playername, result);
                        plug.saveData();
                        LogUtil.success("Successfully set &a" + playername + "&r's age to &a"
                                + result + "&r.", p);
                        break;
                    }

                    case "sub": {
                        if (args.length != 2 && args.length != 3) {
                            LogUtil.failed("Invalid argument list length");
                            return true;
                        }
                        Integer oldAge = plug.ageData.getInt(playerUUID + ".age");
                        Integer subtrahend = args.length == 2 ? 1 : Integer.parseInt(args[2]);
                        Integer result = oldAge - subtrahend;
                        if (oldAge == 0) {
                            LogUtil.failed("The age is &e0&r which doesn't need to be subtracted.",
                                    p);
                            return true;
                        }
                        if (result < 0) {
                            LogUtil.failed(
                                    "The subtrahend must be &csmaller &rthan &e" + oldAge + "&r.",
                                    p);
                            return true;
                        }
                        plug.controller.updateAge(playername, result);
                        plug.saveData();
                        LogUtil.success("Successfully set &a" + playername + "&r's age to &a"
                                + result + "&r.", p);
                        break;
                    }

                    case "setexp": {
                        if (args.length != 3) {
                            LogUtil.failed("Invalid argument list length", p);
                            return true;
                        }
                        Integer newExp = Integer.parseInt(args[2]);
                        if (!plug.controller.updateExperience(playername, newExp)) {
                            LogUtil.failed(
                                    "An &cfatal error&r occurred, please check the &econsole&r.",
                                    p);
                            return true;
                        }
                        LogUtil.success("Successfully set &a" + playername + "&r's exp to &a"
                                + args[2] + "&r.", p);
                        break;
                    }

                    case "addexp": {
                        if (args.length != 3) {
                            LogUtil.failed("Invalid argument list length", p);
                            return true;
                        }
                        Integer oldExp = plug.ageData.getInt(playerUUID + ".exp");
                        Integer newExp = oldExp + Integer.parseInt(args[2]);
                        if (!plug.controller.updateExperience(playername, newExp)) {
                            LogUtil.failed(
                                    "An &cfatal error&r occurred, please check if the player is &conline&r and the input is &ccalculatable&r.",
                                    p);
                            return true;
                        }
                        LogUtil.success("Successfully give &a" + args[2] + " exp &rto &a"
                                + playername + "&r.", p);
                        break;
                    }

                    case "subexp": {
                        if (args.length != 3) {
                            LogUtil.failed("Invalid argument list length", p);
                            return true;
                        }
                        Integer oldExp = plug.ageData.getInt(playerUUID + ".exp");
                        Integer newExp = oldExp - Integer.parseInt(args[2]);
                        if (!plug.controller.updateExperience(playername, newExp)) {
                            LogUtil.failed(
                                    "An &cfatal error&r occurred, please check if the player is &conline&r and the input is &ccalculatable&r.",
                                    p);
                            return true;
                        }
                        LogUtil.success("Successfully take &a" + args[2] + " exp &rfrom &a"
                                + playername + "&r.", p);
                        break;
                    }

                    case "getexp": {
                        if (args.length != 2) {
                            LogUtil.failed("Invalid argument list length", p);
                            return true;
                        }
                        Integer exp = plug.ageData.getInt(playerUUID + ".exp");
                        LogUtil.info("The experience value of &a" + playername + "&r is &a"
                                + exp.toString() + "&r.", p);
                        break;
                    }

                    case "reload": {
                        plug.reloadConfig();
                        plug.reloadData();
                        LogUtil.success(
                                "Successfully reloaded the plugin configuration and age data", p);
                        break;
                    }
                }
                return true;
            }
        }
        return false;
    }
}
