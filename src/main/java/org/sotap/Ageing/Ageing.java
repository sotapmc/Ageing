package org.sotap.Ageing;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class Ageing extends JavaPlugin {
    public FileConfiguration ageData;
    public DataController controller;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        ageData = load("age.yml");
        controller = new DataController(this);
        getLogger().info(G.translateColor(G.SUCCESS + "The plugin has been &aenabled&r."));
        Bukkit.getPluginCommand("age").setExecutor(new CommandHandler(this));
        getServer().getPluginManager().registerEvents(new Events(this), this);
    }

    @Override
    public void onDisable() {
        getLogger().info(G.translateColor(G.SUCCESS + "The plugin has been &cdisabled&r."));
    }

    public FileConfiguration load(String filename) {
        File folder = getDataFolder();
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
        ageData = load("age.yml");
    }

    public void saveData() {
        try {
            ageData.save(new File(getDataFolder(), "age.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void log(String message) {
        getLogger().info(message);
    }
}