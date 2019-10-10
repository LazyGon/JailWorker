package fr.alienationgaming.jailworker.commands;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import fr.alienationgaming.jailworker.JailSystem;
import fr.alienationgaming.jailworker.config.BlockPoints;
import fr.alienationgaming.jailworker.config.JailConfig;
import fr.alienationgaming.jailworker.config.Messages;
import fr.alienationgaming.jailworker.config.Prisoners;

public class Info extends SubCommand {

    Info() {
    }

    @Override
    boolean runCommand(CommandSender sender, String[] args) {

        if (args.length > 1 && args[1].equalsIgnoreCase("blocks")) {
            int page = 1;
            if (args.length > 2) {
                try {
                    page = Integer.parseInt(args[2]);
                } catch (NumberFormatException ignore) {
                }
            }

            List<String> punishmentBlocks = BlockPoints.getAllBlocks().stream().map(Material::name)
                    .collect(Collectors.toList());
            Collections.sort(punishmentBlocks);

            int size = punishmentBlocks.size();
            int maxPage = size % 9 == 0 ? size / 9 : size / 9 + 1;
            page = Math.min(maxPage, page);

            if (page <= 0) {
                page = 1;
            }

            Messages.sendMessage(sender, "command.info.info.punishment-blocks",
                    Map.of("%page%", page, "%max-page%", maxPage));
            punishmentBlocks.stream().skip((page - 1) * 9).limit(9).forEach(material -> {
                Messages.sendMessage(sender, false, "command.info.info.punishment-blocks-format",
                        Map.of("%material%", material, "%point%", BlockPoints.getPoint(Material.valueOf(material))));
            });
            return true;
        }

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
            int punishmentBlockInterval = JailConfig.getBlockInterval(jailName);
            String World = JailConfig.getWorld(jailName).getName();

            Messages.sendMessage(sender, "command.info.info.jail-name", Map.of("%jail-name%", jailName));
            Messages.sendMessage(sender, false, "command.general.info.line");
            Messages.sendMessage(sender, false, "command.info.info.jail-is-" + (running ? "" : "not-") + "running");
            Messages.sendMessage(sender, false, "command.info.info.jail-world-name", Map.of("%world-name%", World));
            Messages.sendMessage(sender, false, "command.info.info.jail-max-punishment-blocks",
                    Map.of("%max-punishment-blocks%", maxPunishmentBlock));
            Messages.sendMessage(sender, false, "command.info.info.jail-block-appear-interval",
                    Map.of("%punishment-block-interval%", punishmentBlockInterval));
            Messages.sendMessage(sender, false, "command.info.info.jail-punishment-blocks");
            JailConfig.getPunishmentBlocks(jailName).forEach(material -> Messages.sendMessage(sender, false,
                    "command.info.info.jail-punishment-blocks-format", Map.of("%material%", material.name())));
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
            String punisherName = "CONSOLE";
            OfflinePlayer punisher = Prisoners.getPunisher(prisoner);
            if (punisher != null && punisher.getName() != null) {
                punisherName = punisher.getName();
            }
            Messages.sendMessage(sender, false, "command.info.info.prisoner-punisher-name",
                    Map.of("%punisher%", punisherName));
            Messages.sendMessage(sender, false, "command.info.info.prisoner-punishment-point",
                    Map.of("%point%", Prisoners.getPunishmentPoint(prisoner)));
            Messages.sendMessage(sender, false, "command.info.info.prisoner-cause",
                    Map.of("%reason%", Prisoners.getCause(prisoner)));

            long unixTime = Prisoners.getJailedDate(prisoner);
            LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(unixTime),
                    ZoneId.systemDefault());
            String time = localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            Messages.sendMessage(sender, false, "command.info.info.prisoner-punishment-time", Map.of("%time%", time));
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
        List<String> subCommands = List.of("jail", "prisoner", "blocks");
        if (args.length == 2) {
            return StringUtil.copyPartialMatches(args[1], subCommands, result);
        }

        String subCommand = args[1].toLowerCase(Locale.ROOT);
        if (!subCommands.contains(subCommand)) {
            return result;
        }

        if (args.length == 3) {
            switch (subCommand) {
            case "jail":
                List<String> jails = JailConfig.getJails();
                jails.removeIf(jail -> !JailConfig.exist(jail));
                return StringUtil.copyPartialMatches(args[2], jails, result);
            case "prisoner":
                List<String> prisoners = Prisoners.getPrisoners().stream().map(OfflinePlayer::getName)
                        .filter(Objects::nonNull).collect(Collectors.toList());
                return StringUtil.copyPartialMatches(args[2], prisoners, result);
            case "blocks":
                int size = BlockPoints.getAllBlocks().size();
                int maxPage = size % 9 == 0 ? size / 9 : size / 9 + 1;
                List<String> pages = IntStream.rangeClosed(1, maxPage).boxed().map(page -> page.toString())
                        .collect(Collectors.toList());
                return StringUtil.copyPartialMatches(args[2], pages, result);
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
