package fr.alienationgaming.jailworker.listner;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import fr.alienationgaming.jailworker.JailWorker;

public class JWConfigJailListener implements Listener {

    private JailWorker plugin;
    private String jailName;

    private CommandSender user;

    public JWConfigJailListener(JailWorker jailworker, String jailName, CommandSender user) {
        this.plugin = jailworker;
        this.jailName = jailName;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.user = user;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (player != user) {
            return;
        }

        if (event.getMessage().startsWith("M:")) {
            if (plugin.getAllowBlocks().contains(event.getMessage().substring(2).toUpperCase())) {
                plugin.getJailConfig().set("Jails." + jailName + ".Type", event.getMessage().substring(2).toUpperCase());
                player.sendMessage(plugin.toLanguage("info-listener-configtips2"));
                player.sendMessage(plugin.toLanguage("help-listener-config-example2"));
                event.setCancelled(true);
            } else {
                player.sendMessage(plugin.toLanguage("info-listener-configtips2"));
                player.sendMessage(plugin.toLanguage("info-listener-allowedblock"));
            }
        }
        if (event.getMessage().startsWith("P:")) {
            try {
                plugin.getJailConfig().set("Jails." + jailName + ".Blocks", Integer.parseInt(event.getMessage().substring(2)));
                player.sendMessage(plugin.toLanguage("info-listener-configtips3"));
                player.sendMessage(plugin.toLanguage("help-listener-config-example3"));
                event.setCancelled(true);
            } catch (NumberFormatException e) {
                player.sendMessage(plugin.toLanguage("error-command-invalidumber"));
            }
        }
        if (event.getMessage().startsWith("C:")) {
            try {
                plugin.getJailConfig().set("Jails." + jailName + ".MaxSand", Integer.parseInt(event.getMessage().substring(2)));
                player.sendMessage(plugin.toLanguage("info-listener-configtips4"));
                player.sendMessage(plugin.toLanguage("help-listener-config-example4"));
                event.setCancelled(true);
            } catch (NumberFormatException e) {
                player.sendMessage(plugin.toLanguage("error-command-invalidumber"));
            }
        }
        if (event.getMessage().startsWith("F:")) {
            try {
                plugin.getJailConfig().set("Jails." + jailName + ".Speed", Integer.parseInt(event.getMessage().substring(2)));
                event.setCancelled(true);
                plugin.saveJailConfig();
                plugin.reloadJailConfig();
                AsyncPlayerChatEvent.getHandlerList().unregister(this);
                player.sendMessage(plugin.toLanguage("info-listener-configsaved"));
            } catch (NumberFormatException e) {
                player.sendMessage(plugin.toLanguage("error-command-invalidumber"));
            }
        }
    }
}
