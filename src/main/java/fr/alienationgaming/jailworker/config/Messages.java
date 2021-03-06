package fr.alienationgaming.jailworker.config;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.ucchyocean.lc.channel.ChannelPlayer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import fr.alienationgaming.jailworker.JailWorker;

public final class Messages {
    private static JailWorker plugin = JailWorker.getInstance();
    private static Set<String> supportedLanguages = new HashSet<String>() {
        private static final long serialVersionUID = 1L;
        {
            add("en_us");
            add("fr_fr");
            add("ja_jp");
        }
    };

    private static final Map<String, CustomConfig> languages = new HashMap<String, CustomConfig>();

    /**
     * Cannot use constructor.
     */
    private Messages() {
    }

    /**
     * Send message to player. The message will be their own language or English. To
     * add language, make yaml file named their own locale in languages folder, like
     * this {@code JailWorker/languages/en_us.yml}. {@code placeholder}'s key will be
     * replaced with its value.
     * 
     * @param player
     * @param addPrefix
     * @param path
     * @param placeholders
     * 
     * @see https://minecraft.gamepedia.com/Language
     */
    public static void sendMessage(CommandSender sender, boolean addPrefix, String path, Map<String, Object> placeholders) {
        String code;
        if (Config.isUsingPlayerLocale() && sender instanceof Player) {
            code = getLocale((Player) sender);
        } else {
            code = Config.getDefaultLanguage();
        }

        if (!languages.containsKey(code)) {
            initLanguage(sender);
        }

        FileConfiguration config = languages.getOrDefault(code, languages.get("en_us")).getConfig();
        String prefix;
        if (addPrefix) {
            prefix = config.getString("plugin.prefix", "&8[&cJail&aWorker&8]&r") + " ";
        } else {
            prefix = "";
        }
        String message = ChatColor.translateAlternateColorCodes('&', prefix + config.getString(path, path));
        for (Map.Entry<String, Object> placeholder : placeholders.entrySet()) {
            message = message.replace(placeholder.getKey(), placeholder.getValue().toString());
        }
        sender.sendMessage(message);
        return;
    }

    /**
     * Send message to player. The message will be their own language or English. To
     * add language, make yaml file named their own locale in languages folder, like
     * this {@code JailWorker/languages/en_us.yml}. {@code placeholder}'s key will be
     * replaced with its value.
     * 
     * @param player
     * @param path
     * @param placeholders
     * 
     * @see https://minecraft.gamepedia.com/Language
     */
    public static void sendMessage(CommandSender sender, String path, Map<String, Object> placeholders) {
        sendMessage(sender, true, path, placeholders);
    }

    /**
     * Send message to player. The message will be their own language or English. To
     * add language, make yaml file named their own locale in languages folder, like
     * this {@code JailWorker/languages/en_us.yml}.
     * 
     * @param sender
     * @param path
     */
    public static void sendMessage(CommandSender sender, String path) {
        sendMessage(sender, path, Messages.placeholder());
    }

    /**
     * Send message to player. The message will be their own language or English. To
     * add language, make yaml file named their own locale in languages folder, like
     * this {@code JailWorker/languages/en_us.yml}.
     * 
     * @param sender
     * @param addPrefix
     * @param path
     */
    public static void sendMessage(CommandSender sender, boolean addPrefix, String path) {
        sendMessage(sender, addPrefix, path, Messages.placeholder());
    }

    /**
     * Send message to channel player.
     * 
     * @param channelPlayer
     * @param path
     * @param placeholders
     */
    public static void sendMessage(ChannelPlayer channelPlayer, String path, Map<String, Object> placeholder) {
        Player player = channelPlayer.getPlayer();
        if (player != null) {
            sendMessage(player, path, placeholder);
        }
    }

    /**
     * Send message to channel player.
     * 
     * @param channelPlayer
     * @param path
     */
    public static void sendMessage(ChannelPlayer channelPlayer, String path) {
        sendMessage(channelPlayer, path, Messages.placeholder());
    }

    public static Map<String, Object> placeholder(Object ... elements) {
        if (elements.length % 2 != 0) {
            throw new IllegalArgumentException("elements length cannot be odd number.");
        }

        Map<String, Object> result = new HashMap<>();
        for (int i = 0; i < elements.length; i+=2) {
            if (elements.length - 1 < i + 1) {
                break;
            }
            if (!(elements[i] instanceof String)) {
                continue;
            }

            String key = (String) elements[i];
            if (key == null || elements[i + 1] == null) {
                continue;
            }
            result.put(key, elements[i + 1]);
        }

        return result;
    }

    /**
     * Initialize language and put into map.
     * 
     * @param player
     */
    private static void initLanguage(CommandSender sender) {
        Path languageFolder = plugin.getDataFolder().toPath().resolve("languages");
        if (!Files.exists(languageFolder)) {
            languageFolder.toFile().mkdirs();
        }

        supportedLanguages.forEach(code -> {
            if (!Files.exists(languageFolder.resolve(code + ".yml"))) {
                plugin.saveResource("languages/" + code + ".yml", false);
            }
        });

        String code = (sender instanceof Player) ? getLocale((Player) sender) : Config.getDefaultLanguage();
        if (!supportedLanguages.contains(code)) {
            code = "en_us";
        }
        if (Files.exists(languageFolder.resolve(code + ".yml"))) {
            languages.put(code, new CustomConfig("languages/" + code + ".yml"));
        } else if (!languages.containsKey("en_us")) {
            languages.put("en_us", new CustomConfig("languages/en_us.yml"));
        }
    }

    /**
     * Reload all the language config on map.
     */
    public static void reload() {
        initLanguage(Bukkit.getConsoleSender());
        languages.values().forEach(CustomConfig::initConfig);
    }

    private static final String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    private static String getLocale(Player player) {
        if (version.compareTo("v1_12") >= 0 && !version.startsWith("v1_11")) {
            return player.getLocale();
        } else {
            return Config.getDefaultLanguage();
        }
    }

    private static List<String> getInstalledLanguages() {
        List<String> result = new ArrayList<>();
        Path languageFolder = plugin.getDataFolder().toPath().resolve("languages");
        if (!Files.exists(languageFolder)) {
            return result;
        }

        for (File yml : languageFolder.toFile().listFiles()) {
            String name = yml.getName();
            if (!name.endsWith(".yml")) {
                continue;
            }

            result.add(name.substring(0, name.length() - 4));
        }

        return result;
    }

    private static void addLanguageKey(String locale, String key, String message) {
        Path languageFile = plugin.getDataFolder().toPath().resolve("languages").resolve(locale + ".yml");
        if (!Files.exists(languageFile)) {
            return;
        }

        CustomConfig languageConfig = new CustomConfig(languageFile.toFile());
        if (!languageConfig.getConfig().isString(key)) {
            languageConfig.getConfig().set(key, message);
            languageConfig.saveConfig();
        }
    }

    static void addKey(String key, Map<String, String> localeMessageMap) {
        Messages.getInstalledLanguages().forEach(language -> {
            String message = localeMessageMap.getOrDefault(language, "en_us");
            Messages.addLanguageKey(language, "command.general.info.usage", message);
        });
    }
}