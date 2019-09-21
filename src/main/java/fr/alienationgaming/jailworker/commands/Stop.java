package fr.alienationgaming.jailworker.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import fr.alienationgaming.jailworker.JailWorker;

public class Stop implements CommandExecutor {

	JailWorker plugin;
	public Stop(JailWorker jailWorker) {
		plugin = jailWorker;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if (args.length == 0){
			return false;
		}
		String jailName = args[0];
		
		if (sender instanceof ConsoleCommandSender || plugin.hasPerm(((Player)sender), "jailworker.jw-admin") || (plugin.hasPerm(((Player)sender), "jailworker.jw-stop") && plugin.getJailConfig().getStringList("Jails." + jailName + ".Owners").contains(((Player)sender).getName()))){
			if (!plugin.getJailConfig().contains("Jails." + jailName)){
				sender.sendMessage(plugin.toLanguage("error-command-jailnotexist", jailName));
				return true;
			}
			if (!plugin.getJailConfig().getBoolean("Jails." + jailName + ".isStarted")){
				sender.sendMessage(ChatColor.RED + "Jail is not started!");
				return true;
			}
			plugin.getServer().getScheduler().cancelTask(plugin.tasks.get(jailName));
			plugin.tasks.remove(jailName);
			plugin.getJailConfig().set("Jails." + jailName + ".isStarted", false);
			PlayerInteractEvent.getHandlerList().unregister(plugin.jwblockbreaklistener);
			plugin.saveJailConfig();
			plugin.reloadJailConfig();
			sender.sendMessage(plugin.toLanguage("info-command-jailstoped"));
		}else if (!plugin.getJailConfig().getStringList("Jails." + jailName + ".Owners").contains(((Player)sender).getName())) {
			((Player)sender).sendMessage(plugin.toLanguage("error-command-notowner"));
		}
		return true;
	}

}
