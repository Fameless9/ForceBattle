package net.fameless.forcebattle.command;

import net.fameless.forcebattle.command.framework.CallerType;
import net.fameless.forcebattle.command.framework.Command;
import net.fameless.forcebattle.command.framework.CommandCaller;
import net.fameless.forcebattle.gui.impl.SettingsGUI;
import net.fameless.forcebattle.player.BattlePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ConfigSettingsCommand extends Command {

    public ConfigSettingsCommand() {
        super(
                "configsettings",
                List.of("menu"),
                CallerType.PLAYER,
                "/configsettings",
                "forcebattle.settings",
                "Command to open the settings menu"
        );
    }

    @Override
    public void executeCommand(@NotNull CommandCaller caller, String[] args) {
        if (caller instanceof BattlePlayer player) {
            new SettingsGUI().open(player);
        }
    }

    @Override
    public List<String> tabComplete(CommandCaller caller, String[] args) {
        return List.of();
    }

}
