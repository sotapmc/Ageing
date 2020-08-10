package org.sotap.Ageing.Commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

public final class Tab implements TabCompleter {
    private static final String[] BASE = {"set", "get", "add", "sub", "setexp", "addexp", "subexp", "getexp", "reload"};

    public Tab() {}

    public List<String> getAvailableCommands(Player p) {
        List<String> available = new ArrayList<>();
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
            result = StringUtil.copyPartialMatches(arguments[0], getAvailableCommands((Player) sender), result);
            Collections.sort(result);
        }
        return result;
    }
}
