package net.fameless.spigot.command;

import net.fameless.core.command.framework.Command;
import net.fameless.core.command.framework.CommandCaller;
import net.fameless.spigot.player.BukkitPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

public class CommandManager {

    private static final Logger logger = LoggerFactory.getLogger("ForceBattle/" + CommandManager.class.getSimpleName());
    private static final CommandMap COMMAND_MAP = getCommandMap();

    public static void registerAll(@NotNull Collection<Command> commands) {
        for (Command command : commands) {
            register(command);
        }
    }

    public static void register(@NotNull Command command) {
        BukkitCommand bukkitCommand = new BukkitCommand(command.getId(), command.getDescription(), "", command.getAliases()) {
            @Override
            public boolean execute(@NotNull final CommandSender sender, @NotNull final String commandLabel, @NotNull final String[] args) {
                Command.execute(command.getId(), createCaller(sender), args);
                return false;
            }

            @Override
            public @NotNull @Unmodifiable List<String> tabComplete(@NotNull final CommandSender sender, @NotNull final String alias, @NotNull final String[] args) throws
                    IllegalArgumentException {
                return Command.tabComplete(command.getId(), createCaller(sender), args);
            }
        };
        bukkitCommand.setPermission(command.getPermission());

        COMMAND_MAP.register(command.getId(), bukkitCommand);
        logger.info("Registered command: {}", command.getId());
    }

    private static @NotNull CommandMap getCommandMap() {
        try {
            Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            return (CommandMap) field.get(Bukkit.getServer());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to access CommandMap", e);
        }
    }

    private static @NotNull CommandCaller createCaller(CommandSender sender) {
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
