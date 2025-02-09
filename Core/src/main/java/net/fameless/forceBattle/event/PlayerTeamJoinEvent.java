package net.fameless.forceBattle.event;

import net.fameless.forceBattle.game.Team;
import net.fameless.forceBattle.player.BattlePlayer;

public class PlayerTeamJoinEvent implements CancellableEvent {

    private final BattlePlayer<?> whoJoined;
    private final Team team;
    private boolean cancelled;

    public PlayerTeamJoinEvent(Team team, BattlePlayer<?> whoJoined) {
        this.team = team;
        this.whoJoined = whoJoined;
    }

    public Team getTeam() {
        return team;
    }

    public BattlePlayer<?> getWhoJoined() {
        return whoJoined;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}
