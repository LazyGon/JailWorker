package fr.alienationgaming.jailworker.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import fr.alienationgaming.jailworker.Jail;

public class Owner extends JWSubCommand {

    Owner() {
    }

    // jw owner add <jail> <player1> [player2] [player3] ...
    // jw owner remove <jail> <player1> [player2] [player3] ...
    // jw owner list <jail>
    @Override
    boolean runCommand(CommandSender sender, String[] args) {
        if (args.length < 3) {
            //TODO: not enough argument
            return false;
        }

        String arg = args[1].toLowerCase();
        String jail = args[2];
        if (!Jail.exist(jail)) {
            sender.sendMessage(plugin.toLanguage("error-command-jailnotexist", jail));
            return true;
        }

        if (!isAdminOrOwner(sender, jail)) {
            sender.sendMessage(plugin.toLanguage("error-command-notowner"));
            return false;
        }

        if (arg.equals("list")) {
            return listOwnersFromJail(sender, jail);
        }

        if (args.length == 3) {
            //TODO: not enough argument
            return false;
        }

        List<String> values = new ArrayList<>();
        for (int i = 3; i < args.length; i++) {
            values.add(args[i]);
        }

        switch (arg) {
        case "add":
            return addOwnerToJail(sender, jail, values);
        case "remove":
        case "rm":
        case "delete":
        case "del":
            return removeOwnerToJail(sender, jail, values);
        default:
            return false;
        }
    }

    public boolean addOwnerToJail(CommandSender sender, String jail, List<String> addition) {
        List<String> owners = plugin.getJailConfig().getStringList("Jails." + jail + ".Owners");
        addition.removeAll(owners);
        owners.addAll(addition);
        Collections.sort(owners);
        plugin.getJailConfig().set("Jails." + jail + ".Owners", owners);
        plugin.saveJailConfig();
        plugin.reloadJailConfig();
        sender.sendMessage(plugin.toLanguage("info-command-ownerlistsaved", jail));
        return true;
    }

    public boolean removeOwnerToJail(CommandSender sender, String jail, List<String> deletion) {
        List<String> owners = plugin.getJailConfig().getStringList("Jails." + jail + ".Owners");
        owners.removeAll(deletion);
        plugin.getJailConfig().set("Jails." + jail + ".Owners", owners);
        plugin.saveJailConfig();
        plugin.reloadJailConfig();
        sender.sendMessage(plugin.toLanguage("info-command-ownerlistsaved", jail));
        return true;
    }

    public boolean listOwnersFromJail(CommandSender sender, String jail) {
        List<String> owners = plugin.getJailConfig().getStringList("Jails." + jail + ".Owners");
        sender.sendMessage(plugin.toLanguage("info-command-jailownerslist", jail, owners));
        return true;
    }

    @Override
    List<String> runTabComplete(CommandSender sender, String[] args) {
        List<String> result = new ArrayList<>();
        List<String> operations = List.of("add", "remove", "list");

        if (args.length == 2) {
            return StringUtil.copyPartialMatches(args[1], operations, result);
        }

        String operation = args[1].toLowerCase();
        if (!operations.contains(operation)) {
            return result;
        }

        List<String> jails = new ArrayList<>(plugin.getJailConfig().getConfigurationSection("Jails").getKeys(false));
        jails.removeIf(jail -> !isAdminOrOwner(sender, jail));
        if (args.length == 3) {
            return StringUtil.copyPartialMatches(args[2], jails, result);
        }

        String jail = args[2];

        if (operation.equals("list") || !jails.contains(jail)) {
            return result;
        }

        if (args.length >= 4) {
            if (operation.equals("add")) {
                return StringUtil.copyPartialMatches(args[args.length - 1], List.of("§r<new-prisoner>"), result);
            } else {
                List<String> owners = plugin.getJailConfig().getStringList("Jails." + jail + ".Owners");
                if (args.length > 4) {
                    List<String> deletion = new ArrayList<>();
                    for (int i = 3; i < args.length; i++) {
                        deletion.add(args[i]);
                    }
                    owners.removeAll(deletion);
                }
                return StringUtil.copyPartialMatches(args[args.length - 1], owners, result);
            }
        }

        return result;
    }

    @Override
    String getPermissionNode() {
        return "jailworker.manageowners";
    }

    @Override
    String getDescription() {
        return "add, remove or list jail owners";
    }

    @Override
    String getUsage() {
        return "/jailworker owner <add|remove|list> <jail-name> [player1] [player2] ...";
    }

}