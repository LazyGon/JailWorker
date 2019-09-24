package fr.alienationgaming.jailworker.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.StringUtil;

import fr.alienationgaming.jailworker.Jail;

public class Stop extends JWSubCommand {

    Stop() {
    }

    @Override
    boolean runCommand(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return false;
        }
        String jailName = args[1];

        if (isAdminOrOwner(sender, jailName)) {
            sender.sendMessage(plugin.toLanguage("error-command-notowner"));
            return false;
        }

        if (!Jail.exist(jailName)) {
            sender.sendMessage(plugin.toLanguage("error-command-jailnotexist", jailName));
            return true;
        }
        if (!plugin.getJailConfig().getBoolean("Jails." + jailName + ".isStarted")) {
            sender.sendMessage(ChatColor.RED + "Jail is not started!");
            return true;
        }

        BukkitRunnable task = plugin.tasks.get(jailName);
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
        plugin.tasks.remove(jailName);
        plugin.getJailConfig().set("Jails." + jailName + ".isStarted", false);
        PlayerInteractEvent.getHandlerList().unregister(plugin.jwblockbreaklistener);
        plugin.saveJailConfig();
        plugin.reloadJailConfig();
        sender.sendMessage(plugin.toLanguage("info-command-jailstoped"));

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
        return "jailworker.stop";
    }

    @Override
    String getDescription() {
        return "stop jail system.";
    }

    @Override
    String getUsage() {
        return "/jailworker stop <jail-name>";
    }

}
