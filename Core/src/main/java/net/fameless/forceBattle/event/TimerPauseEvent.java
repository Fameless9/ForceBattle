package net.fameless.forceBattle.event;

import net.fameless.forceBattle.ForceBattle;

public class TimerPauseEvent implements CancellableEvent {

    private boolean cancelled;
    private int pauseTime;

    public TimerPauseEvent(int pauseTime) {
        this.pauseTime = pauseTime;
    }

    public int getPauseTime() {
        return pauseTime;
    }

    public void setPauseTime(int pauseTime) {
        if (pauseTime != this.pauseTime) {
            ForceBattle.logger().info("TimerPauseEvent: value pauseTime has been changed.");
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

}
