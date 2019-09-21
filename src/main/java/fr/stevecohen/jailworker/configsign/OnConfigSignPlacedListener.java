package fr.stevecohen.jailworker.configsign;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import fr.alienationgaming.jailworker.JailWorker;

public class OnConfigSignPlacedListener implements Listener {
    private SignConfigurator configSigns;

    JailWorker plugin;

    public OnConfigSignPlacedListener(JailWorker jailworker) {
        plugin = jailworker;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSignChange(SignChangeEvent event) {

        Player player = event.getPlayer();

        if ("[JW-CONFIG]".equals(event.getLine(0)) && !event.getLine(1).isEmpty()) {
            String jailName = event.getLine(1);
            if (!plugin.getJailConfig().contains("Jails." + jailName)) {
                player.sendMessage(plugin.toLanguage("error-command-jailnotexist", jailName));
                event.getBlock().breakNaturally();
                return;
            }
            if (plugin.hasPerm(player, "jailworker.jw-admin") || (plugin.hasPerm(player, "jailworker.jw-config") && plugin.getJailConfig().getStringList("Jails." + jailName + ".Owners").contains(player.getName()))) {
                event.setCancelled(true);
                configSigns = new SignConfigurator(player, jailName);
                if (initAllSigns(event.getBlock())) {
                    configSigns.initConfigProcess();
                    configSigns.startProcess();
                } else {
                    player.sendMessage(plugin.toLanguage("error-listener-configsign-missingsign"));
                }
            } else if (!plugin.getJailConfig().getStringList("Jails." + jailName + ".Owners").contains(player.getName())) {
                player.sendMessage(plugin.toLanguage("error-command-notowner"));
            }
        }
    }

    private boolean initAllSigns(Block sign) {
        return configSigns.getRelativesSignsByCenterSign(sign);
    }
}
