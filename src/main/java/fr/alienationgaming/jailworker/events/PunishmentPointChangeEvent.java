package fr.alienationgaming.jailworker.events;

import org.bukkit.OfflinePlayer;

public class PunishmentPointChangeEvent extends JailWorkerCancellableEvent {

    private final OfflinePlayer player;
    private int previousPunishmentPoint;
    private int newPunishmentPoint;

    public PunishmentPointChangeEvent(OfflinePlayer player, int previousPunishmentPoint, int newPunishmentPoint) {
        cancel = false;
        this.player = player;
        this.previousPunishmentPoint = previousPunishmentPoint;
        this.newPunishmentPoint = newPunishmentPoint;
    }

    /**
     * @return the player
     */
    public OfflinePlayer getPlayer() {
        return player;
    }
    
    /**
     * @return the previousPunishmentPoint
     */
    public int getPreviousPunishmentPoint() {
        return previousPunishmentPoint;
    }
    
    /**
     * @return the newPunishmentPoint
     */
    public int getNewPunishmentPoint() {
        return newPunishmentPoint;
    }

    /**
     * @param newPunishmentPoint the newPunishmentPoint to set
     */
    public void setNewPunishmentPoint(int newPunishmentPoint) {
        this.newPunishmentPoint = newPunishmentPoint;
    }
}