package fr.alienationgaming.jailworker.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.StringUtil;

import fr.alienationgaming.jailworker.JailWorker;
import fr.alienationgaming.jailworker.config.JailConfig;
import fr.alienationgaming.jailworker.config.Messages;

public class Create extends SubCommand {

    private static JailWorker plugin = JailWorker.getInstance();
    final static Map<Player, InputGetter> data = new HashMap<>();

    private class InputGetter implements Listener {

        private final String jailName;
        private final Player player;
        private int maxPunishmentBlocks = 30;
        private int punishmentBlockSpeed = 10;
        private Location pos1;
        private Location pos2;
        private Location spawn;

        InputGetter(String jailName, Player player) {
            this.jailName = jailName;
            this.player = player;
            Bukkit.getPluginManager().registerEvents(this, plugin);
            Region region = getWorldEditSelection();
            if (region == null) {
                Messages.sendMessage(player, "command.create.info.define-tips");
                Messages.sendMessage(player, "command.create.info.waiting-for-first");
            } else {
                World world = Bukkit.getWorld(region.getWorld().getName());
                BlockVector3 max = region.getMaximumPoint();
                BlockVector3 min = region.getMinimumPoint();
                pos1 = new Location(world, max.getX(), max.getY(), max.getZ());
                pos2 = new Location(world, min.getX(), min.getY(), min.getZ());
                Messages.sendMessage(player, "command.create.info.retrieve-worldedit");
            }
        }

        InputGetter(String jailName, Player player, int maxPunishmentBlocks) {
            this(jailName, player);
            if (maxPunishmentBlocks >= 0) {
                this.maxPunishmentBlocks = maxPunishmentBlocks;
            }
        }

        InputGetter(String jailName, Player player, int maxPunishmentBlocks, int punishmentBlockSpeed) {
            this(jailName, player);
            if (maxPunishmentBlocks >= 0) {
                this.maxPunishmentBlocks = maxPunishmentBlocks;
            }

            if (punishmentBlockSpeed >= 0) {
                this.punishmentBlockSpeed = punishmentBlockSpeed;
            }
        }

        @EventHandler
        public void onPlayerInteract(PlayerInteractEvent event) {
            if (event.getHand() == EquipmentSlot.OFF_HAND) {
                return;
            }

            Block block = event.getClickedBlock();
            if (block == null || event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                return;
            }

            event.setCancelled(true);
            Location pos = block.getLocation();
            if (pos1 == null) {
                pos1 = pos;
                player.sendMessage(ChatColor.BLUE + "Position 1:");
                printLocation(pos);
                Messages.sendMessage(player, "command.create.info.waiting-for-second");
            } else if (pos2 == null) {
                pos2 = pos;
                player.sendMessage(ChatColor.BLUE + "Position 2:");
                printLocation(pos);
                Messages.sendMessage(player, "command.create.info.waiting-for-spawn");
            } else if (spawn == null) {
                spawn = pos.add(0.5, 1, 0.5);
                player.sendMessage(ChatColor.BLUE + "Spawn:");
                printLocation(pos);
                Messages.sendMessage(player, "command.create.info.finish", Map.of("%jail-name%", jailName));
                HandlerList.unregisterAll(this);
                data.remove(player);
                JailConfig.addJail(jailName, maxPunishmentBlocks, punishmentBlockSpeed, List.of(), pos.getWorld(), pos1,
                        pos2, spawn);
            }
        }

        @EventHandler
        private void onLogOut(PlayerQuitEvent event) {
            if (event.getPlayer().equals(player) || data.containsKey(player)) {
                HandlerList.unregisterAll(this);
                data.remove(player);
            }
        }

        private void printLocation(Location pos) {
            player.sendMessage(ChatColor.BLUE + "x :" + ChatColor.RESET + pos.getX());
            player.sendMessage(ChatColor.BLUE + "y :" + ChatColor.RESET + pos.getY());
            player.sendMessage(ChatColor.BLUE + "z :" + ChatColor.RESET + pos.getZ());
        }

        private Region getWorldEditSelection() {
            if (!Bukkit.getPluginManager().isPluginEnabled("WorldEdit")) {
                return null;
            }
            com.sk89q.worldedit.entity.Player wePlayer = BukkitAdapter.adapt(player);
            LocalSession playerSession = WorldEdit.getInstance().getSessionManager().get(wePlayer);
            if (playerSession.getSelectionWorld() == null) {
                return null;
            }

            try {
                return playerSession.getSelection(playerSession.getSelectionWorld());
            } catch (IncompleteRegionException e) {
                return null;
            }
        }
    }

    Create() {
    }

    @Override
    boolean runCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            Messages.sendMessage(sender, "command.general.error.only-player");
            return false;
        }

        if (args.length == 1) {
            Messages.sendMessage(sender, "command.general.error.not-enough-arguments");
            return false;
        }

        if (data.containsKey(sender)) {
            Messages.sendMessage(sender, "command.create.error.you-are-already-in-creation",
                    Map.of("%jail-name%", data.get(sender).jailName));
            return false;
        }

        String jailName = args[1];
        if (!hasPermission(sender, jailName)) {
            Messages.sendMessage(sender, "command.general.error.no-permission");
            return false;
        }

        if (JailConfig.exist(jailName)) {
            Messages.sendMessage(sender, "command.create.error.jail-already-exists", Map.of("%jail-name%", jailName));
            return false;
        }

        if (args.length == 2) {
            new InputGetter(args[1], (Player) sender);
            return true;
        }

        if (args.length == 3) {
            try {
                new InputGetter(args[1], (Player) sender, Integer.parseInt(args[2]));
            } catch (NumberFormatException e) {
                new InputGetter(args[1], (Player) sender);
            }
            return true;
        }

        if (args.length >= 4) {
            try {
                new InputGetter(args[1], (Player) sender, Integer.parseInt(args[2]), Integer.parseInt(args[3]));
            } catch (NumberFormatException e) {
                new InputGetter(args[1], (Player) sender);
            }
            return true;
        }

        return true;
    }

    @Override
    List<String> runTabComplete(CommandSender sender, String[] args) {
        List<String> result = new ArrayList<>();
        if (args.length == 2) {
            return StringUtil.copyPartialMatches(args[1], List.of("r<jail-name>"), result);
        }

        if (args.length == 3) {
            return StringUtil.copyPartialMatches(args[2], List.of("10", "20", "30"), result);
        }

        if (args.length == 4) {
            return StringUtil.copyPartialMatches(args[3], List.of("10", "15", "20"), result);
        }

        return result;
    }

    @Override
    String getPermissionNode() {
        return "jailworker.command.create";
    }

    @Override
    String getDescription() {
        return "set jail region";
    }

    @Override
    String getUsage() {
        return "/jailworker create <jail-name>";
    }
}
