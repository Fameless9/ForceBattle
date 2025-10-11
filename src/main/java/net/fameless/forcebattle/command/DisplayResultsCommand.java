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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        Map<BattlePlayer, Integer> PLAYER_POINTS_MAP = new HashMap<>();
        Map<Team, Integer> TEAM_POINTS_MAP = new HashMap<>();

        for (BattlePlayer battlePlayer : BattlePlayer.BATTLE_PLAYERS) {
            PLAYER_POINTS_MAP.put(battlePlayer, battlePlayer.getPoints());
        }

        for (Team team : Team.teams) {
            TEAM_POINTS_MAP.put(team, team.getPoints());
        }

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        final long delay = 750;
        final long[] counter = {0};

        switch (args[0].toLowerCase()) {
            case "player" -> {
                List<Map.Entry<BattlePlayer, Integer>> sortedPlayers = PLAYER_POINTS_MAP.entrySet().stream()
                        .sorted(Map.Entry.<BattlePlayer, Integer>comparingByValue().reversed())
                        .toList();

                List<Map.Entry<BattlePlayer, Integer>> reversed = new ArrayList<>(sortedPlayers);
                Collections.reverse(reversed);

                int currentPlace = 0;
                int skip = 0;
                int lastPoints = Integer.MIN_VALUE;

                Map<BattlePlayer, Integer> placeMap = new HashMap<>();
                for (Map.Entry<BattlePlayer, Integer> entry : sortedPlayers) {
                    int points = entry.getValue();
                    if (points != lastPoints) {
                        currentPlace += 1 + skip;
                        skip = 0;
                        lastPoints = points;
                    } else skip++;
                    placeMap.put(entry.getKey(), currentPlace);
                }

                for (Map.Entry<BattlePlayer, Integer> entry : reversed) {
                    BattlePlayer player = entry.getKey();
                    int points = entry.getValue();
                    int place = placeMap.get(player);

                    scheduler.schedule(() -> ForceBattle.broadcast(
                            MiniMessage.miniMessage().deserialize(
                                    "<gold>" + place + ". <blue>" + player.getName() + "<dark_gray>: <gold>" + points
                            )
                    ), counter[0] * delay, TimeUnit.MILLISECONDS);
                    counter[0]++;
                }

                scheduler.shutdown();
            }

            case "team" -> {
                List<Map.Entry<Team, Integer>> sortedTeams = TEAM_POINTS_MAP.entrySet().stream()
                        .sorted(Map.Entry.<Team, Integer>comparingByValue().reversed())
                        .toList();

                List<Map.Entry<Team, Integer>> reversed = new ArrayList<>(sortedTeams);
                Collections.reverse(reversed);

                int currentPlace = 0;
                int skip = 0;
                int lastPoints = Integer.MIN_VALUE;

                Map<Team, Integer> placeMap = new HashMap<>();
                for (Map.Entry<Team, Integer> entry : sortedTeams) {
                    int points = entry.getValue();
                    if (points != lastPoints) {
                        currentPlace += 1 + skip;
                        skip = 0;
                        lastPoints = points;
                    } else skip++;
                    placeMap.put(entry.getKey(), currentPlace);
                }

                for (Map.Entry<Team, Integer> entry : reversed) {
                    Team team = entry.getKey();
                    int points = entry.getValue();
                    int place = placeMap.get(team);

                    String players = team.getPlayers().stream()
                            .sorted(Comparator.comparingInt(BattlePlayer::getPoints).reversed())
                            .map(p -> p.getName() + " (" + p.getPoints() + ")")
                            .collect(Collectors.joining(", "));

                    scheduler.schedule(() -> ForceBattle.broadcast(
                            MiniMessage.miniMessage().deserialize(
                                    "<gold>" + place + ". <blue>" + team.getId() + " (<gray>" + players +
                                            "<blue>)<dark_gray>: <gold>" + points
                            )
                    ), counter[0] * delay, TimeUnit.MILLISECONDS);
                    counter[0]++;
                }

                scheduler.shutdown();
            }

            default -> sendUsage(caller);
        }
    }

    @Override
    public List<String> tabComplete(CommandCaller caller, String @NotNull [] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], List.of("player", "team"), new ArrayList<>());
        }
        return List.of();
    }
}
