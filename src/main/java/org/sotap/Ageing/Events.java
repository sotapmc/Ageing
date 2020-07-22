package org.sotap.Ageing;

import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class Events implements Listener {
    public Ageing plug;
    public Timer timer;

    public Events(Ageing plug) {
        this.plug = plug;
        this.timer = new Timer(plug);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (!p.hasPlayedBefore()) {
            this.plug.ageData.set(p.getName(), 0);
            this.plug.saveData();
        }
        UUID uuid = p.getUniqueId();
        this.timer.startTimerFor(uuid);
    }

    @EventHandler
    public void onPlayerExit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        this.timer.stopTimerFor(uuid);
    }
}