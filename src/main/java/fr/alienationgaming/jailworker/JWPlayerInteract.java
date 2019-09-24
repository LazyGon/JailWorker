package fr.alienationgaming.jailworker;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class JWPlayerInteract {
    JailWorker plugin;

    public JWPlayerInteract(JailWorker jailworker) {
        plugin = jailworker;
    }

    public void freePlayer(Player player) {
        Vector lastPos = null;
        World lastWorld = null;
        String playerName = null;
        playerName = player.getName();
        plugin.iv.clear(player);
        lastPos = plugin.getJailConfig().getVector("Prisoners." + playerName + ".PreviousPosition");
        lastWorld = Bukkit.getWorld(plugin.getJailConfig().getString("Prisoners." + player.getName() + ".PreviousWorld"));
        player.teleport(new Location(lastWorld, lastPos.getX(), lastPos.getY(), lastPos.getZ()));
        player.setGameMode(GameMode.valueOf(plugin.getJailConfig().getString("Prisoners." + playerName + ".Gamemode")));
        plugin.iv.restore(player);
        plugin.getJailConfig().set("Prisoners." + player.getName(), null);
        plugin.saveJailConfig();
        plugin.reloadJailConfig();

        player.sendMessage(plugin.toLanguage("info-other-nowfree"));
    }
}
