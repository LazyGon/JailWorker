package fr.alienationgaming.jailworker.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import fr.alienationgaming.jailworker.JailWorker;

public class Reload implements CommandExecutor {

    JailWorker plugin;

    public Reload(JailWorker jailWorker) {
        plugin = jailWorker;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (sender instanceof ConsoleCommandSender || plugin.hasPerm(((Player) sender), "jailworker.jw-admin") || plugin.hasPerm(((Player) sender), "jailworker.jw-reload")) {
            plugin.saveConfig();
            plugin.saveJailConfig();
            plugin.reloadConfig();
            plugin.reloadJailConfig();
            plugin.reloadLangConfig();
            sender.sendMessage(plugin.toLanguage("info-command-reloadsuccess"));
        }
        return true;
    }
}