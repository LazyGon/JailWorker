package fr.alienationgaming.jailworker.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import fr.alienationgaming.jailworker.JailSystem;
import fr.alienationgaming.jailworker.config.Config;
import fr.alienationgaming.jailworker.config.JailConfig;
import fr.alienationgaming.jailworker.config.Messages;
import fr.alienationgaming.jailworker.config.Prisoners;

public class Reload extends SubCommand {

    Reload() {
    }

    @Override
    boolean runCommand(CommandSender sender, String[] args) {
        if (args.length == 1) {
            Config.reload();
            JailConfig.reload();
            Prisoners.reload();
            Messages.reload();
        } else {
            switch (args[1].toLowerCase(Locale.ROOT)) {
            case "config":
                Config.reload();
                break;
            case "jailconfig":
                JailConfig.reload();
                JailSystem.stopAllJails();
                JailSystem.runAllJails();
                break;
            case "prisoners":
                Prisoners.reload();
                break;
            case "messages":
                Messages.reload();
                break;
            }
        }

        Messages.sendMessage(sender, "command.reload.info.success");
        return true;
    }

    @Override
    List<String> runTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return StringUtil.copyPartialMatches(args[1], List.of("config", "jailconfig", "prisoners", "messages"), new ArrayList<>());
        } else {
            return List.of();
        }
    }

    @Override
    String getPermissionNode() {
        return "jailworker.command.reload";
    }

    @Override
    String getDescription() {
        return "reload all configuration files.";
    }

    @Override
    String getUsage() {
        return "/jailworker reload";
    }
}