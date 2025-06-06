package net.fameless.core.util;

import net.fameless.core.game.Team;
import net.fameless.core.player.BattlePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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

}
