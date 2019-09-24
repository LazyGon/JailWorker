package fr.alienationgaming.jailworker.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;
import org.bukkit.util.Vector;

import fr.alienationgaming.jailworker.Jail;
import fr.alienationgaming.jailworker.Utils;

public class Clean extends JWSubCommand {

    Clean() {
    }

    @Override
    boolean runCommand(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return false;
        }

        String jailName = args[1];

        if (!Jail.exist(jailName)) {
            sender.sendMessage(plugin.toLanguage("error-command-jailnotexist", jailName));
            return false;
        }

        if (!isAdminOrOwner(sender, jailName)) {
            sender.sendMessage(plugin.toLanguage("error-command-notowner"));
            return false;
        }

        Vector vec1 = plugin.getJailConfig().getVector("Jails." + jailName + ".Location.Block1");
        Vector vec2 = plugin.getJailConfig().getVector("Jails." + jailName + ".Location.Block2");
        World world = Bukkit.getWorld(plugin.getJailConfig().getString("Jails." + jailName + ".World"));
        Location block1 = new Location(world, vec1.getX(), vec1.getY(), vec1.getZ());
        Location block2 = new Location(world, vec2.getX(), vec2.getY(), vec2.getZ());
        Material material = Material.valueOf(plugin.getJailConfig().getString("Jails." + jailName + ".Type").toUpperCase());
        int var = Utils.removeBlockInRegion(material, block1, block2);
        if (material == Material.DIRT)
            var += Utils.removeBlockInRegion(Material.GRASS_BLOCK, block1, block2);
        sender.sendMessage(plugin.toLanguage("info-command-blocksdeleted", var));

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
        return "jailworker.clean";
    }

    @Override
    String getDescription() {
        return "delete all sand blocks on jail.";
    }

    @Override
    String getUsage() {
        return "/jailworker clean <jail-name>";
    }
}
