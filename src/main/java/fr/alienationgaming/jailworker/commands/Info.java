package fr.alienationgaming.jailworker.commands;

import java.util.Iterator;
import java.util.Set;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import fr.alienationgaming.jailworker.JailWorker;

public class Info implements CommandExecutor {

    JailWorker plugin;

    public Info(JailWorker jailworker) {
        plugin = jailworker;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (sender instanceof ConsoleCommandSender || plugin.hasPerm(((Player) sender), "jailworker.jw-admin") || plugin.hasPerm(((Player) sender), "jailworker.jw-info")) {
            if (args.length == 0)
                return false;
            String jailName = args[0];

            if (!plugin.getJailConfig().contains("Jails." + jailName)) {
                sender.sendMessage(plugin.toLanguage("error-command-jailnotexist", jailName));
                return true;
            }
            /* getValues */
            String Name = jailName;
            List<String> Owners = plugin.getJailConfig().getStringList("Jails." + jailName + ".Owners");
            boolean isStarted = plugin.getJailConfig().getBoolean("Jails." + jailName + ".isStarted");
            int MaxSand = plugin.getJailConfig().getInt("Jails." + jailName + ".MaxSand");
            int Blocks = plugin.getJailConfig().getInt("Jails." + jailName + ".Blocks");
            int Speed = plugin.getJailConfig().getInt("Jails." + jailName + ".Speed");
            String World = plugin.getJailConfig().getString("Jails." + jailName + ".World");
            String Type = plugin.getJailConfig().getString("Jails." + jailName + ".Type");

            sender.sendMessage(plugin.toLanguage("info-command-jwinfoname", Name.toLowerCase()));
            sender.sendMessage(plugin.toLanguage("info-command-jailownerslist", Name, Owners));
            sender.sendMessage("------------------------\n");
            if (isStarted)
                sender.sendMessage(plugin.toLanguage("info-command-jwinfostarted"));
            else
                sender.sendMessage(plugin.toLanguage("info-command-jwinfostoped"));
            sender.sendMessage(plugin.toLanguage("info-command-jwinfotype", Type));
            sender.sendMessage(plugin.toLanguage("info-command-jwinfomaxblk", MaxSand));
            sender.sendMessage(plugin.toLanguage("info-command-jwinfodefaultbreak", Blocks));
            sender.sendMessage(plugin.toLanguage("info-command-jwinfospeed", Speed));
            sender.sendMessage("\n");
            sender.sendMessage(plugin.toLanguage("info-command-jwinfoworld", World));
            sender.sendMessage("------------------------\n");
            sender.sendMessage(plugin.toLanguage("info-command-jwinfoprisoners"));
            Set<String> s = plugin.getJailConfig().getConfigurationSection("Prisoners").getKeys(false);
            Iterator<String> it = s.iterator();

            while (it.hasNext()) {
                String prisoner = (String) it.next();
                int remain = plugin.getJailConfig().getInt("Prisoners." + prisoner + ".RemainingBlocks");
                sender.sendMessage(ChatColor.BLUE + prisoner + ChatColor.RESET + " => " + ChatColor.RED + remain);
            }
            sender.sendMessage("------------------------\n");
        }
        return true;
    }
}
