package net.fameless.core.game;

import net.fameless.core.ForceBattle;
import net.fameless.core.caption.Caption;
import net.fameless.core.player.BattlePlayer;
import net.fameless.core.util.BattleType;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimerTask;

public class Timer {

    private static final Logger logger = LoggerFactory.getLogger("ForceBattle/" + Timer.class.getSimpleName());

    private int startTime;
    private int time;
    private boolean running;
    private boolean hitZero;

    public Timer(int time, boolean running) {
        this.startTime = time;
        this.time = time;

        try {
            setRunning(running);
        } catch (IllegalStateException e) {
            logger.error("Failed to set running state for Timer: {}", e.getMessage());
            this.running = false;
        }

        runTimerTask();
        runActionbarTask();
    }

    private void runActionbarTask() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (running && !BattleType.isAnyEnabled()) {
                    running = false;
                    ForceBattle.platform().broadcast(Caption.of("notification.no_objectives_available"));
                }
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
        this.startTime = newStartTime;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int newTime) {
        this.time = newTime;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) throws IllegalStateException {
        if (running) {
            if (!BattleType.isAnyEnabled()) {
                throw new IllegalStateException("Cannot start the timer when no objectives are available.");
            }
            this.time = startTime;
            this.hitZero = false;
            this.running = true;
        } else {
            this.running = false;
        }
    }

    public boolean hasHitZero() {
        return hitZero;
    }

}
