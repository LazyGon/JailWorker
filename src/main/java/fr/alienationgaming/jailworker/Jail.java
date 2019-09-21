package fr.alienationgaming.jailworker;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.FallingBlock;
import org.bukkit.util.Vector;

public class Jail {

    private World world;
    private JailWorker plugin;
    private String jailName;
    private Utils utils = new Utils(plugin);
    private Vector vec1 = null;
    private Vector vec2 = null;
    // private List<Block> emptyBlocks;
    private Vector spwn = null;
    private Location Blk1 = null;
    private Location Blk2 = null;
    private Location BlkSpwn = null;
    private int blocksOnJail = 0;
    private long speed = 0;
    private int randomX = 0;
    private int randomY = 0;
    private int randomZ = 0;
    private String type = null;
    FallingBlock fallingblock = null;
    Location randomLocation = null;
    int taskid;
    int sandMax = 0;

    public Jail(JailWorker jailworker, World _world, String _jailName) {
        plugin = jailworker;
        world = _world;
        jailName = _jailName;
        vec1 = plugin.getJailConfig().getVector("Jails." + jailName + ".Location.Block1");
        vec2 = plugin.getJailConfig().getVector("Jails." + jailName + ".Location.Block2");
        spwn = plugin.getJailConfig().getVector("Jails." + jailName + ".Location.PrisonerSpawn");
        Blk1 = new Location(world, vec1.getX(), vec1.getY(), vec1.getZ());
        Blk2 = new Location(world, vec2.getX(), vec2.getY(), vec2.getZ());
        BlkSpwn = new Location(world, spwn.getX(), spwn.getY(), spwn.getZ());
//		emptyBlocks = getEmptyBlocks();
        speed = plugin.getJailConfig().getLong("Jails." + jailName + ".Speed");
        sandMax = plugin.getJailConfig().getInt("Jails." + jailName + ".MaxSand");
        type = plugin.getJailConfig().getString("Jails." + jailName + ".Type");
        taskid = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @SuppressWarnings("deprecation")
            @Override
            public void run() {
                int lowerX = (int) Math.min(Blk1.getX(), Blk2.getX());
                int higherX = (int) (Math.max(Blk1.getX(), Blk2.getX()));
                int lowerY = (int) Math.min(Blk1.getY(), Blk2.getY());
                int higherY = (int) Math.max(Blk1.getY(), Blk2.getY());
                int lowerZ = (int) Math.min(Blk1.getZ(), Blk2.getZ());
                int higherZ = (int) (Math.max(Blk1.getZ(), Blk2.getZ()));
                blocksOnJail = utils.countBlockInRegion(Material.valueOf(type.toUpperCase()), Blk1, Blk2);
                boolean notFull = (((higherX - lowerX) * (higherY - lowerY) * (higherZ - lowerZ)) > utils.countBlockInRegion(Material.AIR, Blk1, Blk2));
                //System.out.println("BlockOnjail/sandMax => " + blocksOnJail + "/" + sandMax);
                //System.out.println("notfull: "  + (((higherX - lowerX) * (higherY - lowerY) * (higherZ - lowerZ)) + ">" + utils.countBlockInRegion(-1, Blk1, Blk2)));
                if ((blocksOnJail < sandMax) && notFull) {
                    randomX = (int) (Math.random() * (higherX - lowerX)) + lowerX;
                    randomY = (int) (Math.random() * (higherY - lowerY)) + lowerY;
                    randomZ = (int) (Math.random() * (higherZ - lowerZ)) + lowerZ;
                    randomLocation = new Location(world, randomX, randomY, randomZ);
                    while ((randomLocation.getBlock().getType() != Material.AIR) || (randomX == BlkSpwn.getBlockX() && randomZ == BlkSpwn.getBlockZ())) {
                        randomX = (int) (Math.random() * (higherX - lowerX)) + lowerX;
                        randomY = (int) (Math.random() * (higherY - lowerY)) + lowerY;
                        randomZ = (int) (Math.random() * (higherZ - lowerZ)) + lowerZ;
                        randomLocation = new Location(Blk1.getWorld(), randomX, randomY, randomZ);
                    }

                    world.spawnFallingBlock(randomLocation, Material.getMaterial(type.toUpperCase()), (byte) 0);
                }
            }
        }, 30L, (speed * 30));
    }

//	private ArrayList<Block> getEmptyBlocks() {
//		ArrayList<Block> list = new ArrayList<Block>();
//		int lowerX = (int) Math.min(Blk1.getX(), Blk2.getX());
//		int higherX = (int) (Math.max(Blk1.getX(), Blk2.getX()));
//		int lowerY = (int) Math.min(Blk1.getY(), Blk2.getY());
//		int higherY = (int) Math.max(Blk1.getY(), Blk2.getY());
//		int lowerZ = (int) Math.min(Blk1.getZ(), Blk2.getZ());
//		int higherZ = (int) (Math.max(Blk1.getZ(), Blk2.getZ()));
//		for (int x = lowerX; x < higherX; ++x) {
//			for (int y = lowerY; y < higherY; ++y) {
//				for (int z = lowerZ; z < higherZ; ++z) {
//					Block block = world.getBlockAt(x, y, z);
//					if (block.isEmpty())
//						list.add(block);
//				}
//			}
//		}
//		return list;
//	}

    public int getTaskId() {
        return this.taskid;
    }
}