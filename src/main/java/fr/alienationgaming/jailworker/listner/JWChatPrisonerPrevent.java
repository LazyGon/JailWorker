package fr.alienationgaming.jailworker.listner;

import java.util.Iterator;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import fr.alienationgaming.jailworker.Config;
import fr.alienationgaming.jailworker.JailWorker;

public class JWChatPrisonerPrevent implements Listener {

    private JailWorker plugin;

    public JWChatPrisonerPrevent(JailWorker jailworker) {
        plugin = jailworker;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerCommand(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Config config = new Config(plugin);
        if (!config.prisonersSpeak()) {
            if (plugin.getJailConfig().contains("Prisoners." + player.getName())) {
                event.setCancelled(true);
                player.sendMessage(plugin.toLanguage("info-listener-justwork"));
            }
        }

        if (!config.prisonersEar()) {
            Set<String> s = plugin.getJailConfig().getConfigurationSection("Prisoners").getKeys(false);
            Iterator<String> it = s.iterator();
            while (it.hasNext()) {
                String elem = (String) it.next();
                event.getRecipients().remove(plugin.getServer().getPlayer(elem));
            }
        }
    }
}