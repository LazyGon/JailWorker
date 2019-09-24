package fr.alienationgaming.jailworker.commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.alienationgaming.jailworker.listner.JWRegionSelectListener;

public class Create extends JWSubCommand {

    Create() {
    }

    @Override
    boolean runCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("error-command-notconsole");
            return true;
        }
        Player player = (Player) sender;

        plugin.blockJail1.put(player, null);
        plugin.blockJail2.put(player, null);
        /* Listener */
        new JWRegionSelectListener(plugin, player);

        player.sendMessage(plugin.toLanguage("info-command-definetips"));
        player.sendMessage(plugin.toLanguage("info-command-waitfirstblock"));
        return true;
    }

    @Override
    List<String> runTabComplete(CommandSender sender, String[] args) {
        return List.of();
    }

    @Override
    String getPermissionNode() {
        return "jailworker.create";
    }

    @Override
    String getDescription() {
        return "set jail region";
    }

    @Override
    String getUsage() {
        return "/jailworker create <jail-name>";
    }
}
