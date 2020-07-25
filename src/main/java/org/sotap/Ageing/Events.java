package org.sotap.Ageing;

import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class Events implements Listener {
    public Ageing plug;

    public Events(Ageing plug) {
        this.plug = plug;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        String uuid = p.getUniqueId().toString();
        if (!p.hasPlayedBefore() || !this.plug.ageData.contains(uuid)) {
            String name = p.getName();
            this.plug.log(G.translateColor(G.info + "Detected a new player &a" + name + "&r, initializing data..."));
            this.plug.ageData.set(uuid + ".username", name);
            this.plug.ageData.set(uuid + ".age", 0);
            this.plug.ageData.set(uuid + ".exp", 0);
            this.plug.saveData();
            this.plug.log(G.translateColor(G.success + "Initialization OK"));
        } else {
            if (this.plug.ageData.getString(uuid + ".username") != p.getName()) {
                this.plug.ageData.set(uuid + ".username", p.getName());
            }
        }
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
        FileConfiguration config = this.plug.getConfig();
        Player p = e.getPlayer();
        String uuid = p.getUniqueId().toString();
        String commandLabel = e.getMessage().substring(1);

        Integer currentAge = this.plug.ageData.getInt(uuid + ".age");
        Integer lowestLimit = config.getInt("command_lowest_limit");
        Integer finalAgeLimit = 0;

        ConfigurationSection limitedCommands = config.getConfigurationSection("limited_commands");
        List<String> ignoredCommands = config.getStringList("ignored_commands");

        if (!ignoredCommands.contains(commandLabel)) {
            if (limitedCommands.contains(commandLabel)) {
                finalAgeLimit = limitedCommands.getInt(commandLabel) < lowestLimit ? lowestLimit : limitedCommands.getInt(commandLabel);
            } else {
                finalAgeLimit = lowestLimit;
            }
    
            if (currentAge < finalAgeLimit) {
                p.sendMessage(G.translateColor(G.warn + "You are not old enough to execute the command."));
                e.setCancelled(true);
            }
        }
    }
}