package fr.alienationgaming.jailworker.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import fr.alienationgaming.jailworker.JailSystem;
import fr.alienationgaming.jailworker.config.JailConfig;
import fr.alienationgaming.jailworker.config.Messages;


public class Start extends SubCommand {

    Start() {
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
            Messages.sendMessage(sender, "command.start.error.jail-is-already-running", Map.of("%jail-name%", jailName));
            return false;
        }

        JailSystem task = JailSystem.getTask(jailName);
        if (task == null) {
            Messages.sendMessage(sender, "command.start.error.faulure", Map.of("%jail-name%", jailName));        
            return false;
        }
        
        task.start();
        Messages.sendMessage(sender, "command.start.info.success", Map.of("%jail-name%", jailName));
        return true;
    }

    @Override
    List<String> runTabComplete(CommandSender sender, String[] args) {
        List<String> result = new ArrayList<>();
        List<String> jails = JailConfig.getJails();
        jails.removeIf(jail -> JailSystem.isRunning(jail));
        jails.removeIf(jail -> !hasPermission(sender, jail));
        if (args.length == 2) {
            return StringUtil.copyPartialMatches(args[1], jails, result);
        }

        return result;
    }

    @Override
    String getPermissionNode() {
        return "jailworker.command.start.<jail-name>";
    }

    @Override
    String getDescription() {
        return "start jail system.";
    }

    @Override
    String getUsage() {
        return "/jailworker start <jail-name>";
    }
}
