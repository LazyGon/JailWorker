package fr.alienationgaming.jailworker.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
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
import fr.alienationgaming.jailworker.config.BlockPoints;
import fr.alienationgaming.jailworker.config.Config;
import fr.alienationgaming.jailworker.config.JailConfig;
import fr.alienationgaming.jailworker.config.Messages;
import fr.alienationgaming.jailworker.events.JailCreateEvent;

public class Create extends SubCommand {

    private static JailWorker plugin = JailWorker.getInstance();
    final static Map<Player, InputGetter> data = new HashMap<>();

    private class InputGetter implements Listener {

        private final String jailName;
        private final Player player;
        private int maxPunishmentBlocks = Config.getDefaultMaxBlocks();
        private int punishmentBlockInterval = Config.getDefaultBlockInterval();
        private Set<Material> punishmentBlocks;
        private Location pos1;
        private Location pos2;
        private Location spawn;

        InputGetter(String jailName, Player player, int maxPunishmentBlocks, int punishmentBlockInterval,
                Set<Material> punishmentBlocks) {
            this.jailName = jailName;
            this.player = player;
            this.punishmentBlocks = punishmentBlocks;
            Bukkit.getPluginManager().registerEvents(this, plugin);
            
            if (Bukkit.getPluginManager().isPluginEnabled("WorldEdit")) {
                WEInputGetter weInputGetter = new WEInputGetter(player);
                pos1 = weInputGetter.getPos1();
                pos2 = weInputGetter.getPos2();
                if (pos1 != null && pos2 != null) {
                    Messages.sendMessage(player, "command.create.info.retrieve-worldedit");
                }
            }
            if ((pos1 != null && pos1.getWorld() != player.getWorld()) || pos2 != null && pos2.getWorld() != player.getWorld()) {
                pos1 = null;
                pos2 = null;
            }

            if (pos1 == null || pos2 == null) {
                Messages.sendMessage(player, "command.create.info.define-tips");
                Messages.sendMessage(player, "command.create.info.waiting-for-first");
            }

            if (maxPunishmentBlocks >= 0) {
                this.maxPunishmentBlocks = maxPunishmentBlocks;
            }

            if (punishmentBlockInterval >= 0) {
                this.punishmentBlockInterval = punishmentBlockInterval;
            }
        }

        @EventHandler
        public void onPlayerInteract(PlayerInteractEvent event) {
            if (!event.getPlayer().equals(player)) {
                return;
            }

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
                player.sendMessage(ChatColor.BLUE + "pos1:");
                printLocation(pos);
                Messages.sendMessage(player, "command.create.info.waiting-for-second");
            } else if (pos2 == null) {
                pos2 = pos;
                player.sendMessage(ChatColor.BLUE + "pos2:");
                printLocation(pos);
                Messages.sendMessage(player, "command.create.info.waiting-for-spawn");
            } else if (spawn == null) {
                spawn = pos.add(0.5, 1, 0.5);
                player.sendMessage(ChatColor.BLUE + "spawn:");
                printLocation(pos);
                Messages.sendMessage(player, "command.create.info.finish", Messages.placeholder("%jail-name%", jailName));
                HandlerList.unregisterAll(this);
                data.remove(player);
                JailCreateEvent createEvent = new JailCreateEvent(jailName, maxPunishmentBlocks,
                        punishmentBlockInterval, punishmentBlocks, pos1, pos2, spawn);
                Bukkit.getPluginManager().callEvent(createEvent);
                if (!createEvent.isCancelled()) {
                    maxPunishmentBlocks = createEvent.getMaxPunishmentBlocks();
                    punishmentBlockInterval = createEvent.getPunishmentBlockInterval();
                    punishmentBlocks = createEvent.getPunishmentBlocks();
                    pos1 = createEvent.getPos1();
                    pos2 = createEvent.getPos2();
                    spawn = createEvent.getSpawn();
                    JailConfig.addJail(jailName, maxPunishmentBlocks, punishmentBlockInterval, punishmentBlocks,
                            new ArrayList<>(), pos.getWorld(), pos1, pos2, spawn);
                }
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


    }

    private class WEInputGetter {

        private final Player player;
        private final Location pos1;
        private final Location pos2;

        private WEInputGetter(Player player) {
            this.player = player;
            Region region = getWorldEditSelection();
            if (region == null) {
                pos1 = null;
                pos2 = null;
                return;
            }
            World world = Bukkit.getWorld(region.getWorld().getName());
            BlockVector3 max = region.getMaximumPoint();
            BlockVector3 min = region.getMinimumPoint();
            pos1 = new Location(world, max.getX(), max.getY(), max.getZ());
            pos2 = new Location(world, min.getX(), min.getY(), min.getZ());
        }

        private Location getPos1() {
            return pos1;
        }

        private Location getPos2() {
            return pos2;
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

        if (args.length < 2) {
            Messages.sendMessage(sender, "command.general.error.not-enough-arguments");
            Messages.sendMessage(sender, "command.general.info.usage", Messages.placeholder("%usage%", getUsage()));
            return false;
        }

        if (data.containsKey(sender)) {
            Messages.sendMessage(sender, "command.create.error.you-are-already-in-creation",
                    Messages.placeholder("%jail-name%", data.get(sender).jailName));
            return false;
        }

        String jailName = args[1];
        if (!hasPermission(sender, jailName)) {
            Messages.sendMessage(sender, "command.general.error.no-permission");
            return false;
        }

        if (JailConfig.exist(jailName)) {
            Messages.sendMessage(sender, "command.create.error.jail-already-exists", Messages.placeholder("%jail-name%", jailName));
            return false;
        }

        int maxBlocks = Config.getDefaultMaxBlocks();
        int blockInterval = Config.getDefaultBlockInterval();

        if (args.length >= 3) {
            try {
                maxBlocks = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                maxBlocks = Config.getDefaultMaxBlocks();
            }
        }

        if (args.length >= 4) {
            try {
                blockInterval = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                blockInterval = Config.getDefaultBlockInterval();
            }
        }

        Set<Material> punishmentBlocks = new HashSet<>();

        for (int i = 4; i < args.length; i++) {
            String blockName = args[i].toUpperCase(Locale.ROOT);
            try {
                punishmentBlocks.add(Material.valueOf(blockName));
            } catch (IllegalArgumentException ignored) {
                Messages.sendMessage(sender, "command.general.error.invalid-material",
                        Messages.placeholder("%material%", blockName));
                return false;
            }
        }

        data.put((Player) sender, new InputGetter(jailName, (Player) sender, maxBlocks, blockInterval, punishmentBlocks));
        return true;
    }

    @Override
    List<String> runTabComplete(CommandSender sender, String[] args) {
        List<String> result = new ArrayList<>();
        if (args.length == 2) {
            return StringUtil.copyPartialMatches(args[1], Arrays.asList("<jail-name>"), result);
        }

        if (args.length == 3) {
            return StringUtil.copyPartialMatches(args[2], Arrays.asList("10", "20", "30"), result);
        }

        if (args.length == 4) {
            return StringUtil.copyPartialMatches(args[3], Arrays.asList("10", "15", "20"), result);
        }

        if (args.length >= 5) {
            List<String> punishmentBlocks = BlockPoints.getAllBlocks().stream().map(Enum::name)
                    .collect(Collectors.toList());
            if (args.length > 5) {
                List<String> inputBlock = new ArrayList<>();
                for (int i = 4; i < args.length; i++) {
                    inputBlock.add(args[i]);
                }
                punishmentBlocks.removeAll(inputBlock);
            }
            return StringUtil.copyPartialMatches(args[args.length - 1], punishmentBlocks, result);
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
        return "/jailworker create <jail-name> [max-punishment-block] [punishment-block-interval] [punishment-bock-1] [punishment-bock-2]...";
    }
}
