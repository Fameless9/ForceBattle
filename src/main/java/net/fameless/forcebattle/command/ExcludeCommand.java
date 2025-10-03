package net.fameless.forcebattle.command;

import net.fameless.forcebattle.caption.Caption;
import net.fameless.forcebattle.command.framework.CallerType;
import net.fameless.forcebattle.command.framework.Command;
import net.fameless.forcebattle.command.framework.CommandCaller;
import net.fameless.forcebattle.player.BattlePlayer;
import net.fameless.forcebattle.util.StringUtil;
import net.fameless.forcebattle.util.TabCompletions;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExcludeCommand extends Command {

    public ExcludeCommand() {
        super(
                "exclude",
                List.of(),
                CallerType.NONE,
                "/exclude <player>",
                "forcebattle.exclude",
                "Command to exclude a player from the battle"
        );
    }

    @Override
    public void executeCommand(CommandCaller caller, String[] args) {
        if (args.length < 1) {
            if (!caller.callerType().equals(CallerType.PLAYER)) {
                caller.sendMessage(Caption.of(CallerType.PLAYER.getErrorMessageKey()));
                return;
            }
            @NotNull Optional<BattlePlayer> battlePlayerOpt = BattlePlayer.adapt(caller.getName());
            battlePlayerOpt.ifPresent(battlePlayer -> battlePlayer.setExcluded(!battlePlayer.isExcluded()));
            return;
        }
        @NotNull Optional<BattlePlayer> toExcludeOpt = BattlePlayer.adapt(args[0]);
        toExcludeOpt.ifPresent(toExclude -> toExclude.setExcluded(!toExclude.isExcluded()));
    }

    @Override
    public List<String> tabComplete(CommandCaller caller, String @NotNull [] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], TabCompletions.getPlayerNamesTabCompletions(), new ArrayList<>());
        }
        return List.of();
    }

}
