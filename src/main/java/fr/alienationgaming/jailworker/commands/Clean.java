package fr.alienationgaming.jailworker.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.util.Vector;

import fr.alienationgaming.jailworker.Jail;
import fr.alienationgaming.jailworker.Utils;

public class Clean extends JWSubCommand {

    Clean() {
    }

    @Override
    boolean runCommand(CommandSender sender, String[] args) {
        String jailName = args[0];

        if (!Jail.exist(jailName)) {
            sender.sendMessage(plugin.toLanguage("error-command-jailnotexist", jailName));
            return false;
        }

        if (!isAdminOrOwner(sender, jailName)) {
            return false;
        }

        Vector vec1 = plugin.getJailConfig().getVector("Jails." + jailName + ".Location.Block1");
        Vector vec2 = plugin.getJailConfig().getVector("Jails." + jailName + ".Location.Block2");
        World world = Bukkit.getWorld(plugin.getJailConfig().getString("Jails." + jailName + ".World"));
        Location Blk1 = new Location(world, vec1.getX(), vec1.getY(), vec1.getZ());
        Location Blk2 = new Location(world, vec2.getX(), vec2.getY(), vec2.getZ());
        Material material = Material.valueOf(plugin.getJailConfig().getString("Jails." + jailName + ".Type").toUpperCase());
        int var = Utils.removeBlockInRegion(material, Blk1, Blk2);
        if (material == Material.DIRT)
            var += Utils.removeBlockInRegion(Material.GRASS_BLOCK, Blk1, Blk2);
        sender.sendMessage(plugin.toLanguage("info-command-blocksdeleted", var));

        return true;
    }

    @Override
    List<String> runTabComplete(CommandSender sender, String[] args) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    String getPermissionNode() {
        // TODO Auto-generated method stub
        return "jailworker.clean";
    }

    @Override
    String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }
}
