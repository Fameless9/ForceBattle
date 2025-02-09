package net.fameless.forceBattle.command;

import net.fameless.forceBattle.ForceBattle;
import net.fameless.forceBattle.command.framework.CallerType;
import net.fameless.forceBattle.command.framework.Command;
import net.fameless.forceBattle.command.framework.CommandCaller;
import net.fameless.forceBattle.gui.SettingsGUI;
import net.fameless.forceBattle.player.BattlePlayer;
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
                "forcebattle.settings"
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
