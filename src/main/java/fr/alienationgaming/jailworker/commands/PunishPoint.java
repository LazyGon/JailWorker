package fr.alienationgaming.jailworker.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import fr.alienationgaming.jailworker.config.Messages;
import fr.alienationgaming.jailworker.config.Prisoners;
import fr.alienationgaming.jailworker.config.WantedPlayers;
import fr.alienationgaming.jailworker.events.PunishmentPointChangeEvent;

public class PunishPoint extends SubCommand {

    PunishPoint() {
    }

    @Override
    boolean runCommand(CommandSender sender, String[] args) {

        if (args.length < 4) {
            Messages.sendMessage(sender, "command.general.error.not-enough-arguments");
            Messages.sendMessage(sender, "command.general.info.usage", Messages.placeholder("%usage%", getUsage()));
            return false;
        }

        if (!hasPermission(sender)) {
            Messages.sendMessage(sender, "command.general.error.no-permission");
            return false;
        }

        String subCommand = args[1].toLowerCase(Locale.ROOT);

        if (subCommand.equals("add") && subCommand.equals("remove") && subCommand.equals("set")) {
            Messages.sendMessage(sender, "command.general.error.missing-argument",
                    Messages.placeholder("%missing-argument%", subCommand));
            return false;
        }

        @SuppressWarnings("deprecation")
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);
        if (!target.hasPlayedBefore() || target.getName() == null) {
            Messages.sendMessage(sender, "command.general.error.player-has-never-played", Messages.placeholder("%player%", args[2]));
            return false;
        }

        // player not on jail
        if (!Prisoners.isJailed(target) && !WantedPlayers.isWanted(target)) {
            Messages.sendMessage(sender, "command.general.error.player-is-not-jailed", Messages.placeholder("%player%", args[2]));
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
            int currentPoint = (WantedPlayers.isWanted(target)) ? WantedPlayers.getPunishmentPoint(target)
                    : Prisoners.getPunishmentPoint(target);
            newValue = (subCommand.equals("remove") ? -1 : 1) * dif + currentPoint;
        } else {
            dif = newValue - Prisoners.getPunishmentPoint(target);
        }

        PunishmentPointChangeEvent changeEvent = new PunishmentPointChangeEvent(
                target,
                (WantedPlayers.isWanted(target))
                        ? WantedPlayers.getPunishmentPoint(target)
                        : Prisoners.getPunishmentPoint(target),
                newValue);
        Bukkit.getPluginManager().callEvent(changeEvent);
        if (changeEvent.isCancelled()) {
            return false;
        }

        newValue = changeEvent.getNewPunishmentPoint();
        dif = Math.abs(newValue - changeEvent.getPreviousPunishmentPoint());

        if (dif >= 0) {
            Messages.sendMessage(sender, "command.punish-point.info.notice-increase-sender",
                    Messages.placeholder("%player%", target.getName(), "%point%", dif, "%new-point%", newValue));
        } else {
            Messages.sendMessage(sender, "command.punish-point.info.notice-decrease-sender",
                    Messages.placeholder("%player%", target.getName(), "%point%", dif, "%new-point%", newValue));
        }

        if (target.isOnline()) {
            if (dif >= 0) {
                Messages.sendMessage(target.getPlayer(), "command.punish-point.info.notice-increase-target",
                        Messages.placeholder("%sender%", sender.getName(), "%point%", dif, "%new-point%", newValue));
            } else {
                Messages.sendMessage(target.getPlayer(), "command.punish-point.info.notice-decrease-target",
                        Messages.placeholder("%sender%", sender.getName(), "%point%", dif, "%new-point%", newValue));
            }

            if (args.length > 4) {
                StringBuilder reasonBuilder = new StringBuilder();
                for (int i = 4; i < args.length; ++i) {
                    reasonBuilder.append(args[i]).append(" ");
                }
                String reason = ChatColor.translateAlternateColorCodes('&', reasonBuilder.toString());
                Messages.sendMessage(target.getPlayer(), "command.punish-point.info.display-reason",
                        Messages.placeholder("%reason%", reason));
            }
        }

        if (WantedPlayers.isWanted(target)) {
            WantedPlayers.setPunishmentPoint(target, newValue);
        } else {
            Prisoners.setPunishmentPoint(target, newValue);
        }

        return true;
    }

    @Override
    List<String> runTabComplete(CommandSender sender, String[] args) {
        List<String> result = new ArrayList<>();

        List<String> subCommands = Arrays.asList("add", "set", "remove");
        if (args.length == 2) {
            return StringUtil.copyPartialMatches(args[1], subCommands, result);
        }

        List<String> prisoners = Prisoners.getPrisoners().stream().map(OfflinePlayer::getName).filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (args.length == 3) {
            return StringUtil.copyPartialMatches(args[2], prisoners, result);
        }

        if (!prisoners.contains(args[2])) {
            return result;
        }

        if (args.length == 4) {
            return StringUtil.copyPartialMatches(args[3], Arrays.asList("1", "10", "100", "1000"), result);
        }

        if (args.length == 5) {
            return StringUtil.copyPartialMatches(args[4], Arrays.asList("[reason]"), result);
        }

        return result;
    }

    @Override
    String getPermissionNode() {
        return "jailworker.command.punish-point";
    }

    @Override
    String getDescription() {
        return "Changes punishment point of the player.";
    }

    @Override
    String getUsage() {
        return "/jailworker punishpoint <set|add|remove> <player> <point> [reason]";
    }

}