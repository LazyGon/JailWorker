package fr.alienationgaming.jailworker.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import fr.alienationgaming.jailworker.config.JailConfig;
import fr.alienationgaming.jailworker.config.Messages;
import fr.alienationgaming.jailworker.events.JailDeleteEvent;

public class Delete extends SubCommand {

    Delete() {
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
            Messages.sendMessage(sender, "command.general.error.jail-does-not-exist", Messages.placeholder("%jail-name%", jailName));
            return false;
        }

        // JailConfig#removeJail will also remove running task.
        JailDeleteEvent deleteEvent = new JailDeleteEvent(jailName);
        Bukkit.getPluginManager().callEvent(deleteEvent);
        if (deleteEvent.isCancelled()) {
            return false;
        }
        JailConfig.removeJail(jailName);

        if (!JailConfig.exist(jailName)) {
            Messages.sendMessage(sender, "command.delete.info.success", Messages.placeholder("%jail-name%", jailName));
        } else {
            Messages.sendMessage(sender, "command.delete.error.failure", Messages.placeholder("%jail-name%", jailName));
            return false;
        }

        return true;
    }

    @Override
    List<String> runTabComplete(CommandSender sender, String[] args) {
        List<String> result = new ArrayList<>();
        List<String> jails = JailConfig.getJails();
        jails.removeIf(jail -> !JailConfig.exist(jail));
        jails.removeIf(jail -> !hasPermission(sender, jail));
        if (args.length == 2) {
            return StringUtil.copyPartialMatches(args[1], jails, result);
        }

        return result;
    }

    @Override
    String getPermissionNode() {
        return "jailworker.command.delete.<jail-name>";
    }

    @Override
    String getDescription() {
        return "delete the jail.";
    }

    @Override
    String getUsage() {
        return "/jailworker delete <jail-name>";
    }
}
