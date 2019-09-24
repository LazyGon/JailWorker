package fr.alienationgaming.jailworker.commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.alienationgaming.jailworker.listner.JWSelectPrisonerSpawn;

public class SetSpawn extends JWSubCommand {

    SetSpawn() {
    }

    @Override
    boolean runCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("error-command-notconsole");
            return true;
        }
        Player player = (Player) sender;

        plugin.JailPrisonerSpawn.put(player, null);
        /* Listener */
        new JWSelectPrisonerSpawn(plugin, player);
        player.sendMessage(plugin.toLanguage("info-command-definespawnblk"));
        
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
        return "set the prisoner spawn on jail.";
    }

    @Override
    String getUsage() {
        return "/jailworker setspawn";
    }
}
