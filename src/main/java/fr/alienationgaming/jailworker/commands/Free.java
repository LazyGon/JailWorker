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
import org.bukkit.util.StringUtil;

import fr.alienationgaming.jailworker.config.Messages;
import fr.alienationgaming.jailworker.config.Prisoners;
import fr.alienationgaming.jailworker.config.WantedPlayers;

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
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if (!target.hasPlayedBefore() || target.getName() == null) {
            Messages.sendMessage(sender, "command.general.error.player-has-never-played", Map.of("%player%", args[1]));
            return false;
        }

        if (!Prisoners.isJailed(target) && !WantedPlayers.isWanted(target)) {
            Messages.sendMessage(sender, "command.general.error.player-is-not-jailed", Map.of("%player%", args[1]));
            return false;
        }

        String jailName = Prisoners.getJailPlayerIsIn(target);
        if (jailName == null) {
            jailName = WantedPlayers.getJail(target);
        }
        Messages.sendMessage(sender, "command.free.info.free-player",
                Map.of("%player%", target.getName(), "%jail-name%", jailName));

        if (target.isOnline()) {
            Messages.sendMessage(target.getPlayer(), "command.free.info.you-are-now-free",
                    Map.of("%sender%", sender.getName(), "%jail-name%", jailName));
            Prisoners.freePlayer(target.getPlayer());

            if (args.length > 2) {
                StringBuilder reasonBuilder = new StringBuilder();
                for (int i = 2; i < args.length; ++i) {
                    reasonBuilder.append(args[i]).append(" ");
                }
                String reason = ChatColor.translateAlternateColorCodes('&', reasonBuilder.toString());
                Messages.sendMessage(target.getPlayer(), "command.free.info.display-reason",
                        Map.of("%reason%", reason));
            }
        } else {
            WantedPlayers.removeWantedPlayer(target);
        }

        return true;
    }

    @Override
    List<String> runTabComplete(CommandSender sender, String[] args) {
        List<String> result = new ArrayList<>();
        List<OfflinePlayer> prisoners = Prisoners.getPrisoners();
        prisoners.addAll(WantedPlayers.getWantedPlayers());
        List<String> punishedPlayers = prisoners.stream().map(OfflinePlayer::getName).filter(Objects::nonNull)
                .collect(Collectors.toList());
        

        if (args.length == 2) {
            return StringUtil.copyPartialMatches(args[1], punishedPlayers, result);
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
