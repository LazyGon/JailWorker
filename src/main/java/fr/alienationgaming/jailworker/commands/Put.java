package fr.alienationgaming.jailworker.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import fr.alienationgaming.jailworker.JailSystem;
import fr.alienationgaming.jailworker.config.Config;
import fr.alienationgaming.jailworker.config.JailConfig;
import fr.alienationgaming.jailworker.config.Messages;
import fr.alienationgaming.jailworker.config.Prisoners;
import fr.alienationgaming.jailworker.config.WantedPlayers;
import fr.alienationgaming.jailworker.events.PlayerJailedEvent;

public class Put extends SubCommand {

    Put() {
    }

    @Override
    boolean runCommand(CommandSender sender, String[] args) {
        if (args.length < 5) {
            Messages.sendMessage(sender, "command.general.error.not-enough-arguments");
            return false;
        }

        String jailName = args[2];
        if (!hasPermission(sender, jailName)) {
            Messages.sendMessage(sender, "command.general.error.no-permission");
            return false;
        }

        if (!JailConfig.exist(jailName)) {
            Messages.sendMessage(sender, "command.general.error.jail-does-not-exist", Messages.placeholder("%jail-name%", jailName));
            return false;
        }

        @SuppressWarnings("deprecation")
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if (!target.hasPlayedBefore() || target.getName() == null) {
            Messages.sendMessage(sender, "command.general.error.player-has-never-played", Messages.placeholder("%player%", args[1]));
            return false;
        }

        if (!JailSystem.isRunning(jailName)) {
            Messages.sendMessage(sender, "command.general.error.jail-is-not-running", Messages.placeholder("%jail-name%", jailName));
            return false;
        }

        if (Prisoners.isJailed(target) || WantedPlayers.isWanted(target)) {
            Messages.sendMessage(sender, "command.put.error.player-is-already-jailed", Messages.placeholder("%player%", args[1]));
            return false;
        }

        int point = 500;
        try {
            point = Integer.parseInt(args[3]);
        } catch (IllegalArgumentException ignore) {
        }

        if (point <= 0) {
            point = 1;
        }

        String reason = "No reason.";
        if (args.length > 4) {
            StringBuilder reasonBuilder = new StringBuilder();
            for (int i = 4; i < args.length; ++i) {
                reasonBuilder.append(args[i]).append(" ");
            }
            reason = ChatColor.translateAlternateColorCodes('&', reasonBuilder.toString());
        }

        OfflinePlayer punisher = (sender instanceof Player) ? (OfflinePlayer) sender : null;
        PlayerJailedEvent jailedEvent = new PlayerJailedEvent(target, jailName, punisher, point, reason);
        Bukkit.getPluginManager().callEvent(jailedEvent);
        if (jailedEvent.isCancelled()) {
            return false;
        }

        punisher = jailedEvent.getPunisher();
        point = jailedEvent.getPunishmentPoint();
        reason = jailedEvent.getReason();
        
        if (target.isOnline()) {
            Prisoners.punishPlayer(target.getPlayer(), jailName, punisher, point, reason);
            sendJailedMessage(target.getPlayer(), jailName, sender, point, reason);
        } else {
            WantedPlayers.addWantedPlayer(target, jailName, point, reason);
            Messages.sendMessage(sender, "command.put.info.player-is-now-wanted", Messages.placeholder("%player%", target.getName()));
        }
        return true;
    }

    @Override
    List<String> runTabComplete(CommandSender sender, String[] args) {
        List<String> result = new ArrayList<>();

        List<String> offlinePlayers = Arrays.stream(Bukkit.getOfflinePlayers()).parallel().map(OfflinePlayer::getName)
                .filter(Objects::nonNull).collect(Collectors.toList());
        if (args.length == 2) {
            return StringUtil.copyPartialMatches(args[1], offlinePlayers, result);
        }

        if (!offlinePlayers.contains(args[1])) {
            return result;
        }

        @SuppressWarnings("deprecation")
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if (Prisoners.isJailed(target)) {
            return result;
        }

        List<String> jails = JailConfig.getJails();
        jails.removeIf(jail -> !JailSystem.isRunning(jail));
        jails.removeIf(jail -> !hasPermission(sender, jail));
        if (args.length == 3) {
            return StringUtil.copyPartialMatches(args[2], jails, result);
        }

        if (!jails.contains(args[2])) {
            return result;
        }

        if (args.length == 4) {
            return StringUtil.copyPartialMatches(args[3], Arrays.asList("1", "10", "100", "1000"), result);
        }

        try {
            Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            return result;
        }

        if (args.length == 5) {
            return StringUtil.copyPartialMatches(args[4], Arrays.asList("[reason]"), result);
        }

        return result;
    }

    public static void sendJailedMessage(Player player, String jailName, CommandSender punisher, int punishmentPoint, String reason) {
        Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
            if (onlinePlayer.equals(player)) {
                return;
            }
            Messages.sendMessage(onlinePlayer, "command.put.info.broadcast-jailed",
                    Messages.placeholder("%player%", player.getName(), "%jail-name%", jailName));
            if (!Config.canPrisonerSpeak()) {
                Messages.sendMessage(onlinePlayer, "command.put.info.broadcast-prisoners-cannot-speak",
                        Messages.placeholder("%player%", player.getName()));
            }
            if (!Config.canPrisonersHear()) {
                Messages.sendMessage(onlinePlayer, "command.put.info.broadcast-prisoners-cannot-hear",
                        Messages.placeholder("%player%", player.getName()));
            }
        });

        Messages.sendMessage(player, "command.put.info.jailed",
                Messages.placeholder("%sender%", punisher.getName(), "%jail-name%", jailName));
        if (!reason.equals("No reason.")) {
            Messages.sendMessage(player, "command.put.info.display-reason", Messages.placeholder("%reason%", reason));
        }

        Messages.sendMessage(player, "command.put.info.punishment-point", Messages.placeholder("%point%", punishmentPoint));
        Messages.sendMessage(player, "command.put.info.punishment-tips");
    }

    @Override
    String getPermissionNode() {
        return "jailworker.command.put.<jail-name>";
    }

    @Override
    String getDescription() {
        return "send player to prison.";
    }

    @Override
    String getUsage() {
        return "/jailworker put <player> <jail-name> <punishment-point> [reason]";
    }
}
