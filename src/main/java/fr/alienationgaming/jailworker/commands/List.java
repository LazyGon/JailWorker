package fr.alienationgaming.jailworker.commands;

import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import fr.alienationgaming.jailworker.JailWorker;

public class List implements CommandExecutor {

	JailWorker plugin;
	public List(JailWorker jailworker){
		plugin = jailworker;
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if (sender instanceof ConsoleCommandSender || plugin.hasPerm(((Player)sender), "jailworker.jw-admin") || plugin.hasPerm(((Player)sender), "jailworker.jw-list")){
			Set<String> s = plugin.getJailConfig().getConfigurationSection("Jails").getKeys(false);
			Iterator<String> it = s.iterator();
			Vector<String> vec = new Vector<String>();
			String tab[];
			String elem;
			
			sender.sendMessage(plugin.toLanguage("info-command-listjail"));
			sender.sendMessage(plugin.toLanguage("info-command-colorstatus"));
			sender.sendMessage("----------------------------");
			while (it.hasNext()){
				elem = (String) it.next();
				if (plugin.getJailConfig().getBoolean("Jails." + elem + ".isStarted")){
					vec.add(ChatColor.GREEN + elem);
				}
				else{
					vec.add(ChatColor.RED + elem);
				}
			}
			tab = vec.toArray(new String[vec.size()]);
			sender.sendMessage(tab);
			sender.sendMessage("----------------------------");
		}
		return true;
	}
}
