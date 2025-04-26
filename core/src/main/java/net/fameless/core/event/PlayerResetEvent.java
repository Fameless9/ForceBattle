package net.fameless.core.event;

import net.fameless.core.player.BattlePlayer;

public class PlayerResetEvent implements CancellableEvent {

    private final BattlePlayer<?> player;
    private boolean cancelled;

    public PlayerResetEvent(BattlePlayer<?> player) {
        this.player = player;
    }

    public BattlePlayer<?> getPlayer() {
        return player;
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
