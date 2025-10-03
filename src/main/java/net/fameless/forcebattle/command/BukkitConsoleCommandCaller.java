package net.fameless.forcebattle.command;

import net.fameless.forcebattle.command.framework.CallerType;
import net.fameless.forcebattle.command.framework.ConsoleCommandCaller;
import net.fameless.forcebattle.util.BukkitUtil;
import net.kyori.adventure.text.Component;

public class BukkitConsoleCommandCaller implements ConsoleCommandCaller {

    private static BukkitConsoleCommandCaller instance;

    public static BukkitConsoleCommandCaller instance() {
        if (instance == null) {
            instance = new BukkitConsoleCommandCaller();
        }
        return instance;
    }

    @Override
    public CallerType callerType() {
        return CallerType.CONSOLE;
    }

    @Override
    public String getName() {
        return "CONSOLE";
    }

    @Override
    public void sendMessage(Component message) {
        BukkitUtil.BUKKIT_AUDIENCES.console().sendMessage(message);
    }

    @Override
    public boolean hasPermission(String permission) {
        return true;
    }

}
