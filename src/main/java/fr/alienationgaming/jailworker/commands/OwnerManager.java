package fr.alienationgaming.jailworker.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;

import fr.alienationgaming.jailworker.Jail;

public class OwnerManager extends JWSubCommand {

    OwnerManager() {
    }

    // jw owner add <jail> <player1> [player2] [player3] ...
    // jw owner remove <jail> <player1> [player2] [player3] ...
    // jw owner list <jail>
    @Override
    boolean runCommand(CommandSender sender, String[] args) {
        if (args.length < 3) {
            return false;
        }

        String arg = args[1].toLowerCase();
        String jail = args[2];
        if (!Jail.exist(jail)) {
            sender.sendMessage(plugin.toLanguage("error-command-jailnotexist", jail));
            return true;
        }

        if (!isAdminOrOwner(sender, jail)) {
            return false;
        }

        if (arg.equals("list")) {
            return listOwnersFromJail(sender, jail);
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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    String getPermissionNode() {
        return "jailworker.manageowners";
    }

    @Override
    String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

}