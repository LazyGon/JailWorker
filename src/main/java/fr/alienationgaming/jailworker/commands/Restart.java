package fr.alienationgaming.jailworker.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import fr.alienationgaming.jailworker.Jail;

public class Restart extends JWSubCommand {

    Restart() {
    }

    @Override
    boolean runCommand(CommandSender sender, String[] args) {

        String jailName = args[0];
        if (!Jail.exist(jailName)) {
            sender.sendMessage(plugin.toLanguage("error-command-jailnotexist", jailName));
            return false;
        }

        if (!isAdminOrOwner(sender, jailName)) {
            return false;
        }

        if (plugin.getJailConfig().getBoolean("Jails." + jailName + ".isStarted")) {
            /* Stopping */
            BukkitRunnable task = plugin.tasks.get(jailName);
            if (!task.isCancelled()) {
                task.cancel();
            }
            plugin.tasks.remove(jailName);
            plugin.getJailConfig().set("Jails." + jailName + ".isStarted", false);
            PlayerInteractEvent.getHandlerList().unregister(plugin.jwblockbreaklistener);
        }

        /* Restarting */
        World world = Bukkit.getWorld(plugin.getJailConfig().getString("Jails." + jailName + ".World"));
        Jail runjailsystem = new Jail(world, jailName);
        BukkitRunnable task = runjailsystem.getTask();
        plugin.tasks.put(jailName, task);
        Bukkit.getPluginManager().registerEvents(plugin.jwblockbreaklistener, plugin);
        plugin.getJailConfig().set("Jails." + jailName + ".isStarted", true);
        plugin.saveJailConfig();
        plugin.reloadJailConfig();
        sender.sendMessage(plugin.toLanguage("info-command-restarted"));

        return true;
    }

    @Override
    List<String> runTabComplete(CommandSender sender, String[] args) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    String getPermissionNode() {
        return "jailworker.clean";
    }

    @Override
    String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }
}
