package fr.alienationgaming.jailworker;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

@SuppressWarnings("all")
public class Jail {

    private static JailWorker plugin = JailWorker.getInstance();
    private static final Map<String, Jail> jails = new HashMap<>();

    private World world;
    private String name;
    private Location location1 = null;
    private Location location2 = null;
    private Location spawn = null;
    private int blocksOnJail = 0;
    private long blockSpeed = 0;
    private int randomX = 0;
    private int randomY = 0;
    private int randomZ = 0;
    private Set<String> prisoners;
    private String type = null;
    FallingBlock fallingblock = null;
    Location randomLocation = null;
    private BukkitRunnable task;
    int sandMax = 0;

    public Jail(World world, String jailName) {
        this.world = world;
        this.name = jailName;
        Vector vector1 = plugin.getJailConfig().getVector("Jails." + jailName + ".Location.Block1");
        Vector vector2 = plugin.getJailConfig().getVector("Jails." + jailName + ".Location.Block2");
        Vector vectorSpawn = plugin.getJailConfig().getVector("Jails." + jailName + ".Location.PrisonerSpawn");
        location1 = new Location(world, vector1.getX(), vector1.getY(), vector1.getZ());
        location2 = new Location(world, vector2.getX(), vector2.getY(), vector2.getZ());
        spawn = new Location(world, vectorSpawn.getX(), vectorSpawn.getY(), vectorSpawn.getZ());
        // emptyBlocks = getEmptyBlocks();
        blockSpeed = plugin.getJailConfig().getLong("Jails." + jailName + ".Speed");
        sandMax = plugin.getJailConfig().getInt("Jails." + jailName + ".MaxSand");
        type = plugin.getJailConfig().getString("Jails." + jailName + ".Type");
        task = new BukkitRunnable() {
            @SuppressWarnings("deprecation")
            @Override
            public void run() {
                int lowerX = (int) Math.min(location1.getX(), location2.getX());
                int higherX = (int) (Math.max(location1.getX(), location2.getX()));
                int lowerY = (int) Math.min(location1.getY(), location2.getY());
                int higherY = (int) Math.max(location1.getY(), location2.getY());
                int lowerZ = (int) Math.min(location1.getZ(), location2.getZ());
                int higherZ = (int) (Math.max(location1.getZ(), location2.getZ()));
                blocksOnJail = Utils.countBlockInRegion(Material.valueOf(type.toUpperCase()), location1, location2);
                boolean notFull = (((higherX - lowerX) * (higherY - lowerY) * (higherZ - lowerZ)) > Utils
                        .countBlockInRegion(Material.AIR, location1, location2));
                // System.out.println("BlockOnjail/sandMax => " + blocksOnJail + "/" + sandMax);
                // System.out.println("notfull: " + (((higherX - lowerX) * (higherY - lowerY) *
                // (higherZ - lowerZ)) + ">" + utils.countBlockInRegion(-1, location1,
                // location2)));
                if ((blocksOnJail < sandMax) && notFull) {
                    randomX = (int) (Math.random() * (higherX - lowerX)) + lowerX;
                    randomY = (int) (Math.random() * (higherY - lowerY)) + lowerY;
                    randomZ = (int) (Math.random() * (higherZ - lowerZ)) + lowerZ;
                    randomLocation = new Location(world, randomX, randomY, randomZ);
                    while ((randomLocation.getBlock().getType() != Material.AIR)
                            || (randomX == spawn.getBlockX() && randomZ == spawn.getBlockZ())) {
                        randomX = (int) (Math.random() * (higherX - lowerX)) + lowerX;
                        randomY = (int) (Math.random() * (higherY - lowerY)) + lowerY;
                        randomZ = (int) (Math.random() * (higherZ - lowerZ)) + lowerZ;
                        randomLocation = new Location(location1.getWorld(), randomX, randomY, randomZ);
                    }

                    world.spawnFallingBlock(randomLocation, Material.getMaterial(type.toUpperCase()), (byte) 0);
                }
            }
        };

        task.runTaskTimer(plugin, 30L, (blockSpeed * 30));
    }

    // private ArrayList<Block> getEmptyBlocks() {
    // ArrayList<Block> list = new ArrayList<Block>();
    // int lowerX = (int) Math.min(location1.getX(), location2.getX());
    // int higherX = (int) (Math.max(location1.getX(), location2.getX()));
    // int lowerY = (int) Math.min(location1.getY(), location2.getY());
    // int higherY = (int) Math.max(location1.getY(), location2.getY());
    // int lowerZ = (int) Math.min(location1.getZ(), location2.getZ());
    // int higherZ = (int) (Math.max(location1.getZ(), location2.getZ()));
    // for (int x = lowerX; x < higherX; ++x) {
    // for (int y = lowerY; y < higherY; ++y) {
    // for (int z = lowerZ; z < higherZ; ++z) {
    // Block block = world.getBlockAt(x, y, z);
    // if (block.isEmpty())
    // list.add(block);
    // }
    // }
    // }
    // return list;
    // }

    public BukkitRunnable getTask() {
        return this.task;
    }

    public boolean isOwner(OfflinePlayer player) {
        if (player.getName() == null) {
            return false;
        }
        return plugin.getJailConfig()
                .getStringList("Jails." + name + ".Owners").contains(player.getName());
    }

    public static boolean exist(String name) {
        return plugin.getJailConfig().contains("Jails." + name);
        // TODO: return jails.containsKey(name);
    }

    public static boolean isJailed(OfflinePlayer player) {
        if (player.getName() == null) {
            return false;
        }
        return plugin.getJailConfig().contains("Prisoners." + player.getName());

        // TODO: USE THIS METHOD
        //return jails.values().stream().anyMatch(jail -> jail.prisoners.contains(player.getUniqueId().toString())
        //        || jail.prisoners.contains(player.getName()));
    }

    public boolean contains(OfflinePlayer player) {
        if (player.getName() == null) {
            return false;
        }
        
        if (isJailed(player)) {
            return false;
        }

        return plugin.getJailConfig().getString("Prisoners." + player.getName() + ".Prison").equals(name);
    }

    public static Jail getJail(String name) {
        return jails.get(name);
    }
}