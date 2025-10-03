package net.fameless.forcebattle.util;

import net.fameless.forcebattle.game.Team;
import net.fameless.forcebattle.player.BattlePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class TabCompletions {

    private TabCompletions() {
    }

    public static @NotNull List<String> getPlayerNamesTabCompletions() {
        List<String> playerNames = new ArrayList<>();
        BattlePlayer.BATTLE_PLAYERS.forEach(battlePlayer -> playerNames.add(battlePlayer.getName()));
        return playerNames;
    }

    public static @NotNull List<String> getTeamIdTabCompletions() {
        List<String> teamIdList = new ArrayList<>();
        Team.teams.forEach(team -> teamIdList.add(String.valueOf(team.getId())));
        return teamIdList;
    }

    public static @NotNull List<String> getTeamPlaces() {
        List<String> placeList = new ArrayList<>();
        Map<Integer, Team> places = Team.getPlaces();
        places.forEach((place, team) -> placeList.add(String.valueOf(place)));
        return placeList;
    }

    public static @NotNull List<String> getPlayerPlaces() {
        List<String> placeList = new ArrayList<>();
        Map<Integer, BattlePlayer> places = BattlePlayer.getPlaces();
        places.forEach((place, player) -> placeList.add(String.valueOf(place)));
        return placeList;
    }
}
