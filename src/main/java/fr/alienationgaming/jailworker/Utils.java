package fr.alienationgaming.jailworker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public final class Utils {

    //private static JailWorker plugin = JailWorker.getInstance();

    private Utils(JailWorker jailWorker) {
    }

    public static String getDate() {
        /* Get Date*/
        @SuppressWarnings("unused")
        Locale locale = Locale.getDefault();
        Date actuelle = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String date = dateFormat.format(actuelle);

        return date;
    }

    public static boolean isInRegion(Location loc, Location loc1, Location loc2) {
        if (loc1.getWorld().equals(loc2.getWorld()) && loc.getWorld().equals(loc1.getWorld()) && ((Math.min(loc1.getBlockX(), loc2.getBlockX()) <= loc.getBlockX()) && (loc.getBlockX() <= Math.max(loc1.getBlockX(), loc2.getBlockX()))) &&
                ((Math.min(loc1.getBlockY(), loc2.getBlockY()) <= loc.getBlockY()) && (loc.getBlockY() <= Math.max(loc1.getBlockY(), loc2.getBlockY()))) &&
                ((Math.min(loc1.getBlockZ(), loc2.getBlockZ()) <= loc.getBlockZ()) && (loc.getBlockZ() <= Math.max(loc1.getBlockZ(), loc2.getBlockZ())))) {
            return true;
        }
        return false;
    }

    public static void printBlockPos(Player player, Block block) {
        player.sendMessage(ChatColor.BLUE + "x :" + ChatColor.RESET + block.getX());
        player.sendMessage(ChatColor.BLUE + "y :" + ChatColor.RESET + block.getY());
        player.sendMessage(ChatColor.BLUE + "z :" + ChatColor.RESET + block.getZ());
    }

    public static int countBlockInRegion(Material blockMaterial, Location loc1, Location loc2) {
        int result = 0;
        int x = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int y = Math.min(loc1.getBlockY(), loc2.getBlockY()) - 1;
        int z = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        Location tempLocation = new Location(loc1.getWorld(), x, y, z);

        while (z < Math.max(loc1.getBlockZ(), loc2.getBlockZ())) {
            y = Math.min(loc1.getBlockY(), loc2.getBlockY()) - 1;
            while (y < Math.max(loc1.getBlockY(), loc2.getBlockY())) {
                x = Math.min(loc1.getBlockX(), loc2.getBlockX());
                while (x < Math.max(loc1.getX(), loc2.getX())) {
                    tempLocation.setX(x);
                    if (tempLocation.getBlock().getType() == blockMaterial || tempLocation.getBlock().getType() != Material.AIR) {
                        result++;
                    }
                    x++;
                }
                y++;
                tempLocation.setY(y);
            }
            z++;
            tempLocation.setZ(z);
        }
        return result;
    }

    public static int removeBlockInRegion(Material blockMaterial, Location loc1, Location loc2) {
        int result = 0;
        int x = (int) Math.min(loc1.getX(), loc2.getX());
        int y = (int) (Math.min(loc1.getY(), loc2.getY()) - 1);
        int z = (int) Math.min(loc1.getZ(), loc2.getZ());
        Location tempLocation = new Location(loc1.getWorld(), x, y, z);

        while (z <= Math.max(loc1.getZ(), loc2.getZ())) {
            y = (int) (Math.min(loc1.getY(), loc2.getY()) - 1);
            while (y <= Math.max(loc1.getY(), loc2.getY())) {
                x = (int) Math.min(loc1.getX(), loc2.getX());
                while (x <= Math.max(loc1.getX(), loc2.getX())) {
                    tempLocation.setX(x);
                    if (tempLocation.getBlock().getType() == blockMaterial) {
                        tempLocation.getBlock().setType(Material.AIR);
                        result++;
                    }
                    x++;
                }
                y++;
                tempLocation.setY(y);
            }
            z++;
            tempLocation.setZ(z);
        }
        return result;
    }
}
