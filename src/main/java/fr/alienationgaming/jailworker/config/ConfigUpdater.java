package fr.alienationgaming.jailworker.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

import fr.alienationgaming.jailworker.JailWorker;

//import fr.alienationgaming.jailworker.JailWorker;

public final class ConfigUpdater {

    private static JailWorker plugin = JailWorker.getInstance();

    private ConfigUpdater() {
    }

    /**
     * Version sintax: majour.minor.patch
     * <p>
     * Change which is incompatible to previous version -> majour
     * <p>
     * Change which is compatible to previous version -> minor
     * <p>
     * Bug fix which is compatible to previous version -> patch
     * <p>
     */

    /**
     * Updates old config. If config is incompatible because of majour update, it
     * will be copied to {@code old} folder and deleted.
     */
    public static void update() {
        Config.saveAllDefaultConfigs();
        String version = plugin.getDescription().getVersion();
        String oldVersion = Config.getConfigVersion();

        if (!oldVersion.startsWith(String.valueOf(version.charAt(0)))) {
            Path datafoler = plugin.getDataFolder().toPath();
            Path oldDirectory = datafoler.resolve("old").resolve(oldVersion);
            try {
                Files.createDirectories(oldDirectory);
                for (File file : datafoler.toFile().listFiles()) {
                    if (file.getName().equals("old")) {
                        continue;
                    }
                    Path dest = oldDirectory.resolve(file.getName());
                    copy(file.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
                    delete(file.toPath());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            plugin.getLogger()
                    .info("Current config files might be incompatible to this version. Please configure again.");
            plugin.getLogger()
                    .info("Go to http://dev.bukkit.org/bukkit-plugins/jail-worker/ for news and reporting bugs");
            return;
        }

        if (version.compareTo(oldVersion) > 0) {
            plugin.getLogger().info("Updating...");

            if (oldVersion.equals("3.0.0")) {
                oldVersion = "3.1.0";
                Config.get().set("plugin.version", oldVersion);
            }
            
            if (oldVersion.equals("3.1.0")) {
                oldVersion = "3.1.1";
                Config.get().set("plugin.version", oldVersion);
            }
            
            if (oldVersion.equals("3.1.1")) {
                oldVersion = "3.3.1";
                Config.get().set("plugin.version", oldVersion);
            }
            
            if (oldVersion.equals("3.3.1")) {
                oldVersion = "3.3.2";
                Config.get().set("plugin.version", oldVersion);
            }
            
            if (oldVersion.equals("3.3.2")) {
                oldVersion = "3.3.3";
                Config.get().set("plugin.version", oldVersion);
            }
            
            if (oldVersion.equals("3.3.3")) {
                oldVersion = "3.3.4";
                Config.get().set("plugin.version", oldVersion);
            }
            
            if (oldVersion.equals("3.3.4")) {
                // 3.3.5, 3.3.6, 3.3.7 were not released.
                oldVersion = "3.3.8";
                Config.get().set("plugin.version", oldVersion);
                Map<String, String> localeMessageMap = new HashMap<String, String>() {
                    private static final long serialVersionUID = 1L;
                    {
                        put("en_us", "Usage: %usage%");
                        put("ja_jp", "使用法: %usage%");
                    }
                };
                Messages.addKey("command.general.info.usage", localeMessageMap);
            }

            if (oldVersion.equals("3.3.8")) {
                oldVersion = "3.3.9";
                Config.get().set("plugin.version", oldVersion);
            }

            if (oldVersion.equals("3.3.9")) {
                oldVersion = "3.3.10";
                Config.get().set("plugin.version", oldVersion);
            }

            plugin.getLogger()
                    .info("Go to http://dev.bukkit.org/bukkit-plugins/jail-worker/ for news and reporting bugs");
            Config.save();
        }

    }

    private static void copy(Path file, Path dest, StandardCopyOption option) {
        try {
            if (Files.isDirectory(file)) {
                Files.createDirectories(dest);
                for (File subFile : file.toFile().listFiles()) {
                    copy(subFile.toPath(), dest.resolve(subFile.getName()), option);
                }
            } else {
                Files.copy(file, dest, option);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void delete(Path file) {
        try {
            if (Files.isDirectory(file)) {
                for (File subFile : file.toFile().listFiles()) {
                    delete(subFile.toPath());
                }
            }

            Files.delete(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}