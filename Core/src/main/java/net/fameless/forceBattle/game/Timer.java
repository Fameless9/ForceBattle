package net.fameless.forceBattle.game;

import net.fameless.forceBattle.ForceBattle;
import net.fameless.forceBattle.caption.Caption;
import net.fameless.forceBattle.event.EventDispatcher;
import net.fameless.forceBattle.event.TimerPauseEvent;
import net.fameless.forceBattle.event.TimerSetTimeEvent;
import net.fameless.forceBattle.event.TimerStartEvent;
import net.fameless.forceBattle.event.TimerStartTimeChangeEvent;
import net.fameless.forceBattle.player.BattlePlayer;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;

import java.util.TimerTask;

public class Timer {

    private int startTime;
    private int time;
    private boolean running;
    private boolean hitZero;

    public Timer(int time, boolean running) {
        this.startTime = time;
        this.time = time;
        this.running = running;
        runTimerTask();
        runActionbarTask();
    }

    private void runActionbarTask() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                BattlePlayer.getOnlinePlayers().forEach(
                        forceBattlePlayer -> forceBattlePlayer.sendActionbar(Caption.of(running ? "timer.running" : "timer.paused")));
            }
        };

        java.util.Timer timer = new java.util.Timer("forcebattle/actionbar");
        timer.scheduleAtFixedRate(task, 0L, 150L);
    }

    private void runTimerTask() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (running) {
                    time--;
                    if (time < 11) {
                        BattlePlayer.BATTLE_PLAYERS.forEach(battlePlayer -> battlePlayer.playSound(
                                Sound.sound(
                                        Key.key("block.note_block.bass"),
                                        Sound.Source.MASTER,
                                        20,
                                        20
                                )
                        ));
                    }
                }
                if (time == 0) {
                    running = false;
                    time = startTime;
                    hitZero = true;
                    ForceBattle.platform().broadcast(Caption.of("notification.battle_over"));
                }
            }
        };

        java.util.Timer timer = new java.util.Timer("forcebattle/timer");
        timer.scheduleAtFixedRate(task, 0L, 1000L);
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int newStartTime) {
        TimerStartTimeChangeEvent startTimeChangeEvent = new TimerStartTimeChangeEvent(newStartTime);
        EventDispatcher.post(startTimeChangeEvent);
        if (startTimeChangeEvent.isCancelled()) {
            ForceBattle.logger().info("TimerStartTimeChangeEvent has been denied by an external plugin.");
            return;
        }
        this.startTime = startTimeChangeEvent.getNewStartTime();
    }

    public int getTime() {
        return time;
    }

    public void setTime(int newTime) {
        TimerSetTimeEvent setTimeEvent = new TimerSetTimeEvent(newTime);
        EventDispatcher.post(setTimeEvent);
        if (setTimeEvent.isCancelled()) {
            ForceBattle.logger().info("TimerSetTimeEvent has been denied by an external plugin.");
            return;
        }
        this.time = setTimeEvent.getNewTime();
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        if (running) {
            TimerStartEvent startEvent = new TimerStartEvent(this.time);
            EventDispatcher.post(startEvent);
            if (startEvent.isCancelled()) {
                ForceBattle.logger().info("TimerStartEvent has been denied by an external plugin.");
                return;
            }
            this.time = startEvent.getStartTime();
            this.hitZero = false;
            this.running = true;
        } else {
            TimerPauseEvent pauseEvent = new TimerPauseEvent(this.time);
            EventDispatcher.post(pauseEvent);
            if (pauseEvent.isCancelled()) {
                ForceBattle.logger().info("TimerPauseEvent has been denied by an external plugin.");
                return;
            }
            this.time = pauseEvent.getPauseTime();
            this.running = false;
        }
    }

    public boolean hasHitZero() {
        return hitZero;
    }

    public void start() {
        running = true;
    }

    public void pause() {
        running = false;
    }

}
