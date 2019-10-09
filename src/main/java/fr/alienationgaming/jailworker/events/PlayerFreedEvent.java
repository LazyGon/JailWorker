package fr.alienationgaming.jailworker.events;

import org.bukkit.OfflinePlayer;

public class PlayerFreedEvent extends JailWorkerCancellableEvent {

    private final OfflinePlayer player;

    public PlayerFreedEvent(OfflinePlayer player) {
        cancel = false;
        this.player = player;
    }

    /**
     * @return the player
     */
    public OfflinePlayer getPlayer() {
        return player;
    }
}