package net.fameless.core.event;

import net.fameless.core.game.Objective;
import net.fameless.core.player.BattlePlayer;
import net.fameless.core.util.EventLogger;

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
            EventLogger.LOGGER.info("ObjectiveUpdateEvent: value newObjective has been changed to {}.", newObjective.getObjectiveString());
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
