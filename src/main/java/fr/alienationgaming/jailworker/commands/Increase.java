package fr.alienationgaming.jailworker.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import fr.alienationgaming.jailworker.JailWorker;

public class Increase implements CommandExecutor {

	JailWorker plugin;

	public Increase(JailWorker jailworker){
		plugin = jailworker;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		Player target = null;
		String reason = "";
		if (sender instanceof ConsoleCommandSender || plugin.hasPerm(((Player)sender), "jailworker.jw-admin") || plugin.hasPerm(((Player)sender), "jailworker.jw-increase")){
			if (args.length < 2)
				return false;
			target = plugin.getServer().getPlayer(args[0]);
			String jailName = plugin.getJailConfig().getString("Prisoners." + target.getName() + ".Prison");
			//player not on jail
			if (!plugin.getJailConfig().contains("Prisoners." + args[0])) {
				sender.sendMessage(plugin.toLanguage("error-command-missingonjail", args[0]));
				return true;
			}
			if (sender instanceof ConsoleCommandSender || plugin.getJailConfig().getStringList("Jails." + jailName + ".Owners").contains(((Player)sender).getName())) {
				/* GetNbr */
				int add = 0;
				try{
					add = Integer.parseInt(args[1]);
				}
				catch (Exception e){
					sender.sendMessage(plugin.toLanguage("error-command-invalidnumber"));
					return false;
				}
				/* Increment punishement */
				int newVal = plugin.getJailConfig().getInt("Prisoners." + args[0] + ".RemainingBlocks") + add;
				plugin.getJailConfig().set("Prisoners." + args[0] + ".RemainingBlocks", newVal);
				if (args.length > 2)
					for (int i = 2; i < args.length; ++i){
						reason += args[i];
						reason += " ";
					}
				else
					reason = "No Reason.";
				if (target != null){
					target.sendMessage(plugin.toLanguage("info-command-increasement", sender.getName(), add));
					if (!reason.equals("No Reason"))
						target.sendMessage(plugin.toLanguage("info-command-displayreason", reason));
				}
				plugin.saveJailConfig();
				plugin.reloadJailConfig();
				sender.sendMessage(plugin.toLanguage("info-command-increasesuccess", add, args[0]));
			}
		}else {
			sender.sendMessage(plugin.toLanguage("error-command-notowner"));
		}
		return true;
	}

}