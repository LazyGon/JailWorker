package fr.alienationgaming.jailworker.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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

        String jailName = args[1];
        if (!Jail.exist(jailName)) {
            sender.sendMessage(plugin.toLanguage("error-command-jailnotexist", jailName));
            return false;
        }

        if (!isAdminOrOwner(sender, jailName)) {
            return false;
        }

        @SuppressWarnings("deprecation")
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(plugin.toLanguage("error-command-playeroffline", args[0]));
            return true;
        }

        if (!plugin.getJailConfig().getBoolean("Jails." + jailName + ".isStarted")) {
            sender.sendMessage(plugin.toLanguage("error-command-notstarted", jailName));
            return true;
        }

        /* Get number blocks to break by default for the jail */
        int blocks = 0;
        if (args.length >= 3) {
            try {
                blocks = Integer.parseInt(args[2]);
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
        // TODO Auto-generated method stub
        return null;
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
