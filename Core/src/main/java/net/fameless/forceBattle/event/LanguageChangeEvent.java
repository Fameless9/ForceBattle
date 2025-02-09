package net.fameless.forceBattle.event;

import net.fameless.forceBattle.ForceBattle;
import net.fameless.forceBattle.caption.Language;

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
            ForceBattle.logger().info("LanguageChangeEvent: value newLanguage has been changed.");
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
