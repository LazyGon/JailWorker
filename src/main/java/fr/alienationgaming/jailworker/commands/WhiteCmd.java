package fr.alienationgaming.jailworker.commands;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import fr.alienationgaming.jailworker.JailWorker;

public class WhiteCmd implements CommandExecutor {
	
	JailWorker plugin;
	public WhiteCmd(JailWorker jailworker){
		plugin = jailworker;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if (args.length > 1) {
			String arg = args[0];
			if (sender instanceof ConsoleCommandSender || plugin.hasPerm(((Player)sender), "jailworker.jw-admin") || (plugin.hasPerm(((Player)sender), "jailworker.jw-whitecmd"))){
				if (arg.equalsIgnoreCase("add") && args.length >= 2) {
					return addWhiteCmd(args, sender);
				}else if (arg.equalsIgnoreCase("remove") || arg.equalsIgnoreCase("rem") || arg.equalsIgnoreCase("delete") && args.length >= 2) {
					return removeWhiteCmd(args, sender);
				}else if (arg.equalsIgnoreCase("list") && args.length >= 1) {
					return listWhiteCmd(args[1], sender);
				}
			}
		}
		return false;
	}

	public boolean addWhiteCmd(String[] args, CommandSender sender) {
		List<String> cmds = plugin.getConfig().getStringList("Plugin.Whitelisted-Commands");
		for (int i = 1; i < args.length; i++) {
			String cmd = args[i];
			if (cmds.contains(cmd)) {
				sender.sendMessage(plugin.toLanguage("info-command-whitecmdalreadyexist", cmd));
			}else {
				cmds.add(cmd);
				sender.sendMessage(plugin.toLanguage("info-command-cmdadded", cmd));
			}
		}
		Collections.sort(cmds);
		plugin.getConfig().set("Plugin.Whitelisted-Commands", cmds);
		plugin.saveConfig();
		plugin.reloadConfig();
		sender.sendMessage(plugin.toLanguage("info-command-whitecmdslistsaved"));
		return true;
	}

	public boolean removeWhiteCmd(String[] args, CommandSender sender) {
		List<String> cmds = plugin.getConfig().getStringList("Plugin.Whitelisted-Commands");
		for (int i = 1; i < args.length; i++) {
			String cmd = args[i];
			if (cmds.contains(cmd)) {
				cmds.remove(cmd);
				sender.sendMessage(plugin.toLanguage("info-command-whitecmddeleted", cmd));
			}else {
				sender.sendMessage(plugin.toLanguage("info-command-whitecmdnotfound", cmd));
			}
		}
		plugin.getConfig().set("Plugin.Whitelisted-Commands", cmds);
		plugin.saveConfig();
		plugin.reloadConfig();
		sender.sendMessage(plugin.toLanguage("info-command-whitecmdslistsaved"));
		return true;
	}

	public boolean listWhiteCmd(String jail, CommandSender sender) {
		List<String> owners = plugin.getJailConfig().getStringList("Jails." + jail + ".Owners");
		sender.sendMessage(plugin.toLanguage("info-command-jailownerslist", jail, owners));
		return true;
	}
}