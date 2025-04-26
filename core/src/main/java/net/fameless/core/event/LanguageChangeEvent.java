package net.fameless.core.event;

import net.fameless.core.caption.Language;
import net.fameless.core.util.EventLogger;

public class LanguageChangeEvent implements CancellableEvent {

    private boolean cancelled;
    private Language newLanguage;

    public LanguageChangeEvent(Language newLanguage) {
        this.newLanguage = newLanguage;
    }

    public Language getNewLanguage() {
        return newLanguage;
    }

    public void setNewLanguage(Language newLanguage) {
        if (newLanguage != this.newLanguage) {
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

}
