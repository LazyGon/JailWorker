package fr.alienationgaming.jailworker.config;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import fr.alienationgaming.jailworker.JailSystem;
import fr.alienationgaming.jailworker.JailWorker;
import fr.alienationgaming.jailworker.commands.Put;

public class WantedPlayers implements Listener {

    private static JailWorker plugin = JailWorker.getInstance();
    private static CustomConfig wantedListConfig = new CustomConfig("wanted-list.yml");

    private static WantedPlayers instance = new WantedPlayers();

    private WantedPlayers() {
    }

    public static void startListener() {
        Bukkit.getPluginManager().registerEvents(instance, plugin);
    }

    public static void stopListener() {
        HandlerList.unregisterAll(instance);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!isWanted(player)) {
            return;
        }
        if (Prisoners.isJailed(player)) {
            removeWantedPlayer(player);
            return;
        }

        String jailName = getJail(player);
        if (jailName.isEmpty()) {
            List<String> jails = JailConfig.getJails();
            jails.removeIf(jail -> !JailSystem.isRunning(jail));
            jailName = jails.get(new Random().nextInt(jails.size()));
        }

        int punishmentPoint = getPunishmentPoint(player);
        String reason = getReason(player);
        Prisoners.punishPlayer(player, jailName, null, punishmentPoint, reason);
        Put.sendJailedMessage(player, jailName, Bukkit.getConsoleSender(), punishmentPoint, reason);
        removeWantedPlayer(player);
    }

    public static int getPunishmentPoint(OfflinePlayer player) throws IllegalArgumentException {
        if (!isWanted(player)) {
            throw new IllegalArgumentException("The player is not wanted.");
        }

        int punishmentPoint = get().getInt(player.getUniqueId().toString() + ".punishment-point");
        if (punishmentPoint <= 0) {
            return 1;
        } else {
            return punishmentPoint;
        }
    }

    public static void setPunishmentPoint(OfflinePlayer player, int punishmentPoint) throws IllegalArgumentException {
        if (!isWanted(player)) {
            throw new IllegalArgumentException("The player is not wanted.");
        }

        if (punishmentPoint <= 0) {
            punishmentPoint = 1;
        }

        get().set(player.getUniqueId().toString() + ".punishment-point", punishmentPoint);
        save();
    }

    public static String getJail(OfflinePlayer player) throws IllegalArgumentException {
        if (!isWanted(player)) {
            throw new IllegalArgumentException("The player is not wanted.");
        }

        String jailName = get().getString(player.getUniqueId().toString() + ".jail-name", "");
        if (!JailConfig.exist(jailName)) {
            jailName = "";
        }

        return jailName;
    }

    public static void setJail(OfflinePlayer player, String jailName) throws IllegalArgumentException {
        if (!isWanted(player)) {
            throw new IllegalArgumentException("The player is not wanted.");
        }

        if (!JailConfig.exist(jailName)) {
            return;
        }

        get().set(player.getUniqueId().toString() + ".jail-name", jailName);
        save();
    }

    public static void setReason(OfflinePlayer player, String reason) throws IllegalArgumentException {
        if (!isWanted(player)) {
            throw new IllegalArgumentException("The player is not wanted.");
        }

        get().set(player.getUniqueId().toString() + ".reason", reason);
        save();
    }

    public static String getReason(OfflinePlayer player) {
        if (!isWanted(player)) {
            throw new IllegalArgumentException("The player is not wanted.");
        }

        return get().getString(player.getUniqueId().toString() + ".reason", "No reason.");
    }

    public static boolean isWanted(OfflinePlayer player) {
        return getWantedPlayers().contains(player);
    }

    public static Set<OfflinePlayer> getWantedPlayers() {
        return get().getKeys(false).stream().map(uuid -> {
            try {
                return UUID.fromString(uuid);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }).filter(Objects::nonNull).map(Bukkit::getOfflinePlayer).collect(Collectors.toSet());
    }

    public static boolean addWantedPlayer(OfflinePlayer player, String jailName, int punishmentPoint, String reason) {
        if (Prisoners.isJailed(player)) {
            throw new IllegalArgumentException("The player is already jailed.");
        }

        get().createSection(player.getUniqueId().toString());
        try {
            setJail(player, jailName);
            setPunishmentPoint(player, punishmentPoint);
            setReason(player, reason);
            return true;
        } catch (IllegalArgumentException e) {
            removeWantedPlayer(player);
            return false;
        }
    }

    public static void removeWantedPlayer(OfflinePlayer player) {
        if (!isWanted(player)) {
            throw new IllegalArgumentException("The player is not wanted.");
        }

        get().set(player.getUniqueId().toString(), null);
        save();
    }

    /**
     * Reload block points config. If this method used before
     * {@code JailConfig.save()}, the data on memory will be lost.
     */
    public static void reload() {
        wantedListConfig.initConfig();
    }

    /**
     * Saves data on memory to yaml.
     */
    public static void save() {
        wantedListConfig.saveConfig();
    }

    /**
     * Copies yaml from jar to data folder.
     */
    public static void saveDefault() {
        wantedListConfig.saveDefaultConfig();
    }

    /**
     * Gets FileConfiguration of block points config.
     * 
     * @return Block points config.
     */
    static FileConfiguration get() {
        return wantedListConfig.getConfig();
    }
}