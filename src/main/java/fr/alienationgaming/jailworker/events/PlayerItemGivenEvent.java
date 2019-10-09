package fr.alienationgaming.jailworker.events;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerItemGivenEvent extends JailWorkerCancellableEvent {

    private final Player player;
    private ItemStack item;

    public PlayerItemGivenEvent(Player player, ItemStack item) {
        cancel = false;
        this.player = player;
        this.item = item;
    }

    /**
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @return the item
     */
    public ItemStack getItem() {
        return item;
    }

    /**
     * @param item the item to set
     */
    public void setItem(ItemStack item) {
        this.item = item;
    }
}