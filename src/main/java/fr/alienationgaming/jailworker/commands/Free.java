package fr.alienationgaming.jailworker.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.alienationgaming.jailworker.Jail;

public class Free extends JWSubCommand {

    Free() {
    }

    @Override
    boolean runCommand(CommandSender sender, String[] args) {

        if (args.length == 1) {
            // TODO: not enough arg message.
            // sender.sendMessage(plugin.toLanguage("error-command-notenougharguments"));
            return false;
        }

        @SuppressWarnings("deprecation")
        Player target = Bukkit.getPlayer(args[1]);

        // Player offline
        if (target == null) {
            sender.sendMessage(plugin.toLanguage("error-command-playeroffline", args[1]));
            return false;
        }

        // player not on jail
        if (!Jail.isJailed(target)) {
            sender.sendMessage(plugin.toLanguage("error-command-missingonjail", args[1]));
            return false;
        }

        String jailName = plugin.getJailConfig().getString("Prisoners." + target.getName() + ".Prison");
        if (!isAdminOrOwner(sender, jailName)) {
            return false;
        }

        plugin.interactWithPlayer.freePlayer(target);
        target.sendMessage(plugin.toLanguage("info-command-playerfree", sender.getName()));

        if (args.length > 2) {
            StringBuilder reasonBuilder = new StringBuilder();
            for (int i = 2; i < args.length; ++i) {
                reasonBuilder.append(args[i]).append(" ");
            }
            String reason = ChatColor.translateAlternateColorCodes('&', reasonBuilder.toString());
            target.sendMessage(plugin.toLanguage("info-command-displayreason", reason));
        }

        return true;
    }

    @Override
    List<String> runTabComplete(CommandSender sender, String[] args) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    String getPermissionNode() {
        return "jailworker.free";
    }

    @Override
    String getDescription() {
        return "let player out of jail.";
    }

    @Override
    String getUsage() {
        return "/jailworker free <player> [reason]";
    }

}
