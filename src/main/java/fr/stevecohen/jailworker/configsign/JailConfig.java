package fr.stevecohen.jailworker.configsign;

import fr.alienationgaming.jailworker.Jail;
import fr.alienationgaming.jailworker.JailWorker;

public class JailConfig {

    private JailWorker plugin;
    private String jailName;

    private String type;
    private int blocks;
    private int maxSand;
    private int speed;

    public JailConfig(String jailName) {
        this.plugin = JailWorker.getInstance();
        this.jailName = jailName;
    }

    public boolean loadJailConfig() {
        if (Jail.exist(jailName)) {
            setType(plugin.getJailConfig().getString("Jails." + jailName + ".Type"));
            setBlocks(plugin.getJailConfig().getInt("Jails." + jailName + ".Blocks"));
            setMaxSand(plugin.getJailConfig().getInt("Jails." + jailName + ".MaxSand"));
            setSpeed(plugin.getJailConfig().getInt("Jails." + jailName + ".Speed"));
            return true;
        }
        return false;
    }

    public boolean saveJailConfig() {
        if (Jail.exist(jailName)) {
            plugin.getJailConfig().set("Jails." + jailName + ".Type", type);
            plugin.getJailConfig().set("Jails." + jailName + ".Blocks", blocks);
            plugin.getJailConfig().set("Jails." + jailName + ".MaxSand", maxSand);
            plugin.getJailConfig().set("Jails." + jailName + ".Speed", speed);
            plugin.saveJailConfig();
            plugin.reloadJailConfig();
            return true;
        }
        return false;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getBlocks() {
        return blocks;
    }

    public void setBlocks(int blocks) {
        this.blocks = blocks;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getMaxSand() {
        return maxSand;
    }

    public void setMaxSand(int maxSand) {
        this.maxSand = maxSand;
    }
}
