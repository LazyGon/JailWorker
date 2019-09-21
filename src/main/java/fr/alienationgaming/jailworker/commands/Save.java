package fr.alienationgaming.jailworker.commands;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.sk89q.worldedit.bukkit.selections.Selection;

import fr.alienationgaming.jailworker.JailWorker;

public class Save implements CommandExecutor {

	private JailWorker			plugin;
	private boolean				useWorldEditSelection = false;
	
	
	public Save(JailWorker jailworker){
		plugin = jailworker;
	}

	@SuppressWarnings("serial")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if (!(sender instanceof Player)) {
			sender.sendMessage("error-command-notconsole");
			return true;
		}
		final Player player = (Player) sender;

		if (args.length == 0){
			return false;
		}
		if (plugin.hasPerm(player, "jailworker.jw-admin") || plugin.hasPerm(player, "jailworker.jw-create")){
			Selection worldEditSelection = this.getWorldEditSelection(player);
			if (worldEditSelection == null) { //Si pas de selection worldEdit ou pas le plugin WorldEdit installé
				useWorldEditSelection = false;
				if ((plugin.blockJail1.get(player) == null  || plugin.blockJail2.get(player) == null) || plugin.JailPrisonerSpawn.get(player) == null){ // On check les 3 points obligatoires
					player.sendMessage(plugin.toLanguage("error-command-invalidarea"));
					return true;
				}
			}else if (worldEditSelection != null && plugin.JailPrisonerSpawn.get(player) == null) { // WorldEdit installé et selection OK mais pas de point de spawn
				player.sendMessage(plugin.toLanguage("error-command-invalidarea"));
				return true;
			} else{ //WorldEdit installé, selection OK et point de spawn OK
				useWorldEditSelection = true;
			}
			String jailName = args[0];
			boolean redefined = false;
			if (plugin.getJailConfig().contains("Jails." + jailName)){
				redefined = true;
				if (!plugin.getJailConfig().getStringList("Jails." + jailName + ".Owners").contains(player.getName())){
					player.sendMessage(plugin.toLanguage("error-command-alreadyexist", jailName));
					player.sendMessage(plugin.toLanguage("error-command-notpermtoredefine"));
					return true;
				}else {
					Vector locBlk1 = useWorldEditSelection ? worldEditSelection.getMinimumPoint().toVector() : plugin.blockJail1.get(player).getLocation().toVector();
					plugin.getJailConfig().set("Jails." + jailName + ".Location.Block1", locBlk1);
					Vector locBlk2 = useWorldEditSelection ? worldEditSelection.getMaximumPoint().toVector() : plugin.blockJail2.get(player).getLocation().toVector();
					plugin.getJailConfig().set("Jails." + jailName + ".Location.Block2", locBlk2);
					Vector locspawn = plugin.JailPrisonerSpawn.get(player).toVector();
					plugin.getJailConfig().set("Jails." + jailName + ".Location.PrisonerSpawn", locspawn);
				}
			} else {
				/* Setup Default values */
				Vector locBlk1 = useWorldEditSelection ? worldEditSelection.getMinimumPoint().toVector() : plugin.blockJail1.get(player).getLocation().toVector();
				plugin.getJailConfig().set("Jails." + jailName + ".Location.Block1", locBlk1);
				Vector locBlk2 = useWorldEditSelection ? worldEditSelection.getMaximumPoint().toVector() : plugin.blockJail2.get(player).getLocation().toVector();
				plugin.getJailConfig().set("Jails." + jailName + ".Location.Block2", locBlk2);
				Vector locspawn = plugin.JailPrisonerSpawn.get(player).toVector();
				plugin.getJailConfig().set("Jails." + jailName + ".Location.PrisonerSpawn", locspawn);
				
				plugin.getJailConfig().set("Jails." + jailName + ".MaxSand", plugin.getdefaultvalues.MaxSand());
				plugin.getJailConfig().set("Jails." + jailName + ".Blocks", plugin.getdefaultvalues.Blocks());
				plugin.getJailConfig().set("Jails." + jailName + ".Type", plugin.getConfig().getString("Jails.Type"));
				plugin.getJailConfig().set("Jails." + jailName + ".Speed", plugin.getdefaultvalues.Speed());
				plugin.getJailConfig().set("Jails." + jailName + ".World", player.getWorld().getName());
				plugin.getJailConfig().set("Jails." + jailName + ".Owners", new ArrayList<String>() {{ add(player.getName()); }});
				plugin.getJailConfig().set("Jails." + jailName + ".isStarted", false);

				player.sendMessage(plugin.toLanguage("info-command-configtips"));
				player.sendMessage(plugin.toLanguage("info-signconf-tips"));
				player.sendMessage(new String[] {"--------------###############--------------\n", 
												 "--------------#-------------#--------------\n", 
												 "--------------#-------------#--------------\n", 
												 "--------------#-------------#--------------\n", 
												 "--------------#-------------#--------------\n", 
												 "###########################################\n",
												 "#-------------#--[JW-CONFIG]-#-------------#\n",
												 "#-------------#--<jail-name>--#-------------#\n",
												 "#-------------#-------------#-------------#\n",
												 "#-------------#-------------#-------------#\n",
												 "###########################################\n", 
												 "--------------#-------------#--------------\n",
												 "--------------#-------------#--------------\n", 
												 "--------------#-------------#--------------\n", 
												 "--------------#-------------#--------------\n", 
												 "--------------###############--------------\n"});
			}
			/* Save and reload jails.yml */
			plugin.saveJailConfig();
			plugin.reloadJailConfig();
			if (!redefined)
				player.sendMessage(plugin.toLanguage("info-command-jailsaved", jailName));
			else
				player.sendMessage(plugin.toLanguage("info-command-jailredefined", jailName));

			/* Reset constants variables */
			plugin.blockJail1.remove(player);
			plugin.blockJail2.remove(player);
			plugin.JailPrisonerSpawn.remove(player);
		}
		return true;
	}

	private Selection getWorldEditSelection(Player player) {
		if (plugin.worldEdit != null)
			return (plugin.worldEdit.getSelection(player));
		return null;
	}
}
