package net.fameless.forceBattle.command.framework;

import net.kyori.adventure.text.Component;

public interface CommandCaller {

    CallerType callerType();

    String getName();

    void sendMessage(Component component);

    boolean hasPermission(String permission);

}
