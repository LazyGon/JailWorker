package fr.alienationgaming.jailworker.listner;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import fr.alienationgaming.jailworker.Config;
import fr.alienationgaming.jailworker.Jail;
import fr.alienationgaming.jailworker.JailWorker;

public class JWChatPrisonerPrevent implements Listener {

    private JailWorker plugin;

    public JWChatPrisonerPrevent(JailWorker jailworker) {
        plugin = jailworker;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {
        if (Bukkit.getPluginManager().isPluginEnabled("LunaChat")) {
            return;
        }
        Player player = event.getPlayer();
        Config config = new Config(plugin);
        if (!config.prisonersSpeak()) {
            if (Jail.isJailed(player)) {
                event.setCancelled(true);
                player.sendMessage(plugin.toLanguage("info-listener-justwork"));
            }
        }

        if (!config.prisonersEar()) {
            plugin.getJailConfig().getConfigurationSection("Prisoners").getKeys(false).forEach(prisonerName -> {
                @SuppressWarnings("deprecation")
                Player prisoner = Bukkit.getPlayer(prisonerName);
                if (prisoner != null) {
                    event.getRecipients().remove(prisoner);
                }
            });
        }
    }
}