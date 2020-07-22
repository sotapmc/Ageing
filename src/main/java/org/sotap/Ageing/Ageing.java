package org.sotap.Ageing;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Ageing extends JavaPlugin {
    FileConfiguration ageData;
    FileConfiguration timerData;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.ageData = this.load("age.yml");
        this.timerData = this.load("timer.yml");
        this.getLogger().info(G.translateColor(G.success + "The plugin has been &aenabled&r."));
        Bukkit.getPluginCommand("age").setExecutor(new CommandHandler(this));
    }

    @Override
    public void onDisable() {
        this.getLogger().info(G.translateColor(G.success + "The plugin has been &cdisabled&r."));
    }

    public FileConfiguration load(String filename) {
        File folder = this.getDataFolder();
        File file = new File(folder, filename);
        if (!folder.exists()) {
            folder.mkdir();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    public void reloadData() {
        this.ageData = this.load("age.yml");
    }

    public void saveData() {
        try {
            this.ageData.save(new File(this.getDataFolder(), "age.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}