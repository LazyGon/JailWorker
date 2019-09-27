package fr.alienationgaming.jailworker.commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import fr.alienationgaming.jailworker.JailWorker;

public abstract class SubCommand {

    protected static JailWorker plugin = JailWorker.getInstance();

    SubCommand() {
    }

    /**
     * Run this subcommand.
     * 
     * @param sender CommandSender
     * @param args   Arguments
     * @return True if subcommand is successfully executed, otherwise false.
     */
    abstract boolean runCommand(CommandSender sender, String[] args);

    /**
     * Run tab complete on this command.
     * 
     * @param sender CommandSender
     * @param args   Arguments.
     * @return List of completion.
     */
    abstract List<String> runTabComplete(CommandSender sender, String[] args);

    /**
     * Gets permission node for this subcommand.
     * 
     * @return permission node
     */
    abstract String getPermissionNode();

    /**
     * Gets description for this subcommand.
     * 
     * @return description
     */
    abstract String getDescription();

    /**
     * Gets usage of this subcommand.
     * 
     * @return
     */
    abstract String getUsage();

    protected boolean isAdmin(CommandSender sender) {
        return sender instanceof ConsoleCommandSender || sender.hasPermission("jailworker.admin");
    }

    protected boolean hasPermission(CommandSender sender) {
        return isAdmin(sender) || sender.hasPermission(getPermissionNode());
    }

    protected boolean hasPermission(CommandSender sender, String jailName) {
        return isAdmin(sender)
                || sender.hasPermission(getPermissionNode().replace("<jail-name>", jailName))
                || sender.hasPermission(getPermissionNode().replace("<jail-name>", "*"));
    }
}