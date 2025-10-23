package net.fameless.forcebattle.command;

import net.fameless.forcebattle.caption.Caption;
import net.fameless.forcebattle.command.framework.CallerType;
import net.fameless.forcebattle.command.framework.Command;
import net.fameless.forcebattle.command.framework.CommandCaller;
import net.fameless.forcebattle.configuration.SettingsManager;
import net.fameless.forcebattle.game.GameListener;
import net.fameless.forcebattle.player.BattlePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class BackpackCommand extends Command {

    public BackpackCommand() {
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
        if (!SettingsManager.isEnabled(SettingsManager.Setting.BACKPACK)) {
            caller.sendMessage(Caption.of("error.backpacks_disabled"));
            return;
        }
        if (GameListener.startPhase) return;

        Optional<BattlePlayer> senderOpt = BattlePlayer.adapt(caller.getName());

        senderOpt.ifPresent(BattlePlayer::openBackpack);
    }

    @Override
    public List<String> tabComplete(CommandCaller caller, String @NotNull [] args) {
        return List.of();
    }
}
