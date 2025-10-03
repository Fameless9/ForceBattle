package net.fameless.forcebattle.command;

import net.fameless.forcebattle.ForceBattle;
import net.fameless.forcebattle.command.framework.CallerType;
import net.fameless.forcebattle.command.framework.Command;
import net.fameless.forcebattle.command.framework.CommandCaller;
import net.fameless.forcebattle.gui.SettingsGUI;
import net.fameless.forcebattle.player.BattlePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SettingsCommand extends Command {

    private final SettingsGUI settingsGUI;

    public SettingsCommand() {
        super(
                "settings",
                List.of("menu"),
                CallerType.PLAYER,
                "/settings",
                "forcebattle.settings",
                "Command to open the settings menu"
        );

        settingsGUI = ForceBattle.get().getSettingsGUI();
    }

    @Override
    public void executeCommand(@NotNull CommandCaller caller, String[] args) {
        if (caller instanceof BattlePlayer player) {
            player.openInventory(settingsGUI.getSettingsGUI());
        }
    }

    @Override
    public List<String> tabComplete(CommandCaller caller, String[] args) {
        return List.of();
    }

}
