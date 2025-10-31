package net.fameless.forcebattle.game;

import lombok.Getter;
import lombok.Setter;
import net.fameless.forcebattle.ForceBattle;
import net.fameless.forcebattle.player.BattlePlayer;
import net.fameless.forcebattle.util.BattleType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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

    public static @NotNull List<Objective> finishedBy(BattlePlayer battlePlayer) {
        return objectives.stream()
                .filter(Objective::isFinished)
                .filter(o -> o.whoFinished.equals(battlePlayer))
                .sorted(Comparator.comparingInt(Objective::getTime).reversed())
                .toList();
    }

    public static @NotNull List<Objective> finished() {
        return objectives.stream()
                .filter(Objective::isFinished)
                .sorted(Comparator.comparingInt(Objective::getTime).reversed())
                .toList();
    }

    public void delete() {
        objectives.remove(this);
    }

    public boolean hasBeenSkipped() {
        return hasBeenSkipped;
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

    public void setFinished(BattlePlayer whoFinished) {
        this.whoFinished = whoFinished;
        this.time = ForceBattle.getTimer().getTime();
    }
}
