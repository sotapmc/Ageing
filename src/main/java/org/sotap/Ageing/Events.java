package org.sotap.Ageing;

import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.sotap.Ageing.Utils.Functions;
import org.sotap.Ageing.Utils.LogUtil;
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
        FileConfiguration config = plug.getConfig();
        if (!p.hasPlayedBefore()) {
            LogUtil.info("检测到新玩家 &a" + playername + "&r，处理中...");
            List<String> firstJoinAward = config.getStringList("firstjoin_commands");
            if (firstJoinAward != null) {
                Functions.dispatchCommands(firstJoinAward, playername, uuid);
            }
        }
        if (!plug.ageData.contains(uuid)) {
            LogUtil.info("检测到玩家 &a" + playername + "&r 数据为空，正在初始化数据...");
            plug.ageData.set(uuid + ".playername", playername);
            plug.ageData.set(uuid + ".age", 0);
            plug.ageData.set(uuid + ".exp", 0);
            plug.saveData();
            LogUtil.success("初始化完成。");
            List<String> zeroAgeAward = plug.controller.getAgeAwardsAt(config, 0);
            if (zeroAgeAward != null) {
                Functions.dispatchCommands(zeroAgeAward, playername, uuid);
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
                LogUtil.warn("你的年龄太小，不能执行这个指令。", p);
                e.setCancelled(true);
            }
        }
    }
}
