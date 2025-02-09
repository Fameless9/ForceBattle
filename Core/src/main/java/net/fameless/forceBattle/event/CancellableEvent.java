package net.fameless.forceBattle.event;

public interface CancellableEvent {

    boolean isCancelled();

    void setCancelled(boolean cancelled);

}
