package fr.alienationgaming.jailworker.commands;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import fr.alienationgaming.jailworker.JailWorker;

public class Delete implements CommandExecutor {

    JailWorker plugin;

    public Delete(JailWorker jailworker) {
        plugin = jailworker;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (args.length == 0)
            return false;
        String jailName = args[0];

        if (sender instanceof ConsoleCommandSender || plugin.hasPerm(((Player) sender), "jailworker.jw-admin") || (plugin.hasPerm(((Player) sender), "jailworker.jw-delete") && plugin.getJailConfig().getStringList("Jails." + jailName + ".Owners").contains(((Player) sender).getName()))) {
            if (!plugin.getJailConfig().contains("Jails." + jailName)) {
                sender.sendMessage(plugin.toLanguage("error-command-jailnotexist", jailName));
                return true;
            }

            if (plugin.getJailConfig().getBoolean("Jails." + jailName + ".isStarted")) {
                plugin.getServer().getScheduler().cancelTask(plugin.tasks.get(jailName));
                PlayerInteractEvent.getHandlerList().unregister(plugin.jwblockbreaklistener);
            }
            /* Delete task */
            plugin.tasks.remove(jailName);
            Vector spawn = plugin.getJailConfig().getVector("Jails." + jailName + ".Location.PrisonerSpawn");
            World world = plugin.getServer().getWorld(plugin.getJailConfig().getString("Jails." + jailName + ".World"));

            /* Delete red block for prisoner spawn */
            Location locSp = new Location(world, spawn.getBlockX(), spawn.getBlockY() - 1, spawn.getBlockZ());
            Location locSpNei = new Location(world, spawn.getBlockX() - 1, spawn.getBlockY() - 1, spawn.getBlockZ());
            locSp.getBlock().setType(locSpNei.getBlock().getType());

            /* Delete "jailName" section */
            plugin.getJailConfig().set("Jails." + jailName, null);
            plugin.saveJailConfig();
            plugin.reloadJailConfig();
            if (!plugin.getJailConfig().contains("Jails." + jailName))
                sender.sendMessage(plugin.toLanguage("info-command-jailremovesuccess", jailName));
            else
                sender.sendMessage(plugin.toLanguage("error-command-jailremoveechec"));
        } else if (!plugin.getJailConfig().getStringList("Jails." + jailName + ".Owners").contains(((Player) sender).getName())) {
            sender.sendMessage(plugin.toLanguage("error-command-notowner"));
        }
        return true;
    }
}
