package net.fameless.forcebattle.event;

import lombok.Getter;
import net.fameless.forcebattle.caption.Language;
import net.fameless.forcebattle.util.EventLogger;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class LanguageChangeEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled;

    @Getter
    private Language newLanguage;

    public LanguageChangeEvent(Language newLanguage) {
        this.newLanguage = newLanguage;
    }

    public void setNewLanguage(Language newLanguage) {
        if (this.newLanguage != newLanguage) {
            EventLogger.LOGGER.info("LanguageChangeEvent: value newLanguage has been changed to {}.", newLanguage.name());
            this.newLanguage = newLanguage;
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
