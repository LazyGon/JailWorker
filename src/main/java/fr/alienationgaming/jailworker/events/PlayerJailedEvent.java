package fr.alienationgaming.jailworker.events;

import org.bukkit.OfflinePlayer;

public class PlayerJailedEvent extends JailWorkerCancellableEvent {

    private final OfflinePlayer player;
    private String jailName;
    private OfflinePlayer punisher;
    private int punishmentPoint;
    private String reason;

    public PlayerJailedEvent(OfflinePlayer player, String jailName, OfflinePlayer punisher, int punishmentPoint, String reason) {
        cancel = false;
        this.player = player;
        this.jailName = jailName;
        this.punisher = punisher;
        this.punishmentPoint = punishmentPoint;
        this.reason = reason;
    }

    /**
     * @return the player
     */
    public OfflinePlayer getPlayer() {
        return player;
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
     * @return the punisher
     */
    public OfflinePlayer getPunisher() {
        return punisher;
    }

    /**
     * @param punisher the punisher to set
     */
    public void setPunisher(OfflinePlayer punisher) {
        this.punisher = punisher;
    }

    /**
     * @return the punishmentPoint
     */
    public int getPunishmentPoint() {
        return punishmentPoint;
    }

    /**
     * @param punishmentPoint the punishmentPoint to set
     */
    public void setPunishmentPoint(int punishmentPoint) {
        this.punishmentPoint = punishmentPoint;
    }

    /**
     * @return the reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * @param reason the reason to set
     */
    public void setReason(String reason) {
        this.reason = reason;
    }
}