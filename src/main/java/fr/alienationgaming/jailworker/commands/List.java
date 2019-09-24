package fr.alienationgaming.jailworker.commands;

import java.util.Vector;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class List extends JWSubCommand {

    List() {
    }

    @Override
    boolean runCommand(CommandSender sender, String[] args) {

        Vector<String> vec = new Vector<String>();
        String[] tab;

        sender.sendMessage(plugin.toLanguage("info-command-listjail"));
        sender.sendMessage(plugin.toLanguage("info-command-colorstatus"));
        sender.sendMessage("----------------------------");
        plugin.getJailConfig().getConfigurationSection("Jails").getKeys(false).forEach(jail -> {
            if (plugin.getJailConfig().getBoolean("Jails." + jail + ".isStarted")) {
                vec.add(ChatColor.GREEN + jail);
            } else {
                vec.add(ChatColor.RED + jail);
            }
        });
        tab = vec.toArray(new String[vec.size()]);
        sender.sendMessage(tab);
        sender.sendMessage("----------------------------");

        return true;
    }

    @Override
    java.util.List<String> runTabComplete(CommandSender sender, String[] args) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    String getPermissionNode() {
        return "jailworker.list";
    }

    @Override
    String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }
}
