package net.fameless.core.event;

import net.fameless.core.util.EventLogger;

public class TimerStartTimeChangeEvent {

    private boolean cancelled;
    private int newStartTime;

    public TimerStartTimeChangeEvent(int newStartTime) {
        this.newStartTime = newStartTime;
    }

    public int getNewStartTime() {
        return newStartTime;
    }

    public void setNewStartTime(int newStartTime) {
        if (newStartTime != this.newStartTime) {
            EventLogger.LOGGER.info("TimerStartTimeChangeEvent: value newStartTime has been changed to {}.", newStartTime);
            this.newStartTime = newStartTime;
        }
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}
