package net.fameless.forcebattle.event;

public interface CancellableEvent {

    boolean isCancelled();

    void setCancelled(boolean cancelled);

}
