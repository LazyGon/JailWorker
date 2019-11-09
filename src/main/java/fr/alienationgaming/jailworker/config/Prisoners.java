package fr.alienationgaming.jailworker.config;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public final class Prisoners {

    private static CustomConfig jailConfig = new CustomConfig("prisoners.yml");
    static {
        saveDefault();
    }

    /**
     * Gets who punished the player. If punisher is console, returns null.
     * 
     * @param player
     * @return Punisher or null.
     */
    public static OfflinePlayer getPunisher(OfflinePlayer player) {
        if (!isValidPrisoner(player)) {
            throw new IllegalArgumentException("The player is null or has never played before.");
        }

        String punisher = get().getString(player.getUniqueId().toString() + ".punisher");
        if (punisher.equalsIgnoreCase("CONSOLE")) {
            return null;
        }

        return getOfflinePlayer(punisher);
    }

    /**
     * Sets who punished the player.
     * 
     * @param player
     * @param punisher if punisher is console, input must be null.
     * @throws IllegalArgumentException 
     */
    public static void setPunisher(OfflinePlayer prisoner, OfflinePlayer punisher) throws IllegalArgumentException {
        if (punisher != null && !punisher.hasPlayedBefore()) {
            throw new IllegalArgumentException("The punisher has never played before.");
        }

        if (!isValidPrisoner(prisoner)) {
            throw new IllegalArgumentException("The prisoner is null or has never played before or is not jailed.");
        }

        String punisherValue = (punisher == null) ? "CONSOLE" : punisher.getUniqueId().toString();
        get().set(prisoner.getUniqueId().toString() + ".punisher", punisherValue);
        save();
    }



    /**
     * Restores players previous inventory.
     * 
     * @param player
     */
    public static void clearPreviousInventory(Player player) {
        if (!isValidPrisoner(player)) {
            throw new IllegalArgumentException("The player is null or not jailed");
        }

        get().set(player.getUniqueId().toString() + ".inventory", null);
        save();
    }

    /**
     * Restores players previous inventory.
     * 
     * @param player
     */
    public static void restorePreviousInventory(Player player) {
        if (!isValidPrisoner(player)) {
            throw new IllegalArgumentException("The player is null or not jailed");
        }

        ItemStack[] contents = player.getInventory().getContents();
        for (int slot = 0; slot < 40; slot++) {
            contents[slot] = get().getItemStack(player.getUniqueId().toString() + ".inventory." + slot);
        }
        player.getInventory().getContents()[7] = new ItemStack(Material.STONE, 64);
    }

    /**
     * Sets players previous inventory. If player is null or not jailed, operation will be aborted.
     * 
     * @param player
     */
    public static void setPreviousInventory(Player player) {
        if (!isValidPrisoner(player)) {
            throw new IllegalArgumentException("The player is null or not jailed");
        }

        ItemStack[] contents = player.getInventory().getContents();
        for (int slot = 0; slot < 40; slot++) {
            get().set(player.getUniqueId().toString() + ".inventory." + slot, contents[slot]);
        }
        save();
    }

    /**
     * Gets previous location of punished player.
     * 
     * @param jailName
     * @return Previous location.
     * @throws IllegalArgumentException When prisoner is invalid.
     */
    public static Location getPreviousPosition(OfflinePlayer player) throws IllegalArgumentException {
        if (!isValidPrisoner(player)) {
            throw new IllegalArgumentException("The player is null or not jailed");
        }

        World world = Bukkit.getWorld(get().getString(player.getUniqueId().toString() + ".previous-location.world"));
        if (world == null) {
            throw new IllegalArgumentException("The world this jail located in does not exist.");
        }

        Vector vec = get().getVector(player.getUniqueId().toString() + ".previous-location.position");
        if (vec == null) {
            throw new IllegalArgumentException("The location in config is invalid.");
        }
        return new Location(world, vec.getBlockX(), vec.getBlockY(), vec.getBlockZ());
    }

    /**
     * Sets previous location of punished player.
     * 
     * @param player
     * @param location
     * @throws IllegalArgumentException When the world included location is null.
     */
    public static void setPreviousPosition(Player player, Location location) throws IllegalArgumentException {
        if (!isValidPrisoner(player)) {
            throw new IllegalArgumentException("The player is null or not jailed");
        }

        World world = location.getWorld();
        if (world == null) {
            throw new IllegalArgumentException("The world this jail located in does not exist.");
        }

        Vector vec = location.toVector();
        get().set(player.getUniqueId().toString() + ".previous-location.world", world.getName());
        get().set(player.getUniqueId().toString() + ".previous-location.position", vec);
        save();
    }

    /**
     * Sets previous location to player's current location.
     * 
     * @param player
     */
    public static void setPreviousPosition(Player player) throws IllegalArgumentException {
        setPreviousPosition(player, player.getLocation());
    }

    /**
     * Gets punishment point for this player.
     * 
     * @param player
     * @return punishment point. If player is not prisoner, returns always 0.
     */
    public static int getPunishmentPoint(OfflinePlayer player) {
        if (!isValidPrisoner(player)) {
            throw new IllegalArgumentException("The player is null or not jailed");
        }

        int punishmentPoint = get().getInt(player.getUniqueId().toString() + ".punishment-point");
        if (punishmentPoint < 0) {
            punishmentPoint = 0;
        }
        return punishmentPoint;
    }
    
    /**
     * Sets punishment point for this player.
     * 
     * @param player
     * @param punishmentPoint
     */
    public static void setPunishmentPoint(OfflinePlayer player, int punishmentPoint) {
        if (!isValidPrisoner(player)) {
            throw new IllegalArgumentException("The player is null or not jailed");
        }

        if (punishmentPoint < 0) {
            punishmentPoint = 0;
        }

        get().set(player.getUniqueId().toString() + ".punishment-point", punishmentPoint);
        save();
    }

    /**
     * Adds punishment point for this player.
     * 
     * @param player
     * @param punishmentPoint
     * @return New value. If player is not prisoner, returns always 0.
     */
    public static int addPunishmentPoint(OfflinePlayer player, int punishmentPoint) {
        int newPoint = getPunishmentPoint(player) + punishmentPoint;
        if (newPoint < 0) {
            newPoint = 0;
        }
        setPunishmentPoint(player, newPoint);
        return newPoint;
    }

    public static List<OfflinePlayer> getPrisoners() {
        return get().getKeys(false).stream().map(Prisoners::getOfflinePlayer)
                .filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * Gets cause player is jailed. If not set, returns "No reason."
     * 
     * @param player
     * @return Why the player is jailed.
     */
    public static String getCause(OfflinePlayer player) {
        if (!isValidPrisoner(player)) {
            throw new IllegalArgumentException("The player is null or not jailed");
        }

        return get().getString(player.getUniqueId().toString() + ".cause", "No reason.");
    }

    /**
     * Sets cause player is jailed.
     * 
     * @param player
     * @param cause
     */
    public static void setCause(OfflinePlayer player, String cause) {
        if (!isValidOfflinePlayer(player)) {
            throw new IllegalArgumentException("The player is null or has never played before.");
        }

        get().set(player.getUniqueId().toString() + ".cause", cause);
        save();
    }

    /**
     * Sets cause player is jailed to "No reason.".
     * 
     * @param player
     */
    public static void setCause(OfflinePlayer player) {
        setCause(player, "No reason.");
    }

    /**
     * Gets prisoners previous game mode.
     * 
     * @param player
     * @return Previous game mode.
     */
    public static GameMode getPreviousGameMode(OfflinePlayer player) {
        if (!isValidPrisoner(player)) {
            throw new IllegalArgumentException("The player is null or not jailed");
        }

        try {
            return GameMode.valueOf(get().getString(player.getUniqueId().toString() + ".previous-gamemode", "SURVIVAL"));
        } catch (IllegalArgumentException e) {
            return GameMode.SURVIVAL;
        }
    }

    /**
     * Sets prisoner previous game mode.
     * 
     * @param player
     * @param gameMode
     */
    public static void setPreviousGameMode(OfflinePlayer player, GameMode gameMode) {
        if (!isValidOfflinePlayer(player)) {
            throw new IllegalArgumentException("The player is null or has never played before.");
        }

        if (gameMode == null) {
            gameMode = GameMode.SURVIVAL;
        }

        get().set(player.getUniqueId().toString() + ".previous-gamemode", gameMode.name());
        save();
    }

    /**
     * Sets prisoner previous game mode to current game mode.
     * 
     * @param player
     */
    public static void setPreviousGameMode(Player player) {
        setPreviousGameMode(player, player.getGameMode());
    }

    /**
     * Gets time when player is jailed. If not set, returns {@code Long.MIN_VALUE}
     * 
     * @param player
     * @return When the player is jailed in unix time or {@code Long.MIN_VALUE}
     */
    public static long getJailedDate(OfflinePlayer player) {
        if (!isValidPrisoner(player)) {
            throw new IllegalArgumentException("The player is null or not jailed");
        }

        return get().getLong(player.getUniqueId().toString() + ".punished-date", Long.MIN_VALUE);
    }

    /**
     * Sets when player is jailed.
     * 
     * @param player
     * @param unixTime
     */
    public static void setJailedDate(OfflinePlayer player, long unixTime) {
        if (!isValidOfflinePlayer(player)) {
            throw new IllegalArgumentException("The player is null or has never played before.");
        }

        get().set(player.getUniqueId().toString() + ".punished-date", unixTime);
        save();
    }

    /**
     * Sets when player is jailed to now.
     * 
     * @param player
     */
    public static void setJailedDate(OfflinePlayer player) {
        setJailedDate(player, System.currentTimeMillis());
    }

    /**
     * Gets name of jail that the player is in or null if player is not punished.
     * 
     * @param player
     * @return jailName or null if the player is not punished.
     */
    public static String getJailPlayerIsIn(OfflinePlayer player) {
        return get().getString(player.getUniqueId().toString() + ".jail-name");
    }

    /**
     * Gets name of jail that the player is in or null if player is not punished.
     * 
     * @param player
     * @param jailName
     */
    public static void setJailPlayerIsIn(OfflinePlayer player, String jailName) {
        if (!isValidOfflinePlayer(player)) {
            throw new IllegalArgumentException("The player is null or has never played before.");
        }

        get().set(player.getUniqueId().toString() + ".jail-name", jailName);
        save();
    }

    /**
     * Check is the player is jailed.
     * 
     * @param player
     * @return True if the player is jailed, otherwise false.
     */
    public static boolean isJailed(OfflinePlayer player) {
        return getJailPlayerIsIn(player) != null;
    }

    /**
     * Puts player into jail, with some params. If punisher is Console, put null into {@code punisher}.
     * This method do not send any message.
     * 
     * @param player
     * @param jailName
     * @param punisher
     * @param punishmentPoint
     * @param cause
     */
    public static void punishPlayer(Player player, String jailName, OfflinePlayer punisher, int punishmentPoint, String cause) {
        if (!isValidOfflinePlayer(player)) {
            throw new IllegalArgumentException("The player is null or has never played before.");
        }

        if (punisher != null && !punisher.hasPlayedBefore()) {
            throw new IllegalArgumentException("The punisher has never played before.");
        }
        
        if (isJailed(player)) {
            throw new IllegalArgumentException("The player is already jailed.");
        }
        
        if (!JailConfig.exist(jailName)) {
            throw new IllegalArgumentException("The jail does not exist.");
        }

        setJailPlayerIsIn(player, jailName);
        setPunisher(player, punisher);
        setJailedDate(player);
        setPreviousInventory(player);
        setPreviousPosition(player.getPlayer());
        setPunishmentPoint(player, punishmentPoint);
        setCause(player, cause);
        setPreviousGameMode(player);

        player.teleport(JailConfig.getSpawnLocation(jailName));
        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().clear();
        player.getEquipment().clear();
    }

    /**
     * Free player from jail.
     * 
     * @param player
     * @param jailName
     * @param punisher
     * @param punishmentBlocks
     * @param cause
     */
    public static void freePlayer(Player player) {
        if (!isJailed(player)) {
            throw new IllegalArgumentException("The player is not jailed.");
        }

        Location previousLocation = getPreviousPosition(player);
        GameMode previousGameMode = getPreviousGameMode(player);
        restorePreviousInventory(player);
        // If prisoner data is not null, player cannot teleport.
        get().set(player.getUniqueId().toString(), null); 
        save();
        player.teleport(previousLocation);
        player.setGameMode(previousGameMode);
    }

    /**
     * Reload prisoners config. If this method used before {@code JailConfig.save()}, the
     * data on memory will be lost.
     */
    public static void reload() {
        jailConfig.initConfig();
    }

    /**
     * Saves data on memory to yaml.
     */
    public static void save() {
        jailConfig.saveConfig();
    }

    /**
     * Copies yaml from jar to data folder.
     */
    public static void saveDefault() {
        jailConfig.saveDefaultConfig();
    }

    /**
     * Gets FileConfiguration of prisoners config.
     * 
     * @return Prisoners config.
     */
    static FileConfiguration get() {
        return jailConfig.getConfig();
    }

    /**
     * Gets OfflinePlayer by input. Input can be name or uuid. Returns null if player has never
     * played before.
     * 
     * @param nameOrUuid
     * @return OfflinePlayer
     */
    private static OfflinePlayer getOfflinePlayer(String nameOrUuid) {
        try {
            return Bukkit.getOfflinePlayer(UUID.fromString(nameOrUuid));
        } catch (IllegalArgumentException e) {
            @SuppressWarnings("deprecation")
            OfflinePlayer player = Bukkit.getOfflinePlayer(nameOrUuid);
            if (player.hasPlayedBefore() && player.getName() != null) {
                return player;
            } else {
                return null;
            }
        }
    }

    /**
     * Check the OfflinePlayer has name and has played before.
     * 
     * @param player
     * @return True if the player has name and has played before.
     */
    public static boolean isValidOfflinePlayer(OfflinePlayer player) {
        return player != null && player.hasPlayedBefore() && player.getName() != null;
    }

    /**
     * Check if the player is valid OfflinePlayer and is jailed.
     * 
     * @param player
     * @return True if the player is valid and jailed.
     */
    public static boolean isValidPrisoner(OfflinePlayer player) {
        return isValidOfflinePlayer(player) && isJailed(player);
    }
}