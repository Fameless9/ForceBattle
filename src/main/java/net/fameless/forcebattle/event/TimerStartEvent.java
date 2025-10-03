package net.fameless.forcebattle.event;

import lombok.Getter;
import net.fameless.forcebattle.util.EventLogger;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class TimerStartEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private boolean cancelled;
    @Getter
    private int startTime;

    public TimerStartEvent(int startTime) {
        this.startTime = startTime;
    }

    public void setStartTime(int startTime) {
        if (startTime != this.startTime) {
            EventLogger.LOGGER.info("TimerStartEvent: value startTime has been changed to {}.", startTime);
            this.startTime = startTime;
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
