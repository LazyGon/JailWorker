package fr.alienationgaming.jailworker.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.bukkit.util.Vector;

import fr.alienationgaming.jailworker.JWInventorySaver;
import fr.alienationgaming.jailworker.Jail;
import fr.alienationgaming.jailworker.Utils;

public class Put extends JWSubCommand {

    Put() {
    }

    @Override
    boolean runCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            return false;
        }

        String jailName = args[2];
        if (!Jail.exist(jailName)) {
            sender.sendMessage(plugin.toLanguage("error-command-jailnotexist", jailName));
            return false;
        }

        if (!isAdminOrOwner(sender, jailName)) {
            sender.sendMessage(plugin.toLanguage("error-command-notowner"));
            return false;
        }

        @SuppressWarnings("deprecation")
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(plugin.toLanguage("error-command-playeroffline", args[1]));
            return true;
        }

        if (!plugin.getJailConfig().getBoolean("Jails." + jailName + ".isStarted")) {
            sender.sendMessage(plugin.toLanguage("error-command-notstarted", jailName));
            return true;
        }

        // Get number of blocks to break by default for the jail
        int blocks = 0;
        if (args.length >= 3) {
            try {
                blocks = Integer.parseInt(args[3]);
            } catch (Exception e) {
                sender.sendMessage(plugin.toLanguage("error-command-invalidumber"));
                return false;
            }
        } else
            blocks = plugin.getJailConfig().getInt("Jails." + jailName + ".Blocks");

        /* Get Cause */
        String reason = "No Reason.";
        if (args.length >= 4) {
            StringBuilder reasonBuilder = new StringBuilder();
            for (int i = 3; i < args.length; ++i) {
                reasonBuilder.append(args[i]).append(" ");
            }
            reason = ChatColor.translateAlternateColorCodes('&', reasonBuilder.toString());
        }

        if (blocks < 0) {
            sender.sendMessage(plugin.toLanguage("error-command-invalidumber"));
            return false;
        }
        /* Get inventory */
        JWInventorySaver invSaver = new JWInventorySaver(plugin);

        /* Create Section prisoner for target */
        plugin.getJailConfig().set("Prisoners." + target.getName() + ".Prison", jailName);
        plugin.getJailConfig().set("Prisoners." + target.getName() + ".Punisher", sender.getName());
        plugin.getJailConfig().set("Prisoners." + target.getName() + ".Date", Utils.getDate());
        plugin.getJailConfig().set("Prisoners." + target.getName() + ".PreviousPosition",
                target.getLocation().toVector());
        plugin.getJailConfig().set("Prisoners." + target.getName() + ".PreviousWorld", target.getWorld().getName());
        plugin.getJailConfig().set("Prisoners." + target.getName() + ".PunishToBreak", blocks);
        plugin.getJailConfig().set("Prisoners." + target.getName() + ".RemainingBlocks", blocks);
        plugin.getJailConfig().set("Prisoners." + target.getName() + ".Cause", reason);
        plugin.getJailConfig().set("Prisoners." + target.getName() + ".Gamemode", target.getGameMode().name());
        if (target.getGameMode() == GameMode.CREATIVE) {
            target.setGameMode(GameMode.SURVIVAL);
        }
        invSaver.save(target);
        invSaver.clear(target);
        plugin.saveJailConfig();
        plugin.reloadJailConfig();

        /* Send Player to jail */
        target.getInventory().clear();
        target.getEquipment().clear();
        Vector spawn = plugin.getJailConfig().getVector("Jails." + jailName + ".Location.PrisonerSpawn");
        World world = Bukkit.getWorld(plugin.getJailConfig().getString("Jails." + jailName + ".World"));
        target.teleport(new Location(world, spawn.getX(), spawn.getY(), spawn.getZ()));
        Bukkit.broadcastMessage(
                plugin.toLanguage("info-command-broadcastpunish", target.getName(), jailName, sender.getName()));
        Bukkit.broadcastMessage(plugin.toLanguage("info-command-broadcastcantear"));
        target.sendMessage(plugin.toLanguage("info-command-sendtojail", sender.getName()));
        if (!reason.equals("No Reason.")) {
            target.sendMessage(plugin.toLanguage("info-command-displayreason", reason));
        }
        target.sendMessage(plugin.toLanguage("info-command-prisonerorder", blocks,
                plugin.getJailConfig().getString("Jails." + jailName + ".Type")));
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

        if (prisoners.contains(args[1])) {
            return result;
        }

        List<String> jails = new ArrayList<>(plugin.getJailConfig().getConfigurationSection("Jails").getKeys(false));
        jails.removeIf(jail -> !isAdminOrOwner(sender, jail));
        if (args.length == 3) {
            return StringUtil.copyPartialMatches(args[2], jails, result);
        }

        if (!jails.contains(args[2])) {
            return result;
        }

        if (args.length == 4) {
            return StringUtil.copyPartialMatches(args[3], List.of("1", "10", "100", "1000"), result);
        }

        try {
            Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            return result;
        }

        if (args.length == 4) {
            return StringUtil.copyPartialMatches(args[3], List.of("§r[reason]"), result);
        }

        return result;
    }

    @Override
    String getPermissionNode() {
        return "jailworker.put";
    }

    @Override
    String getDescription() {
        return "send player to prison.";
    }

    @Override
    String getUsage() {
        return "/jailworker put <player> <jail-name> <block-amount-to-get-out> [reason]";
    }
}
