package fr.alienationgaming.jailworker.listner;

import com.github.ucchyocean.lc.channel.ChannelPlayer;
import com.github.ucchyocean.lc.event.LunaChatChannelChatEvent;
import com.github.ucchyocean.lc.event.LunaChatChannelMessageEvent;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import fr.alienationgaming.jailworker.Config;
import fr.alienationgaming.jailworker.Jail;
import fr.alienationgaming.jailworker.JailWorker;

public class JWLunaChatPrisonerPrevent implements Listener {

    private JailWorker plugin;

    public JWLunaChatPrisonerPrevent(JailWorker jailworker) {
        plugin = jailworker;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(LunaChatChannelChatEvent event) {
        ChannelPlayer player = event.getPlayer();
        Config config = new Config(plugin);
        if (!config.prisonersSpeak()) {
            if (Jail.isJailed(player.getPlayer())) {
                event.setCancelled(true);
                player.sendMessage(plugin.toLanguage("info-listener-justwork"));
            }
        }
    }

    @EventHandler
    public void onReceiveMessage(LunaChatChannelMessageEvent event) {
        Config config = new Config(plugin);
        if (!config.prisonersEar()) {
            plugin.getJailConfig().getConfigurationSection("Prisoners").getKeys(false).forEach(prisoner -> {
                event.getRecipients().removeIf(channelPlayer -> channelPlayer.equals(ChannelPlayer.getChannelPlayer(prisoner)));
            });
        }
    }
}