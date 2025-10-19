package net.fameless.forcebattle.command;

import net.fameless.forcebattle.ForceBattle;
import net.fameless.forcebattle.caption.Caption;
import net.fameless.forcebattle.command.framework.CallerType;
import net.fameless.forcebattle.command.framework.Command;
import net.fameless.forcebattle.command.framework.CommandCaller;
import net.fameless.forcebattle.game.Team;
import net.fameless.forcebattle.gui.impl.ResultGUI;
import net.fameless.forcebattle.player.BattlePlayer;
import net.fameless.forcebattle.util.StringUtility;
import net.fameless.forcebattle.util.TabCompletions;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ResultCommand extends Command {

    public ResultCommand() {
        super(
                "result",
                List.of(),
                CallerType.PLAYER,
                "/result <team|player|playeranimated|teamanimated> <team id|player|place>",
                "forcebattle.result",
                "Command to display the results of a battle"
        );
    }

    @Override
    public void executeCommand(@NotNull CommandCaller caller, String[] args) {
        if (ForceBattle.getTimer().isRunning()) {
            caller.sendMessage(Caption.of("command.timer_running"));
            return;
        }
        @NotNull Optional<BattlePlayer> whoOpensOpt = BattlePlayer.adapt(caller.getName());
        if (whoOpensOpt.isEmpty()) {
            return;
        }
        BattlePlayer whoOpens = whoOpensOpt.get();
        if (args.length == 0) {
            new ResultGUI().open(whoOpens);
            return;
        }
        if (args.length < 2 && !args[0].equals("animated")) {
            sendUsage(caller);
            return;
        }
        switch (args[0]) {
            case "player" -> {
                @NotNull Optional<BattlePlayer> targetOpt = BattlePlayer.adapt(args[1]);
                targetOpt.ifPresentOrElse(
                        target -> new ResultGUI(target, false).open(whoOpens),
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
                        team -> new ResultGUI(team, false).open(whoOpens),
                        () -> caller.sendMessage(Caption.of("command.no_such_team"))
                );
            }
            case "playeranimated" -> {
                if (!caller.hasPermission("forcebattle.result.animated")) return;

                int place;
                try {
                    place = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    caller.sendMessage(Caption.of("command.not_a_number"));
                    return;
                }
                BattlePlayer battlePlayer = BattlePlayer.getPlaces().get(place);
                for (BattlePlayer player : BattlePlayer.getOnlinePlayers()) {
                    new ResultGUI(battlePlayer, true).open(player);
                }
            }
            case "teamanimated" -> {
                if (!caller.hasPermission("forcebattle.result.animated")) return;

                int place;
                try {
                    place = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    caller.sendMessage(Caption.of("command.not_a_number"));
                    return;
                }
                Team team = Team.getPlaces().get(place);
                for (BattlePlayer player : BattlePlayer.getOnlinePlayers()) {
                    new ResultGUI(team, true).open(player);
                }
            }
        }
    }

    @Override
    public List<String> tabComplete(CommandCaller caller, String @NotNull [] args) {
        if (args.length == 1) {
            return StringUtility.copyPartialMatches(args[0], List.of("player", "team", "playeranimated", "teamanimated"), new ArrayList<>());
        } else if (args.length == 2) {
            return switch (args[0]) {
                case "player" -> StringUtility.copyPartialMatches(args[1], TabCompletions.getPlayerNamesTabCompletions(), new ArrayList<>());
                case "team" -> StringUtility.copyPartialMatches(args[1], TabCompletions.getTeamIdTabCompletions(), new ArrayList<>());
                case "playeranimated" -> StringUtility.copyPartialMatches(args[1], TabCompletions.getPlayerPlaces(), new ArrayList<>());
                case "teamanimated" -> StringUtility.copyPartialMatches(args[1], TabCompletions.getTeamPlaces(), new ArrayList<>());
                default -> List.of();
            };
        }
        return List.of();
    }

}
