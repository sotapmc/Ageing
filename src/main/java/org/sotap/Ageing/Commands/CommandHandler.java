package org.sotap.Ageing.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.sotap.Ageing.Ageing;
import org.sotap.Ageing.G;

public final class CommandHandler implements CommandExecutor {
    public Ageing plug;

    public CommandHandler(Ageing plug) {
        this.plug = plug;
    }

    public static void noPermission(Player p) {
        p.sendMessage(G.translateColor(G.FAILED + "你没有执行该指令的权限。"));
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
                        sender.sendMessage(G.translateColor(
                                G.FAILED + "The player isn't &conline&r or does &cnot exist&r."));
                        return true;
                    }

                    if (!plug.ageData.contains(playerUUID)) {
                        sender.sendMessage(G.translateColor(
                                G.FAILED + "There is no data for &c" + playername + "&r."));
                        return true;
                    }
                }

                FileConfiguration config = plug.getConfig();

                if (args.length == 3) {
                    if (!G.isStringIntegerNatural(args[2])) {
                        sender.sendMessage(
                                G.translateColor(G.FAILED + "&cNatural integer &rrequired."));
                        return true;
                    }
                }

                switch (arg) {
                    case "set": {
                        if (args.length != 3) {
                            sender.sendMessage(
                                    G.translateColor(G.FAILED + "Invalid argument list length."));
                            return true;
                        }
                        Integer newAge = Integer.parseInt(args[2]);
                        if (newAge > config.getInt("max_age")) {
                            sender.sendMessage(G.translateColor(G.FAILED
                                    + "The age must be &csmaller&r than the maximum value (&e"
                                    + config.getInt("max_age") + "&r) defined in the config."));
                            return true;
                        }
                        plug.controller.updateAge(playername, newAge);
                        plug.saveData();
                        sender.sendMessage(G.translateColor(G.SUCCESS + "Successfully set &a"
                                + playername + "&r's age to &a" + args[2] + "&r."));
                        break;
                    }

                    case "get": {
                        if (args.length != 2) {
                            sender.sendMessage(
                                    G.translateColor(G.FAILED + "Invalid argument list length"));
                            return true;
                        }
                        Integer age = plug.ageData.getInt(playerUUID + ".age");
                        sender.sendMessage(G.translateColor(G.INFO + "The age of &a" + playername
                                + "&r is &a" + Integer.toString(age) + "&r."));
                        break;
                    }

                    case "add": {
                        if (args.length != 2 && args.length != 3) {
                            sender.sendMessage(
                                    G.translateColor(G.FAILED + "Invalid argument list length"));
                            return true;
                        }
                        Integer maxAge = config.getInt("max_age");
                        Integer oldAge = plug.ageData.getInt(playerUUID + ".age");
                        Integer addend = args.length == 2 ? 1 : Integer.parseInt(args[2]);
                        Integer result = oldAge + addend;
                        if (maxAge == oldAge) {
                            sender.sendMessage(G.translateColor(
                                    G.FAILED + "The age is already &cat maximum&r!"));
                            return true;
                        }
                        if (result > maxAge) {
                            sender.sendMessage(G.translateColor(G.FAILED
                                    + "Invalid addend. (Must be &cequal&r to or &csmaller &rthan &e"
                                    + (maxAge - oldAge) + "&r)"));
                            return true;
                        }
                        plug.controller.updateAge(playername, result);
                        plug.saveData();
                        sender.sendMessage(G.translateColor(G.SUCCESS + "Successfully set &a"
                                + playername + "&r's age to &a" + result + "&r."));
                        break;
                    }

                    case "sub": {
                        if (args.length != 2 && args.length != 3) {
                            sender.sendMessage(
                                    G.translateColor(G.FAILED + "Invalid argument list length"));
                            return true;
                        }
                        Integer oldAge = plug.ageData.getInt(playerUUID + ".age");
                        Integer subtrahend = args.length == 2 ? 1 : Integer.parseInt(args[2]);
                        Integer result = oldAge - subtrahend;
                        if (oldAge == 0) {
                            sender.sendMessage(G.translateColor(G.FAILED
                                    + "The age is &e0&r which doesn't need to be subtracted."));
                            return true;
                        }
                        if (result < 0) {
                            sender.sendMessage(G.translateColor(
                                    G.FAILED + "The subtrahend must be &csmaller &rthan &e" + oldAge
                                            + "&r."));
                            return true;
                        }
                        plug.controller.updateAge(playername, result);
                        plug.saveData();
                        sender.sendMessage(G.translateColor(G.SUCCESS + "Successfully set &a"
                                + playername + "&r's age to &a" + result + "&r."));
                        break;
                    }

                    case "setexp": {
                        if (args.length != 3) {
                            sender.sendMessage(
                                    G.translateColor(G.FAILED + "Invalid argument list length"));
                            return true;
                        }
                        Integer newExp = Integer.parseInt(args[2]);
                        if (!plug.controller.updateExperience(playername, newExp)) {
                            sender.sendMessage(G.translateColor(G.FAILED
                                    + "An &cfatal error&r occurred, please check the &econsole&r."));
                            return true;
                        }
                        sender.sendMessage(G.translateColor(G.SUCCESS + "Successfully set &a"
                                + playername + "&r's exp to &a" + args[2] + "&r."));
                        break;
                    }

                    case "addexp": {
                        if (args.length != 3) {
                            sender.sendMessage(
                                    G.translateColor(G.FAILED + "Invalid argument list length"));
                            return true;
                        }
                        Integer oldExp = plug.ageData.getInt(playerUUID + ".exp");
                        Integer newExp = oldExp + Integer.parseInt(args[2]);
                        if (!plug.controller.updateExperience(playername, newExp)) {
                            sender.sendMessage(G.translateColor(G.FAILED
                                    + "An &cfatal error&r occurred, please check if the player is &conline&r and the input is &ccalculatable&r."));
                            return true;
                        }
                        sender.sendMessage(G.translateColor(G.SUCCESS + "Successfully give &a"
                                + args[2] + " exp &rto &a" + playername + "&r."));
                        break;
                    }

                    case "subexp": {
                        if (args.length != 3) {
                            sender.sendMessage(
                                    G.translateColor(G.FAILED + "Invalid argument list length"));
                            return true;
                        }
                        Integer oldExp = plug.ageData.getInt(playerUUID + ".exp");
                        Integer newExp = oldExp - Integer.parseInt(args[2]);
                        if (!plug.controller.updateExperience(playername, newExp)) {
                            sender.sendMessage(G.translateColor(G.FAILED
                                    + "An &cfatal error&r occurred, please check if the player is &conline&r and the input is &ccalculatable&r."));
                            return true;
                        }
                        sender.sendMessage(G.translateColor(G.SUCCESS + "Successfully take &a"
                                + args[2] + " exp &rfrom &a" + playername + "&r."));
                        break;
                    }

                    case "getexp": {
                        if (args.length != 2) {
                            sender.sendMessage(
                                    G.translateColor(G.FAILED + "Invalid argument list length"));
                            return true;
                        }
                        Integer exp = plug.ageData.getInt(playerUUID + ".exp");
                        sender.sendMessage(G.translateColor(G.INFO + "The experience value of &a"
                                + playername + "&r is &a" + exp.toString() + "&r."));
                        break;
                    }

                    case "reload": {
                        plug.reloadConfig();
                        plug.reloadData();
                        sender.sendMessage(G.translateColor(G.SUCCESS
                                + "Successfully reloaded the plugin configuration and age data"));
                        break;
                    }
                }
                return true;
            }
        }
        return false;
    }
}
