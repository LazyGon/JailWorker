package fr.alienationgaming.jailworker.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import fr.alienationgaming.jailworker.JailWorker;
import fr.alienationgaming.jailworker.listner.JWConfigJailListener;

public class ConfigCmd implements CommandExecutor {

    JailWorker plugin;

    public ConfigCmd(JailWorker jailworker) {
        plugin = jailworker;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (args.length == 0) {
            return false;
        }
        String jailName = args[0];

        if (sender instanceof ConsoleCommandSender || plugin.hasPerm((Player) sender, "jailworker.jw-admin") || (plugin.hasPerm((Player) sender, "jailworker.jw-config") && plugin.getJailConfig().getStringList("Jails." + jailName + ".Owners").contains(((Player) sender).getName()))) {
            if (!plugin.getJailConfig().contains("Jails." + jailName)) {
                sender.sendMessage(plugin.toLanguage("error-command-jailnotexist", jailName));
                return true;
            }
            new JWConfigJailListener(plugin, args[0], sender);
            sender.sendMessage(plugin.toLanguage("help-command-config-instru1"));
            sender.sendMessage(plugin.toLanguage("help-command-config-example1"));
        } else if (!plugin.getJailConfig().getStringList("Jails." + jailName + ".Owners").contains(((Player) sender).getName())) {
            ((Player) sender).sendMessage(plugin.toLanguage("error-command-notowner"));
        }
        return true;
    }
}
