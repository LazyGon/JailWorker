package fr.alienationgaming.jailworker.commands;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import fr.alienationgaming.jailworker.JailWorker;
import fr.alienationgaming.jailworker.Utils;

public class Clean implements CommandExecutor {

    JailWorker plugin;
    Utils utils = new Utils(plugin);

    public Clean(JailWorker jailWorker) {
        plugin = jailWorker;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (args.length == 0)
            return false;
        String jailName = args[0];

        if (sender instanceof ConsoleCommandSender || plugin.hasPerm((Player) sender, "jailworker.jw-admin") || (plugin.hasPerm((Player) sender, "jailworker.jw-clean") && plugin.getJailConfig().getStringList("Jails." + jailName + ".Owners").contains(((Player) sender).getName()))) {
            if (!plugin.getJailConfig().contains("Jails." + jailName)) {
                sender.sendMessage(plugin.toLanguage("error-command-jailnotexist", jailName));
                return true;
            }
            Vector vec1 = plugin.getJailConfig().getVector("Jails." + jailName + ".Location.Block1");
            Vector vec2 = plugin.getJailConfig().getVector("Jails." + jailName + ".Location.Block2");
            World world = plugin.getServer().getWorld(plugin.getJailConfig().getString("Jails." + jailName + ".World"));
            Location Blk1 = new Location(world, vec1.getX(), vec1.getY(), vec1.getZ());
            Location Blk2 = new Location(world, vec2.getX(), vec2.getY(), vec2.getZ());
            Material material = Material.getMaterial(plugin.getJailConfig().getString("Jails." + jailName + ".Type").toUpperCase());
            int var = utils.removeBlockInRegion(material, Blk1, Blk2);
            if (material == Material.DIRT)
                var += utils.removeBlockInRegion(Material.GRASS_BLOCK, Blk1, Blk2);
            sender.sendMessage(plugin.toLanguage("info-command-blocksdeleted", var));
        } else if (!plugin.getJailConfig().getStringList("Jails." + jailName + ".Owners").contains(((Player) sender).getName())) {
            sender.sendMessage(plugin.toLanguage("error-command-notowner"));
        }
        return true;
    }
}
