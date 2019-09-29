package fr.alienationgaming.jailworker.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import fr.alienationgaming.jailworker.config.Messages;
import fr.alienationgaming.jailworker.config.Prisoners;

public class ChangePoint extends SubCommand {

    ChangePoint() {
    }

    @Override
    boolean runCommand(CommandSender sender, String[] args) {

        if (args.length < 4) {
            Messages.sendMessage(sender, "command.general.error.not-enough-arguments");
            return false;
        }

        if (!hasPermission(sender)) {
            Messages.sendMessage(sender, "command.general.error.no-permission");
            return false;
        }

        String subCommand = args[1].toLowerCase(Locale.ROOT);

        if (subCommand.equals("add") && subCommand.equals("remove") && subCommand.equals("set")) {
            Messages.sendMessage(sender, "command.general.error.missing-argument",
                    Map.of("%missing-argument%", subCommand));
            return false;
        }

        @SuppressWarnings("deprecation")
        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) {
            Messages.sendMessage(sender, "command.general.error.player-is-offline", Map.of("%player%", args[2]));
            return false;
        }

        // player not on jail
        if (!Prisoners.isJailed(target)) {
            Messages.sendMessage(sender, "command.general.error.player-is-not-jailed", Map.of("%player%", args[2]));
            return false;
        }

        int dif;
        try {
            dif = Integer.parseInt(args[3]);
            if (dif <= 0) {
                throw new IllegalArgumentException("Number must be positive.");
            }
        } catch (NumberFormatException e) {
            Messages.sendMessage(sender, "command.general.error.invalid-number");
            return false;
        }

        int newValue = dif;
        if (!subCommand.equals("set")) {
            newValue = (subCommand.equals("remove") ? -1 : 1) * dif + Prisoners.getPunishmentPoint(target);
        } else {
            dif = newValue - Prisoners.getPunishmentPoint(target);
        }

        if (dif >= 0) {
            Messages.sendMessage(target, "command.change-point.info.notice-increase-target",
                    Map.of("%sender%", sender.getName(), "%point%", dif, "%new-point%", newValue));
            Messages.sendMessage(sender, "command.change-point.info.notice-increase-sender",
                    Map.of("%player%", target.getName(), "%point%", dif, "%new-point%", newValue));
        } else {
            Messages.sendMessage(target, "command.change-point.info.notice-decrease-target",
                    Map.of("%sender%", sender.getName(), "%point%", dif, "%new-point%", newValue));
            Messages.sendMessage(sender, "command.change-point.info.notice-decrease-sender",
                    Map.of("%player%", target.getName(), "%point%", dif, "%new-point%", newValue));
        }

        Prisoners.setPunishmentPoint(target, newValue);

        if (args.length > 4) {
            StringBuilder reasonBuilder = new StringBuilder();
            for (int i = 4; i < args.length; ++i) {
                reasonBuilder.append(args[i]).append(" ");
            }
            String reason = ChatColor.translateAlternateColorCodes('&', reasonBuilder.toString());
            Messages.sendMessage(target, "command.change-point.info.display-reason", Map.of("%reason%", reason));
        }

        return true;
    }

    @Override
    List<String> runTabComplete(CommandSender sender, String[] args) {
        List<String> result = new ArrayList<>();

        List<String> subCommands = List.of("add", "set", "remove");
        if (args.length == 2) {
            return StringUtil.copyPartialMatches(args[1], subCommands, result);
        }

        List<String> prisoners = Prisoners.getPrisoners().stream().map(OfflinePlayer::getName)
                .collect(Collectors.toList());

        if (args.length == 3) {
            return StringUtil.copyPartialMatches(args[2], prisoners, result);
        }

        if (!prisoners.contains(args[2])) {
            return result;
        }

        if (args.length == 4) {
            return StringUtil.copyPartialMatches(args[3], List.of("1", "10", "100", "1000"), result);
        }

        if (args.length == 5) {
            return StringUtil.copyPartialMatches(args[4], List.of("[reason]"), result);
        }

        return result;
    }

    @Override
    String getPermissionNode() {
        return "jailworker.command.change-point";
    }

    @Override
    String getDescription() {
        return "Changes punishment point of the player.";
    }

    @Override
    String getUsage() {
        return "/jailworker changepoint <set|add|remove> <player> <point> [reason]";
    }

}