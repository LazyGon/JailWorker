package fr.alienationgaming.jailworker.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import fr.alienationgaming.jailworker.Jail;

public class Goto extends JWSubCommand {

    Goto() {
    }

    @Override
    boolean runCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.toLanguage("error-command-notconsole"));
            return false;
        }

        if (args.length == 1) {
            // TODO: not enough arg message
            return false;
        }

        String jailName = args[1];

        if (!Jail.exist(jailName)) {
            sender.sendMessage(plugin.toLanguage("error-command-jailnotexist", jailName));
            return true;
        }

        if (!isAdminOrOwner(sender, jailName)) {
            return false;
        }

        Vector dest = plugin.getJailConfig().getVector("Jails." + jailName + ".Location.PrisonerSpawn");
        World world = Bukkit.getWorld(plugin.getJailConfig().getString("Jails." + jailName + ".World"));
        
        if (((Player) sender).teleport(dest.toLocation(world))) {
            sender.sendMessage(plugin.toLanguage("info-command-gotowelcome", jailName));
            sender.sendMessage(plugin.toLanguage("help-command-gotoback"));
        } else {
            sender.sendMessage(plugin.toLanguage("error-command-wronglocation"));
            return false;
        }

        return true;
    }

    @Override
    List<String> runTabComplete(CommandSender sender, String[] args) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    String getPermissionNode() {
        return "jailworker.goto";
    }

    @Override
    String getDescription() {
        return "teleport to the jail. Usefull when it's far.";
    }

    @Override
    String getUsage() {
        return "/jailworker goto <jail-name>";
    }
}
