package org.sotap.Ageing;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

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
        LogUtil.origin = this.getLogger();
        controller = new DataController(this);
        try {
            ageData = load("age.yml");
        } catch (IOException e) {
            LogUtil.failed("无法创建数据文件，插件将无法正常使用。请修复该问题后尝试重启解决。");
        }
        LogUtil.success("插件已&a启用&r。");
        Objects.requireNonNull(Bukkit.getPluginCommand("ageing")).setExecutor(new CommandHandler(this));
        Objects.requireNonNull(Bukkit.getPluginCommand("ageing")).setTabCompleter(new Tab());
        getServer().getPluginManager().registerEvents(new Events(this), this);
        api = new API(this);
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            @SuppressWarnings({"unused", "deprecation"})
            boolean papiStatus = new Placeholder(this).register();
        } else {
            LogUtil.info("Ageing 支持 &bPlaceholderAPI&r，但并未找到。");
        }
    }

    @Override
    public void onDisable() {
        LogUtil.success("插件已&c禁用&r。");
    }

    public FileConfiguration load(String filename) throws IOException {
        File folder = getDataFolder();
        File file = new File(folder, filename);
        if (!folder.exists()) {
            boolean state = folder.mkdir();
            if (!state) {
                throw new IOException("cannot create folder automatically");
            }
        }
        if (!file.exists()) {
            try {
                boolean state = file.createNewFile();
                if (!state) {
                    throw new IOException("cannot create file automatically");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    public boolean reloadData() {
        try {
            ageData = load("age.yml");
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void saveData() {
        try {
            ageData.save(new File(getDataFolder(), "age.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
