package fr.stevecohen.jailworker.configsign;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import fr.alienationgaming.jailworker.JailWorker;
import fr.stevecohen.jailworker.configsign.SignConfigurator.SignClickedCallback;

public class OnConfigSignClickedListener implements Listener {

	private JailWorker 					plugin;
	private MySign 						sign;
	private Player						processingPlayer;
	private SignClickedCallback 		callback;
	
	public OnConfigSignClickedListener(MySign sign, Player processingPlayer, SignClickedCallback callback) {
		plugin = JailWorker.getInstance();
		this.sign = sign;
		this.processingPlayer = processingPlayer;
		this.callback = callback;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		System.out.println("ClickEvent registered");
	}

	@EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {
		
		System.out.println("Click catched!");
		Player player = event.getPlayer();
		if (hasRightClickedOnTheSign(event) && isDispatcherPoocessingPlayer(player)) {
			callback.onSignClicked();
		}
	}

	private boolean isDispatcherPoocessingPlayer(Player player) {
		if (player == this.processingPlayer)
			return true;
		return false;
	}

	private boolean hasRightClickedOnTheSign(PlayerInteractEvent event) {
		if (event.getClickedBlock() != null &&
				event.getAction() == Action.RIGHT_CLICK_BLOCK &&
				event.getClickedBlock().getLocation().distance(this.sign.location) == 0) {
			return true;
		}
		return false;
	}
}
