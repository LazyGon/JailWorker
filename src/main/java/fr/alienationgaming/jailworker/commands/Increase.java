package fr.alienationgaming.jailworker.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import fr.alienationgaming.jailworker.Jail;

public class Increase extends JWSubCommand {

    Increase() {
    }

    @Override
    boolean runCommand(CommandSender sender, String[] args) {

        if (args.length < 3) {
            return false;
        }

        @SuppressWarnings("deprecation")
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(plugin.toLanguage("error-command-playeroffline", args[1]));
            return false;
        }

        // player not on jail
        if (!Jail.isJailed(target)) {
            sender.sendMessage(plugin.toLanguage("error-command-missingonjail", args[1]));
            return true;
        }

        String jailName = plugin.getJailConfig().getString("Prisoners." + target.getName() + ".Prison");
        if (!isAdminOrOwner(sender, jailName)) {
            sender.sendMessage(plugin.toLanguage("error-command-notowner"));
            return false;
        }

        // Get number
        int add;
        try {
            add = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.toLanguage("error-command-invalidnumber"));
            return false;
        }

        /* Increment punishement */
        int newVal = plugin.getJailConfig().getInt("Prisoners." + args[1] + ".RemainingBlocks") + add;
        plugin.getJailConfig().set("Prisoners." + args[1] + ".RemainingBlocks", newVal);
        
        target.sendMessage(plugin.toLanguage("info-command-increasement", sender.getName(), add));

        if (args.length > 3) {
            StringBuilder reasonBuilder = new StringBuilder();
            for (int i = 3; i < args.length; ++i) {
                reasonBuilder.append(args[i]).append(" ");
            }
            String reason = ChatColor.translateAlternateColorCodes('&', reasonBuilder.toString());
            target.sendMessage(plugin.toLanguage("info-command-displayreason", reason));
        }
        
        plugin.saveJailConfig();
        plugin.reloadJailConfig();
        sender.sendMessage(plugin.toLanguage("info-command-increasesuccess", add, args[1]));

        return true;
    }

    @Override
    List<String> runTabComplete(CommandSender sender, String[] args) {
        List<String> result = new ArrayList<>();
        if (!plugin.getJailConfig().isConfigurationSection("Prisoners")) {
            return result;
        }

        List<String> prisoners = new ArrayList<>(plugin.getJailConfig().getConfigurationSection("Prisoners").getKeys(false));
        
        if (args.length == 2) {
            return StringUtil.copyPartialMatches(args[1], prisoners, result);
        }

        if (args.length == 3) {
            return StringUtil.copyPartialMatches(args[2], List.of("1", "10", "100", "1000"), result);
        }

        if (args.length == 4) {
            return StringUtil.copyPartialMatches(args[3], List.of("§r[reason]"), result);
        }
        
        return result;
    }

    @Override
    String getPermissionNode() {
        return "jailworker.increase";
    }

    @Override
    String getDescription() {
        return "increase a player punishment of <number> blocks";
    }

    @Override
    String getUsage() {
        return "/jailworker increase <player> <number> [reason]";
    }

}