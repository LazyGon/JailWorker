package fr.alienationgaming.jailworker;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import fr.alienationgaming.jailworker.commands.JWCommand;
import fr.alienationgaming.jailworker.config.JailConfig;

public class JailWorker extends JavaPlugin {

    private static JailWorker instance;

    @Override
    public void onEnable() {
        this.getLogger().info(JailSystem.runAllJails() + " of " + JailConfig.getJails().size() + " jails started!");
        new JWCommand();
    }
    
    @Override
    public void onDisable() {
        JailSystem.stopAllJails();
    }

    public static JailWorker getInstance() {
        if (instance == null) {
            instance = (JailWorker) Bukkit.getPluginManager().getPlugin("JailWorker");
        }
        return instance;
    }
}