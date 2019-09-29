
package fr.alienationgaming.jailworker.config;

import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

import fr.alienationgaming.jailworker.JailWorker;

public final class Config {

    private static JailWorker plugin = JailWorker.getInstance();
    private static FileConfiguration config = plugin.getConfig();

    private Config() {
    }



    public static int getAutoPointDecreaseInterval() {
        int autoReducePointInterval = config.getInt("prisoners.auto-reduce-point-interval");
        if (autoReducePointInterval < 0) {
            autoReducePointInterval = 0;
        }
        return autoReducePointInterval;
    }

    public static int getAfkTime() {
        int afkTime = config.getInt("prisoners.afk-time");
        if (afkTime < 0) {
            afkTime = 0;
        }
        return afkTime;
    }

    public static List<String> getGlobalAllowedCommands() {
        return config.getStringList("jails.global-allowed-commands");
    }

    public static boolean isUsingPlayerLocale() {
        return config.getBoolean("plugin.use-palyer-locale", true);
    }

    public static String getDefaultLanguage() {
        return config.getString("plugin.default-language", "en_us");
    }

    public static int getDefaultMaxBlocks() {
        return config.getInt("jails.default-max-punishment-blocks", 30);
    }

    public static int getDefaultBlockSpeed() {
        return config.getInt("jails.default-block-spawn-speed", 10);
    }

    public static boolean canPrisonerSpeak() {
        return config.getBoolean("prisoners.can-speak");
    }

    public static boolean canPrisonersHear() {
        return config.getBoolean("prisoners.can-hear");
    }

    /**
     * Reload jail config. If this method used before {@code JailConfig.save()}, the
     * data on memory will be lost.
     */
    public static void reload() {
        saveDefault();
        plugin.reloadConfig();
        config = plugin.getConfig();
    }

    /**
     * Saves data on memory to yaml.
     */
    public static void save() {
        plugin.saveConfig();
    }

    /**
     * Copies yaml from jar to data folder.
     */
    public static void saveDefault() {
        plugin.saveDefaultConfig();
    }
}