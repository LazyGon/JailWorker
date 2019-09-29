package fr.alienationgaming.jailworker.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.util.Vector;

import fr.alienationgaming.jailworker.JailSystem;
import fr.alienationgaming.jailworker.JailWorker;

public final class JailConfig {

    private static JailWorker plugin = JailWorker.getInstance();
    private static CustomConfig jailConfig = new CustomConfig("jails.yml");
    static {
        saveDefault();
    }

    /**
     * Addds punishment block that appear in this jail.
     * 
     * @param jailName
     * @param material A punishment block.
     */
    public static void addPunishmentBlock(String jailName, Material material) {
        Set<Material> materials = getPunishmentBlocks(jailName);
        if (materials.add(material)) {
            setPunishmentBlocks(jailName, materials);
        }
    }

    /**
     * Removes punishment block that appear in this jail.
     * 
     * @param jailName
     * @param material A punishment block.
     */
    public static void removePunishmentBlock(String jailName, Material material) {
        Set<Material> materials = getPunishmentBlocks(jailName);
        if (materials.remove(material)) {
            setPunishmentBlocks(jailName, materials);
        }
    }

    /**
     * Sets punishment blocks that appear in this jail.
     * 
     * @param jailName
     * @param newValue Punishment blocks.
     */
    public static void setPunishmentBlocks(String jailName, Set<Material> newValue) {
        List<String> materialNames = newValue.stream().filter(BlockPoints::exist).map(Enum::name).collect(Collectors.toList());
        get().set(jailName + ".punishment-blocks", materialNames);
        save();
    }

    /**
     * Gets punishment blocks that appear in this jail.
     * 
     * @param jailName
     * @return Punishment blocks.
     */
    public static Set<Material> getPunishmentBlocks(String jailName) {
        List<String> materialNames = get().getStringList(jailName + ".punishment-blocks");
        return materialNames.stream().map(materialName -> {
            try {
                return Material.valueOf(materialName.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning(e.getMessage());
                return null;
            }
        }).filter(Objects::nonNull).distinct().collect(Collectors.toSet());
    }

    /**
     * Gets block speed to be placed.
     * 
     * @param jailName
     * @return Block speed to be placed.
     */
    public static int getBlockSpeed(String jailName) {
        return get().getInt(jailName + ".block-appear-speed");
    }

    /**
     * Sets block speed to be placed.
     * 
     * @param jailName
     * @param second
     */
    public static void setBlockSpeed(String jailName, int second) {
        get().set(jailName + ".block-appear-speed", second);
        save();
    }

    /**
     * Gets allowed command for prisoners in this jail.
     * 
     * @param jailName
     * @return Allowed commands list.
     */
    public static List<String> getAllowedCommands(String jailName) {
        return get().getStringList(jailName + ".allowed-commands");
    }

    /**
     * Sets allowed command.
     * 
     * @param jailName
     * @param allowedCommands
     */
    public static void setAllowedCommands(String jailName, List<String> allowedCommands) {
        get().set(jailName + ".allowed-commands", allowedCommands);
        save();
    }

    /**
     * Adds allowed commands.
     * 
     * @param jailName
     * @param allowedCommand
     */
    public static void addAllowedCommands(String jailName, List<String> addition) {
        List<String> allowedCommands = getAllowedCommands(jailName);
        allowedCommands.removeAll(addition);
        allowedCommands.addAll(addition);
        Collections.sort(allowedCommands);
        setAllowedCommands(jailName, allowedCommands);
    }

    /**
     * Removes allowed commands.
     * 
     * @param jailName
     * @param allowedCommand
     */
    public static void removeAllowedCommands(String jailName, List<String> deletion) {
        List<String> allowedCommands = getAllowedCommands(jailName);
        allowedCommands.removeAll(deletion);
        Collections.sort(allowedCommands);
        setAllowedCommands(jailName, allowedCommands);
    }

    /**
     * Gets max amount of block placed in jail.
     * 
     * @param jailName
     * @return Max amount of punishment block
     */
    public static int getMaxPunishmentBlocks(String jailName) {
        return get().getInt(jailName + ".max-punishment-blocks");
    }

    /**
     * Gets max amount of block placed in jail.
     * 
     * @param jailName
     * @param amount
     * @return Max amount of punishment block
     */
    public static void setMaxPunishmentBlocks(String jailName, int amount) {
        if (amount <= 0) {
            amount = 1;
        }
        get().set(jailName + ".max-punishment-blocks", amount);
        save();
    }

    /**
     * Gets first location of this jail.
     * 
     * @param jailName
     * @return
     * @throws IllegalArgumentException When the world this jail located in does not
     *                                  exist.
     */
    public static Location getPosition1(String jailName) throws IllegalArgumentException {
        World world = getWorld(jailName);
        if (world == null) {
            throw new IllegalArgumentException("The world this jail " + jailName + " located in does not exist.");
        }

        Vector vec = get().getVector(jailName + ".location.position-1");
        if (vec == null) {
            throw new IllegalArgumentException("The location of the jail " + jailName + " in config is invalid.");
        }
        return new Location(world, vec.getBlockX(), vec.getBlockY(), vec.getBlockZ());
    }

    /**
     * Sets first location of this jail.
     * 
     * @param jailName
     * @param position1
     * @return
     * @throws IllegalArgumentException When world including location is null.
     */
    public static void setPosition1(String jailName, Location position1) throws IllegalArgumentException {
        if (position1.getWorld() == null) {
            throw new IllegalArgumentException("The world this jail " + jailName + " located in does not exist.");
        }

        setWorld(jailName, position1.getWorld());
        Vector vec = new Vector(position1.getX(), position1.getY(), position1.getZ());
        get().set(jailName + ".location.position-1", vec);
        save();
    }

    /**
     * Gets second location of this jail.
     * 
     * @param jailName
     * @return
     * @throws IllegalArgumentException When the world this jail located in does not
     *                                  exist.
     */
    public static Location getPosition2(String jailName) throws IllegalArgumentException {
        World world = getWorld(jailName);
        if (world == null) {
            throw new IllegalArgumentException("The world this jail " + jailName + " located in does not exist.");
        }

        Vector vec = get().getVector(jailName + ".location.position-2");
        if (vec == null) {
            throw new IllegalArgumentException("The location in config is invalid.");
        }
        return new Location(world, vec.getBlockX(), vec.getBlockY(), vec.getBlockZ());
    }

    /**
     * Sets second location of this jail.
     * 
     * @param jailName
     * @param position2
     * @return
     * @throws IllegalArgumentException When world including location is null.
     */
    public static void setPosition2(String jailName, Location position2) throws IllegalArgumentException {
        if (position2.getWorld() == null) {
            throw new IllegalArgumentException("The world this jail " + jailName + " located in does not exist.");
        }

        setWorld(jailName, position2.getWorld());
        Vector vec = new Vector(position2.getX(), position2.getY(), position2.getZ());
        get().set(jailName + ".location.position-2", vec);
        save();
    }

    /**
     * Gets spawn location of this jail.
     * 
     * @param jailName
     * @return
     * @throws IllegalArgumentException When the world this jail located in does not
     *                                  exist.
     */
    public static Location getSpawnLocation(String jailName) throws IllegalArgumentException {
        World world = getWorld(jailName);
        if (world == null) {
            throw new IllegalArgumentException("The world this jail " + jailName + " located in does not exist.");
        }

        Vector vec = get().getVector(jailName + ".location.spawn");
        return new Location(world, vec.getBlockX(), vec.getBlockY(), vec.getBlockZ());
    }

    /**
     * Sets spawn location of this jail.
     * 
     * @param jailName
     * @param spawn
     * @return
     * @throws IllegalArgumentException When world this location including is null.
     */
    public static void setSpawnLocation(String jailName, Location spawn) throws IllegalArgumentException {
        if (spawn.getWorld() == null) {
            throw new IllegalArgumentException("The world this jail " + jailName + " located in does not exist.");
        }

        setWorld(jailName, spawn.getWorld());
        Vector vec = new Vector(spawn.getX(), spawn.getY(), spawn.getZ());
        get().set(jailName + ".location.spawn", vec);
        save();
    }

    /**
     * Gets world the {@code jailName} located in. Returns null if world does not
     * exist.
     * 
     * @param jailName
     * @return World or null.
     */
    public static World getWorld(String jailName) {
        return Bukkit.getWorld(get().getString(jailName + ".location.world", ""));
    }

    /**
     * Sets world the {@code jailName} located in.
     * 
     * @param jailName
     * @param world
     */
    public static void setWorld(String jailName, World world) {
        World oldWorld = getWorld(jailName);
        if (oldWorld != null && oldWorld.getUID().equals(world.getUID())) {
            return;
        }

        get().set(jailName + ".location.world", world.getName());
        save();
    }

    /**
     * Gets list of jails.
     * 
     * @return list of all jails.
     */
    public static List<String> getJails() {
        return new ArrayList<>(get().getKeys(false));
    }

    /**
     * Removes jail.
     * 
     * @param jailName
     */
    public static void removeJail(String jailName) {
        if (JailSystem.isRunning(jailName)) {
            JailSystem.removeTask(jailName);
        }
        get().set(jailName, null);
        save();
    }

    public static boolean isInJail(String jailName, Location loc) {
        Location pos1 = JailConfig.getPosition1(jailName);
        Location pos2 = JailConfig.getPosition2(jailName);
        return loc.getWorld() != null && pos1.getWorld() != null && pos2.getWorld() != null
                && loc.getWorld().equals(pos1.getWorld())
                && pos1.getWorld().equals(pos2.getWorld())
                && Math.min(pos1.getBlockX(), pos2.getBlockX()) <= loc.getBlockX()
                && Math.max(pos1.getBlockX(), pos2.getBlockX()) >= loc.getBlockX()
                && Math.min(pos1.getBlockY(), pos2.getBlockY()) <= loc.getBlockY()
                && Math.max(pos1.getBlockY(), pos2.getBlockY()) >= loc.getBlockY()
                && Math.min(pos1.getBlockZ(), pos2.getBlockZ()) <= loc.getBlockZ()
                && Math.max(pos1.getBlockZ(), pos2.getBlockZ()) >= loc.getBlockZ();
    }

    /**
     * Check if the jail exists.
     * 
     * @param jailName
     * @return True if the jail exists, otherwise false.
     */
    public static boolean exist(String jailName) {
        return exist(jailName, false);
    }

    /**
     * Check if the jail exists, with console message on error.
     * 
     * @param jailName
     * @return True if the jail exists, otherwise false.
     */
    public static boolean exist(String jailName, boolean debug) {
        try {
            if (!getJails().contains(jailName)) {
                return false;
            }
            getPosition1(jailName);
            getPosition2(jailName);
            getSpawnLocation(jailName);
            return true;
        } catch (IllegalArgumentException e) {
            if (debug) {
                plugin.getLogger().warning(e.getMessage());
            }
            return false;
        }
    }

    public static void addJail(String jailName, int maxPunishmentBlocks, int punishmentBlockSpeed,
            Set<Material> punishmentBlocks, List<String> allowedCommands, World world, Location position1, Location position2, Location spawn) {
        get().createSection(jailName);
        setMaxPunishmentBlocks(jailName, maxPunishmentBlocks);
        setBlockSpeed(jailName, punishmentBlockSpeed);
        setPunishmentBlocks(jailName, punishmentBlocks);
        setAllowedCommands(jailName, allowedCommands);
        setWorld(jailName, world);
        setPosition1(jailName, position1);
        setPosition2(jailName, position2);
        setSpawnLocation(jailName, spawn);
    }

    /**
     * Reload jail config. If this method used before {@code JailConfig.save()}, the
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
     * Gets FileConfiguration of jail config.
     * 
     * @return jail config.
     */
    static FileConfiguration get() {
        return jailConfig.getConfig();
    }
}