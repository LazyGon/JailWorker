package fr.alienationgaming.jailworker.config;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
    private static Method getCreativeModeTab;
    private static Field name;
    static {
        try {
            asNMSCopy = Class.forName("org.bukkit.craftbukkit." + nmsVersion + ".inventory.CraftItemStack").getMethod("asNMSCopy",
                    ItemStack.class);
            getItem = Class.forName(nmsPackageName + ".ItemStack").getMethod("getItem");
            for (Method method : Class.forName(nmsPackageName + ".Item").getMethods()) {
                if (method.getReturnType().getName().equals(nmsPackageName + ".CreativeModeTab")) {
                    getCreativeModeTab = method;
                }
            }
            for (Field field : Class.forName(nmsPackageName + ".CreativeModeTab").getDeclaredFields()) {
                if (field.getType() == String.class && Modifier.isFinal(field.getModifiers())) {
                    name = field;
                }
            }
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

    @SuppressWarnings("deprecation")
    private static boolean isBuildingBlocks(Material material) {
        if (getCreativeModeTab == null || name == null) {
            return false;
        }
        try {
            Object itemStackInvokeObj = asNMSCopy.invoke(null, new ItemStack(material));
            Object itemInvokeObj = getItem.invoke(itemStackInvokeObj);
            Object creativeModeTabInvokeObj = getCreativeModeTab.invoke(itemInvokeObj);
            if (creativeModeTabInvokeObj == null) {
                return false;
            }
            boolean accessChanged = false;
            if (!name.isAccessible()) {
                accessChanged = true;
                name.setAccessible(true);
            }
            boolean matche = ((String) name.get(creativeModeTabInvokeObj)).equals("buildingBlocks");
            if (accessChanged) {
                name.setAccessible(false);
            }
            return matche;
        } catch (IllegalAccessException | InvocationTargetException ignored) {
            return false;
        }
    }
}