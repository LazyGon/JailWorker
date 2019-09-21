package fr.alienationgaming.jailworker.listner;

import java.util.Iterator;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import fr.alienationgaming.jailworker.GetConfigValues;
import fr.alienationgaming.jailworker.JailWorker;

public class JWChatPrisonerPrevent implements Listener {

	private JailWorker plugin;
	public JWChatPrisonerPrevent(JailWorker jailworker){
		plugin = jailworker;
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void PlayerCommand(AsyncPlayerChatEvent event){
		Player player = event.getPlayer();
		GetConfigValues gcv = new GetConfigValues(plugin);
		if (!gcv.prisonersSpeak())
		{
			if (plugin.getJailConfig().contains("Prisoners." + player.getName())){
				event.setCancelled(true);
				player.sendMessage(plugin.toLanguage("info-listener-justwork"));
			}
		}
		
		if (!gcv.prisonersEar())
		{
			Set<String> s = plugin.getJailConfig().getConfigurationSection("Prisoners").getKeys(false);
			Iterator<String> it = s.iterator();
			while (it.hasNext()){
				String elem = (String) it.next();
				event.getRecipients().remove(plugin.getServer().getPlayer(elem));
			}
		}
	}
}