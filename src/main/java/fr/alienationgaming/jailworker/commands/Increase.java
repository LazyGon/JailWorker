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

import fr.alienationgaming.jailworker.config.JailConfig;
import fr.alienationgaming.jailworker.config.Messages;
import fr.alienationgaming.jailworker.config.Prisoners;

public class Increase extends SubCommand {

    Increase() {
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

        @SuppressWarnings("deprecation")
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            Messages.sendMessage(sender, "command.general.error.player-is-offline", Map.of("%player%", args[1]));
            return false;
        }

        // player not on jail
        if (!Prisoners.isJailed(target)) {
            Messages.sendMessage(sender, "command.general.error.player-is-not-jailed", Map.of("%player%", args[1]));
            return false;
        }

        Material material;
        try {
            material = Material.valueOf(args[2].toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            Messages.sendMessage(sender, "command.general.error.material-does-not-exist", Map.of("%material%", args[2].toUpperCase(Locale.ROOT)));
            return false;
        }

        int add;
        try {
            add = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            Messages.sendMessage(sender, "command.general.error.invalid-number");
            return false;
        }

        if (add <= 0) {
            Messages.sendMessage(sender, "command.general.error.invalid-number");
            return false;
        }

        int newValue = add + Prisoners.getRemainingBlock(target, material);

        // Increment punishement
        Prisoners.setRemainingBlock(target, material, newValue);
        Messages.sendMessage(target, "command.increase.info.notice-target", Map.of("%sender%", sender.getName(), "%material%", material.name(), "%amount%", add, "%new-value%", newValue));
        Messages.sendMessage(sender, "command.increase.info.notice-sender", Map.of("%player%", target.getName(), "%material%", material.name(), "%amount%", add, "%new-value%", newValue));

        if (args.length > 4) {
            StringBuilder reasonBuilder = new StringBuilder();
            for (int i = 4; i < args.length; ++i) {
                reasonBuilder.append(args[i]).append(" ");
            }
            String reason = ChatColor.translateAlternateColorCodes('&', reasonBuilder.toString());
            Messages.sendMessage(target, "command.increase.info.display-reason", Map.of("%reason%", reason));
        }

        return true;
    }

    @Override
    List<String> runTabComplete(CommandSender sender, String[] args) {
        List<String> result = new ArrayList<>();
        List<String> prisoners = Prisoners.getPrisoners().stream()
                .map(OfflinePlayer::getName).collect(Collectors.toList());
        
        if (args.length == 2) {
            return StringUtil.copyPartialMatches(args[1], prisoners, result);
        }

        if (!prisoners.contains(args[1])) {
            return result;
        }

        if (args.length == 3) {
            return StringUtil.copyPartialMatches(args[2], JailConfig.getValidBlocks(), result);
        }

        if (!JailConfig.getValidBlocks().contains(args[2].toUpperCase(Locale.ROOT))) {
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
        return "jailworker.command.increase";
    }

    @Override
    String getDescription() {
        return "increase a player punishment of <number> blocks";
    }

    @Override
    String getUsage() {
        return "/jailworker increase <player> <block> <amount> [reason]";
    }

}