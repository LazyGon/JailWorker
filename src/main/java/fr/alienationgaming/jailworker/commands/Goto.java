package fr.alienationgaming.jailworker.commands;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import fr.alienationgaming.jailworker.JailWorker;

public class Goto implements CommandExecutor {

	JailWorker plugin;
	
	public Goto(JailWorker jailworker){
		plugin = jailworker;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if (!(sender instanceof Player)) {
			sender.sendMessage(plugin.toLanguage("error-command-notconsole"));
			return true;
		}
		Player player = (Player) sender;
		Vector dest = null;
		World world = null;
		boolean tpworks = false;
		if (args.length == 0){
			return false;
		}
		String jailName = args[0];
		if (plugin.hasPerm(player, "jailworker.jw-admin") || plugin.hasPerm(player, "jailworker.jw-goto")){
			if (!plugin.getJailConfig().contains("Jails." + jailName)){
				player.sendMessage(plugin.toLanguage("error-command-jailnotexist", jailName));
				return true;
			}
			dest = plugin.getJailConfig().getVector("Jails." + jailName + ".Location.PrisonerSpawn");
			world = plugin.getServer().getWorld(plugin.getJailConfig().getString("Jails." + jailName + ".World"));
			tpworks = player.teleport(dest.toLocation(world));
			if (tpworks){
				player.sendMessage(plugin.toLanguage("info-command-gotowelcome", jailName));
				player.sendMessage(plugin.toLanguage("help-command-gotoback"));
			} else
				player.sendMessage(plugin.toLanguage("error-command-wronglocation"));
			
		}else if (!plugin.getJailConfig().getStringList("Jails." + jailName + ".Owners").contains(player.getName())) {
			player.sendMessage(plugin.toLanguage("error-command-notowner"));
		}
		return true;
	}
}
