package fr.alienationgaming.jailworker.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
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

import fr.alienationgaming.jailworker.JailSystem;
import fr.alienationgaming.jailworker.JailWorker;
import fr.alienationgaming.jailworker.config.BlockPoints;
import fr.alienationgaming.jailworker.config.JailConfig;
import fr.alienationgaming.jailworker.config.Messages;

public class Edit extends SubCommand {

    private static JailWorker plugin = JailWorker.getInstance();
    final static Map<Player, InputGetter> data = new HashMap<>();

    private class InputGetter implements Listener {

        private final String jailName;
        private final Player player;
        private final String commandName;

        InputGetter(String jailName, Player player, String commandName) {
            this.jailName = jailName;
            this.player = player;
            this.commandName = commandName;
            Bukkit.getPluginManager().registerEvents(this, plugin);
            Messages.sendMessage(player, "command.edit.info.define-tips");
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

            switch (commandName) {
            case "spawn":
                JailConfig.setSpawnLocation(jailName, pos.add(0.5, 1, 0.5));
                break;
            case "pos1":
                JailConfig.setPosition1(jailName, pos);
                break;
            case "pos2":
                JailConfig.setPosition2(jailName, pos);
                break;
            }
            player.sendMessage(ChatColor.BLUE + commandName + ":");
            player.sendMessage(ChatColor.BLUE + "x :" + ChatColor.RESET + pos.getX());
            player.sendMessage(ChatColor.BLUE + "y :" + ChatColor.RESET + pos.getY());
            player.sendMessage(ChatColor.BLUE + "z :" + ChatColor.RESET + pos.getZ());
            Messages.sendMessage(player, "command.edit.info.success", Map.of("%location%", commandName));
            HandlerList.unregisterAll(this);
            data.remove(player);
        }

        @EventHandler
        private void onLogOut(PlayerQuitEvent event) {
            if (event.getPlayer().equals(player) || data.containsKey(player)) {
                data.remove(player);
                HandlerList.unregisterAll(this);
            }
        }
    }

    Edit() {
    }

    @Override
    boolean runCommand(CommandSender sender, String[] args) {
        if (args.length < 3) {
            Messages.sendMessage(sender, "command.general.error.not-enough-arguments");
            return false;
        }
        String jailName = args[1];
        if (!hasPermission(sender, jailName)) {
            Messages.sendMessage(sender, "command.general.error.no-permission");
            return false;
        }

        if (!JailConfig.exist(jailName)) {
            Messages.sendMessage(sender, "command.general.error.jail-does-not-exist", Map.of("%jail-name%", jailName));
            return false;
        }

        String command = args[2].toLowerCase(Locale.ROOT);
        switch (command) {
        case "punishmentblock":
            if (args.length < 5) {
                Messages.sendMessage(sender, "command.general.error.not-enough-arguments");
                return false;
            }

            Material material;
            String materialName = args[4].toUpperCase(Locale.ROOT);
            try {
                material = Material.valueOf(materialName);
                if (!BlockPoints.exist(material)) {
                    throw new IllegalArgumentException("The material is not defined in config.");
                }
            } catch (IllegalArgumentException e) {
                Messages.sendMessage(sender, "command.general.error.invalid-material",
                        Map.of("%material%", materialName));
                return false;
            }

            if (args[3].equals("add")) {
                JailConfig.addPunishmentBlock(jailName, material);
                Messages.sendMessage(sender, "command.edit.info.add-punishment-block",
                        Map.of("%material%", materialName));
                return true;
            } else if (args[3].equals("remove")) {
                JailConfig.removePunishmentBlock(jailName, material);
                Messages.sendMessage(sender, "command.edit.info.remove-punishment-block",
                        Map.of("%material%", materialName));
                return true;
            } else {
                Messages.sendMessage(sender, "command.general.error.missing-argument",
                        Map.of("%missing-argument%", args[3]));
                return false;
            }

        case "maxblock":
        case "blockinterval":
            int amountOrInterval;
            if (args.length == 3) {
                Messages.sendMessage(sender, "command.general.error.not-enough-arguments");
                return false;
            }

            try {
                amountOrInterval = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                Messages.sendMessage(sender, "command.general.error.invalid-number");
                return false;
            }

            if (command.equals("maxblock")) {
                JailConfig.setMaxPunishmentBlocks(jailName, amountOrInterval);
                Messages.sendMessage(sender, "command.edit.info.set-max-block-amount-success");
            } else {
                JailConfig.setBlockInterval(jailName, amountOrInterval);
                Messages.sendMessage(sender, "command.edit.info.set-block-interval-success");
            }

            if (JailSystem.isRunning(jailName)) {
                JailSystem.getTask(jailName).stop();
                JailSystem.getTask(jailName).start();
            }

            return true;
        case "spawn":
        case "pos1":
        case "pos2":
            if (!(sender instanceof Player)) {
                Messages.sendMessage(sender, "command.general.error.only-player");
                return false;
            }
            new InputGetter(jailName, (Player) sender, command);
            return true;
        default:
            Messages.sendMessage(sender, "command.general.error.missing-argument",
                    Map.of("%missing-argument%", command));
            return false;
        }
    }

    @Override
    List<String> runTabComplete(CommandSender sender, String[] args) {
        List<String> result = new ArrayList<>();
        List<String> jails = JailConfig.getJails();
        jails.removeIf(jail -> !JailConfig.exist(jail));
        jails.removeIf(jail -> !hasPermission(sender, jail));
        if (args.length == 2) {
            return StringUtil.copyPartialMatches(args[1], jails, result);
        }

        String jailName = args[1];

        if (!jails.contains(jailName)) {
            return result;
        }

        List<String> subCommands = List.of("maxblock", "blockinterval", "punishmentblock", "spawn", "pos1", "pos2");

        if (args.length == 3) {
            return StringUtil.copyPartialMatches(args[2], subCommands, result);
        }

        String subCommand = args[2].toLowerCase(Locale.ROOT);

        if (!subCommands.contains(subCommand)) {
            return result;
        }

        if (args.length == 4) {
            switch (subCommand) {
            case "maxblock":
                return StringUtil.copyPartialMatches(args[3], List.of("<max-punishment-blocks>"), result);
            case "blockinterval":
                return StringUtil.copyPartialMatches(args[3], List.of("<max-block-interval>"), result);
            case "punishmentblock":
                return StringUtil.copyPartialMatches(args[3], List.of("add", "remove"), result);
            }
        }

        if (!subCommand.equals("punishmentblock")) {
            return result;
        }

        if (!args[3].equalsIgnoreCase("add") && !args[3].equalsIgnoreCase("remove")) {
            return result;
        }

        if (args.length == 5) {
            List<String> available;
            Set<Material> punishmentBlocks = JailConfig.getPunishmentBlocks(jailName);
            if (args[3].equalsIgnoreCase("add")) {
                available = BlockPoints.getAllBlocks().stream().filter(material -> !punishmentBlocks.contains(material))
                        .map(Enum::name).collect(Collectors.toList());
            } else {
                available = punishmentBlocks.stream().map(Enum::name).collect(Collectors.toList());
            }
            return StringUtil.copyPartialMatches(args[4], available, result);
        }

        return result;
    }

    @Override
    String getPermissionNode() {
        return "jailworker.command.edit.<jail-name>";
    }

    @Override
    String getDescription() {
        return "edit jail settings with command and click.";
    }

    @Override
    String getUsage() {
        return "/jailworker edit <jail> <args...>";
    }
}
