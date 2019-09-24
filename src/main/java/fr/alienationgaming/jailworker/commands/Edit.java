package fr.alienationgaming.jailworker.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import fr.alienationgaming.jailworker.Jail;
import fr.alienationgaming.jailworker.listner.JWConfigJailListener;

public class Edit extends JWSubCommand {

    Edit() {
    }

    @Override
    boolean runCommand(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return false;
        }
        String jailName = args[1];

        if (!Jail.exist(jailName)) {
            sender.sendMessage(plugin.toLanguage("error-command-jailnotexist", jailName));
            return false;
        }

        if (!isAdminOrOwner(sender, jailName)) {
            sender.sendMessage(plugin.toLanguage("error-command-notowner"));
            return false;
        }

        new JWConfigJailListener(plugin, jailName, sender);
        sender.sendMessage(plugin.toLanguage("help-command-config-instru1"));
        sender.sendMessage(plugin.toLanguage("help-command-config-example1"));
        return true;
    }

    @Override
    List<String> runTabComplete(CommandSender sender, String[] args) {
        List<String> result = new ArrayList<>();
        List<String> jails = new ArrayList<>(plugin.getJailConfig().getConfigurationSection("Jails").getKeys(false));
        jails.removeIf(jail -> !isAdminOrOwner(sender, jail));
        if (args.length == 2) {
            return StringUtil.copyPartialMatches(args[1], jails, result);
        }

        return result;
    }

    @Override
    String getPermissionNode() {
        return "jailworker.edit";
    }

    @Override
    String getDescription() {
        return "start to edit config of your jail.";
    }

    @Override
    String getUsage() {
        // TODO Auto-generated method stub
        return "/jailworker edit <jail>";
    }
}
