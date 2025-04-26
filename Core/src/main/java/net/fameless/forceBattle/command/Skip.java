package net.fameless.forceBattle.command;

import net.fameless.forceBattle.caption.Caption;
import net.fameless.forceBattle.command.framework.CallerType;
import net.fameless.forceBattle.command.framework.Command;
import net.fameless.forceBattle.command.framework.CommandCaller;
import net.fameless.forceBattle.player.BattlePlayer;
import net.fameless.forceBattle.util.StringUtil;
import net.fameless.forceBattle.util.TabCompletions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Skip extends Command {

    public Skip() {
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
            @NotNull Optional<BattlePlayer<?>> battlePlayerOpt = BattlePlayer.of(args[0]);
            battlePlayerOpt.ifPresentOrElse(
                    battlePlayer -> {
                        if (battlePlayer.isOffline()) {
                            return;
                        }
                        battlePlayer.updateObjective(false);
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
