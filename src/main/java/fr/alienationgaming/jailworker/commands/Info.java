package fr.alienationgaming.jailworker.commands;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import fr.alienationgaming.jailworker.JailSystem;
import fr.alienationgaming.jailworker.config.JailConfig;
import fr.alienationgaming.jailworker.config.Messages;
import fr.alienationgaming.jailworker.config.Prisoners;

public class Info extends SubCommand {

    Info() {
    }

    @Override
    boolean runCommand(CommandSender sender, String[] args) {

        if (args.length < 3) {
            Messages.sendMessage(sender, "command.general.error.not-enough-arguments");
            return false;
        }

        if (args[1].equalsIgnoreCase("jail")) {

            String jailName = args[2];
            if (!sender.hasPermission(getPermissionNode() + ".jail." + jailName)
                    && !sender.hasPermission(getPermissionNode() + ".jail.*")) {
                Messages.sendMessage(sender, "command.general.error.no-permission");
                return false;
            }

            if (!JailConfig.exist(jailName)) {
                Messages.sendMessage(sender, "command.general.error.jail-does-not-exist",
                        Map.of("%jail-name%", jailName));
                return false;
            }

            // getValues
            boolean running = JailSystem.isRunning(jailName);
            int maxPunishmentBlock = JailConfig.getMaxPunishmentBlocks(jailName);
            int punishmentBlockSpeed = JailConfig.getBlockSpeed(jailName);
            String World = JailConfig.getWorld(jailName).getName();

            Messages.sendMessage(sender, "command.info.info.jail-name", Map.of("%jail-name%", jailName));
            Messages.sendMessage(sender, false, "command.general.info.line");
            Messages.sendMessage(sender, false, "command.info.info.jail-is-" + (running ? "" : "not-") + "running");
            Messages.sendMessage(sender, false, "command.info.info.jail-max-punishment-blocks",
                    Map.of("%max-punishment-blocks%", maxPunishmentBlock));
            Messages.sendMessage(sender, false, "command.info.info.jail-block-appear-speed",
                    Map.of("%punishment-block-speed%", punishmentBlockSpeed));
            Messages.sendMessage(sender, false, "command.info.info.jail-world-name", Map.of("%world-name%", World));
            Messages.sendMessage(sender, false, "command.general.info.line");

            return true;

        } else if (args[1].equalsIgnoreCase("prisoner")) {

            if (!sender.hasPermission(getPermissionNode() + ".prisoner")) {
                Messages.sendMessage(sender, "command.general.error.no-permission");
                return false;
            }

            @SuppressWarnings("deprecation")
            OfflinePlayer prisoner = Bukkit.getOfflinePlayer(args[2]);
            if (!prisoner.hasPlayedBefore() || prisoner.getName() == null) {
                Messages.sendMessage(sender, "command.general.error.player-has-never-played",
                        Map.of("%player%", args[2]));
                return false;
            }

            if (!Prisoners.isJailed(prisoner)) {
                Messages.sendMessage(sender, "command.general.error.player-is-not-jailed", Map.of("%player%", args[2]));
                return false;
            }

            Messages.sendMessage(sender, "command.info.info.prisoner-header", Map.of("%player%", prisoner.getName()));
            Messages.sendMessage(sender, false, "command.general.info.line");
            Messages.sendMessage(sender, false, "command.info.info.prisoner-jail-name",
                    Map.of("%jail-name%", Prisoners.getJailPlayerIsIn(prisoner)));
            Messages.sendMessage(sender, false, "command.info.info.prisoner-punisher-name",
                    Map.of("%punisher%", Prisoners.getPunisher(prisoner).getName()));
            Messages.sendMessage(sender, false, "command.info.info.prisoner-cause",
                    Map.of("%reason%", Prisoners.getCause(prisoner)));

            long unixTime = Prisoners.getJailedDate(prisoner);
            LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(unixTime),
                    ZoneId.systemDefault());
            String time = localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            Messages.sendMessage(sender, false, "command.info.info.prisoner-punishment-time", Map.of("%time%", time));
            Messages.sendMessage(sender, false, "command.info.info.prisoner-punishment-blocks");
            Prisoners.getRemainingBlocks(prisoner).forEach((material, amount) -> Messages.sendMessage(sender,
                    false, "command.info.info.prisoner-punishment-blocks-format",
                    Map.of("%material%", material.name(), "%amount%", amount))
            );
            Messages.sendMessage(sender, false, "command.general.info.line");

            return true;

        } else {
            Messages.sendMessage(sender, "command.general.error.missing-argument",
                    Map.of("%missing-argument%", args[1]));
            return false;
        }
    }

    @Override
    List<String> runTabComplete(CommandSender sender, String[] args) {
        List<String> result = new ArrayList<>();
        if (args.length == 2) {
            return StringUtil.copyPartialMatches(args[1], List.of("jail", "prisoner"), result);
        }

        if (args.length == 3) {
            if (args[1].equalsIgnoreCase("jail")) {
                List<String> jails = JailConfig.getJails();
                return StringUtil.copyPartialMatches(args[2], jails, result);
            } else if (args[1].equalsIgnoreCase("prisoner")) {
                List<String> prisoners = Prisoners.getPrisoners().stream()
                        .map(OfflinePlayer::getName).collect(Collectors.toList());
                return StringUtil.copyPartialMatches(args[2], prisoners, result);
            }
        }
        
        return result;
    }

    @Override
    String getPermissionNode() {
        return "jailworker.command.info";
    }

    @Override
    String getDescription() {
        return "get info about a jail or prisoner.";
    }

    @Override
    String getUsage() {
        return "/jailworker info <jail|prisoner> <jail-name|prisoner-name>";
    }
}
