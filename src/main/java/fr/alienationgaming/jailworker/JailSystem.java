package fr.alienationgaming.jailworker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.github.ucchyocean.lc.channel.ChannelPlayer;
import com.github.ucchyocean.lc.event.LunaChatChannelChatEvent;
import com.github.ucchyocean.lc.event.LunaChatChannelMessageEvent;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import fr.alienationgaming.jailworker.config.Config;
import fr.alienationgaming.jailworker.config.JailConfig;
import fr.alienationgaming.jailworker.config.Messages;
import fr.alienationgaming.jailworker.config.Prisoners;

public class JailSystem extends BukkitRunnable implements Listener {

    private static JailWorker plugin = JailWorker.getInstance();
    private static final Map<String, JailSystem> tasks = new HashMap<>();

    private final String jailName;
    private final Set<Block> punishmentBlocks;

    private class LunaChatListener implements Listener {

        LunaChatListener() {
            Bukkit.getPluginManager().registerEvents(this, plugin);
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onChat(LunaChatChannelChatEvent event) {
            ChannelPlayer player = event.getPlayer();
            if (Config.canPrisonerSpeak() || !Prisoners.isJailed(player.getPlayer())) {
                return;
            }
            event.setCancelled(true);
            Messages.sendMessage(player, "in-jail.cannot-speak");
        }

        @EventHandler
        public void onReceiveMessage(LunaChatChannelMessageEvent event) {
            if (Config.canPrisonersHear()) {
                return;
            }
            Prisoners.getPrisoners().forEach(prisoner -> {
                event.getRecipients().removeIf(
                        channelPlayer -> channelPlayer.equals(ChannelPlayer.getChannelPlayer(prisoner.getName())));
            });
        }
    }

    private LunaChatListener lcListener = null;

    public JailSystem(String jailName) throws IllegalArgumentException {
        this.jailName = jailName;
        this.punishmentBlocks = new HashSet<>();

        if (!JailConfig.exist(jailName, true)) {
            throw new IllegalArgumentException("The jail " + jailName + " is not defined.");
        }

        if (tasks.containsKey(jailName)) {
            throw new IllegalArgumentException("Task for jail " + jailName + " is already running.");
        }
    }

    public void start() {
        tasks.put(jailName, this);
        Bukkit.getPluginManager().registerEvents(this, plugin);
        if (Bukkit.getPluginManager().isPluginEnabled("LunaChat")) {
            lcListener = new LunaChatListener();
        }
        if (!isRunning()) {
            runTaskTimer(plugin, 30L, (JailConfig.getBlockSpeed(jailName) * 30));
        }
    }

    public void stop() {
        tasks.remove(jailName);
        clearPunishmentBlocks();
        HandlerList.unregisterAll(this);
        if (Bukkit.getPluginManager().isPluginEnabled("LunaChat")) {
            HandlerList.unregisterAll(lcListener);
            lcListener = null;
        }
        if (isRunning()) {
            cancel();
        }
    }

    public int clearPunishmentBlocks() {
        int count = punishmentBlocks.size();
        punishmentBlocks.forEach(block -> block.setType(Material.AIR));
        punishmentBlocks.clear();
        return count;
    }

    public boolean isRunning() {
        try {
            return !isCancelled();
        } catch (IllegalStateException e) {
            return false;
        }
    }

    /**
     * Check if jail system is running.
     * 
     * @param jailName
     * @return True if the jail system is running, otherwise false.
     */
    public static boolean isRunning(String jailName) {
        JailSystem task = tasks.get(jailName);
        return task != null && task.isRunning();
    }

    public static JailSystem getTask(String jailName) {
        try {
            return tasks.getOrDefault(jailName, new JailSystem(jailName));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static void removeTask(String jailName) {
        JailSystem task = tasks.get(jailName);
        if (task != null && task.isRunning()) {
            task.stop();
        }
    }

    /**
     * Run all available jails.
     * 
     * @return Number of jails initialized.
     */
    public static int runAllJails() {
        int initJails = 0;
        List<String> jails = JailConfig.getJails();
        jails.removeAll(tasks.keySet());
        for (String jail : jails) {
            try {
                new JailSystem(jail).start();
                initJails++;
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning(e.getMessage());
            }
        }
        return initJails;
    }

    public static void stopAllJails() {
        JailConfig.getJails().forEach(jail -> {
            removeTask(jail);
        });
        ;
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onBlockBreak(BlockBreakEvent event) {
        // Bukkit.getPlayer("lazy_gon").sendMessage("test: 1");
        Player player = event.getPlayer();
        Block broken = event.getBlock();

        if (!isInRegion(broken.getLocation(), JailConfig.getPosition1(jailName), JailConfig.getPosition2(jailName))) {
            return;
        }

        event.setDropItems(false);

        if (canBypass(player)) {
            punishmentBlocks.remove(broken);
            return;
        }

        if (Prisoners.isJailed(player) && Prisoners.getJailPlayerIsIn(player).equals(jailName)
                && punishmentBlocks.contains(broken)) {

            Set<Material> jailPunishmentBlocks = JailConfig.getPunishmentBlocks(jailName);
            if (jailPunishmentBlocks.contains(broken.getType())) {
                int point = Prisoners.getPunishmentPoint(player) - 1;
                Prisoners.setPunishmentPoint(player, point);
                if (point != 0 && point % 20 == 0) {
                    Messages.sendMessage(player, "in-jail.punishment-point-notice",
                            Map.of("%point%", point));
                }
                if (point <= 0) {
                    Bukkit.getOnlinePlayers().forEach(onlinePlayer -> Messages.sendMessage(onlinePlayer,
                            "in-jail.broadcast-finish-work", Map.of("%player%", player.getName())));
                    Prisoners.freePlayer(player);
                }
            }

            punishmentBlocks.remove(broken);
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    private void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block broken = event.getBlock();

        if (!isInRegion(broken.getLocation(), JailConfig.getPosition1(jailName), JailConfig.getPosition2(jailName))) {
            return;
        }

        if (canBypass(player)) {
            return;
        }

        if (Prisoners.isJailed(player) && Prisoners.getJailPlayerIsIn(player).equals(jailName)
                && JailConfig.getPunishmentBlocks(jailName).contains(broken.getType())) {
            punishmentBlocks.add(broken);
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onChat(AsyncPlayerChatEvent event) {
        if (Bukkit.getPluginManager().isPluginEnabled("LunaChat")) {
            return;
        }
        Player player = event.getPlayer();
        if (!Config.canPrisonerSpeak() && Prisoners.isJailed(player)) {
            event.setCancelled(true);
            Messages.sendMessage(player, "in-jail.cannot-speak");
        }

        if (!Config.canPrisonersHear()) {
            Prisoners.getPrisoners().forEach(prisoner -> {
                if (prisoner.isOnline()) {
                    event.getRecipients().remove(prisoner.getPlayer());
                }
            });
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (Prisoners.isJailed(player) && Prisoners.getJailPlayerIsIn(player).equals(jailName)) {
            event.setRespawnLocation(JailConfig.getSpawnLocation(jailName));
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onCommand(PlayerCommandPreprocessEvent event) {

        Player player = event.getPlayer();
        if (!Prisoners.isJailed(player)) {
            return;
        }

        String command = event.getMessage();
        if (command.startsWith("/jw give") || command.startsWith("/jw free") || command.startsWith("/jw changepoint")) {
            event.setCancelled(true);
            Messages.sendMessage(player, "in-jail.forbidden-command", Map.of("%command%", command));
            return;
        }

        if (player.hasPermission("jailworker.bypass.allowedcommand")) {
            return;
        }

        if (Config.getGlobalAllowedCommands().stream().anyMatch(command::startsWith)) {
            return;
        }

        if (JailConfig.getAllowedCommands(jailName).stream().anyMatch(command::startsWith)) {
            return;
        }

        event.setCancelled(true);
        Messages.sendMessage(player, "in-jail.command-not-allowed",
                Map.of("%command%", command, "%jail-name%", jailName));
    }

    @Override
    public void run() {
        // Bukkit.getPlayer("lazy_gon").sendMessage("test: 1");
        List<OfflinePlayer> prisoners = Prisoners.getPrisoners();
        prisoners.removeIf(prisoner -> !Prisoners.getJailPlayerIsIn(prisoner).equals(jailName));

        if (prisoners.size() == 0) {
            if (punishmentBlocks.size() != 0) {
                clearPunishmentBlocks();
            }
            return;
        }

        if (punishmentBlocks.size() >= JailConfig.getMaxPunishmentBlocks(jailName)) {
            return;
        }

        Location loc1 = JailConfig.getPosition1(jailName);
        Location loc2 = JailConfig.getPosition2(jailName);
        Location spawn = JailConfig.getSpawnLocation(jailName);
        int minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
        int maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
        int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        int maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());

        for (int i = 0; i < prisoners.size(); i++) {
            Random random = new Random();
            Block punishmentBlock = null;
            Map<Integer, Integer> checked = new HashMap<>();
            while (punishmentBlock == null) {
                int x = random.nextInt(maxX + 1 - minX) + minX;
                int y = maxY;
                int z = random.nextInt(maxZ + 1 - minZ) + minZ;

                if (x == spawn.getBlockX() && z == spawn.getBlockZ()) {
                    continue;
                }

                Block check = new Location(loc1.getWorld(), x, y, z).getBlock();
                while (true) {
                    Block down = check.getRelative(BlockFace.DOWN);
                    if (check.getType() == Material.AIR && down.getType().isSolid()) {
                        punishmentBlock = check;
                        break;
                    }

                    if (check.getLocation().getBlockY() == minY) {
                        break;
                    }

                    check = down;
                }
                checked.put(x, z);
                if (checked.size() > 30) {
                    return;
                }
            }

            punishmentBlocks.add(punishmentBlock);
            List<Material> blocks = new ArrayList<>(JailConfig.getPunishmentBlocks(jailName));
            if (blocks.size() > 0) {
                Material chosen = blocks.get(random.nextInt(blocks.size()));
                punishmentBlock.setType(chosen);
            }
        }
    }

    private static boolean isInRegion(Location loc, Location loc1, Location loc2) {
        return loc1.getWorld() != null && loc2.getWorld() != null && loc1.getWorld().equals(loc2.getWorld())
                && loc.getWorld().equals(loc1.getWorld())
                && Math.min(loc1.getBlockX(), loc2.getBlockX()) <= loc.getBlockX()
                && Math.max(loc1.getBlockX(), loc2.getBlockX()) >= loc.getBlockX()
                && Math.min(loc1.getBlockY(), loc2.getBlockY()) <= loc.getBlockY()
                && Math.max(loc1.getBlockY(), loc2.getBlockY()) >= loc.getBlockY()
                && Math.min(loc1.getBlockZ(), loc2.getBlockZ()) <= loc.getBlockZ()
                && Math.max(loc1.getBlockZ(), loc2.getBlockZ()) >= loc.getBlockZ();
    }

    private boolean canBypass(Player player) {
        if (player.hasPermission("jailworker.bypass.blockedit.*")) {
            return true;
        }
        if (player.hasPermission("jailworker.bypass.blockedit." + jailName)) {
            return true;
        }

        return false;
    }
}