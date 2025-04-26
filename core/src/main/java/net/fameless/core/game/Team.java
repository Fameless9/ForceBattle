package net.fameless.core.game;

import net.fameless.core.caption.Caption;
import net.fameless.core.event.EventDispatcher;
import net.fameless.core.event.PlayerTeamJoinEvent;
import net.fameless.core.event.PlayerTeamLeaveEvent;
import net.fameless.core.player.BattlePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Team {

    private static final Logger logger = LoggerFactory.getLogger("ForceBattle/" + Team.class.getSimpleName());
    public static final List<Team> teams = new ArrayList<>();

    private final List<BattlePlayer<?>> players;
    private final int ID;
    private boolean privateTeam = false;
    private JoinRequest lastJoinRequest = null;

    public Team(List<BattlePlayer<?>> players) {
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

    public void sendJoinRequest(BattlePlayer<?> battlePlayer) {
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

    public void addPlayer(BattlePlayer<?> battlePlayer) {
        PlayerTeamJoinEvent teamJoinEvent = new PlayerTeamJoinEvent(this, battlePlayer);
        EventDispatcher.post(teamJoinEvent);
        if (teamJoinEvent.isCancelled()) {
            logger.info("PlayerTeamJoinEvent has been denied by an external plugin.");
            return;
        }

        players.forEach(teamPlayer -> teamPlayer.sendMessage(Caption.of(
                "notification.player_joined",
                TagResolver.resolver("player", Tag.inserting(Component.text(battlePlayer.getName())))
        )));
        players.add(battlePlayer);
    }

    public void removePlayer(BattlePlayer<?> battlePlayer) {
        PlayerTeamLeaveEvent teamLeaveEvent = new PlayerTeamLeaveEvent(this, battlePlayer);
        EventDispatcher.post(teamLeaveEvent);
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

    public int getPoints() {
        int teamPoints = 0;

        for (BattlePlayer<?> teamPlayer : players) {
            teamPoints += teamPlayer.getPoints();
        }

        return teamPoints;
    }

    public int getId() {
        return ID;
    }

    public List<BattlePlayer<?>> getPlayers() {
        return players;
    }

    public JoinRequest getLastJoinRequest() {
        return lastJoinRequest;
    }

    public static class JoinRequest {

        private final BattlePlayer<?> battlePlayer;
        private final Team team;

        private JoinRequest(BattlePlayer<?> battlePlayer, Team team) {
            this.battlePlayer = battlePlayer;
            this.team = team;
        }

        private void accept() {
            battlePlayer.addToTeam(team);
        }

        private void reject() {
            battlePlayer.sendMessage(Caption.of("notification.player_rejected"));
        }

        public BattlePlayer<?> getBattlePlayer() {
            return battlePlayer;
        }

        public Team getTeam() {
            return team;
        }

    }

}
