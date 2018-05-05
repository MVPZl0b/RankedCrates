package main;

import Crates.CrateManager;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import commands.CrateCommand;
import events.BeaconEvent;
import events.RewardEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class main extends JavaPlugin {

    private static main instance;


    public void onEnable() {
        FileConfiguration config = getConfig();
        config.options().copyDefaults(true);
        createConfig();
        saveDefaultConfig();
        main.instance = this;
        register();
        CrateManager.loadCrates();

    }

    public void onDisable() {

    }

    public void register() {
        PluginManager pm = Bukkit.getServer().getPluginManager();
        getCommand("crate").setExecutor(new CrateCommand());
        pm.registerEvents(new BeaconEvent(), this);
        pm.registerEvents(new RewardEvent(), this);
    }

    public static main getInstance() {
        return instance;
    }

    private void createConfig() {
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }
            File file = new File(getDataFolder(), "config.yml");
            if (!file.exists()) {
                getLogger().info("Config.yml not found, creating!");
                saveDefaultConfig();
            } else {
                getLogger().info("Config.yml found, loading!");
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    public static WorldGuardPlugin getWorldGuard() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");

        // WorldGuard may not be loaded
        if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
            return null; // Maybe you want throw an exception instead
        }

        return (WorldGuardPlugin) plugin;
    }

}
