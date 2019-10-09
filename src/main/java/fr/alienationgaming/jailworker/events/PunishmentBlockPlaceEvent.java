package fr.alienationgaming.jailworker.events;

import org.bukkit.block.Block;

public class PunishmentBlockPlaceEvent extends JailWorkerCancellableEvent {

    private final String jailName;
    private Block block;

    public PunishmentBlockPlaceEvent(String jailName, Block block) {
        cancel = false;
        this.jailName = jailName;
        this.block = block;
    }

    /**
     * @return the jailName
     */
    public String getJailName() {
        return jailName;
    }
    
    /**
     * @return the block
     */
    public Block getBlock() {
        return block;
    }

    /**
     * @param block the block to set
     */
    public void setBlock(Block block) {
        this.block = block;
    }
}