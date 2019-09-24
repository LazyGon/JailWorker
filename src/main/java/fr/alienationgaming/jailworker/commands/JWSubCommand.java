package fr.alienationgaming.jailworker.commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import fr.alienationgaming.jailworker.JailWorker;

public abstract class JWSubCommand {

    protected static JailWorker plugin = JailWorker.getInstance();

    JWSubCommand() {
    }

    /**
     * Run this subcommand.
     * 
     * @param sender CommandSender
     * @param args Arguments
     * @return True if subcommand is successfully executed, otherwise false. 
     */
    abstract boolean runCommand(CommandSender sender, String[] args);

    /**
     * Run tab complete on this command.
     * 
     * @param sender CommandSender
     * @param args Arguments.
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

    protected boolean hasPermission(CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) {
            return true;
        }
        
        return sender.hasPermission(getPermissionNode()) || sender.hasPermission("jailworker.admin");
    }

    /**
     * Check if sender has permission to use command for {@code jailName}.
     * Otherwise sender is admin, sender should have permission and be owner.
     * 
     * @param sender
     * @param jailName
     * @return True if sender is admin or jail owner otherwise false.
     */
    protected boolean isAdminOrOwner(CommandSender sender, String jailName) {
        // Admin can bypass this check.
        if (sender instanceof ConsoleCommandSender || sender.hasPermission("jailworker.admin")) {
            return true;
        }

        // Owner check.
        if (plugin.getJailConfig().getStringList("Jails." + jailName + ".Owners").contains(sender.getName())) {
            return true;
        }

        return false;
    }
}