package org.sotap.Ageing;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.sotap.Ageing.Commands.CommandHandler;
import org.sotap.Ageing.Commands.Tab;
import org.sotap.Ageing.Utils.DataController;
import org.sotap.Ageing.Utils.LogUtil;

public final class Ageing extends JavaPlugin {
    public FileConfiguration ageData;
    public DataController controller;
    public API api;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        ageData = load("age.yml");
        LogUtil.origin = this.getLogger();
        controller = new DataController(this);
        LogUtil.success("The plugin has been &aenabled&r.");
        Bukkit.getPluginCommand("ageing").setExecutor(new CommandHandler(this));
        Bukkit.getPluginCommand("ageing").setTabCompleter(new Tab());
        getServer().getPluginManager().registerEvents(new Events(this), this);
        api = new API(this);
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            @SuppressWarnings({"unused", "deprecation"})
            boolean papiStatus = new Placeholder(this).register();
        } else {
            LogUtil.info("Ageing now supports &ePlaceholderAPI &rbut it seems not installed on your server!");
        }
    }

    @Override
    public void onDisable() {
        LogUtil.success("The plugin has been &cdisabled&r.");
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
