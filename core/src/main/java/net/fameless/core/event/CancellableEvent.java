package net.fameless.core.event;

public interface CancellableEvent {

    boolean isCancelled();

    void setCancelled(boolean cancelled);

}
