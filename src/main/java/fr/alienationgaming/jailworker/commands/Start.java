package fr.alienationgaming.jailworker.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.StringUtil;

import fr.alienationgaming.jailworker.Jail;

public class Start extends JWSubCommand {

    Start() {
    }

    @Override
    boolean runCommand(CommandSender sender, String[] args) {
        if (args.length == 1) {
            // TODO: send not enough argument message
            return false;
        }
        String jailName = args[1];

        if (!Jail.exist(jailName)) {
            sender.sendMessage(plugin.toLanguage("error-command-jailnotexist", jailName));
            return true;
        }

        if (isAdminOrOwner(sender, jailName)) {
            sender.sendMessage(plugin.toLanguage("error-command-notowner"));
            return false;
        }

        if (plugin.getJailConfig().getBoolean("Jails." + jailName + ".isStarted")) {
            sender.sendMessage(plugin.toLanguage("error-command-alreadystarted"));
            return true;
        }

        World world = Bukkit.getWorld(plugin.getJailConfig().getString("Jails." + jailName + ".World"));
        Jail runjailsystem = new Jail(world, jailName);
        BukkitRunnable task = runjailsystem.getTask();
        plugin.tasks.put(jailName, task);
        Bukkit.getPluginManager().registerEvents(plugin.jwblockbreaklistener, plugin);
        plugin.getJailConfig().set("Jails." + jailName + ".isStarted", true);
        plugin.saveJailConfig();
        plugin.reloadJailConfig();
        sender.sendMessage(plugin.toLanguage("info-command-jailstarted"));

        return true;
    }

    @Override
    List<String> runTabComplete(CommandSender sender, String[] args) {
        List<String> result = new ArrayList<>();
        List<String> jails = new ArrayList<>(plugin.getJailConfig().getConfigurationSection("Jails").getKeys(false));
        jails.removeIf(jail -> !isAdminOrOwner(sender, jail));
        if (args.length == 2) {
            return StringUtil.copyPartialMatches(args[1], jails, result);
        }

        return result;
    }

    @Override
    String getPermissionNode() {
        return "jailworker.start";
    }

    @Override
    String getDescription() {
        return "start jail system.";
    }

    @Override
    String getUsage() {
        return "/jailworker start <jail-name>";
    }
}
