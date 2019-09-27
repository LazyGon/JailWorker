package fr.alienationgaming.jailworker.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import fr.alienationgaming.jailworker.JailSystem;
import fr.alienationgaming.jailworker.config.Config;
import fr.alienationgaming.jailworker.config.JailConfig;
import fr.alienationgaming.jailworker.config.Messages;
import fr.alienationgaming.jailworker.config.Prisoners;

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
            Messages.sendMessage(sender, "command.general.error.jail-does-not-exist", Map.of("%jail-name%", jailName));
            return false;
        }

        @SuppressWarnings("deprecation")
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            Messages.sendMessage(sender, "command.general.error.player-is-offline", Map.of("%player%", args[1]));
            return false;
        }

        if (!JailSystem.isRunning(jailName)) {
            Messages.sendMessage(sender, "command.general.error.jail-is-not-running", Map.of("%jail-name%", jailName));
            return false;
        }

        if (Prisoners.isJailed(target)) {
            Messages.sendMessage(sender, "command.put.error.player-is-already-jailed", Map.of("%player%", args[1]));
            return false;
        }

        Material punishmentBlock;
        try {
            punishmentBlock = Material.valueOf(args[3].toUpperCase());
        } catch (IllegalArgumentException e) {
            Messages.sendMessage(sender, "command.general.error.material-does-not-exist",
                    Map.of("%material%", args[3].toUpperCase(Locale.ROOT)));
            return false;
        }

        if (!JailConfig.getValidBlocks().contains(punishmentBlock.name())) {
            Messages.sendMessage(sender, "command.general.error.invalid-material",
                    Map.of("%material%", punishmentBlock.name()));
            return false;
        }

        int amount = 500;
        try {
            amount = Integer.parseInt(args[4]);
        } catch (IllegalArgumentException ignore) {
        }

        if (amount <= 0) {
            amount = 1;
        }

        String reason = "No Reason.";
        if (args.length > 5) {
            StringBuilder reasonBuilder = new StringBuilder();
            for (int i = 5; i < args.length; ++i) {
                reasonBuilder.append(args[i]).append(" ");
            }
            reason = ChatColor.translateAlternateColorCodes('&', reasonBuilder.toString());
        }

        OfflinePlayer punisher = (sender instanceof Player) ? (OfflinePlayer) sender : null;
        Prisoners.punishPlayer(target, jailName, punisher, Map.of(punishmentBlock, amount), reason);

        Bukkit.getOnlinePlayers().forEach(player -> {
            if (player.equals(target)) {
                return;
            }
            Messages.sendMessage(player, "command.put.info.broadcast-jailed",
                    Map.of("%player%", target.getName(), "%jail-name%", jailName));
            if (!Config.canPrisonerSpeak()) {
                Messages.sendMessage(player, "command.put.info.broadcast-prisoners-cannot-speak",
                        Map.of("%player%", target.getName()));
            }
            if (!Config.canPrisonersHear()) {
                Messages.sendMessage(player, "command.put.info.broadcast-prisoners-cannot-hear",
                        Map.of("%player%", target.getName()));
            }
        });

        Messages.sendMessage(target, "command.put.info.jailed",
                Map.of("%sender%", sender.getName(), "%jail-name%", jailName));
        if (!reason.equals("No Reason.")) {
            Messages.sendMessage(target, "command.put.info.display-reason", Map.of("%reason%", reason));
        }

        Messages.sendMessage(target, "command.put.info.punishment-block-header");
        Messages.sendMessage(target, "command.put.info.punishment-block-format",
                Map.of("%material%", punishmentBlock.name(), "%amount%", amount));
        return true;
    }

    @Override
    List<String> runTabComplete(CommandSender sender, String[] args) {
        List<String> result = new ArrayList<>();

        List<String> onlinePlayers = Bukkit.getOnlinePlayers().stream().map(Player::getName)
                .collect(Collectors.toList());
        if (args.length == 2) {
            return StringUtil.copyPartialMatches(args[1], onlinePlayers, result);
        }

        if (!onlinePlayers.contains(args[1])) {
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
            return StringUtil.copyPartialMatches(args[3], JailConfig.getValidBlocks(), result);
        }

        if (!JailConfig.getValidBlocks().contains(args[3].toUpperCase(Locale.ROOT))) {
            return result;
        }

        if (args.length == 5) {
            return StringUtil.copyPartialMatches(args[4], List.of("1", "10", "100", "1000"), result);
        }

        try {
            Integer.parseInt(args[4]);
        } catch (NumberFormatException e) {
            return result;
        }

        if (args.length == 6) {
            return StringUtil.copyPartialMatches(args[5], List.of("[reason]"), result);
        }

        return result;
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
        return "/jailworker put <player> <jail-name> <punishment-block-type> <punishment-block-amount> [reason]";
    }
}
