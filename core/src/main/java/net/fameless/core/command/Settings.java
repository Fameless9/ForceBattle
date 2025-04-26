package net.fameless.core.command;

import net.fameless.core.ForceBattle;
import net.fameless.core.command.framework.CallerType;
import net.fameless.core.command.framework.Command;
import net.fameless.core.command.framework.CommandCaller;
import net.fameless.core.gui.SettingsGUI;
import net.fameless.core.player.BattlePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Settings extends Command {

    private final SettingsGUI<?> settingsGUI;

    public Settings() {
        super(
                "settings",
                List.of("menu"),
                CallerType.PLAYER,
                "/settings",
                "forcebattle.settings",
                "Command to open the settings menu"
        );

        settingsGUI = ForceBattle.injector().getInstance(SettingsGUI.class);
    }

    @Override
    public void executeCommand(@NotNull CommandCaller caller, String[] args) {
        if (caller instanceof BattlePlayer<?> player) {
            player.openInventory(settingsGUI.getSettingsGUI());
        }
    }

    @Override
    public List<String> tabComplete(CommandCaller caller, String[] args) {
        return List.of();
    }

}
