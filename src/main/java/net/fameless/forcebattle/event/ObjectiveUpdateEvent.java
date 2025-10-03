package net.fameless.forcebattle.event;

import lombok.Getter;
import net.fameless.forcebattle.game.Objective;
import net.fameless.forcebattle.player.BattlePlayer;
import net.fameless.forcebattle.util.EventLogger;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ObjectiveUpdateEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled;

    @Getter
    private final BattlePlayer battlePlayer;
    @Getter
    private Objective newObjective;

    public ObjectiveUpdateEvent(BattlePlayer battlePlayer, Objective newObjective) {
        this.battlePlayer = battlePlayer;
        this.newObjective = newObjective;
    }

    public void setNewObjective(Objective newObjective) {
        if (this.newObjective != newObjective) {
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

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
