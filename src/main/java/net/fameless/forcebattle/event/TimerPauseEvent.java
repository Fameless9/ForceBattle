package net.fameless.forcebattle.event;

import lombok.Getter;
import net.fameless.forcebattle.util.EventLogger;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class TimerPauseEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private boolean cancelled;

    @Getter
    private int pauseTime;

    public TimerPauseEvent(int pauseTime) {
        this.pauseTime = pauseTime;
    }

    public void setPauseTime(int pauseTime) {
        if (pauseTime != this.pauseTime) {
            EventLogger.LOGGER.info("TimerPauseEvent: value pauseTime has been changed to {}.", pauseTime);
            this.pauseTime = pauseTime;
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
