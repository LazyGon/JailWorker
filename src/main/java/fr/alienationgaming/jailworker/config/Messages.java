package fr.alienationgaming.jailworker.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.github.ucchyocean.lc.channel.ChannelPlayer;

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
            code = ((Player) sender).getLocale();
        } else {
            code = Config.getDefaultLanguage();
        }

        if (!languages.containsKey(code)) {
            initLanguage(sender);
        }

        FileConfiguration config = languages.getOrDefault(code, languages.get("en_us")).getConfig();
        String prefix;
        if (addPrefix) {
            prefix = config.getString("command.general.info.plugin-prefix", "&8[&cJail&aWorker&8]&r") + " ";
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
        sendMessage(sender, path, Map.of());
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
        sendMessage(sender, addPrefix, path, Map.of());
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
        sendMessage(channelPlayer, path, Map.of());
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

        String code = (sender instanceof Player) ? ((Player) sender).getLocale() : Config.getDefaultLanguage();
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
        languages.values().forEach(CustomConfig::initConfig);
    }
}