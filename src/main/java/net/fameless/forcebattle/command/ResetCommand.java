package net.fameless.forcebattle.command;

import net.fameless.forcebattle.ForceBattle;
import net.fameless.forcebattle.caption.Caption;
import net.fameless.forcebattle.command.framework.CallerType;
import net.fameless.forcebattle.command.framework.Command;
import net.fameless.forcebattle.command.framework.CommandCaller;
import net.fameless.forcebattle.player.BattlePlayer;
import net.fameless.forcebattle.util.StringUtility;
import net.fameless.forcebattle.util.TabCompletions;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ResetCommand extends Command {

    public ResetCommand() {
        super(
                "reset",
                List.of(),
                CallerType.NONE,
                "/reset <player>",
                "forcebattle.reset",
                "Command to reset a player's state"
        );
    }

    @Override
    public void executeCommand(CommandCaller caller, String @NotNull [] args) {
        if (args.length < 1) {
            ForceBattle.getTimer().setRunning(false);
            ForceBattle.getTimer().setTime(ForceBattle.getTimer().getStartTime());
            BattlePlayer.BATTLE_PLAYERS.forEach(battlePlayer -> battlePlayer.reset(true));
            return;
        }
        @NotNull Optional<BattlePlayer> toResetOpt = BattlePlayer.adapt(args[0]);
        toResetOpt.ifPresentOrElse(
                toReset ->
                    toReset.reset(true)
                , () -> caller.sendMessage(Caption.of("command.no_such_player"))
        );
    }

    @Override
    public List<String> tabComplete(CommandCaller caller, String @NotNull [] args) {
        if (args.length == 1) {
            return StringUtility.copyPartialMatches(args[0], TabCompletions.getPlayerNamesTabCompletions(), new ArrayList<>());
        }
        return List.of();
    }

}
