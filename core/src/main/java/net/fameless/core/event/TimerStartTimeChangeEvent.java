package net.fameless.core.event;

import net.fameless.core.ForceBattle;

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
            ForceBattle.logger().info("TimerStartTimeChangeEvent: value newStartTime has been changed.");
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
