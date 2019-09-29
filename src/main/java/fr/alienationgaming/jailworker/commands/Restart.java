package fr.alienationgaming.jailworker.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import fr.alienationgaming.jailworker.JailSystem;
import fr.alienationgaming.jailworker.config.JailConfig;
import fr.alienationgaming.jailworker.config.Messages;


public class Restart extends SubCommand {

    Restart() {
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

        if (JailSystem.isRunning(jailName)) {
            JailSystem.removeTask(jailName);
        }

        JailSystem task = JailSystem.getTask(jailName);
        if (task == null) {
            Messages.sendMessage(sender, "command.restart.error.failure", Map.of("%jail-name%", jailName));
            return false;
        }

        task.start();
        Messages.sendMessage(sender, "command.restart.info.success", Map.of("%jail-name%", jailName));

        return true;
    }

    @Override
    List<String> runTabComplete(CommandSender sender, String[] args) {
        List<String> result = new ArrayList<>();
        List<String> jails = JailConfig.getJails();
        jails.removeIf(jail -> !hasPermission(sender, jail));
        if (args.length == 2) {
            return StringUtil.copyPartialMatches(args[1], jails, result);
        }

        return result;
    }

    @Override
    String getPermissionNode() {
        return "jailworker.command.restart.<jail-name>";
    }

    @Override
    String getDescription() {
        return "restart a jail (simple stop and start for lazy).";
    }

    @Override
    String getUsage() {
        return "/jailworker restart <jail-name>";
    }
}
