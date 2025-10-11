package net.fameless.forcebattle.game;

import lombok.Getter;
import lombok.Setter;
import net.fameless.forcebattle.ForceBattle;
import net.fameless.forcebattle.caption.Caption;
import net.fameless.forcebattle.event.ObjectiveUpdateEvent;
import net.fameless.forcebattle.event.PlayerTeamJoinEvent;
import net.fameless.forcebattle.event.PlayerTeamLeaveEvent;
import net.fameless.forcebattle.player.BattlePlayer;
import net.fameless.forcebattle.util.Format;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Team {

    private static final Logger logger = LoggerFactory.getLogger("ForceBattle/" + Team.class.getSimpleName());
    public static final List<Team> teams = new ArrayList<>();

    @Getter
    private final List<BattlePlayer> players;
    private final int ID;
    private boolean privateTeam = false;
    @Getter
    @Setter
    private Objective objective;
    @Getter
    private JoinRequest lastJoinRequest = null;

    public Team(List<BattlePlayer> players) {
        this.players = new ArrayList<>(players);

        List<Integer> usedIdList = new ArrayList<>();
        for (Team team : teams) {
            usedIdList.add(team.ID);
        }

        int id = 0;
        while (usedIdList.contains(id)) {
            id++;
        }
        this.ID = id;

        teams.add(this);
    }

    public static Optional<Team> ofId(int id) {
        for (Team team : teams) {
            if (team.getId() == id) {
                return Optional.of(team);
            }
        }
        return Optional.empty();
    }

    public void sendJoinRequest(BattlePlayer battlePlayer) {
        if (!isPrivate()) {
            battlePlayer.addToTeam(this);
            return;
        }
        this.lastJoinRequest = new JoinRequest(battlePlayer, this);
        players.forEach(teamPlayer ->
                teamPlayer.sendMessage(Caption.of(
                        "notification.team_join_request",
                        TagResolver.resolver("player", Tag.inserting(Component.text(teamPlayer.getName())))
                ))
        );
    }

    public void finishLastJoinRequest(boolean accept) {
        if (accept) {
            lastJoinRequest.accept();
        } else {
            lastJoinRequest.reject();
        }
        this.lastJoinRequest = null;
    }

    public boolean isPrivate() {
        return privateTeam;
    }

    public void setPrivate(boolean privateTeam) {
        this.privateTeam = privateTeam;
    }

    public void addPlayer(BattlePlayer battlePlayer) {
        PlayerTeamJoinEvent teamJoinEvent = new PlayerTeamJoinEvent(this, battlePlayer);
        Bukkit.getPluginManager().callEvent(teamJoinEvent);
        if (teamJoinEvent.isCancelled()) {
            logger.info("PlayerTeamJoinEvent has been denied by an external plugin.");
            return;
        }

        players.forEach(teamPlayer -> teamPlayer.sendMessage(Caption.of(
                "notification.player_joined",
                TagResolver.resolver("player", Tag.inserting(Component.text(battlePlayer.getName())))
        )));
        players.add(battlePlayer);
        battlePlayer.sendMessage(Caption.of("notification.team_joined", TagResolver.resolver("id", Tag.inserting(Component.text(this.getId())))));
    }

    public void removePlayer(BattlePlayer battlePlayer) {
        PlayerTeamLeaveEvent teamLeaveEvent = new PlayerTeamLeaveEvent(this, battlePlayer);
        Bukkit.getPluginManager().callEvent(teamLeaveEvent);
        if (teamLeaveEvent.isCancelled()) {
            logger.info("PlayerTeamLeaveEvent has been denied by an external plugin.");
            return;
        }

        players.remove(battlePlayer);
        players.forEach(teamPlayer -> teamPlayer.sendMessage(Caption.of(
                "notification.player_left",
                TagResolver.resolver("player", Tag.inserting(Component.text(battlePlayer.getName())))
        )));
        if (players.isEmpty()) {
            this.delete();
        }
    }

    public void delete() {
        teams.remove(this);
    }

    public static void deleteAll() {
        for (Team team : new ArrayList<>(teams)) {
            team.delete();
        }
    }

    public int getPoints() {
        int teamPoints = 0;

        for (BattlePlayer teamPlayer : players) {
            teamPoints += teamPlayer.getPoints();
        }

        return teamPoints;
    }

    public int getId() {
        return ID;
    }

    public static Map<Integer, Team> getPlaces() {
        Map<Integer, Team> places = new HashMap<>();

        final Map<Team, Integer> TEAM_POINTS_MAP = new HashMap<>();
        for (Team team : teams) {
            TEAM_POINTS_MAP.put(team, team.getPoints());
        }

        List<Map.Entry<Team, Integer>> sortedTeams = TEAM_POINTS_MAP.entrySet()
                .stream()
                .sorted(Map.Entry.<Team, Integer>comparingByValue().reversed())
                .toList();

        int place = 1;
        for (Map.Entry<Team, Integer> entry : sortedTeams) {
            places.put(place++, entry.getKey());
        }

        return places;
    }

    public static int getPlace(Team team) {
        return getPlaces().entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(team))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(-1);
    }

    public void updateObjective(BattlePlayer finisher, boolean finishLast, boolean hasBeenSkipped) {
        Objective newObjective = ForceBattle.getObjectiveManager().getNewObjective(this);
        ObjectiveUpdateEvent updateEvent = new ObjectiveUpdateEvent(this, newObjective);
        Bukkit.getPluginManager().callEvent(updateEvent);
        if (updateEvent.isCancelled()) {
            logger.info("ObjectiveUpdateEvent has been denied by an external plugin.");
            return;
        }

        setCurrentObjective(updateEvent.getNewObjective(), finisher, finishLast, hasBeenSkipped);
    }

    public void setCurrentObjective(Objective newObjective, BattlePlayer finisher, boolean finishLast, boolean hasBeenSkipped) {
        if (finishLast && this.objective != null) {
            this.objective.setFinished(finisher);
            this.objective.setHasBeenSkipped(hasBeenSkipped);

            finisher.setPoints(finisher.getPoints() + 1);
        }

        if (ForceBattle.getTimer().isRunning()) {
            for (BattlePlayer member : players) {
                member.sendMessage(Caption.of(
                        "notification.next_objective",
                        TagResolver.resolver("objective", Tag.inserting(Component.text(Format.formatName(newObjective.getObjectiveString())))),
                        TagResolver.resolver("objective_type", Tag.inserting(Component.text(newObjective.getBattleType().getPrefix())))
                ));
            }
        }

        this.objective = newObjective;
    }

    @Getter
    public static class JoinRequest {

        private final BattlePlayer battlePlayer;
        private final Team team;

        private JoinRequest(BattlePlayer battlePlayer, Team team) {
            this.battlePlayer = battlePlayer;
            this.team = team;
        }

        private void accept() {
            battlePlayer.addToTeam(team);
        }

        private void reject() {
            battlePlayer.sendMessage(Caption.of("notification.player_rejected"));
        }

    }

}
