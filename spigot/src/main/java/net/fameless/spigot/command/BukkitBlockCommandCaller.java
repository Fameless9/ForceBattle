package net.fameless.spigot.command;

import net.fameless.core.command.framework.CallerType;
import net.fameless.core.command.framework.CommandCaller;
import net.fameless.spigot.util.BukkitUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.command.BlockCommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class BukkitBlockCommandCaller implements CommandCaller {

    private final BlockCommandSender sender;

    private BukkitBlockCommandCaller(BlockCommandSender sender) {
        this.sender = sender;
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull BukkitBlockCommandCaller adapt(BlockCommandSender sender) {
        return new BukkitBlockCommandCaller(sender);
    }

    @Override
    public CallerType callerType() {
        return CallerType.CONSOLE;
    }

    @Override
    public String getName() {
        return "BLOCK";
    }

    @Override
    public void sendMessage(final Component component) {
        BukkitUtil.BUKKIT_AUDIENCES.sender(sender).sendMessage(component);
    }

    @Override
    public boolean hasPermission(final String permission) {
        return true;
    }

}
