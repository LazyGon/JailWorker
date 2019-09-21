package fr.alienationgaming.jailworker.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import fr.alienationgaming.jailworker.JailWorker;

public class Free implements CommandExecutor {

    JailWorker plugin;

    public Free(JailWorker jailworker) {
        plugin = jailworker;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Player target = null;
        boolean prisonnerOnline = true;
        boolean prisonnerFound = true;
        String reason = "";
        if (sender instanceof ConsoleCommandSender || plugin.hasPerm(((Player) sender), "jailworker.jw-admin") || (plugin.hasPerm(((Player) sender), "jailworker.jw-free"))) {
            if (args.length == 0)
                return false;
            target = plugin.getServer().getPlayer(args[0]);
            // Player offline
            if (target == null) {
                sender.sendMessage(plugin.toLanguage("error-command-playeroffline", args[0]));
                prisonnerOnline = false;
            }
            //player not on jail
            if (!plugin.getJailConfig().contains("Prisoners." + args[0])) {
                sender.sendMessage(plugin.toLanguage("error-command-missingonjail", args[0]));
                prisonnerFound = false;
            } else {
                prisonnerFound = true;
            }
            if (prisonnerFound && !prisonnerOnline) {
                sender.sendMessage(plugin.toLanguage("error-command-actionofflineplayer"));
                return true;
            } else if (prisonnerFound && prisonnerOnline) {
                String jailName = plugin.getJailConfig().getString("Prisoners." + target.getName() + ".Prison");
                if (sender instanceof ConsoleCommandSender || plugin.getJailConfig().getStringList("Jails." + jailName + ".Owners").contains(((Player) sender).getName())) {
                    plugin.interactWithPlayer.freePlayer(target);
                    if (args.length > 1)
                        for (int i = 1; i < args.length; ++i) {
                            reason += args[i];
                            reason += " ";
                        }
                    else
                        reason = "No Reason.";
                    target.sendMessage(plugin.toLanguage("info-command-playerfree", sender.getName()));
                    if (!reason.equals("No Reason."))
                        target.sendMessage(plugin.toLanguage("info-command-displayreason", reason));
                } else {
                    sender.sendMessage(plugin.toLanguage("error-command-notowner"));
                }
            }
        }
        return true;
    }

}
