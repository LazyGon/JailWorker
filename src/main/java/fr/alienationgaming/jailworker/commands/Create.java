package fr.alienationgaming.jailworker.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.alienationgaming.jailworker.JailWorker;
import fr.alienationgaming.jailworker.listner.JWRegionSelectListener;

public class Create implements CommandExecutor {

    private JailWorker plugin;

    public Create(JailWorker jailworker) {
        plugin = jailworker;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("error-command-notconsole");
            return true;
        }
        Player player = (Player) sender;
        if (plugin.hasPerm(player, "jailworker.jw-admin") || (plugin.hasPerm(player, "jailworker.jw-create"))) {
            plugin.blockJail1.put(player, null);
            plugin.blockJail2.put(player, null);
            /* Listener */
            new JWRegionSelectListener(plugin, player);

            player.sendMessage(plugin.toLanguage("info-command-definetips"));
            player.sendMessage(plugin.toLanguage("info-command-waitfirstblk"));
        }
        return true;
    }
}
