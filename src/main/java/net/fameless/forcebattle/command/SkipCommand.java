package net.fameless.forcebattle.command;

import net.fameless.forcebattle.caption.Caption;
import net.fameless.forcebattle.command.framework.CallerType;
import net.fameless.forcebattle.command.framework.Command;
import net.fameless.forcebattle.command.framework.CommandCaller;
import net.fameless.forcebattle.player.BattlePlayer;
import net.fameless.forcebattle.util.StringUtil;
import net.fameless.forcebattle.util.TabCompletions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SkipCommand extends Command {

    public SkipCommand() {
        super(
                "skip",
                List.of(),
                CallerType.NONE,
                "/skip <player>",
                "forcebattle.skip",
                "Command to skip a player's objective - meant for admins if an item is unobtainable"
        );
    }

    @Override
    public void executeCommand(CommandCaller caller, String[] args) {
        if (args.length > 0) {
            @NotNull Optional<BattlePlayer> battlePlayerOpt = BattlePlayer.adapt(args[0]);
            battlePlayerOpt.ifPresentOrElse(
                    battlePlayer -> {
                        if (battlePlayer.isOffline()) {
                            return;
                        }
                        battlePlayer.updateObjective(false, false);
                        battlePlayer.sendMessage(Caption.of("notification.skip_by_admin_target"));
                        caller.sendMessage(Caption.of(
                                "notification.skip_by_admin_player",
                                TagResolver.resolver("player", Tag.inserting(Component.text(battlePlayer.getName())))
                        ));
                    }, () -> caller.sendMessage(Caption.of("command.no_such_player"))
            );
        } else {
            sendUsage(caller);
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
