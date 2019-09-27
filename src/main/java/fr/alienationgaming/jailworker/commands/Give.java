package fr.alienationgaming.jailworker.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import fr.alienationgaming.jailworker.config.Messages;
import fr.alienationgaming.jailworker.config.Prisoners;

public class Give extends SubCommand {

    Give() {
    }

    @Override
    boolean runCommand(CommandSender sender, String[] args) {

        if (args.length < 3) {
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

        if (!Prisoners.isJailed(target)) {
            Messages.sendMessage(sender, "command.general.error.player-is-not-jailed", Map.of("%player%", args[1]));
            return false;
        }

        Material item;
        try {
            item = Material.valueOf(args[2].toUpperCase());
        } catch (IllegalArgumentException e) {
            Messages.sendMessage(sender, "command.general.error.material-does-not-exist", Map.of("%material%", args[2].toUpperCase(Locale.ROOT)));
            return false;
        }

        int amount = 1;
        if (args.length > 3) {
            try {
                amount = Integer.parseInt(args[3]);
            } catch (NumberFormatException ignore) {
            }
        }

        target.getInventory().addItem(new ItemStack(item, amount));
        Messages.sendMessage(target, "command.give.info.given-item", Map.of("%sender%", sender.getName(), "%item%", item.toString(), "%amount%", String.valueOf(amount)));
        Messages.sendMessage(sender, "command.give.info.give-item", Map.of("%target%", target.getName(), "%item%", item.toString(), "%amount%", String.valueOf(amount)));

        return true;
    }

    @Override
    List<String> runTabComplete(CommandSender sender, String[] args) {
        List<String> result = new ArrayList<>();
        List<String> prisoners = Prisoners.getPrisoners().stream().map(OfflinePlayer::getName)
                .collect(Collectors.toList());

        if (args.length == 2) {
            return StringUtil.copyPartialMatches(args[1], prisoners, result);
        }

        List<String> materials = Arrays.stream(Material.values()).parallel().map(Enum::name)
                .collect(Collectors.toList());

        if (args.length == 3) {
            return StringUtil.copyPartialMatches(args[2], materials, result);
        }

        if (!materials.contains(args[2].toUpperCase())) {
            return result;
        }

        if (args.length == 4) {
            return StringUtil.copyPartialMatches(args[3], List.of("1", "10", "100", "1000"), result);
        }

        return result;
    }

    @Override
    String getPermissionNode() {
        return "jailworker.command.give";
    }

    @Override
    String getDescription() {
        return "give an item to the prisoner.";
    }

    @Override
    String getUsage() {
        return "/jailworker give <player> <material-name> [amount]";
    }
}
