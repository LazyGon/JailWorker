package fr.alienationgaming.jailworker.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.alienationgaming.jailworker.JailWorker;

public class Give implements CommandExecutor {

    private JailWorker plugin;

    public Give(JailWorker jailworker) {
        plugin = jailworker;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Player target = null;
        boolean prisonnerOnline = true;
        boolean prisonnerFound = true;
        Material item = null;
        if (sender instanceof ConsoleCommandSender || plugin.hasPerm(((Player) sender), "jailworker.jw-admin") || plugin.hasPerm(((Player) sender), "jailworker.jw-give")) {
            if (args.length < 2)
                return false;
            target = plugin.getServer().getPlayer(args[0]);
            try {
                item = Material.getMaterial(Integer.parseInt(args[1]));
            } catch (Exception e) {
                sender.sendMessage(plugin.toLanguage("info-command-materialidlink"));
                return false;
            }
            if (item == null) {
                sender.sendMessage(plugin.toLanguage("error-command-invalidmaterial"));
                return true;
            }
            // Player offline
            if (target == null) {
                sender.sendMessage(plugin.toLanguage("error-command-playeroffline", args[0]));
                prisonnerOnline = false;
            }
            //player not on jail
            if (!plugin.getJailConfig().contains("Prisoners." + args[0])) {
                sender.sendMessage(plugin.toLanguage("error-command-missingonjail", args[0]));
                prisonnerFound = false;
            } else {
                prisonnerFound = true;
            }
            if (prisonnerFound && !prisonnerOnline) {
                sender.sendMessage(plugin.toLanguage("error-command-actionofflineplayer"));
                return true;
            } else if (prisonnerFound && prisonnerOnline) {
                String jailName = plugin.getJailConfig().getString("Prisoners." + target.getName() + ".Prison");
                if (sender instanceof ConsoleCommandSender || plugin.getJailConfig().getStringList("Jails." + jailName + ".Owners").contains(((Player) sender).getName())) {
                    target.getInventory().addItem(new ItemStack(item));
                    target.sendMessage(plugin.toLanguage("info-command-giveitem", sender.getName(), item.toString()));
                    sender.sendMessage(plugin.toLanguage("info-command-itemgiven", item.toString()));
                } else {
                    sender.sendMessage(plugin.toLanguage("error-command-notowner"));
                }
            }
        }
        return true;
    }
}
