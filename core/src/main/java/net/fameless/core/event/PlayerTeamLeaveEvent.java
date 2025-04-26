package net.fameless.core.event;

import net.fameless.core.game.Team;
import net.fameless.core.player.BattlePlayer;

public class PlayerTeamLeaveEvent implements CancellableEvent {

    private final BattlePlayer<?> whoLeaves;
    private final Team team;
    private boolean cancelled;

    public PlayerTeamLeaveEvent(Team team, BattlePlayer<?> whoLeaves) {
        this.team = team;
        this.whoLeaves = whoLeaves;
    }

    public BattlePlayer<?> getWhoLeaves() {
        return whoLeaves;
    }

    public Team getTeam() {
        return team;
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
