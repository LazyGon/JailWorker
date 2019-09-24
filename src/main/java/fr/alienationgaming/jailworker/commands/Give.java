package fr.alienationgaming.jailworker.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.alienationgaming.jailworker.Jail;

public class Give extends JWSubCommand {

    Give() {
    }

    @Override
    boolean runCommand(CommandSender sender, String[] args) {

        if (args.length < 3) {
            return false;
        }

        @SuppressWarnings("deprecation")
        Player target = Bukkit.getPlayer(args[1]);
        // Player offline
        if (target == null) {
            sender.sendMessage(plugin.toLanguage("error-command-playeroffline", args[1]));
            return false;
        }

        // player not on jail
        if (!Jail.isJailed(target)) {
            sender.sendMessage(plugin.toLanguage("error-command-missingonjail", args[1]));
            return false;
        }

        String jailName = plugin.getJailConfig().getString("Prisoners." + target.getName() + ".Prison");
        if (!isAdminOrOwner(sender, jailName)) {
            return false;
        }

        Material item;
        try {
            item = Material.valueOf(args[2].toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage(plugin.toLanguage("info-command-materialidlink"));
            return false;
        }

        int amount = 1;
        if (args.length > 3) {
            try {
                amount = Integer.parseInt(args[3]);
            } catch (NumberFormatException ignore) {
            }
        }

        // TODO: remove "error-command-invalidmaterial"

        target.getInventory().addItem(new ItemStack(item, amount));
        target.sendMessage(plugin.toLanguage("info-command-giveitem", sender.getName(), item.toString()));
        sender.sendMessage(plugin.toLanguage("info-command-itemgiven", item.toString()));

        return true;
    }

    @Override
    List<String> runTabComplete(CommandSender sender, String[] args) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    String getPermissionNode() {
        return "jailworker.give";
    }

    @Override
    String getDescription() {
        return "give an item to the prisoner.";
    }

    @Override
    String getUsage() {
        // TODO Auto-generated method stub
        return "/jailworker give <player> <material-name> [amount]";
    }
}
