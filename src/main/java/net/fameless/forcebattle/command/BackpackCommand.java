package net.fameless.forcebattle.command;

import net.fameless.forcebattle.ForceBattle;
import net.fameless.forcebattle.caption.Caption;
import net.fameless.forcebattle.command.framework.CallerType;
import net.fameless.forcebattle.command.framework.Command;
import net.fameless.forcebattle.command.framework.CommandCaller;
import net.fameless.forcebattle.configuration.SettingsManager;
import net.fameless.forcebattle.player.BattlePlayer;
import net.fameless.forcebattle.util.StringUtil;
import net.fameless.forcebattle.util.TabCompletions;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BackpackCommand extends Command {

    public BackpackCommand() {
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

        Optional<BattlePlayer> senderOpt = BattlePlayer.adapt(caller.getName());

        if (args.length > 0 && !args[0].isEmpty()) {
            Optional<BattlePlayer> targetOpt = BattlePlayer.adapt(args[0]);

            if (senderOpt.isPresent() && targetOpt.isPresent() && senderOpt.get().isInTeam() && targetOpt.get().isInTeam() &&
                    senderOpt.get().getTeam() == targetOpt.get().getTeam()) {
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
