package fr.alienationgaming.jailworker;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import fr.alienationgaming.jailworker.commands.JWCommand;
import fr.alienationgaming.jailworker.config.ConfigUpdater;
import fr.alienationgaming.jailworker.config.JailConfig;
import fr.alienationgaming.jailworker.config.WantedPlayers;

public class JailWorker extends JavaPlugin {

    private static JailWorker instance;

    @Override
    public void onEnable() {
        ConfigUpdater.update();
        this.getLogger().info(JailSystem.runAllJails() + " of " + JailConfig.getJails().size() + " jails started!");
        new JWCommand();
        AutoPointRemover.start();
        WantedPlayers.startListener();
    }
    
    @Override
    public void onDisable() {
        JailSystem.stopAllJails();
        AutoPointRemover.stop();
        WantedPlayers.stopListener();
    }

    public static JailWorker getInstance() {
        if (instance == null) {
            instance = (JailWorker) Bukkit.getPluginManager().getPlugin("JailWorker");
        }
        return instance;
    }
}