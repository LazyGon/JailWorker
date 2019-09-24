package fr.alienationgaming.jailworker.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

public class Reload extends JWSubCommand {

    Reload() {
    }

    @Override
    boolean runCommand(CommandSender sender, String[] args) {

        plugin.saveConfig();
        plugin.saveJailConfig();
        plugin.reloadConfig();
        plugin.reloadJailConfig();
        plugin.reloadLangConfig();
        sender.sendMessage(plugin.toLanguage("info-command-reloadsuccess"));

        return true;
    }

    @Override
    List<String> runTabComplete(CommandSender sender, String[] args) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    String getPermissionNode() {
        return "jailworker.reload";
    }

    @Override
    String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }
}