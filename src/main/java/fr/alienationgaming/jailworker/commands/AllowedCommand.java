package fr.alienationgaming.jailworker.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.bukkit.command.CommandSender;

import fr.alienationgaming.jailworker.Jail;

public class AllowedCommand extends JWSubCommand {

    AllowedCommand() {
    }

    @Override
    boolean runCommand(CommandSender sender, String[] args) {
        if (args.length < 3) {
            // TODO: not enough arg message
            return false;
        }
        String arg = args[1].toLowerCase(Locale.ROOT);
        String jail = args[2].toLowerCase(Locale.ROOT);

        if (!Jail.exist(jail)) {
            sender.sendMessage(plugin.toLanguage("error-command-jailnotexist", jail));
            return false;
        }

        if (isAdminOrOwner(sender, jail)) {
            return false;
        }

        if (arg.equals("list")) {
            return listAllowedCommands(sender, jail);
        }

        if (args.length == 3) {
            // TODO: not enough arg message
            return false;
        }

        List<String> commands = new ArrayList<>();
        for (int i = 3; i < args.length; i++) {
            commands.add(args[i]);
        }

        switch (arg) {
        case "add":
            return addAllowedCommands(sender, commands);
        case "remove":
        case "rem":
        case "delete":
        case "del":
            return removeAllowedCommands(sender, commands);
        default:
            return false;
        }
    }

    public boolean addAllowedCommands(CommandSender sender, List<String> addition) {
        List<String> commands = plugin.getConfig().getStringList("Plugin.Whitelisted-Commands");
        addition.removeAll(commands);
        commands.addAll(addition);
        // TODO: remove info-command-allowedcommandalreadyexist
        // TODO: remove info-command-cmdadded
        Collections.sort(commands);
        plugin.getConfig().set("Plugin.Whitelisted-Commands", commands);
        plugin.saveConfig();
        plugin.reloadConfig();
        sender.sendMessage(plugin.toLanguage("info-command-allowedcommandslistsaved"));
        return true;
    }

    public boolean removeAllowedCommands(CommandSender sender, List<String> deletion) {
        List<String> commands = plugin.getConfig().getStringList("Plugin.Whitelisted-Commands");
        commands.removeAll(deletion);
        // TODO: remove info-command-allowedcommanddeleted
        // TODO: info-command-allowedcommandnotfound
        plugin.getConfig().set("Plugin.Whitelisted-Commands", commands);
        plugin.saveConfig();
        plugin.reloadConfig();
        sender.sendMessage(plugin.toLanguage("info-command-allowedcommandslistsaved"));
        return true;
    }

    public boolean listAllowedCommands(CommandSender sender, String jail) {
        List<String> commands = plugin.getConfig().getStringList("Plugin.Whitelisted-Commands");
        // TODO: command list format
        sender.sendMessage(plugin.toLanguage("info-command-jailownerslist", jail, commands));
        return true;
    }

    @Override
    List<String> runTabComplete(CommandSender sender, String[] args) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    String getPermissionNode() {
        return "jailworker.allowedcommand";
    }

    @Override
    String getDescription() {
        return "add, delete or list allowed commands for prisoners.";
    }

    @Override
    String getUsage() {
        // TODO Auto-generated method stub
        return "/jailworker allowedcommand <add|remove|list> <jail-name> [command1] [command2]";
    }
}