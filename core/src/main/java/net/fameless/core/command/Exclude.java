package net.fameless.core.command;

import net.fameless.core.caption.Caption;
import net.fameless.core.command.framework.CallerType;
import net.fameless.core.command.framework.Command;
import net.fameless.core.command.framework.CommandCaller;
import net.fameless.core.player.BattlePlayer;
import net.fameless.core.util.StringUtil;
import net.fameless.core.util.TabCompletions;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Exclude extends Command {

    public Exclude() {
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
            @NotNull Optional<BattlePlayer<?>> battlePlayerOpt = BattlePlayer.of(caller.getName());
            battlePlayerOpt.ifPresent(battlePlayer -> battlePlayer.setExcluded(!battlePlayer.isExcluded()));
            return;
        }
        @NotNull Optional<BattlePlayer<?>> toExcludeOpt = BattlePlayer.of(args[0]);
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
