package fr.alienationgaming.jailworker.listner;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import fr.alienationgaming.jailworker.JailWorker;

public class JWSelectPrisonerSpawn implements Listener {

    JailWorker plugin;
    Player user;

    public JWSelectPrisonerSpawn(JailWorker jailworker, Player user) {
        plugin = jailworker;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.user = user;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getClickedBlock() != null && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (!plugin.JailPrisonerSpawn.containsKey(player) || player != user)
                return;
            Block block = event.getClickedBlock();
            event.setCancelled(true);
            plugin.JailPrisonerSpawn.put(player, new Location(block.getWorld(), block.getX(), block.getY() + 1, block.getZ()));
            player.sendMessage(plugin.toLanguage("info-listener-spawnset"));
            block.setType(Material.RED_WOOL);
            player.sendMessage(plugin.toLanguage("info-listener-allok"));
        }
        PlayerInteractEvent.getHandlerList().unregister(this);
    }
}
