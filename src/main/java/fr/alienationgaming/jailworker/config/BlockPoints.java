package fr.alienationgaming.jailworker.config;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

public final class BlockPoints {

    // NMS reflection variables
    private static String nmsVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    private static String nmsPackageName = "net.minecraft.server." + nmsVersion;
    private static Method asNMSCopy;
    private static Method getItem;
    private static Method p;
    private static Method c;
    static {
        try {
            asNMSCopy = Class.forName("org.bukkit.craftbukkit." + nmsVersion + ".inventory.CraftItemStack").getMethod("asNMSCopy",
                    ItemStack.class);
            getItem = Class.forName(nmsPackageName + ".ItemStack").getMethod("getItem");
            p = Class.forName(nmsPackageName + ".Item").getMethod("p");
            c = Class.forName(nmsPackageName + ".CreativeModeTab").getMethod("c");
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    
    private static final Set<Material> validBlocks = new HashSet<Material>() {
        private static final long serialVersionUID = 1L;

        {
            Arrays.stream(Material.values()).filter(material -> isBuildingBlocks(material))
                    .forEach(material -> add(material));
            remove(Material.BEDROCK);
        }
    };

    private static CustomConfig jailConfig = new CustomConfig("punishment-block-points.yml");
    static {
        saveDefault();
        validBlocks.forEach(material -> {
            if (!get().contains(material.name(), true)) {
                get().set(material.name(), 1);
            }
        });
        save();
    }

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

    private static boolean isBuildingBlocks(Material material) {
        try {
            Object itemStackInvokeObj = asNMSCopy.invoke(null, new ItemStack(material));
            Object itemInvokeObj = getItem.invoke(itemStackInvokeObj);
            Object creativeModeTabInvokeObj = p.invoke(itemInvokeObj);
            if (creativeModeTabInvokeObj == null) {
                return false;
            }
            return ((String) c.invoke(creativeModeTabInvokeObj)).equals("building_blocks");
        } catch (IllegalAccessException | InvocationTargetException ignored) {
            return false;
        }
    }
}