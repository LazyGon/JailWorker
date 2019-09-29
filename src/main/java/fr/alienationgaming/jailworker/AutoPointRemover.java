package fr.alienationgaming.jailworker;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import fr.alienationgaming.jailworker.config.Config;
import fr.alienationgaming.jailworker.config.Messages;
import fr.alienationgaming.jailworker.config.Prisoners;

public class AutoPointRemover implements Listener {

    private static JailWorker plugin = JailWorker.getInstance();
    private static Map<Player, Long> lastMovingTimes = new HashMap<>();
    private static Map<Player, Boolean> isAfkWarnFinished = new HashMap<>();

    private static BukkitRunnable autoPointRemoveTask = new BukkitRunnable() {

        @Override
        public void run() {
            Prisoners.getPrisoners().forEach(prisoner -> {
                if (prisoner.isOnline()) {
                    Player player = prisoner.getPlayer();
                    if (!isAfk(player)) {
                        Prisoners.addPunishmentPoint(prisoner, -1);
                        if (Prisoners.getPunishmentPoint(player) == 0) {
                            Bukkit.getOnlinePlayers().forEach(onlinePlayer -> Messages.sendMessage(onlinePlayer,
                                    "in-jail.broadcast-finish-work", Map.of("%player%", player.getName())));
                            Prisoners.freePlayer(player);
                        }
                    } else if (!isAfkWarnFinished.getOrDefault(player, false)) {
                        isAfkWarnFinished.put(player, true);
                        Messages.sendMessage(player, "in-jail.you-are-now-afk");
                    }
                }
            });
        }
    };

    private static final AutoPointRemover instance = new AutoPointRemover();

    private AutoPointRemover() {
    }

    static void start() {
        Bukkit.getPluginManager().registerEvents(instance, plugin);
        long interval = Config.getAutoPointDecreaseInterval() * 20;
        autoPointRemoveTask.runTaskTimer(plugin, interval, interval);
    }

    static void stop() {
        HandlerList.unregisterAll(instance);
        try {
            autoPointRemoveTask.cancel();
        } catch (IllegalStateException ignored) {
        }
        lastMovingTimes.clear();
        isAfkWarnFinished.clear();
    }

    @EventHandler
    private void onMove(PlayerMoveEvent event) {
        if (Prisoners.isJailed(event.getPlayer())) {
            lastMovingTimes.put(event.getPlayer(), System.currentTimeMillis());
            isAfkWarnFinished.remove(event.getPlayer());
        }
    }

    @EventHandler
    private void onLogout(PlayerQuitEvent event) {
        lastMovingTimes.remove(event.getPlayer());
        isAfkWarnFinished.remove(event.getPlayer());
    }

    private static boolean isAfk(Player player) {
        if (!lastMovingTimes.containsKey(player)) {
            return false;
        }
        return lastMovingTimes.get(player) + (Config.getAfkTime() * 60 * 1000) < System.currentTimeMillis();
    }
}