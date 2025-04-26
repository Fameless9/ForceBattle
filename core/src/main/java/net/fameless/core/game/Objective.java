package net.fameless.core.game;

import net.fameless.core.ForceBattle;
import net.fameless.core.player.BattlePlayer;
import net.fameless.core.util.BattleType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Objective {

    private static final List<Objective> objectives = new ArrayList<>();
    private final BattleType battleType;
    private final String objectiveString;
    private int time;
    private BattlePlayer<?> whoFinished;

    public Objective(BattleType battleType, String objectiveString) {
        this.battleType = battleType;
        this.objectiveString = objectiveString;
        objectives.add(this);
    }

    public static @NotNull Set<Objective> finishedBy(BattlePlayer<?> battlePlayer) {
        Set<Objective> byPlayer = new HashSet<>();
        for (Objective o : objectives) {
            if (o.isFinished() && o.whoFinished.equals(battlePlayer)) {
                byPlayer.add(o);
            }
        }
        return byPlayer;
    }

    public static @NotNull Set<Objective> finished() {
        Set<Objective> finished = new HashSet<>();
        for (Objective o : objectives) {
            if (o.isFinished()) {
                finished.add(o);
            }
        }
        return finished;
    }

    public void delete() {
        objectives.remove(this);
    }

    public BattleType getBattleType() {
        return battleType;
    }

    public String getObjectiveString() {
        return objectiveString;
    }

    public BattlePlayer<?> getWhoFinished() {
        return whoFinished;
    }

    public int getTime() {
        if (!isFinished()) {
            return -1;
        }
        return time;
    }

    public boolean isFinished() {
        return whoFinished != null;
    }

    public void setFinished(BattlePlayer<?> whoFinished) {
        this.whoFinished = whoFinished;
        this.time = ForceBattle.getTimer().getTime();
    }

}
