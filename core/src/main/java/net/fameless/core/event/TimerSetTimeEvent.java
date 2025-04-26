package net.fameless.core.event;

import net.fameless.core.util.EventLogger;

public class TimerSetTimeEvent implements CancellableEvent {

    private boolean cancelled;
    private int newTime;

    public TimerSetTimeEvent(int newTime) {
        this.newTime = newTime;
    }

    public int getNewTime() {
        return newTime;
    }

    public void setNewTime(int newTime) {
        if (newTime != this.newTime) {
            EventLogger.LOGGER.info("TimerSetTimeEvent: value newTime has been changed to {}.", newTime);
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

}
