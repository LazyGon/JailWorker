package fr.alienationgaming.jailworker.config;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

public final class BlockPoints {

    private static CustomConfig jailConfig = new CustomConfig("punishment-block-points.yml");
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

    public static Set<Material> getAllBlocks() {
        return Collections.unmodifiableSet(validBlocks);
    }

    public static void setPoint(Material punishmentBlock, int point) {
        if (!validBlocks.contains(punishmentBlock)) {
            return;
        }

        get().set(punishmentBlock.name(), point);
        save();
    }

    public static int getPoint(Material punishmentBlock) {
        return get().getInt(punishmentBlock.name());
    }

    public static int addPoint(Material punishmentBlock, int add) {
        int newPoint = getPoint(punishmentBlock) + add;
        setPoint(punishmentBlock, newPoint);
        return newPoint;
    }

    /**
     * Check if the Material is defined in block points config.
     * 
     * @param punishmentBlock
     * @return True if the point is defined.
     */
    public static boolean exist(Material punishmentBlock) {
        return validBlocks.contains(punishmentBlock) && get().isInt(punishmentBlock.name());
    }

    /**
     * Reload block points config. If this method used before
     * {@code JailConfig.save()}, the data on memory will be lost.
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
     * Gets FileConfiguration of block points config.
     * 
     * @return Block points config.
     */
    static FileConfiguration get() {
        return jailConfig.getConfig();
    }
}