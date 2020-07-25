package org.sotap.Ageing;

import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

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
            this.plug.log(G.translateColor(G.success + "Initialization OK"));
        } else {
            if (this.plug.ageData.getString(uuid + ".username") != p.getName()) {
                this.plug.ageData.set(uuid + ".username", p.getName());
            }
        }
    }
}