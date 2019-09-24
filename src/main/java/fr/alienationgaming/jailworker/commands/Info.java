package fr.alienationgaming.jailworker.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import fr.alienationgaming.jailworker.Jail;

public class Info extends JWSubCommand {

    Info() {
    }

    @Override
    boolean runCommand(CommandSender sender, String[] args) {

        if (args.length == 1) {
            // TODO: not enough argument
            return false;
        }

        String jailName = args[1];

        if (!Jail.exist(jailName)) {
            sender.sendMessage(plugin.toLanguage("error-command-jailnotexist", jailName));
            return true;
        }

        if (!isAdminOrOwner(sender, jailName)) {
            return false;
        }

        /* getValues */
        List<String> Owners = plugin.getJailConfig().getStringList("Jails." + jailName + ".Owners");
        boolean isStarted = plugin.getJailConfig().getBoolean("Jails." + jailName + ".isStarted");
        int MaxSand = plugin.getJailConfig().getInt("Jails." + jailName + ".MaxSand");
        int Blocks = plugin.getJailConfig().getInt("Jails." + jailName + ".Blocks");
        int Speed = plugin.getJailConfig().getInt("Jails." + jailName + ".Speed");
        String World = plugin.getJailConfig().getString("Jails." + jailName + ".World");
        String Type = plugin.getJailConfig().getString("Jails." + jailName + ".Type");

        sender.sendMessage(plugin.toLanguage("info-command-jwinfoname", jailName.toLowerCase()));
        sender.sendMessage(plugin.toLanguage("info-command-jailownerslist", jailName, Owners));
        sender.sendMessage("------------------------\n");
        sender.sendMessage(plugin.toLanguage("info-command-jwinfo" + (isStarted ? "started" : "stoped")));
        sender.sendMessage(plugin.toLanguage("info-command-jwinfotype", Type));
        sender.sendMessage(plugin.toLanguage("info-command-jwinfomaxblk", MaxSand));
        sender.sendMessage(plugin.toLanguage("info-command-jwinfodefaultbreak", Blocks));
        sender.sendMessage(plugin.toLanguage("info-command-jwinfospeed", Speed));
        sender.sendMessage("\n");
        sender.sendMessage(plugin.toLanguage("info-command-jwinfoworld", World));
        sender.sendMessage("------------------------\n");
        sender.sendMessage(plugin.toLanguage("info-command-jwinfoprisoners"));
        plugin.getJailConfig().getConfigurationSection("Prisoners").getKeys(false).forEach(prisoner -> {
            int remain = plugin.getJailConfig().getInt("Prisoners." + prisoner + ".RemainingBlocks");
            sender.sendMessage(ChatColor.BLUE + prisoner + ChatColor.RESET + " => " + ChatColor.RED + remain);
        });
        sender.sendMessage("------------------------\n");

        return true;
    }

    @Override
    List<String> runTabComplete(CommandSender sender, String[] args) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    String getPermissionNode() {
        return "jailworker.info";
    }

    @Override
    String getDescription() {
        return "get info about a jail.";
    }

    @Override
    String getUsage() {
        return "/jailworker info <jail-name>";
    }
}
