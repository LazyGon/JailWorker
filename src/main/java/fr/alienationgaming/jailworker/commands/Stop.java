package fr.alienationgaming.jailworker.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import fr.alienationgaming.jailworker.JailSystem;
import fr.alienationgaming.jailworker.config.JailConfig;
import fr.alienationgaming.jailworker.config.Messages;


public class Stop extends SubCommand {

    Stop() {
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

        JailSystem.getTask(jailName).stop();
        Messages.sendMessage(sender, "command.stop.info.success", Map.of("%jail-name%", jailName));

        return true;
    }

    @Override
    List<String> runTabComplete(CommandSender sender, String[] args) {
        List<String> result = new ArrayList<>();
        List<String> jails = JailConfig.getJails();
        jails.removeIf(jail -> !JailSystem.isRunning(jail));
        jails.removeIf(jail -> !hasPermission(sender, jail));
        if (args.length == 2) {
            return StringUtil.copyPartialMatches(args[1], jails, result);
        }

        return result;
    }

    @Override
    String getPermissionNode() {
        return "jailworker.command.stop.<jail-name>";
    }

    @Override
    String getDescription() {
        return "stop jail system.";
    }

    @Override
    String getUsage() {
        return "/jailworker stop <jail-name>";
    }

}
