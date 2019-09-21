package fr.stevecohen.jailworker.configsign;

import fr.alienationgaming.jailworker.JailWorker;

public class JailConfig {

    private JailWorker plugin;
    private String jailname;

    private String type;
    private int blocks;
    private int maxSand;
    private int speed;

    public JailConfig(String jailname) {
        this.plugin = JailWorker.getInstance();
        this.jailname = jailname;
    }

    public boolean loadJailConfig() {
        if (plugin.getJailConfig().contains("Jails." + jailname)) {
            setType(plugin.getJailConfig().getString("Jails." + jailname + ".Type"));
            setBlocks(plugin.getJailConfig().getInt("Jails." + jailname + ".Blocks"));
            setMaxSand(plugin.getJailConfig().getInt("Jails." + jailname + ".MaxSand"));
            setSpeed(plugin.getJailConfig().getInt("Jails." + jailname + ".Speed"));
            return true;
        }
        return false;
    }

    public boolean saveJailConfig() {
        if (plugin.getJailConfig().contains("Jails." + jailname)) {
            plugin.getJailConfig().set("Jails." + jailname + ".Type", type);
            plugin.getJailConfig().set("Jails." + jailname + ".Blocks", blocks);
            plugin.getJailConfig().set("Jails." + jailname + ".MaxSand", maxSand);
            plugin.getJailConfig().set("Jails." + jailname + ".Speed", speed);
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
