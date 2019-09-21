package fr.alienationgaming.jailworker.commands;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import fr.alienationgaming.jailworker.JailWorker;

public class OwnerManager implements CommandExecutor {

    JailWorker plugin;

    public OwnerManager(JailWorker jailworker) {
        plugin = jailworker;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (args.length > 1) {
            String arg = args[0];
            String jail = args[1];
            if (!plugin.getJailConfig().contains("Jails." + jail)) {
                sender.sendMessage(plugin.toLanguage("error-command-jailnotexist", jail));
                return true;
            }
            if (sender instanceof ConsoleCommandSender || plugin.hasPerm(((Player) sender), "jailworker.jw-admin") || (plugin.hasPerm(((Player) sender), "jailworker.jw-manageowners") && plugin.getJailConfig().getStringList("Jails." + jail + ".Owners").contains(((Player) sender).getName()))) {
                if (arg.equalsIgnoreCase("add") && args.length >= 3) {
                    return addOwnerToJail(args, sender);
                } else if (arg.equalsIgnoreCase("remove") || arg.equalsIgnoreCase("rem") || arg.equalsIgnoreCase("delete") && args.length >= 3) {
                    return removeOwnerToJail(args, sender);
                } else if (arg.equalsIgnoreCase("list") && args.length >= 2) {
                    return listOwnersFromJail(args[1], sender);
                }
            } else if (!plugin.getJailConfig().getStringList("Jails." + jail + ".Owners").contains(((Player) sender).getName())) {
                ((Player) sender).sendMessage(plugin.toLanguage("error-command-notowner"));
                return true;
            }
        }
        return false;
    }

    public boolean addOwnerToJail(String[] args, CommandSender sender) {
        String jail = args[1];
        List<String> owners = plugin.getJailConfig().getStringList("Jails." + jail + ".Owners");
        for (int i = 2; i < args.length; i++) {
            String owner = args[i];
            if (owners.contains(owner)) {
                sender.sendMessage(plugin.toLanguage("info-command-owneralreadyexist", jail, owner));
            } else {
                owners.add(owner);
                sender.sendMessage(plugin.toLanguage("info-command-owneradded", owner, jail));
            }
        }
        Collections.sort(owners);
        plugin.getJailConfig().set("Jails." + jail + ".Owners", owners);
        plugin.saveJailConfig();
        plugin.reloadJailConfig();
        sender.sendMessage(plugin.toLanguage("info-command-ownerlistsaved", jail));
        return true;
    }

    public boolean removeOwnerToJail(String[] args, CommandSender sender) {
        String jail = args[1];
        List<String> owners = plugin.getJailConfig().getStringList("Jails." + jail + ".Owners");
        for (int i = 2; i < args.length; i++) {
            String owner = args[i];
            if (owners.contains(owner)) {
                owners.remove(owner);
                sender.sendMessage(plugin.toLanguage("info-command-ownerdeleted", owner, jail));
            } else {
                sender.sendMessage(plugin.toLanguage("info-command-ownernotfound", owner, jail));
            }
        }
        plugin.getJailConfig().set("Jails." + jail + ".Owners", owners);
        plugin.saveJailConfig();
        plugin.reloadJailConfig();
        sender.sendMessage(plugin.toLanguage("info-command-ownerlistsaved", jail));
        return true;
    }

    public boolean listOwnersFromJail(String jail, CommandSender sender) {
        List<String> owners = plugin.getJailConfig().getStringList("Jails." + jail + ".Owners");
        sender.sendMessage(plugin.toLanguage("info-command-jailownerslist", jail, owners));
        return true;
    }

}