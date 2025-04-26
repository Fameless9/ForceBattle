package net.fameless.core.event;

import net.fameless.core.player.BattlePlayer;
import net.fameless.core.util.EventLogger;

public class PlayerExcludeEvent implements CancellableEvent {

    private final BattlePlayer<?> player;
    private boolean cancelled;
    private boolean newExcluded;

    public PlayerExcludeEvent(BattlePlayer<?> player, boolean newExcluded) {
        this.player = player;
        this.newExcluded = newExcluded;
    }

    public BattlePlayer<?> getPlayer() {
        return player;
    }

    public boolean isNewExcluded() {
        return newExcluded;
    }

    public void setNewExcluded(boolean newExcluded) {
        if (newExcluded != this.newExcluded) {
            EventLogger.LOGGER.info("PlayerExcludeEvent: value newExcluded has been changed to {}.", newExcluded);
            this.newExcluded = newExcluded;
        }
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
