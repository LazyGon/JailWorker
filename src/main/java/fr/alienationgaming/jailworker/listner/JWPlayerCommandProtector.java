package fr.alienationgaming.jailworker.listner;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import fr.alienationgaming.jailworker.JailWorker;

public class JWPlayerCommandProtector implements Listener {

    JailWorker plugin;

    public JWPlayerCommandProtector(JailWorker jailworker) {
        plugin = jailworker;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void PlayerCommand(PlayerCommandPreprocessEvent event) {

        Player player = event.getPlayer();
        String cmd = event.getMessage();

        if (plugin.getJailConfig().contains("Prisoners." + player.getName())) {
            List<String> whitecmds = plugin.getConfig().getStringList("Plugin.Whitelisted-Commands");
            boolean allowed = false;
            for (int i = 0; i < whitecmds.size(); i++) {
                if (cmd.split(" ")[0].equals(whitecmds.get(i).split(" ")[0])) {
                    allowed = true;
                }
            }
            if (!allowed && !plugin.hasPerm(player, "jailworker.jw-cmdsonjail", true)) {
                event.setCancelled(true);
                player.sendMessage(plugin.toLanguage("error-listener-cantusecmd"));
            }
        }
        Set<String> s = plugin.getJailConfig().getConfigurationSection("Prisoners").getKeys(false);
        Iterator<String> it = s.iterator();
        List<String> list = Arrays.asList(new String[]{"/jw-give", "/jw-free", "/jw-increase"});
        while (it.hasNext()) {
            String elem = (String) it.next();
            if (cmd.contains(elem) && !list.contains(cmd.split(" ")[0])) {
                event.setCancelled(true);
                player.sendMessage(plugin.toLanguage("error-listener-cantinteract"));
            }
        }
    }
}