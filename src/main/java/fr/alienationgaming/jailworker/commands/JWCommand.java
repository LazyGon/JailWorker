package fr.alienationgaming.jailworker.commands;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import fr.alienationgaming.jailworker.JailWorker;

public class JWCommand implements CommandExecutor, TabCompleter {

    private static JailWorker jailWorker = JailWorker.getInstance();

    enum SubCommands {
        CREATE(new Create()),
        PUT(new Put()),
        START(new Start()),
        SET_SPAWN(new SetSpawn()),
        STOP(new Stop()),
        CLEAN(new Clean()),
        SAVE(new Save()),
        CONFIG_COMMAND(new ConfigCmd()),
        LIST(new fr.alienationgaming.jailworker.commands.List()),
        DELETE(new Delete()),
        RESTART(new Restart()),
        INFO(new Info()),
        FREE(new Free()),
        GOTO(new Goto()),
        GIVE(new Give()),
        ALLOWED_COMMAND(new WhiteCmd()),
        RELOAD(new Reload()),
        INCREASE(new Increase()),
        MANAGE_OWNER(new OwnerManager()),
        ;

        private JWSubCommand subCommand;

        private SubCommands(JWSubCommand subCommand) {
            this.subCommand = subCommand;
        }

        public JWSubCommand getInstance() {
            return subCommand;
        }

        public static JWSubCommand getSubCommand(String name) {
            switch (name.toLowerCase(Locale.ROOT)) {
                // TODO: Command and class should have same name.
                case "create":
                return SubCommands.CREATE.getInstance();
                case "put":
                return SubCommands.PUT.getInstance();
                case "start":
                return SubCommands.START.getInstance();
                case "setSpawn":
                return SubCommands.SET_SPAWN.getInstance();
                case "stop":
                return SubCommands.STOP.getInstance();
                case "clean":
                return SubCommands.CLEAN.getInstance();
                case "save":
                return SubCommands.SAVE.getInstance();
                case "edit":
                return SubCommands.CONFIG_COMMAND.getInstance();
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
                case "whiteCmd":
                return SubCommands.ALLOWED_COMMAND.getInstance();
                case "reload":
                return SubCommands.RELOAD.getInstance();
                case "increase":
                return SubCommands.INCREASE.getInstance();
                case "owner":
                return SubCommands.MANAGE_OWNER.getInstance();
                default:
                return null;
            }
        }
    }

    JWCommand() {
        Optional.ofNullable(jailWorker.getCommand("jailworker")).ifPresent(command -> {
            command.setExecutor(this);
            command.setTabCompleter(this);
        });
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            // TODO: no enough argument message
            return false;
        }

        JWSubCommand subCommand = SubCommands.getSubCommand(args[0]);
        if (subCommand == null) {
            // TODO: invalid arg message
            return false;
        }
        if (!subCommand.hasPermissionWithMessage(sender)) {
            return false;
        }

        return subCommand.runCommand(sender, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}