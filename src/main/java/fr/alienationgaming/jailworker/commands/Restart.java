package fr.alienationgaming.jailworker.commands;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import fr.alienationgaming.jailworker.Jail;
import fr.alienationgaming.jailworker.JailWorker;

public class Restart implements CommandExecutor {

	JailWorker plugin;
	public Restart(JailWorker jailworker){
		plugin = jailworker;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if (args.length == 0)
			return false;
		String jailName = args[0];
		
		if (sender instanceof ConsoleCommandSender || plugin.hasPerm(((Player)sender), "jailworker.jw-admin") || (plugin.hasPerm(((Player)sender), "jailworker.jw-clean") && plugin.getJailConfig().getStringList("Jails." + jailName + ".Owners").contains(((Player)sender).getName()))){
			if (!plugin.getJailConfig().contains("Jails." + jailName)){
				sender.sendMessage(plugin.toLanguage("error-command-jailnotexist", jailName));
				return true;
			}
			if (plugin.getJailConfig().getBoolean("Jails." + jailName + ".isStarted")){
				/* Stopping */
				plugin.getServer().getScheduler().cancelTask(plugin.tasks.get(jailName));
				plugin.getJailConfig().set("Jails." + jailName + ".isStarted", false);
				PlayerInteractEvent.getHandlerList().unregister(plugin.jwblockbreaklistener);
			}

			/* Restarting*/
			World world = plugin.getServer().getWorld(plugin.getJailConfig().getString("Jails." + jailName + ".World"));
			Jail runjailsystem = new Jail(plugin, world, jailName);
			int task = runjailsystem.getTaskId();
			plugin.tasks.put(jailName, task);
			plugin.getServer().getPluginManager().registerEvents(plugin.jwblockbreaklistener, plugin);
			plugin.getJailConfig().set("Jails." + jailName + ".isStarted", true);
			plugin.saveJailConfig();
			plugin.reloadJailConfig();
			sender.sendMessage(plugin.toLanguage("info-command-restarted"));
		}else if (!plugin.getJailConfig().getStringList("Jails." + jailName + ".Owners").contains(((Player)sender).getName())) {
			((Player)sender).sendMessage(plugin.toLanguage("error-command-notowner"));
		}
		return true;
	}
}

