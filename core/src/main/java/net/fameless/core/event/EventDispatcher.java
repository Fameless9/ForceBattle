package net.fameless.core.event;

import com.google.common.eventbus.EventBus;

public class EventDispatcher {

    private static final EventBus eventBus = new EventBus();

    public static void register(Object listener) {
        eventBus.register(listener);
    }

    public static void unregister(Object listener) {
        eventBus.unregister(listener);
    }

    public static void post(Object event) {
        eventBus.post(event);
    }

}
