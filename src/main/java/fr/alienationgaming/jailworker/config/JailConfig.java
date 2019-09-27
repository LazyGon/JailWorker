package fr.alienationgaming.jailworker.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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

    // TODO: How to check valid block (or building_blocks) easily?
    private static final Set<Material> validBlocks = new HashSet<Material>() {
        private static final long serialVersionUID = 1L;

        {
            add(Material.STONE);
            add(Material.GRANITE);
            add(Material.POLISHED_GRANITE);
            add(Material.DIORITE);
            add(Material.POLISHED_DIORITE);
            add(Material.ANDESITE);
            add(Material.POLISHED_ANDESITE);
            add(Material.GRASS_BLOCK);
            add(Material.DIRT);
            add(Material.COARSE_DIRT);
            add(Material.PODZOL);
            add(Material.COBBLESTONE);
            add(Material.OAK_PLANKS);
            add(Material.SPRUCE_PLANKS);
            add(Material.BIRCH_PLANKS);
            add(Material.JUNGLE_PLANKS);
            add(Material.ACACIA_PLANKS);
            add(Material.DARK_OAK_PLANKS);
            add(Material.SAND);
            add(Material.RED_SAND);
            add(Material.GRAVEL);
            add(Material.GOLD_ORE);
            add(Material.IRON_ORE);
            add(Material.COAL_ORE);
            add(Material.OAK_LOG);
            add(Material.SPRUCE_LOG);
            add(Material.BIRCH_LOG);
            add(Material.JUNGLE_LOG);
            add(Material.ACACIA_LOG);
            add(Material.DARK_OAK_LOG);
            add(Material.STRIPPED_OAK_LOG);
            add(Material.STRIPPED_SPRUCE_LOG);
            add(Material.STRIPPED_BIRCH_LOG);
            add(Material.STRIPPED_JUNGLE_LOG);
            add(Material.STRIPPED_ACACIA_LOG);
            add(Material.STRIPPED_DARK_OAK_LOG);
            add(Material.STRIPPED_OAK_WOOD);
            add(Material.STRIPPED_SPRUCE_WOOD);
            add(Material.STRIPPED_BIRCH_WOOD);
            add(Material.STRIPPED_JUNGLE_WOOD);
            add(Material.STRIPPED_ACACIA_WOOD);
            add(Material.STRIPPED_DARK_OAK_WOOD);
            add(Material.OAK_WOOD);
            add(Material.SPRUCE_WOOD);
            add(Material.BIRCH_WOOD);
            add(Material.JUNGLE_WOOD);
            add(Material.ACACIA_WOOD);
            add(Material.DARK_OAK_WOOD);
            add(Material.SPONGE);
            add(Material.WET_SPONGE);
            add(Material.GLASS);
            add(Material.LAPIS_ORE);
            add(Material.LAPIS_BLOCK);
            add(Material.SANDSTONE);
            add(Material.CHISELED_SANDSTONE);
            add(Material.CUT_SANDSTONE);
            add(Material.WHITE_WOOL);
            add(Material.ORANGE_WOOL);
            add(Material.MAGENTA_WOOL);
            add(Material.LIGHT_BLUE_WOOL);
            add(Material.YELLOW_WOOL);
            add(Material.LIME_WOOL);
            add(Material.GRAY_WOOL);
            add(Material.LIGHT_GRAY_WOOL);
            add(Material.CYAN_WOOL);
            add(Material.PURPLE_WOOL);
            add(Material.BLUE_WOOL);
            add(Material.BROWN_WOOL);
            add(Material.GREEN_WOOL);
            add(Material.RED_WOOL);
            add(Material.BLACK_WOOL);
            add(Material.GOLD_BLOCK);
            add(Material.IRON_BLOCK);
            add(Material.SMOOTH_QUARTZ);
            add(Material.SMOOTH_RED_SANDSTONE);
            add(Material.SMOOTH_SANDSTONE);
            add(Material.SMOOTH_STONE);
            add(Material.BRICKS);
            add(Material.BOOKSHELF);
            add(Material.MOSSY_COBBLESTONE);
            add(Material.OBSIDIAN);
            add(Material.PURPUR_BLOCK);
            add(Material.PURPUR_PILLAR);
            add(Material.DIAMOND_ORE);
            add(Material.DIAMOND_BLOCK);
            add(Material.REDSTONE_ORE);
            add(Material.ICE);
            add(Material.SNOW_BLOCK);
            add(Material.CLAY);
            add(Material.PUMPKIN);
            add(Material.CARVED_PUMPKIN);
            add(Material.NETHERRACK);
            add(Material.SOUL_SAND);
            add(Material.GLOWSTONE);
            add(Material.JACK_O_LANTERN);
            add(Material.STONE_BRICKS);
            add(Material.MOSSY_STONE_BRICKS);
            add(Material.CRACKED_STONE_BRICKS);
            add(Material.CHISELED_STONE_BRICKS);
            add(Material.MELON);
            add(Material.MYCELIUM);
            add(Material.NETHER_BRICKS);
            add(Material.END_STONE);
            add(Material.END_STONE_BRICKS);
            add(Material.EMERALD_ORE);
            add(Material.EMERALD_BLOCK);
            add(Material.NETHER_QUARTZ_ORE);
            add(Material.CHISELED_QUARTZ_BLOCK);
            add(Material.QUARTZ_BLOCK);
            add(Material.QUARTZ_PILLAR);
            add(Material.TERRACOTTA);
            add(Material.WHITE_TERRACOTTA);
            add(Material.ORANGE_TERRACOTTA);
            add(Material.MAGENTA_TERRACOTTA);
            add(Material.LIGHT_BLUE_TERRACOTTA);
            add(Material.YELLOW_TERRACOTTA);
            add(Material.LIME_TERRACOTTA);
            add(Material.PINK_TERRACOTTA);
            add(Material.LIGHT_GRAY_TERRACOTTA);
            add(Material.GRAY_TERRACOTTA);
            add(Material.CYAN_TERRACOTTA);
            add(Material.PURPLE_TERRACOTTA);
            add(Material.BLUE_TERRACOTTA);
            add(Material.BROWN_TERRACOTTA);
            add(Material.GREEN_TERRACOTTA);
            add(Material.RED_TERRACOTTA);
            add(Material.BLACK_TERRACOTTA);
            add(Material.HAY_BLOCK);
            add(Material.COAL_BLOCK);
            add(Material.PACKED_ICE);
            add(Material.WHITE_STAINED_GLASS);
            add(Material.ORANGE_STAINED_GLASS);
            add(Material.MAGENTA_STAINED_GLASS);
            add(Material.LIGHT_BLUE_STAINED_GLASS);
            add(Material.YELLOW_STAINED_GLASS);
            add(Material.LIME_STAINED_GLASS);
            add(Material.PINK_STAINED_GLASS);
            add(Material.GRAY_STAINED_GLASS);
            add(Material.LIGHT_GRAY_STAINED_GLASS);
            add(Material.CYAN_STAINED_GLASS);
            add(Material.PURPLE_STAINED_GLASS);
            add(Material.BLUE_STAINED_GLASS);
            add(Material.BROWN_STAINED_GLASS);
            add(Material.GREEN_STAINED_GLASS);
            add(Material.RED_STAINED_GLASS);
            add(Material.BLACK_STAINED_GLASS);
            add(Material.PRISMARINE);
            add(Material.PRISMARINE_BRICKS);
            add(Material.DARK_PRISMARINE);
            add(Material.SEA_LANTERN);
            add(Material.RED_SANDSTONE);
            add(Material.CHISELED_RED_SANDSTONE);
            add(Material.CUT_RED_SANDSTONE);
            add(Material.MAGMA_BLOCK);
            add(Material.NETHER_WART_BLOCK);
            add(Material.RED_NETHER_BRICKS);
            add(Material.BONE_BLOCK);
            add(Material.WHITE_CONCRETE);
            add(Material.ORANGE_CONCRETE);
            add(Material.MAGENTA_CONCRETE);
            add(Material.LIGHT_BLUE_CONCRETE);
            add(Material.YELLOW_CONCRETE);
            add(Material.LIME_CONCRETE);
            add(Material.PINK_CONCRETE);
            add(Material.GRAY_CONCRETE);
            add(Material.LIGHT_GRAY_CONCRETE);
            add(Material.CYAN_CONCRETE);
            add(Material.PURPLE_CONCRETE);
            add(Material.BLUE_CONCRETE);
            add(Material.BROWN_CONCRETE);
            add(Material.GREEN_CONCRETE);
            add(Material.RED_CONCRETE);
            add(Material.BLACK_CONCRETE);
            add(Material.WHITE_CONCRETE_POWDER);
            add(Material.ORANGE_CONCRETE_POWDER);
            add(Material.MAGENTA_CONCRETE_POWDER);
            add(Material.LIGHT_BLUE_CONCRETE_POWDER);
            add(Material.YELLOW_CONCRETE_POWDER);
            add(Material.PINK_CONCRETE_POWDER);
            add(Material.GRAY_CONCRETE_POWDER);
            add(Material.LIGHT_GRAY_CONCRETE_POWDER);
            add(Material.CYAN_CONCRETE_POWDER);
            add(Material.PURPLE_CONCRETE_POWDER);
            add(Material.BLUE_CONCRETE_POWDER);
            add(Material.BROWN_CONCRETE_POWDER);
            add(Material.GREEN_CONCRETE_POWDER);
            add(Material.RED_CONCRETE_POWDER);
            add(Material.BLACK_CONCRETE_POWDER);
            add(Material.BLUE_ICE);
            add(Material.DRIED_KELP_BLOCK);

        }
    };

    private static final List<String> validBlockNames = validBlocks.stream().map(Enum::name)
            .collect(Collectors.toList());
    
    public static List<String> getValidBlocks() {
        return Collections.unmodifiableList(validBlockNames);
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

    /**
     * Check if the jail exists.
     * 
     * @param jailName
     * @return True if the jail exists, otherwise false.
     */
    public static boolean exist(String jail) {
        return exist(jail, false);
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
            plugin.getLogger().warning(e.getMessage());
            return false;
        }
    }

    public static void addJail(String jailName, int maxPunishmentBlocks, int punishmentBlockSpeed,
            List<String> allowedCommands, World world, Location position1, Location position2, Location spawn) {
        get().createSection(jailName);
        setMaxPunishmentBlocks(jailName, maxPunishmentBlocks);
        setBlockSpeed(jailName, punishmentBlockSpeed);
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
    private static FileConfiguration get() {
        return jailConfig.getConfig();
    }
}