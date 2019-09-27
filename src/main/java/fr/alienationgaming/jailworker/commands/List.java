package fr.alienationgaming.jailworker.commands;

import java.util.Vector;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import fr.alienationgaming.jailworker.JailSystem;
import fr.alienationgaming.jailworker.config.JailConfig;
import fr.alienationgaming.jailworker.config.Messages;

public class List extends SubCommand {

    List() {
    }

    @Override
    boolean runCommand(CommandSender sender, String[] args) {

        Vector<String> vec = new Vector<String>();
        String[] tab;

        Messages.sendMessage(sender, "command.list.info.list-header");
        Messages.sendMessage(sender, false, "command.general.info.line");
        JailConfig.getJails()
                .forEach(jail -> vec.add((JailSystem.isRunning(jail) ? ChatColor.GREEN : ChatColor.RED) + jail));
        tab = vec.toArray(new String[vec.size()]);
        sender.sendMessage(tab);
        Messages.sendMessage(sender, false, "command.general.info.line");

        return true;
    }

    @Override
    java.util.List<String> runTabComplete(CommandSender sender, String[] args) {
        return java.util.List.of();
    }

    @Override
    String getPermissionNode() {
        return "jailworker.command.list";
    }

    @Override
    String getDescription() {
        return "show jails list";
    }

    @Override
    String getUsage() {
        return "/jailworker list";
    }
}
