package fr.alienationgaming.jailworker.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PunishmentBlockBreakEvent extends JailWorkerCancellableEvent {

    private final Player player;
    private final Block block;

    public PunishmentBlockBreakEvent(Player player, Block block) {
        cancel = false;
        this.player = player;
        this.block = block;
    }

    /**
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @return the block
     */
    public Block getBlock() {
        return block;
    }
}