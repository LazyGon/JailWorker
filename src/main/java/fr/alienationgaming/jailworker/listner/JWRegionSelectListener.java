package fr.alienationgaming.jailworker.listner;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import fr.alienationgaming.jailworker.JailWorker;
import fr.alienationgaming.jailworker.Utils;

public class JWRegionSelectListener implements Listener {

    JailWorker plugin;
    Utils utils = new Utils(plugin);
    Player user;

    public JWRegionSelectListener(JailWorker jailworker, Player _user) {
        plugin = jailworker;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        user = _user;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        if (event.getClickedBlock() != null && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (!plugin.blockJail1.containsKey(player) || player != user)
                return;
            if (plugin.blockJail1.get(player) == null && plugin.blockJail2.get(player) == null) {
                plugin.blockJail1.put(player, event.getClickedBlock());
                event.setCancelled(true);
                player.sendMessage(ChatColor.BLUE + "Block 1:");
                utils.printBlockPos(player, plugin.blockJail1.get(player));
                player.sendMessage(plugin.toLanguage("info-listener-selectblk2"));
            } else if (plugin.blockJail1.get(player) != null && plugin.blockJail2.get(player) == null) {
                plugin.blockJail2.put(player, event.getClickedBlock());
                event.setCancelled(true);
                player.sendMessage(ChatColor.BLUE + "Block 2:");
                utils.printBlockPos(player, plugin.blockJail2.get(player));
                PlayerInteractEvent.getHandlerList().unregister(this);
                player.sendMessage(plugin.toLanguage("info-listener-prisonerspawntips"));
            }
        }
    }
}
