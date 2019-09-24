package fr.alienationgaming.jailworker.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

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
            sender.sendMessage(plugin.toLanguage("error-command-notowner"));
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

        if (operation.equals("list") || !jails.contains(args[2])) {
            return result;
        }

        if (args.length >= 4) {
            if (operation.equals("add")) {
                return StringUtil.copyPartialMatches(args[args.length - 1], List.of("§r<new-allowed-command-with-\"/\">"), result);
            } else {
                List<String> allowedCommands = plugin.getConfig().getStringList("Plugin.Whitelisted-Commands");
                if (args.length > 4) {
                    List<String> inputCommands = new ArrayList<>();
                    for (int i = 3; i < args.length; i++) {
                        inputCommands.add(args[i]);
                    }
                    allowedCommands.removeAll(inputCommands);
                }
                return StringUtil.copyPartialMatches(args[args.length - 1], allowedCommands, result);
            }
        }

        return result;
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
        return "/jailworker allowedcommand <add|remove|list> <jail-name> [command1] [command2]";
    }
}