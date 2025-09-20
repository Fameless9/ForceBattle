package net.fameless.core.command;

import net.fameless.core.command.framework.CallerType;
import net.fameless.core.command.framework.Command;
import net.fameless.core.command.framework.CommandCaller;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Settings extends Command {

    public Settings() {
        super(
                "settings",
                List.of("menu"),
                CallerType.PLAYER,
                "/settings",
                "forcebattle.settings",
                "Command to open the settings menu"
        );
    }

    @Override
    public void executeCommand(@NotNull CommandCaller caller, String[] args) {
        // To be handled by Bukkit SettingsGUI
    }

    @Override
    public List<String> tabComplete(CommandCaller caller, String[] args) {
        return List.of();
    }

}
