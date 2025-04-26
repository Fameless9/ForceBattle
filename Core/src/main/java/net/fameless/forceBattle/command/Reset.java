package net.fameless.forceBattle.command;

import net.fameless.forceBattle.ForceBattle;
import net.fameless.forceBattle.caption.Caption;
import net.fameless.forceBattle.command.framework.CallerType;
import net.fameless.forceBattle.command.framework.Command;
import net.fameless.forceBattle.command.framework.CommandCaller;
import net.fameless.forceBattle.player.BattlePlayer;
import net.fameless.forceBattle.util.StringUtil;
import net.fameless.forceBattle.util.TabCompletions;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Reset extends Command {

    public Reset() {
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
            BattlePlayer.BATTLE_PLAYERS.forEach(battlePlayer -> {
                battlePlayer.reset(true);
                battlePlayer.teleportToSpawnLocation();
            });
            return;
        }
        @NotNull Optional<BattlePlayer<?>> toResetOpt = BattlePlayer.of(args[0]);
        toResetOpt.ifPresentOrElse(
                toReset -> {
                    toReset.reset(true);
                    toReset.teleportToSpawnLocation();
                }, () -> caller.sendMessage(Caption.of("command.no_such_player"))
        );
    }

    @Override
    public List<String> tabComplete(CommandCaller caller, String @NotNull [] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], TabCompletions.getPlayerNamesTabCompletions(), new ArrayList<>());
        }
        return List.of();
    }

}
