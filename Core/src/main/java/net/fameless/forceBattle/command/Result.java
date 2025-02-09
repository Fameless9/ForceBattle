package net.fameless.forceBattle.command;

import net.fameless.forceBattle.ForceBattle;
import net.fameless.forceBattle.caption.Caption;
import net.fameless.forceBattle.command.framework.CallerType;
import net.fameless.forceBattle.command.framework.Command;
import net.fameless.forceBattle.command.framework.CommandCaller;
import net.fameless.forceBattle.game.Team;
import net.fameless.forceBattle.gui.ResultGUI;
import net.fameless.forceBattle.player.BattlePlayer;
import net.fameless.forceBattle.util.StringUtil;
import net.fameless.forceBattle.util.TabCompletions;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Result extends Command {

    private final ResultGUI<?> resultGUI;

    public Result() {
        super(
                "result",
                List.of(),
                CallerType.PLAYER,
                "/result <team|player> <team id|player>",
                "forcebattle.result"
        );

        resultGUI = ForceBattle.injector().getInstance(ResultGUI.class);
    }

    @Override
    public void executeCommand(@NotNull CommandCaller caller, String[] args) {
        if (ForceBattle.getTimer().isRunning()) {
            caller.sendMessage(Caption.of("command.timer_running"));
            return;
        }
        @NotNull Optional<BattlePlayer<?>> whoOpensOpt = BattlePlayer.of(caller.getName());
        if (whoOpensOpt.isEmpty()) {
            return;
        }
        BattlePlayer<?> whoOpens = whoOpensOpt.get();
        if (args.length == 0) {
            whoOpens.openInventory(resultGUI.getResultGUI(whoOpens, 1));
            return;
        }
        if (args.length < 2) {
            sendUsage(caller);
            return;
        }
        switch (args[0]) {
            case "player" -> {
                @NotNull Optional<BattlePlayer<?>> targetOpt = BattlePlayer.of(args[1]);
                targetOpt.ifPresentOrElse(
                        target -> whoOpens.openInventory(resultGUI.getResultGUI(whoOpens, target, 1)),
                        () -> caller.sendMessage(Caption.of("command.no_such_player"))
                );
            }
            case "team" -> {
                int teamId;
                try {
                    teamId = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    caller.sendMessage(Caption.of("command.not_a_number"));
                    return;
                }
                Optional<Team> targetTeamOpt = Team.ofId(teamId);
                targetTeamOpt.ifPresentOrElse(
                        team -> whoOpens.openInventory(resultGUI.getResultGUI(whoOpens, team, 1)),
                        () -> caller.sendMessage(Caption.of("command.no_such_team"))
                );
            }
        }
    }

    @Override
    public List<String> tabComplete(CommandCaller caller, String @NotNull [] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], List.of("player", "team"), new ArrayList<>());
        } else if (args.length == 2) {
            return switch (args[0]) {
                case "player" -> StringUtil.copyPartialMatches(args[1], TabCompletions.getPlayerNamesTabCompletions(), new ArrayList<>());
                case "team" -> StringUtil.copyPartialMatches(args[1], TabCompletions.getTeamIdTabCompletions(), new ArrayList<>());
                default -> List.of();
            };
        }
        return List.of();
    }

}
