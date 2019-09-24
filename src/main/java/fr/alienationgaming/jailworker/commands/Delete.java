package fr.alienationgaming.jailworker.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.StringUtil;
import org.bukkit.util.Vector;

import fr.alienationgaming.jailworker.Jail;

public class Delete extends JWSubCommand {

    Delete() {
    }

    @Override
    boolean runCommand(CommandSender sender, String[] args) {
        if (args.length == 1) {
            // TODO: not enough argument message.
            return false;
        }

        String jailName = args[1];

        if (!Jail.exist(jailName)) {
            sender.sendMessage(plugin.toLanguage("error-command-jailnotexist", jailName));
            return false;
        }

        if (!isAdminOrOwner(sender, jailName)) {
            sender.sendMessage(plugin.toLanguage("error-command-notowner"));
            return false;
        }

        if (plugin.getJailConfig().getBoolean("Jails." + jailName + ".isStarted")) {
            BukkitRunnable task = plugin.tasks.get(jailName);
            if (!task.isCancelled()) {
                task.cancel();
            }
            PlayerInteractEvent.getHandlerList().unregister(plugin.jwblockbreaklistener);
        }
        /* Delete task */
        plugin.tasks.remove(jailName);
        Vector spawn = plugin.getJailConfig().getVector("Jails." + jailName + ".Location.PrisonerSpawn");
        World world = Bukkit.getWorld(plugin.getJailConfig().getString("Jails." + jailName + ".World"));

        /* Delete red block for prisoner spawn */
        Location locSp = new Location(world, spawn.getBlockX(), spawn.getBlockY() - 1, spawn.getBlockZ());
        Location locSpNei = new Location(world, spawn.getBlockX() - 1, spawn.getBlockY() - 1, spawn.getBlockZ());
        locSp.getBlock().setType(locSpNei.getBlock().getType());

        /* Delete "jailName" section */
        plugin.getJailConfig().set("Jails." + jailName, null);
        plugin.saveJailConfig();
        plugin.reloadJailConfig();
        if (!Jail.exist(jailName)) {
            sender.sendMessage(plugin.toLanguage("info-command-jailremovesuccess", jailName));
        } else {
            sender.sendMessage(plugin.toLanguage("error-command-jailremoveechec"));
            return false;
        }

        return true;
    }

    @Override
    List<String> runTabComplete(CommandSender sender, String[] args) {
        List<String> result = new ArrayList<>();
        List<String> jails = new ArrayList<>(plugin.getJailConfig().getConfigurationSection("Jails").getKeys(false));
        jails.removeIf(jail -> !isAdminOrOwner(sender, jail));
        if (args.length == 2) {
            return StringUtil.copyPartialMatches(args[1], jails, result);
        }

        return result;
    }

    @Override
    String getPermissionNode() {
        return "jailworker.delete";
    }

    @Override
    String getDescription() {
        return "delete the jail.";
    }

    @Override
    String getUsage() {
        return "/jailworker delete <jail-name>";
    }
}
