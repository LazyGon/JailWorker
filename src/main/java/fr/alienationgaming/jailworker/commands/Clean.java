package fr.alienationgaming.jailworker.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import fr.alienationgaming.jailworker.JailSystem;
import fr.alienationgaming.jailworker.config.JailConfig;
import fr.alienationgaming.jailworker.config.Messages;

public class Clean extends SubCommand {

    Clean() {
    }

    @Override
    boolean runCommand(CommandSender sender, String[] args) {
        if (args.length == 1) {
            Messages.sendMessage(sender, "command.general.error.not-enough-arguments");
            return false;
        }

        String jailName = args[1];

        if (!hasPermission(sender, jailName)) {
            Messages.sendMessage(sender, "command.general.error.no-permission");
            return false;
        }

        if (!JailConfig.exist(jailName)) {
            Messages.sendMessage(sender, "command.general.error.jail-does-not-exist", Map.of("%jail-name%", jailName));
            return false;
        }

        if (!JailSystem.isRunning(jailName)) {
            Messages.sendMessage(sender, "command.general.error.jail-is-not-running", Map.of("%jail-name%", jailName));
            return false;
        }

        int count = JailSystem.getTask(jailName).clearPunishmentBlocks();
        Messages.sendMessage(sender, "command.clean.info.blocks-deleted", Map.of("%jail-name%", jailName, "%deleted-count%", count));
        return true;
    }

    @Override
    List<String> runTabComplete(CommandSender sender, String[] args) {
        List<String> result = new ArrayList<>();
        List<String> jails = JailConfig.getJails();
        jails.removeIf(jail -> !hasPermission(sender, jail));
        jails.removeIf(jail -> !JailSystem.isRunning(jail));
        
        if (args.length == 2) {
            return StringUtil.copyPartialMatches(args[1], jails, result);
        }

        return result;
    }

    @Override
    String getPermissionNode() {
        return "jailworker.command.clean.<jail-name>";
    }

    @Override
    String getDescription() {
        return "delete all punishment blocks on jail.";
    }

    @Override
    String getUsage() {
        return "/jailworker clean <jail-name>";
    }
}
