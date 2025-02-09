package net.fameless.forceBattle.event;

import net.fameless.forceBattle.ForceBattle;
import net.fameless.forceBattle.game.Objective;
import net.fameless.forceBattle.player.BattlePlayer;

public class ObjectiveUpdateEvent implements CancellableEvent {

    private final BattlePlayer<?> battlePlayer;
    private boolean cancelled;
    private Objective newObjective;

    public ObjectiveUpdateEvent(BattlePlayer<?> battlePlayer, Objective objective) {
        this.battlePlayer = battlePlayer;
        this.newObjective = objective;
    }

    public BattlePlayer<?> getBattlePlayer() {
        return battlePlayer;
    }

    public Objective getNewObjective() {
        return newObjective;
    }

    public void setNewObjective(Objective newObjective) {
        if (newObjective != this.newObjective) {
            ForceBattle.logger().info("ObjectiveUpdateEvent: value newObjective has been changed.");
            this.newObjective = newObjective;
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
