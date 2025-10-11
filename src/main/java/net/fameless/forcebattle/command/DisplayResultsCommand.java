package net.fameless.forcebattle.command;

import net.fameless.forcebattle.ForceBattle;
import net.fameless.forcebattle.caption.Caption;
import net.fameless.forcebattle.command.framework.CallerType;
import net.fameless.forcebattle.command.framework.Command;
import net.fameless.forcebattle.command.framework.CommandCaller;
import net.fameless.forcebattle.game.Team;
import net.fameless.forcebattle.player.BattlePlayer;
import net.fameless.forcebattle.util.StringUtil;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class DisplayResultsCommand extends Command {

    public DisplayResultsCommand() {
        super(
                "displayresults",
                List.of(),
                CallerType.NONE,
                "/displayresults <player/team>",
                "forcebattle.displayresults",
                "Command to display the results of the battle"
        );
    }

    @Override
    protected void executeCommand(CommandCaller caller, String[] args) {
        if (!ForceBattle.getTimer().hasHitZero()) {
            caller.sendMessage(Caption.of("command.not_hit_zero"));
            return;
        }

        if (args.length == 0) {
            sendUsage(caller);
            return;
        }

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        final long delay = 750;
        final long[] counter = {0};

        switch (args[0].toLowerCase()) {
            case "player" -> {
                List<BattlePlayer> sortedPlayers = new ArrayList<>(BattlePlayer.BATTLE_PLAYERS);
                sortedPlayers.sort(Comparator.comparingInt(BattlePlayer::getPoints).reversed());

                for (BattlePlayer player : sortedPlayers) {
                    int place = BattlePlayer.getPlace(player);
                    int points = player.getPoints();

                    scheduler.schedule(() -> ForceBattle.broadcast(
                            MiniMessage.miniMessage().deserialize(
                                    "<gold>" + place + ". <blue>" + player.getName() + "<dark_gray>: <gold>" + points
                            )
                    ), counter[0] * delay, TimeUnit.MILLISECONDS);
                    counter[0]++;
                }
            }

            case "team" -> {
                List<Team> sortedTeams = new ArrayList<>(Team.teams);
                sortedTeams.sort(Comparator.comparingInt(Team::getPoints).reversed());

                for (Team team : sortedTeams) {
                    int place = Team.getPlace(team);
                    int points = team.getPoints();

                    String players = team.getPlayers().stream()
                            .sorted(Comparator.comparingInt(BattlePlayer::getPoints).reversed())
                            .map(p -> p.getName() + " (" + p.getPoints() + ")")
                            .collect(Collectors.joining(", "));

                    scheduler.schedule(() -> ForceBattle.broadcast(
                            MiniMessage.miniMessage().deserialize(
                                    "<gold>" + place + ". <blue>" + team.getId() +
                                            " (<gray>" + players + "<blue>)<dark_gray>: <gold>" + points
                            )
                    ), counter[0] * delay, TimeUnit.MILLISECONDS);
                    counter[0]++;
                }
            }

            default -> sendUsage(caller);
        }

        scheduler.shutdown();
    }

    @Override
    public List<String> tabComplete(CommandCaller caller, String @NotNull [] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], List.of("player", "team"), new ArrayList<>());
        }
        return List.of();
    }
}
