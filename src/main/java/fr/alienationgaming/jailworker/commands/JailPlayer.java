package fr.alienationgaming.jailworker.commands;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import fr.alienationgaming.jailworker.JWInventorySaver;
import fr.alienationgaming.jailworker.JailWorker;
import fr.alienationgaming.jailworker.Utils;

public class JailPlayer implements CommandExecutor {

    JailWorker plugin;
    Utils utils = new Utils(plugin);

    public JailPlayer(JailWorker jailworker) {
        plugin = jailworker;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (args.length < 2)
            return false;
        String jailName = args[1];
        if (sender instanceof ConsoleCommandSender || plugin.hasPerm(((Player) sender), "jailworker.jw-admin") || (plugin.hasPerm(((Player) sender), "jailworker.jw-player") && plugin.getJailConfig().getStringList("Jails." + jailName + ".Owners").contains(((Player) sender).getName()))) {
            Player target = plugin.getServer().getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(plugin.toLanguage("error-command-playeroffline", args[0]));
                return true;
            }
            if (!plugin.getJailConfig().contains("Jails." + jailName)) {
                sender.sendMessage(plugin.toLanguage("error-command-jailnotexist", jailName));
                return true;
            }
            if (!plugin.getJailConfig().getBoolean("Jails." + jailName + ".isStarted")) {
                sender.sendMessage(plugin.toLanguage("error-command-notstarted", jailName));
                return true;
            }

            /* Get nbr blocks to break by default for the jail */
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
            String cause = "";
            if (args.length >= 4) {
                for (int i = 3; i < args.length; ++i) {
                    cause += args[i];
                    cause += " ";
                }
            } else
                cause = "No Reason.";
            if (blocks < 0) {
                sender.sendMessage(plugin.toLanguage("error-command-invalidumber"));
                return true;
            }
            /* Get inventory */
            JWInventorySaver invSaver = new JWInventorySaver(plugin);

            /* Create Section prisoner for target */
            plugin.getJailConfig().set("Prisoners." + target.getName() + ".Prison", jailName);
            plugin.getJailConfig().set("Prisoners." + target.getName() + ".Punisher", sender.getName());
            plugin.getJailConfig().set("Prisoners." + target.getName() + ".Date", utils.getDate());
            plugin.getJailConfig().set("Prisoners." + target.getName() + ".PreviousPosition", target.getLocation().toVector());
            plugin.getJailConfig().set("Prisoners." + target.getName() + ".PreviousWorld", target.getWorld().getName());
            plugin.getJailConfig().set("Prisoners." + target.getName() + ".PunishToBreak", blocks);
            plugin.getJailConfig().set("Prisoners." + target.getName() + ".RemainingBlocks", blocks);
            plugin.getJailConfig().set("Prisoners." + target.getName() + ".Cause", cause);
            plugin.getJailConfig().set("Prisoners." + target.getName() + ".Gamemode", target.getGameMode().name());
            if (target.getGameMode() == GameMode.CREATIVE)
                target.setGameMode(GameMode.SURVIVAL);
            invSaver.save(target);
            invSaver.clear(target);
            plugin.saveJailConfig();
            plugin.reloadJailConfig();

            /* Send Player to jail */
            target.getInventory().clear();
            target.getEquipment().clear();
            Vector spawn = plugin.getJailConfig().getVector("Jails." + jailName + ".Location.PrisonerSpawn");
            World world = plugin.getServer().getWorld(plugin.getJailConfig().getString("Jails." + jailName + ".World"));
            target.teleport(new Location(world, spawn.getX(), spawn.getY(), spawn.getZ()));
            plugin.getServer().broadcastMessage(plugin.toLanguage("info-command-broadcastpunish", target.getName(), jailName, sender.getName()));
            plugin.getServer().broadcastMessage(plugin.toLanguage("info-command-broadcastcantear"));
            target.sendMessage(plugin.toLanguage("info-command-sendtojail", sender.getName()));
            if (!cause.isEmpty() && cause != "No Reason.")
                target.sendMessage(plugin.toLanguage("info-command-displayreason", cause));
            target.sendMessage(plugin.toLanguage("info-command-prisonerorder", blocks, plugin.getJailConfig().getString("Jails." + jailName + ".Type")));
        }
        return true;
    }
}
