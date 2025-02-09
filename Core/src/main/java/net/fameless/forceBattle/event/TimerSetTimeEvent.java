package net.fameless.forceBattle.event;

import net.fameless.forceBattle.ForceBattle;

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
            ForceBattle.logger().info("TimerSetTimeEvent: value newTime has been changed.");
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
