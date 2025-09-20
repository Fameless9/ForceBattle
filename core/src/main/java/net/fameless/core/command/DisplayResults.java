package net.fameless.core.command;

import net.fameless.core.ForceBattle;
import net.fameless.core.caption.Caption;
import net.fameless.core.command.framework.CallerType;
import net.fameless.core.command.framework.Command;
import net.fameless.core.command.framework.CommandCaller;
import net.fameless.core.game.Team;
import net.fameless.core.player.BattlePlayer;
import net.fameless.core.util.StringUtil;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class DisplayResults extends Command {

    public DisplayResults() {
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

        final HashMap<BattlePlayer<?>, Integer> PLAYER_POINTS_MAP = new HashMap<>();
        final HashMap<Team, Integer> TEAM_POINTS_MAP = new HashMap<>();

        for (BattlePlayer<?> battlePlayer : BattlePlayer.BATTLE_PLAYERS) {
            PLAYER_POINTS_MAP.put(battlePlayer, battlePlayer.getPoints());
        }

        LinkedHashMap<BattlePlayer<?>, Integer> sortedPlayerPointsMap = PLAYER_POINTS_MAP.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                        HashMap.Entry::getKey,
                        HashMap.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        for (BattlePlayer<?> battlePlayer : BattlePlayer.BATTLE_PLAYERS) {
            Team team = battlePlayer.getTeam();
            int points = battlePlayer.getPoints();

            if (TEAM_POINTS_MAP.containsKey(team)) {
                TEAM_POINTS_MAP.put(team, points + TEAM_POINTS_MAP.get(team));
            } else {
                TEAM_POINTS_MAP.put(team, points);
            }
        }

        LinkedHashMap<Team, Integer> sortedTeamPointsMap = TEAM_POINTS_MAP.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                        HashMap.Entry::getKey,
                        HashMap.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        final long delay = 750;
        final long[] counter = {0};

        switch (args[0]) {
            case "player" -> {
                sortedPlayerPointsMap.forEach((battlePlayer, points) -> {
                    long place = sortedPlayerPointsMap.size() - counter[0];
                    scheduler.schedule(
                            () -> ForceBattle.platform().broadcast(
                                    MiniMessage.miniMessage().deserialize("<gold>" + place + ". <blue>" + battlePlayer.getName() + "<dark_gray>: <gold>" + points)
                            ), counter[0] * delay, TimeUnit.MILLISECONDS
                    );
                    counter[0]++;
                });

                scheduler.shutdown();
            }
            case "team" -> {
                sortedTeamPointsMap.forEach((team, points) -> {
                    long place = sortedTeamPointsMap.size() - counter[0];

                    String players = team.getPlayers().stream()
                            .sorted(Comparator.comparingInt((BattlePlayer<?> p) -> p.getPoints()).reversed())
                            .map(p -> p.getName() + " (" + p.getPoints() + ")")
                            .collect(Collectors.joining(", "));

                    scheduler.schedule(
                            () -> ForceBattle.platform().broadcast(
                                    MiniMessage.miniMessage().deserialize("<gold>" + place + ". <blue>" + team.getId() + " (<gray>" + players + "<blue>)" + "<dark_gray" +
                                            ">: <gold>" + points)
                            ), counter[0] * delay, TimeUnit.MILLISECONDS
                    );
                    counter[0]++;
                });

                scheduler.shutdown();
            }
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
