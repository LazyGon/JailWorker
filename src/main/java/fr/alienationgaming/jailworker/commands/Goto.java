package fr.alienationgaming.jailworker.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import fr.alienationgaming.jailworker.config.JailConfig;
import fr.alienationgaming.jailworker.config.Messages;

public class Goto extends SubCommand {

    Goto() {
    }

    @Override
    boolean runCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            Messages.sendMessage(sender, "command.general.error.only-player");
            return false;
        }

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

        ((Player) sender).teleport(JailConfig.getSpawnLocation(jailName));
        Messages.sendMessage(sender, "command.goto.info.welcome", Map.of("%jail-name%", jailName));
        Messages.sendMessage(sender, "command.goto.info.to-leave-jail-tips");

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
        return "jailworker.command.goto.<jail-name>";
    }

    @Override
    String getDescription() {
        return "teleport to the jail. Usefull when it's far.";
    }

    @Override
    String getUsage() {
        return "/jailworker goto <jail-name>";
    }
}
