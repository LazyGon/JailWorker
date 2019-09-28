package fr.alienationgaming.jailworker.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import fr.alienationgaming.jailworker.config.JailConfig;
import fr.alienationgaming.jailworker.config.Messages;

public class AllowedCommand extends SubCommand {

    AllowedCommand() {
    }

    @Override
    boolean runCommand(CommandSender sender, String[] args) {
        if (args.length < 3) {
            Messages.sendMessage(sender, "command.general.error.not-enough-arguments");
            return false;
        }

        String arg = args[1].toLowerCase(Locale.ROOT);
        String jailName = args[2].toLowerCase(Locale.ROOT);

        if (!hasPermission(sender, jailName)) {
            Messages.sendMessage(sender, "command.general.error.no-permission");
            return false;
        }

        if (!JailConfig.exist(jailName)) {
            Messages.sendMessage(sender, "command.general.error.jail-does-not-exist", Map.of("%jail-name%", jailName));
            return false;
        }

        if (arg.equals("list")) {
            List<String> commands = JailConfig.getAllowedCommands(jailName);
            Messages.sendMessage(sender, "command.allowed-command.info.list-header", Map.of("%jail-name%", jailName));
            for (int i = 0; i < commands.size(); i++) {
                Messages.sendMessage(sender, "command.allowed-command.info.list-command-line", Map.of("%command%", commands.get(i)));
            }
        }

        if (args.length == 3) {
            Messages.sendMessage(sender, "command.general.error.not-enough-arguments");
            return false;
        }

        List<String> commands = new ArrayList<>();
        for (int i = 3; i < args.length; i++) {
            commands.add(args[i]);
        }

        switch (arg) {
        case "add":
            JailConfig.addAllowedCommands(jailName, commands);
            Messages.sendMessage(sender, "command.allowed-command.info.add-success");
            return true;
        case "remove":
            JailConfig.removeAllowedCommands(jailName, commands);
            Messages.sendMessage(sender, "command.allowed-command.info.remove-success");
            return true;
        default:
            Messages.sendMessage(sender, "command.general.error.missing-argument", Map.of("%missing-argument%", arg));
            return false;
        }
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

        List<String> jails = JailConfig.getJails();
        jails.removeIf(jail -> !hasPermission(sender, jail));
        jails.removeIf(jail -> !JailConfig.exist(jail));
        if (args.length == 3) {
            return StringUtil.copyPartialMatches(args[2], jails, result);
        }

        if (operation.equals("list") || !jails.contains(args[2])) {
            return result;
        }

        if (args.length >= 4) {
            if (operation.equals("add")) {
                return StringUtil.copyPartialMatches(args[args.length - 1],
                        List.of("<new-allowed-command-with-\"/\">"), result);
            } else {
                List<String> allowedCommands = JailConfig.getAllowedCommands(args[2]);
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
        return "jailworker.command.allowed-command.<jail-name>";
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