package fr.alienationgaming.jailworker.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import fr.alienationgaming.jailworker.JailWorker;
import fr.alienationgaming.jailworker.config.Messages;

public class JWCommand implements CommandExecutor, TabCompleter {

    private static JailWorker jailWorker = JailWorker.getInstance();

    enum SubCommands {
        CREATE(new Create()), PUT(new Put()), START(new Start()), STOP(new Stop()), CLEAN(new Clean()),
        EDIT(new Edit()), LIST(new fr.alienationgaming.jailworker.commands.List()), DELETE(new Delete()),
        RESTART(new Restart()), INFO(new Info()), FREE(new Free()), GOTO(new Goto()), GIVE(new Give()),
        ALLOWED_COMMAND(new AllowedCommand()), RELOAD(new Reload()), INCREASE(new Increase()),;

        private SubCommand subCommand;
        private static final List<String> subCommandInputs = List.of("create", "put", "start", "stop", "clean", "edit",
                "list", "delete", "restart", "info", "free", "goto", "give", "allowedcommand", "reload", "increase");

        private SubCommands(SubCommand subCommand) {
            this.subCommand = subCommand;
        }

        public SubCommand getInstance() {
            return subCommand;
        }

        public static SubCommand getSubCommand(String name) {
            switch (name.toLowerCase(Locale.ROOT)) {
            case "create":
                return SubCommands.CREATE.getInstance();
            case "put":
                return SubCommands.PUT.getInstance();
            case "start":
                return SubCommands.START.getInstance();
            case "stop":
                return SubCommands.STOP.getInstance();
            case "clean":
                return SubCommands.CLEAN.getInstance();
            case "edit":
                return SubCommands.EDIT.getInstance();
            case "list":
                return SubCommands.LIST.getInstance();
            case "delete":
                return SubCommands.DELETE.getInstance();
            case "restart":
                return SubCommands.RESTART.getInstance();
            case "info":
                return SubCommands.INFO.getInstance();
            case "free":
                return SubCommands.FREE.getInstance();
            case "goto":
                return SubCommands.GOTO.getInstance();
            case "give":
                return SubCommands.GIVE.getInstance();
            case "allowedcommand":
                return SubCommands.ALLOWED_COMMAND.getInstance();
            case "reload":
                return SubCommands.RELOAD.getInstance();
            case "increase":
                return SubCommands.INCREASE.getInstance();
            default:
                return null;
            }
        }

        public static List<String> getSubCommandsComplete(CommandSender sender) {
            List<String> completion = new ArrayList<>(subCommandInputs);
            completion.removeIf(subCommand -> !getSubCommand(subCommand).hasPermission(sender));
            return completion;
        }

        public static List<String> getSubCommandsInputs() {
            return Collections.unmodifiableList(subCommandInputs);
        }
    }

    public JWCommand() {
        Optional.ofNullable(jailWorker.getCommand("jailworker")).ifPresent(command -> {
            command.setExecutor(this);
            command.setTabCompleter(this);
        });
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            Messages.sendMessage(sender, "command.general.error.not-enough-arguments");
            return false;
        }

        SubCommand subCommand = SubCommands.getSubCommand(args[0]);
        if (subCommand == null) {
            Messages.sendMessage(sender, "command.general.error.missing-argument",
                    Map.of("%missing-argument%", subCommand));
            return false;
        }

        if (!subCommand.hasPermission(sender)) {
            Messages.sendMessage(sender, "command.general.error.no-permission");
            return false;
        }

        return subCommand.runCommand(sender, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], SubCommands.getSubCommandsComplete(sender),
                    new ArrayList<>());
        }

        SubCommand subCommand = SubCommands.getSubCommand(args[0]);
        if (subCommand == null || !subCommand.hasPermission(sender)) {
            return List.of();
        }

        return subCommand.runTabComplete(sender, args);
    }
}