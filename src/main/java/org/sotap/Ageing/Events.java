package org.sotap.Ageing;

import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.sotap.Ageing.Utils.Functions;
import org.sotap.Ageing.Utils.LogUtil;
import java.util.List;
import java.util.Objects;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

@SuppressWarnings("unused")
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
        plug.reloadData();
        plug.reloadConfig();
        if (!p.hasPlayedBefore()) {
            LogUtil.info("检测到新玩家 &a" + playername + "&r，处理中...");
            List<String> firstJoinAward = config.getStringList("firstjoin_commands");
            if (!firstJoinAward.isEmpty()) {
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
            if (!zeroAgeAward.isEmpty()) {
                Functions.dispatchCommands(zeroAgeAward, playername, uuid);
            }
        } else {
            if (!Functions.eq(plug.ageData.getString(uuid + ".playername"), playername)) {
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

        int currentAge = plug.ageData.getInt(uuid + ".age");
        int lowestLimit = config.getInt("command_lowest_limit");
        int finalAgeLimit = 0;

        ConfigurationSection limitedCommands = config.getConfigurationSection("limited_commands");
        if (limitedCommands == null) return;
        List<String> ignoredCommands = config.getStringList("ignored_commands");

        if (!ignoredCommands.contains(commandLabel)) {
            if (limitedCommands.contains(commandLabel)) {
                finalAgeLimit = Math.max(limitedCommands.getInt(commandLabel), lowestLimit);
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
