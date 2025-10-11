package net.fameless.forcebattle.event;

import lombok.Getter;
import net.fameless.forcebattle.game.Objective;
import net.fameless.forcebattle.game.Team;
import net.fameless.forcebattle.player.BattlePlayer;
import net.fameless.forcebattle.util.EventLogger;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ObjectiveUpdateEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    @Getter
    private final BattlePlayer battlePlayer;
    @Getter
    private final Team team;
    @Getter
    private Objective newObjective;
    private boolean cancelled;

    public ObjectiveUpdateEvent(@NotNull BattlePlayer battlePlayer, @NotNull Objective newObjective) {
        this.battlePlayer = battlePlayer;
        this.team = null;
        this.newObjective = newObjective;
    }

    public ObjectiveUpdateEvent(@NotNull Team team, @NotNull Objective newObjective) {
        this.battlePlayer = null;
        this.team = team;
        this.newObjective = newObjective;
    }

    public void setNewObjective(Objective newObjective) {
        if (this.newObjective != newObjective) {
            EventLogger.LOGGER.info("ObjectiveUpdateEvent: newObjective changed to {}.", newObjective.getObjectiveString());
            this.newObjective = newObjective;
        }
    }

    public boolean isTeamEvent() {
        return team != null;
    }

    public boolean isPlayerEvent() {
        return battlePlayer != null;
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
