package net.fameless.core.command;

import net.fameless.core.ForceBattle;
import net.fameless.core.caption.Caption;
import net.fameless.core.command.framework.CallerType;
import net.fameless.core.command.framework.Command;
import net.fameless.core.command.framework.CommandCaller;
import net.fameless.core.configuration.SettingsManager;
import net.fameless.core.player.BattlePlayer;
import net.fameless.core.util.StringUtil;
import net.fameless.core.util.TabCompletions;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Backpack extends Command {

    public Backpack() {
        super(
                "backpack",
                List.of("bp"),
                CallerType.PLAYER,
                "/backpack <player>",
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
        if (!ForceBattle.getTimer().isRunning()) {
            caller.sendMessage(Caption.of("error.game_not_started"));
            return;
        }

        Optional<BattlePlayer<?>> senderOpt = BattlePlayer.of(caller.getName());

        if (args.length > 0 && !args[0].isEmpty()) {
            Optional<BattlePlayer<?>> targetOpt = BattlePlayer.of(args[0]);

            if (senderOpt.isPresent() && targetOpt.isPresent()
                    && senderOpt.get().getTeam() == targetOpt.get().getTeam()) {
                targetOpt.get().openBackpack(senderOpt.get());
                return;
            }

            caller.sendMessage(Caption.of("error.not_same_team"));
        } else {
            senderOpt.ifPresent(BattlePlayer::openBackpack);
        }
    }

    @Override
    public List<String> tabComplete(CommandCaller caller, String @NotNull [] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], TabCompletions.getPlayerNamesTabCompletions(), new ArrayList<>());
        }
        return List.of();
    }
}
