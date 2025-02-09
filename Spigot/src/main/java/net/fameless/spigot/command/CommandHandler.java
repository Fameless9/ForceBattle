package net.fameless.spigot.command;

import net.fameless.forceBattle.command.framework.CommandCaller;
import net.fameless.spigot.player.BukkitPlayer;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommandHandler implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        CommandCaller caller = createCaller(sender);
        net.fameless.forceBattle.command.framework.Command.execute(command.getName(), caller, args);
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        CommandCaller caller = createCaller(sender);
        return net.fameless.forceBattle.command.framework.Command.tabComplete(command.getName(), caller, args);
    }

    private @NotNull CommandCaller createCaller(CommandSender sender) {
        if (sender instanceof Player player) {
            return BukkitPlayer.adapt(player);
        } else if (sender instanceof ConsoleCommandSender) {
            return BukkitConsoleCommandCaller.instance();
        } else if (sender instanceof BlockCommandSender) {
            return BukkitBlockCommandCaller.adapt((BlockCommandSender) sender);
        }
        throw new RuntimeException("Unknown sender type: " + sender.getClass());
    }

}
