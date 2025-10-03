package net.fameless.forcebattle.game;

import lombok.Getter;
import net.fameless.forcebattle.ForceBattle;
import net.fameless.forcebattle.caption.Caption;
import net.fameless.forcebattle.event.TimerEndEvent;
import net.fameless.forcebattle.event.TimerPauseEvent;
import net.fameless.forcebattle.event.TimerSetTimeEvent;
import net.fameless.forcebattle.event.TimerStartEvent;
import net.fameless.forcebattle.event.TimerStartTimeChangeEvent;
import net.fameless.forcebattle.player.BattlePlayer;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimerTask;

public class Timer {

    private static final Logger logger = LoggerFactory.getLogger("ForceBattle/" + Timer.class.getSimpleName());

    @Getter
    private int startTime;
    @Getter
    private int time;
    @Getter
    private boolean running;
    private boolean hitZero;
    private java.util.Timer timer;
    private TimerTask timerTask;

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
        if (timer != null) timer.cancel();
        if (timerTask != null) timerTask.cancel();

        timer = new java.util.Timer("forcebattle/timer");
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (!running) return;

                time--;
                if (time < 11 && time > 0) {
                    BattlePlayer.BATTLE_PLAYERS.forEach(battlePlayer ->
                            battlePlayer.playSound(Sound.sound(Key.key("block.note_block.bass"), Sound.Source.MASTER, 20, 20))
                    );
                }

                if (time <= 0) {
                    running = false;
                    hitZero = true;

                    Bukkit.getScheduler().runTask(ForceBattle.get(), () -> {
                        TimerEndEvent timerEndEvent = new TimerEndEvent();
                        Bukkit.getPluginManager().callEvent(timerEndEvent);
                    });
                }
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0L, 1000L);
    }

    public void setStartTime(int newStartTime) {
        TimerStartTimeChangeEvent startTimeChangeEvent = new TimerStartTimeChangeEvent(newStartTime);
        Bukkit.getPluginManager().callEvent(startTimeChangeEvent);
        if (startTimeChangeEvent.isCancelled()) {
            logger.info("TimerStartTimeChangeEvent has been denied by an external plugin.");
            return;
        }
        this.startTime = startTimeChangeEvent.getNewStartTime();
    }

    public void setTime(int newTime) {
        TimerSetTimeEvent setTimeEvent = new TimerSetTimeEvent(newTime);
        Bukkit.getPluginManager().callEvent(setTimeEvent);
        if (setTimeEvent.isCancelled()) {
            logger.info("TimerSetTimeEvent has been denied by an external plugin.");
            return;
        }
        this.time = setTimeEvent.getNewTime();
    }

    public void setRunning(boolean running) {
        if (running) {
            TimerStartEvent startEvent = new TimerStartEvent(this.time);
            Bukkit.getPluginManager().callEvent(startEvent);
            if (startEvent.isCancelled()) {
                logger.info("TimerStartEvent has been denied by an external plugin.");
                return;
            }
            this.time = startEvent.getStartTime();
            this.hitZero = false;
            this.running = true;
        } else {
            TimerPauseEvent pauseEvent = new TimerPauseEvent(this.time);
            Bukkit.getPluginManager().callEvent(pauseEvent);
            if (pauseEvent.isCancelled()) {
                logger.info("TimerPauseEvent has been denied by an external plugin.");
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
        if (!running) {
            setRunning(true);
        }
    }

    public void pause() {
        if (running) {
            setRunning(false);
        }
    }

}
