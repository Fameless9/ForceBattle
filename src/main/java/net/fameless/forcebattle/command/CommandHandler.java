package net.fameless.forcebattle.command;

import net.fameless.forcebattle.ForceBattle;
import net.fameless.forcebattle.command.framework.CommandCaller;
import net.fameless.forcebattle.player.BattlePlayer;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;

public class CommandHandler implements CommandExecutor, TabCompleter {

    private static final Logger logger = LoggerFactory.getLogger("ForceBattle/" + CommandHandler.class.getSimpleName());
    private static final CommandHandler handler = new CommandHandler();

    public static void registerAll(@NotNull Collection<net.fameless.forcebattle.command.framework.Command> commands) {
        commands.forEach(CommandHandler::register);
    }

    public static void register(@NotNull net.fameless.forcebattle.command.framework.Command command) {
        ForceBattle.get().getCommand(command.getId()).setExecutor(handler);
        logger.info("Registered command: {}", command.getId());
    }

    private static @NotNull CommandCaller createCaller(CommandSender sender) {
        if (sender instanceof Player player) {
            return BattlePlayer.adapt(player);
        } else if (sender instanceof ConsoleCommandSender) {
            return BukkitConsoleCommandCaller.instance();
        } else if (sender instanceof BlockCommandSender) {
            return BukkitBlockCommandCaller.adapt((BlockCommandSender) sender);
        }
        throw new RuntimeException("Unknown sender type: " + sender.getClass());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        CommandCaller caller = createCaller(sender);
        net.fameless.forcebattle.command.framework.Command.execute(command.getName(), caller, args);
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        CommandCaller caller = createCaller(sender);
        return net.fameless.forcebattle.command.framework.Command.tabComplete(command.getName(), caller, args);
    }

}
