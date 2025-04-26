package net.fameless.core.command;

import net.fameless.core.caption.Caption;
import net.fameless.core.command.framework.CallerType;
import net.fameless.core.command.framework.Command;
import net.fameless.core.command.framework.CommandCaller;
import net.fameless.core.configuration.SettingsManager;
import net.fameless.core.player.BattlePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class Backpack extends Command {

    public Backpack() {
        super(
                "backpack",
                List.of("bp"),
                CallerType.PLAYER,
                "/backpack",
                "forcebattle.backpack",
                "Command to open the backpack"
        );
    }

    @Override
    public void executeCommand(CommandCaller caller, String[] args) {
        @NotNull Optional<BattlePlayer<?>> battlePlayerOpt = BattlePlayer.of(caller.getName());
        if (!SettingsManager.isEnabled(SettingsManager.Setting.BACKPACK)) {
            caller.sendMessage(Caption.of("error.backpacks_disabled"));
            return;
        }
        battlePlayerOpt.ifPresent(BattlePlayer::openBackpack);
    }

    @Override
    public List<String> tabComplete(CommandCaller caller, String[] args) {
        return List.of();
    }

}
