package net.fameless.core.event;

import net.fameless.core.game.Team;
import net.fameless.core.player.BattlePlayer;

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
