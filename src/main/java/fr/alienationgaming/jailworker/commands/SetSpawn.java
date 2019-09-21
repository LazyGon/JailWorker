package fr.alienationgaming.jailworker.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.alienationgaming.jailworker.JailWorker;
import fr.alienationgaming.jailworker.listner.JWSelectPrisonerSpawn;

public class SetSpawn implements CommandExecutor {

    private JailWorker plugin;

    public SetSpawn(JailWorker jailWorker) {
        plugin = jailWorker;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("error-command-notconsole");
            return true;
        }
        Player player = (Player) sender;
        if (plugin.hasPerm(player, "jailworker.jw-admin") || plugin.hasPerm(player, "jailworker.jw-create")) {
            plugin.JailPrisonerSpawn.put(player, null);
            /* Listener */
            new JWSelectPrisonerSpawn(plugin, player);
            player.sendMessage(plugin.toLanguage("info-command-definespawnblk"));
        }
        return true;
    }


}
