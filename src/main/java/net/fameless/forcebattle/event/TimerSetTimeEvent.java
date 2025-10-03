package net.fameless.forcebattle.event;

import lombok.Getter;
import net.fameless.forcebattle.util.EventLogger;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class TimerSetTimeEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled;

    @Getter
    private int newTime;

    public TimerSetTimeEvent(int newTime) {
        this.newTime = newTime;
    }

    public void setNewTime(int newTime) {
        if (newTime != this.newTime) {
            EventLogger.LOGGER.info("TimerSetTimeEvent: newTime changed to {}.", newTime);
            this.newTime = newTime;
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
