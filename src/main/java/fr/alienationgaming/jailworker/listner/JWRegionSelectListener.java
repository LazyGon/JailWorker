package fr.alienationgaming.jailworker.listner;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import fr.alienationgaming.jailworker.JailWorker;
import fr.alienationgaming.jailworker.Utils;

public class JWRegionSelectListener implements Listener {

    JailWorker plugin;

    Player user;

    public JWRegionSelectListener(JailWorker jailworker, Player user) {
        this.plugin = jailworker;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.user = user;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.OFF_HAND) {
            return;
        }

        Player player = event.getPlayer();
        if (event.getClickedBlock() != null && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (!plugin.blockJail1.containsKey(player) || player != user)
                return;
            if (plugin.blockJail1.get(player) == null && plugin.blockJail2.get(player) == null) {
                plugin.blockJail1.put(player, event.getClickedBlock());
                event.setCancelled(true);
                player.sendMessage(ChatColor.BLUE + "Block 1:");
                Utils.printBlockPos(player, plugin.blockJail1.get(player));
                player.sendMessage(plugin.toLanguage("info-listener-selectblk2"));
            } else if (plugin.blockJail1.get(player) != null && plugin.blockJail2.get(player) == null) {
                plugin.blockJail2.put(player, event.getClickedBlock());
                event.setCancelled(true);
                player.sendMessage(ChatColor.BLUE + "Block 2:");
                Utils.printBlockPos(player, plugin.blockJail2.get(player));
                PlayerInteractEvent.getHandlerList().unregister(this);
                player.sendMessage(plugin.toLanguage("info-listener-prisonerspawntips"));
            }
        }
    }
}
