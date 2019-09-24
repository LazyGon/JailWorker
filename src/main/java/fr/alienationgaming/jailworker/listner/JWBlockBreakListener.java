package fr.alienationgaming.jailworker.listner;

import java.util.Iterator;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.util.Vector;

import fr.alienationgaming.jailworker.Jail;
import fr.alienationgaming.jailworker.JailWorker;
import fr.alienationgaming.jailworker.Utils;

public class JWBlockBreakListener implements Listener {

    private JailWorker plugin;

    public JWBlockBreakListener(JailWorker jailworker) {
        plugin = jailworker;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        String jailName = null;

        Set<String> s = plugin.getJailConfig().getConfigurationSection("Jails").getKeys(false);
        Iterator<String> it = s.iterator();
        while (it.hasNext()) {
            String elem = (String) it.next();
            World world1 = Bukkit.getWorld(plugin.getJailConfig().getString("Jails." + elem + ".World"));
            Vector vec1 = plugin.getJailConfig().getVector("Jails." + elem + ".Location.Block1");
            Vector vec2 = plugin.getJailConfig().getVector("Jails." + elem + ".Location.Block2");
            if (Utils.isInRegion(event.getBlock().getLocation(), new Location(world1, vec1.getX(), vec1.getY(), vec1.getZ()), new Location(world1, vec2.getX(), vec2.getY(), vec2.getZ()))) {
                jailName = elem;
                break;
            }
        }
        /* If block isnt in a jail */
        if (jailName == null)
            return;
        /* else */
        if (Jail.isJailed(player) || plugin.getJailConfig().getStringList("Jails." + jailName + ".Owners").contains(player.getName())) {
            if (Jail.isJailed(player)) {
                Material type = Material.getMaterial(plugin.getJailConfig().getString("Jails." + jailName + ".Type"));
                if (event.getBlock().getType() == Material.GRASS_BLOCK)
                    event.getBlock().setType(Material.DIRT);
                if (event.getBlock().getType() == type) {
                    event.getBlock().setType(Material.AIR);
                    int remain = plugin.getJailConfig().getInt("Prisoners." + player.getName() + ".RemainingBlocks") - 1;
                    if (remain > 0) {
                        plugin.getJailConfig().set("Prisoners." + player.getName() + ".RemainingBlocks", remain);
                        plugin.saveJailConfig();
                        plugin.reloadJailConfig();
                        player.sendMessage(plugin.toLanguage("info-listener-blockremaning", remain));
                    } else {
                        plugin.interactWithPlayer.freePlayer(player);
                    }
                } else if (event.getBlock().getType() != type) {
                    //player.sendMessage(plugin.toLanguage("info-listener-donttryescape"));
                    event.setCancelled(true);
                }
                return;
            }
        } else {
            player.sendMessage(plugin.toLanguage("info-listener-notowner"));
            event.setCancelled(true);
        }
    }
}
