package net.fameless.forcebattle.event;

import lombok.Getter;
import net.fameless.forcebattle.player.BattlePlayer;
import net.fameless.forcebattle.util.EventLogger;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerExcludeEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled;

    @Getter
    private final BattlePlayer player;
    @Getter
    private boolean newExcluded;

    public PlayerExcludeEvent(BattlePlayer player, boolean newExcluded) {
        this.player = player;
        this.newExcluded = newExcluded;
    }

    public void setNewExcluded(boolean newExcluded) {
        if (this.newExcluded != newExcluded) {
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

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
