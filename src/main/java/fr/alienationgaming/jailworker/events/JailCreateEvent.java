package fr.alienationgaming.jailworker.events;

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;

public class JailCreateEvent extends JailWorkerCancellableEvent {

    private String jailName;
    private int maxPunishmentBlocks;
    private int punishmentBlockInterval;
    private Set<Material> punishmentBlocks;
    private Location pos1;
    private Location pos2;
    private Location spawn;

    public JailCreateEvent(String jailName, int maxPunishmentBlocks, int punishmentBlockInterval, Set<Material> punishmentBlocks, Location pos1, Location pos2, Location spawn) {
        cancel = false;
        this.jailName = jailName;
        this.maxPunishmentBlocks = maxPunishmentBlocks;
        this.punishmentBlockInterval = punishmentBlockInterval;
        this.punishmentBlocks = punishmentBlocks;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.spawn = spawn;
    }

    /**
     * @return the jailName
     */
    public String getJailName() {
        return jailName;
    }

    /**
     * @param jailName the jailName to set
     */
    public void setJailName(String jailName) {
        this.jailName = jailName;
    }

    /**
     * @return the maxPunishmentBlocks
     */
    public int getMaxPunishmentBlocks() {
        return maxPunishmentBlocks;
    }

    /**
     * @param maxPunishmentBlocks the maxPunishmentBlocks to set
     */
    public void setMaxPunishmentBlocks(int maxPunishmentBlocks) {
        this.maxPunishmentBlocks = maxPunishmentBlocks;
    }

    /**
     * @return the punishmentBlockInterval
     */
    public int getPunishmentBlockInterval() {
        return punishmentBlockInterval;
    }

    /**
     * @param punishmentBlockInterval the punishmentBlockInterval to set
     */
    public void setPunishmentBlockInterval(int punishmentBlockInterval) {
        this.punishmentBlockInterval = punishmentBlockInterval;
    }

    /**
     * @return the punishmentBlocks
     */
    public Set<Material> getPunishmentBlocks() {
        return punishmentBlocks;
    }

    /**
     * @param punishmentBlocks the punishmentBlocks to set
     */
    public void setPunishmentBlocks(Set<Material> punishmentBlocks) {
        this.punishmentBlocks = punishmentBlocks;
    }

    /**
     * @return the pos1
     */
    public Location getPos1() {
        return pos1;
    }

    /**
     * @param pos1 the pos1 to set
     */
    public void setPos1(Location pos1) {
        this.pos1 = pos1;
    }

    /**
     * @return the pos2
     */
    public Location getPos2() {
        return pos2;
    }

    /**
     * @param pos2 the pos2 to set
     */
    public void setPos2(Location pos2) {
        this.pos2 = pos2;
    }

    /**
     * @return the spawn
     */
    public Location getSpawn() {
        return spawn;
    }

    /**
     * @param spawn the spawn to set
     */
    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }
}