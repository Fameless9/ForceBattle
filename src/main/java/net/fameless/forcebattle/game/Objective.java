package net.fameless.forcebattle.game;

import lombok.Getter;
import lombok.Setter;
import net.fameless.forcebattle.ForceBattle;
import net.fameless.forcebattle.player.BattlePlayer;
import net.fameless.forcebattle.util.BattleType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Objective {

    private static final List<Objective> objectives = new ArrayList<>();
    @Getter
    private final BattleType battleType;
    @Getter
    private final String objectiveString;
    private int time;
    @Getter
    private BattlePlayer whoFinished;
    @Setter
    private boolean hasBeenSkipped;

    public Objective(BattleType battleType, String objectiveString) {
        this.battleType = battleType;
        this.objectiveString = objectiveString;
        objectives.add(this);
    }

    public static @NotNull Set<Objective> finishedBy(BattlePlayer battlePlayer) {
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

    public boolean hasBeenSkipped() { return hasBeenSkipped; }

    public int getTime() {
        if (!isFinished()) {
            return -1;
        }
        return time;
    }

    public boolean isFinished() {
        return whoFinished != null;
    }

    public void setFinished(BattlePlayer whoFinished) {
        this.whoFinished = whoFinished;
        this.time = ForceBattle.getTimer().getTime();
    }

}
