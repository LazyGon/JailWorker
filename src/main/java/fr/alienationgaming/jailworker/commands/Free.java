package fr.alienationgaming.jailworker.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import fr.alienationgaming.jailworker.config.Messages;
import fr.alienationgaming.jailworker.config.Prisoners;

public class Free extends SubCommand {

    Free() {
    }

    @Override
    boolean runCommand(CommandSender sender, String[] args) {

        if (!hasPermission(sender)) {
            Messages.sendMessage(sender, "command.general.error.no-permission");
            return false;
        }

        if (args.length == 1) {
            Messages.sendMessage(sender, "command.general.error.not-enough-arguments");
            return false;
        }

        @SuppressWarnings("deprecation")
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            Messages.sendMessage(sender, "command.general.error.player-is-offline", Map.of("%player%", args[1]));
            return false;
        }

        if (!Prisoners.isJailed(target)) {
            Messages.sendMessage(sender, "command.general.error.player-is-not-jailed", Map.of("%player%", args[1]));
            return false;
        }

        String jailName = Prisoners.getJailPlayerIsIn(target);
        Messages.sendMessage(sender, "command.free.info.free-player", Map.of("%player%", target.getName(), "%jail-name%", jailName));
        Messages.sendMessage(target, "command.free.info.you-are-now-free", Map.of("%sender%", sender.getName(), "%jail-name%", jailName));
        Prisoners.freePlayer(target);

        if (args.length > 2) {
            StringBuilder reasonBuilder = new StringBuilder();
            for (int i = 2; i < args.length; ++i) {
                reasonBuilder.append(args[i]).append(" ");
            }
            String reason = ChatColor.translateAlternateColorCodes('&', reasonBuilder.toString());
            Messages.sendMessage(target, "command.free.info.display-reason", Map.of("%reason%", reason));
        }

        return true;
    }

    @Override
    List<String> runTabComplete(CommandSender sender, String[] args) {
        List<String> result = new ArrayList<>();
        List<String> prisoners = Prisoners.getPrisoners().stream()
                .map(OfflinePlayer::getName).filter(Objects::nonNull).collect(Collectors.toList());
        
        if (args.length == 2) {
            return StringUtil.copyPartialMatches(args[1], prisoners, result);
        }

        if (args.length == 3) {
            return StringUtil.copyPartialMatches(args[2], List.of("[reason]"), result);
        }
        
        return result;
    }

    @Override
    String getPermissionNode() {
        return "jailworker.command.free";
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
