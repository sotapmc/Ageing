package org.sotap.Ageing;

import javax.validation.constraints.NotNull;
import org.bukkit.OfflinePlayer;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class Placeholder extends PlaceholderExpansion {
    public Ageing plug;

    public Placeholder(Ageing plug) {
        this.plug = plug;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    @NotNull
    public String getAuthor() {
        return "sotapmc";
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return "ageing";
    }

    @Override
    @NotNull
    public String getVersion() {
        return "0.1.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String identifier) {
        if (player == null) {
            return "";
        }

        if (identifier.equals("age")) {
            return plug.ageData.getString(player.getUniqueId().toString() + ".age");
        }

        if (identifier.equals("experience")) {
            return plug.ageData.getString(player.getUniqueId().toString() + ".exp");
        }

        return null;
    }
}