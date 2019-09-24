package fr.alienationgaming.jailworker.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import fr.alienationgaming.jailworker.Jail;
import fr.alienationgaming.jailworker.listner.JWConfigJailListener;

public class ConfigCmd extends JWSubCommand {

    ConfigCmd() {
    }

    @Override
    boolean runCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return false;
        }
        String jailName = args[0];

        if (!Jail.exist(jailName)) {
            sender.sendMessage(plugin.toLanguage("error-command-jailnotexist", jailName));
            return false;
        }

        if (!isAdminOrOwner(sender, jailName)) {
            return false;
        }

        new JWConfigJailListener(plugin, args[0], sender);
        sender.sendMessage(plugin.toLanguage("help-command-config-instru1"));
        sender.sendMessage(plugin.toLanguage("help-command-config-example1"));
        return true;
    }

    @Override
    List<String> runTabComplete(CommandSender sender, String[] args) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    String getPermissionNode() {
        return "jailworker.config";
    }

    @Override
    String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }
}
