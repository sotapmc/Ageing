package org.sotap.Ageing;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class CommandHandler implements CommandExecutor {
    public Ageing plug;

    public CommandHandler(Ageing plug) {
        this.plug = plug;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("age")) {
            if (args.length > 0) {
                String arg = args[0];
                FileConfiguration config = this.plug.getConfig();
                FileConfiguration ageData = this.plug.ageData;
    
                switch (arg) {
                    case "set":
                        if (args.length != 3) {
                            sender.sendMessage(G.translateColor(G.failed + "Invalid argument list length."));
                            return true;
                        }
                        if (!G.isStringIntegerNatural(args[2])) {
                            sender.sendMessage(G.translateColor(G.failed + "The age must be &cpositive integer&r."));
                            return true;
                        }
                        Integer newAge = Integer.parseInt(args[2]);
                        if (newAge > config.getInt("max_age")) {
                            sender.sendMessage(G.translateColor(G.failed + "The age must be &csmaller&r than the maximum value (&e" + config.getInt("max_age") + "&r) in the configuration file."));
                            return true;
                        }
                        ageData.set(args[1], newAge);
                        this.plug.saveData();
                        sender.sendMessage(G.translateColor(G.success + "Successfully set &a" + args[1] + "&r's age to &a" + args[2] + "&r."));
                        break;
    
                    case "get":
                        if (args.length != 2) {
                            sender.sendMessage(G.translateColor(G.failed + "Invalid argument list length"));
                            return true;
                        }
                        Integer age = ageData.getInt(args[1]);
                        sender.sendMessage(G.translateColor(G.info + "The age of &a" + args[1] + "&r is &a" + Integer.toString(age) + "&r."));
                        break;
                    
                    case "reload":
                        this.plug.reloadConfig();
                        this.plug.reloadData();
                        sender.sendMessage(G.translateColor(G.success + "Successfully reload the plugin configuration"));
                        break;
                }
                return true;
            }
        }
        return false;
    }
}