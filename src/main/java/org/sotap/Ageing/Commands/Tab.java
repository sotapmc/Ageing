package org.sotap.Ageing.Commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

public final class Tab implements TabCompleter {
    private static final String[] BASE = {"set", "get", "add", "sub", "setexp", "addexp", "subexp", "getexp", "reload", "me"};

    public Tab() {}

    public List<String> getAvailableCommands(Player p) {
        List<String> available = new ArrayList<>();
        if (p == null) {
            available = Arrays.asList(BASE);
            return available;
        }
        for (String cmd : BASE) {
            if (p.hasPermission("ageing." + cmd)) {
                available.add(cmd);
            }
        }
        return available;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] arguments) {
        List<String> result = new ArrayList<>();
        if (command.getName().equalsIgnoreCase("ageing")) {
            if (arguments.length == 1) {
                result = StringUtil.copyPartialMatches(arguments[0], getAvailableCommands(sender instanceof Player ? (Player) sender : null), result);
                Collections.sort(result);
            } else {
                result = null;
            }
        }
        return result;
    }
}
