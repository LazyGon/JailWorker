package fr.alienationgaming.jailworker.listner;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.util.Vector;

import fr.alienationgaming.jailworker.JailWorker;

public class JWPrisonerDieListener implements Listener {

	JailWorker plugin;
	public JWPrisonerDieListener(JailWorker jailworker){
		plugin = jailworker;
	}
	
	 	@EventHandler(priority = EventPriority.HIGHEST)
	    public void onPlayerRespawn(PlayerRespawnEvent event) {
	        
		 Player player = event.getPlayer();
		 if (plugin.getJailConfig().contains("Prisoners." + player.getName())){
			 String jailName = plugin.getJailConfig().getString("Prisoners." + player.getName() + ".Prison");
			 Vector spawn = plugin.getJailConfig().getVector("Jails." + jailName + ".Location.PrisonerSpawn");
			 World world = plugin.getServer().getWorld(plugin.getJailConfig().getString("Jails." + jailName + ".World"));
			 event.setRespawnLocation(new Location(world, spawn.getX(), spawn.getY(), spawn.getZ()));
			 }
	 	}
}
