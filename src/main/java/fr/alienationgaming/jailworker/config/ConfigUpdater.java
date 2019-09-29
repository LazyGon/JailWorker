package fr.alienationgaming.jailworker.config;

import java.io.File;
import java.io.IOException;

import com.google.common.io.Files;

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
     * will be copied to {@code old} folder.
     */
    public static void update() {
        String version = plugin.getDescription().getVersion();
        String oldversion = Config.getConfigVersion();

        if (version.equals(oldversion)) {
            return;
        }

        if (oldversion.equals("0.9")) {
            oldversion = "0.9.0";
        }

        if (!oldversion.startsWith(String.valueOf(version.charAt(0)))) {
            File datafoler = plugin.getDataFolder();
            File oldDirectory = datafoler.toPath().resolve("old").resolve(oldversion).toFile();
            oldDirectory.mkdirs();
            try {
                for (File file : datafoler.listFiles()) {
                    File dest = oldDirectory.toPath().resolve(file.getName()).toFile();
                    Files.copy(file, dest);
                    file.delete();
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

        if (version.compareTo(oldversion) > 0) {
            // plugin.getLogger().info("Updating...");

            // if (oldversion.equals("3.0.0")) {
            // Latest.
            // }

            plugin.getLogger()
                    .info("Go to http://dev.bukkit.org/bukkit-plugins/jail-worker/ for news and reporting bugs");
        }
    }
}