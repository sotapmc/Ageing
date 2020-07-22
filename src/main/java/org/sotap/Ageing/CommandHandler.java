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
            String arg = args[0];
            FileConfiguration config = this.plug.getConfig();
            FileConfiguration ageData = this.plug.ageData;

            switch (arg) {
                case "set":
                    ageData.set(args[1], args[2]);
                    sender.sendMessage(G.translateColor(G.success + "Successfully set &a" + args[1] + "&r's age to &a" + args[2] + "&r."));
                    break;

                case "get":
                    Integer age = ageData.getInt(args[1]);
                    sender.sendMessage(G.translateColor(G.info + "The age of &a" + args[1] + "&r is &a" + Integer.toString(age) + "&r."));
                    break;
                
                case "reload":
                    this.plug.reloadConfig();
                    this.plug.reloadData();
                    sender.sendMessage(G.translateColor(G.success + "Successfully reload the plugin configuration"));
                    break;
            }

            this.plug.saveConfig();
            this.plug.saveData();
        }
        return false;
    }
}