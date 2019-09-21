package fr.alienationgaming.jailworker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public class UpdateFiles {
    JailWorker plugin;

    public UpdateFiles(JailWorker jailworker) {
        plugin = jailworker;
    }

    @SuppressWarnings("serial")
    public void setUpdate() {
        String version = null, oldversion = null;
        plugin.reloadConfig();
        if (plugin.getJailConfig().contains("Version"))
            oldversion = plugin.getJailConfig().getString("Version");
        else if (plugin.getConfig().contains("Plugin.Version"))
            oldversion = plugin.getConfig().getString("Plugin.Version");
        else
            oldversion = "0.9";
        version = plugin.getDescription().getVersion();
        /* Verif update */
        if (version.compareTo(oldversion) > 0) {
            plugin.getLogger().log(Level.INFO, "Updating...");
            /* 0.9 to 1.0.0 */
            if (oldversion.equals("0.9")) {
                plugin.restartConfigFile();
                Set<String> s = plugin.getJailConfig().getConfigurationSection("Jails").getKeys(false);
                Iterator<String> it = s.iterator();
                while (it.hasNext()) {
                    String elem = (String) it.next();
                    plugin.getJailConfig().set("Jails." + elem + ".Type", plugin.getConfig().getString("Jails.Type"));
                }
                plugin.getJailConfig().set("Version", "1.0.0");
                plugin.saveJailConfig();
                plugin.reloadJailConfig();
                oldversion = plugin.getJailConfig().getString("Version");
                plugin.getLogger().log(Level.INFO, "Updated to 1.0.0.");
            }
            /* 1.0.0 to 1.1.0 */
            if (oldversion.equals("1.0.0")) {
                if (!plugin.getConfig().contains("Plugin.Whitelisted-Commands./jw-free")) {
                    List<String> listOfCmds = plugin.getConfig().getStringList("Plugin.Whitelisted-Commands");
                    listOfCmds.add("/jw-free");
                    plugin.getConfig().set("Plugin.Whitelisted-Commands", listOfCmds);
                }
                plugin.getJailConfig().set("Version", "1.1.0");
                plugin.saveJailConfig();
                plugin.reloadJailConfig();
                oldversion = plugin.getJailConfig().getString("Version");
                plugin.getLogger().log(Level.INFO, "Updated to 1.1.0.");
            }
            if (oldversion.equals("1.1.0")) {
                plugin.restartConfigFile();
                plugin.getJailConfig().set("Version", "1.2.0");
                plugin.saveJailConfig();
                plugin.reloadJailConfig();
                oldversion = plugin.getJailConfig().getString("Version");
                plugin.getLogger().log(Level.INFO, "Updated to 1.2.0.");
                plugin.getLogger().log(Level.WARNING, "/!\\ config.yml reset, please take time to see it and configure again.");
            }
            if (oldversion.equals("1.2.0")) {
                plugin.restartEnFile();
                plugin.restartFrFile();
                plugin.getJailConfig().set("Version", "1.2.1");
//				plugin.saveLangConfig();
                plugin.reloadLangConfig();
                oldversion = plugin.getJailConfig().getString("Version");
                plugin.getLogger().log(Level.INFO, "Updated to 1.2.1.");
            }
            if (oldversion.equals("1.2.1")) {
                plugin.restartEnFile();
                plugin.restartFrFile();
                plugin.getJailConfig().set("Version", "1.2.2");
//				plugin.saveLangConfig();
                plugin.reloadLangConfig();
                oldversion = plugin.getJailConfig().getString("Version");
                plugin.getLogger().log(Level.INFO, "Updated to 1.2.2.");
            }
            if (oldversion.equals("1.2.2")) {
                plugin.restartEnFile();
                plugin.restartFrFile();
                plugin.getConfig().set("Plugin.Version", "2.0.0");
//				plugin.saveLangConfig();
                plugin.reloadLangConfig();
                Set<String> s = plugin.getJailConfig().getConfigurationSection("Jails").getKeys(false);
                Iterator<String> it = s.iterator();
                while (it.hasNext()) {
                    String jail = (String) it.next();
                    final String owner = plugin.getJailConfig().getString("Jails." + jail + ".Creator");
                    plugin.getJailConfig().set("Jails." + jail + ".Owners", new ArrayList<String>() {{
                        add(owner);
                    }});
                    plugin.getJailConfig().set("Jails." + jail + ".Creator", null);
                }
                plugin.getConfig().set("Plugin.Language", "en");
                plugin.restartEnFile();
                plugin.restartFrFile();
                oldversion = plugin.getJailConfig().getString("Version");
                plugin.getJailConfig().set("Version", null);
                plugin.getLogger().log(Level.INFO, "Updated to 2.0.0.");
            }
            plugin.getLogger().log(Level.INFO, "Go to http://dev.bukkit.org/bukkit-plugins/jail-worker/ for news and report bugs");
        }
    }
}
