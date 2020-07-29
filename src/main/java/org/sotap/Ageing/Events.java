package org.sotap.Ageing;

import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public final class Events implements Listener {
    public Ageing plug;

    public Events(Ageing plug) {
        this.plug = plug;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        String uuid = p.getUniqueId().toString();
        String playername = p.getName();
        if (!p.hasPlayedBefore() || !plug.ageData.contains(uuid)) {
            plug.log(G.translateColor(
                    G.INFO + "Detected a new player &a" + playername + "&r, initializing data..."));
            plug.ageData.set(uuid + ".playername", playername);
            plug.ageData.set(uuid + ".age", 0);
            plug.ageData.set(uuid + ".exp", 0);
            plug.saveData();
            plug.log(G.translateColor(G.SUCCESS + "Initialization OK"));
            FileConfiguration config = plug.getConfig();
            List<String> firstJoinAward = plug.controller.getAgeAwardsAt(config, 0);
            if (firstJoinAward != null) {
                for (String award : firstJoinAward) {
                    G.dispatchCommand(award, playername, uuid);
                }
            }
        } else {
            if (plug.ageData.getString(uuid + ".playername") != playername) {
                plug.ageData.set(uuid + ".playername", playername);
            }
        }
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
        FileConfiguration config = plug.getConfig();
        Player p = e.getPlayer();
        String uuid = p.getUniqueId().toString();
        String commandLabel = e.getMessage().substring(1);

        Integer currentAge = plug.ageData.getInt(uuid + ".age");
        Integer lowestLimit = config.getInt("command_lowest_limit");
        Integer finalAgeLimit = 0;

        ConfigurationSection limitedCommands = config.getConfigurationSection("limited_commands");
        List<String> ignoredCommands = config.getStringList("ignored_commands");

        if (!ignoredCommands.contains(commandLabel)) {
            if (limitedCommands.contains(commandLabel)) {
                finalAgeLimit = limitedCommands.getInt(commandLabel) < lowestLimit ? lowestLimit
                        : limitedCommands.getInt(commandLabel);
            } else {
                finalAgeLimit = lowestLimit;
            }

            if (currentAge < finalAgeLimit) {
                p.sendMessage(G
                        .translateColor(G.WARN + "You are not old enough to execute the command."));
                e.setCancelled(true);
            }
        }
    }
}
