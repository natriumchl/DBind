//jni is hard

package com.dbind.dbindmod;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

import java.nio.file.Path;

public class NativeLinker {
    private static final Logger log = LogUtils.getLogger();

    public static native void onModInit(IEventBus modEventBus, IEventBus gameEventBus);
    public static native void onAnyEvent(Event event);

    public static void link(Path nativeLink, IEventBus modEventBus) {
        System.load(nativeLink.toAbsolutePath().toString());
        onModInit(modEventBus, NeoForge.EVENT_BUS);
    }

    public static void dLog(String message) {
        log.info("{}", message);
    }
    
    public static IEventBus dEventBus() {
        return NeoForge.EVENT_BUS;
    }

    
    //event exposing (AI-generated code is unreadable sometimes :cry:)
    public static void dSubscribe(IEventBus bus, String eventClassName) throws ClassNotFoundException {
        dSubscribe(bus, eventClassName, EventPriority.NORMAL, false);
    }
    public static void dSubscribe(
            IEventBus bus,
            String eventClassName,
            EventPriority priority,
            boolean receiveCanceled
    ) throws ClassNotFoundException {
        Class<?> raw = Class.forName(eventClassName);

        if (!Event.class.isAssignableFrom(raw)) {
            throw new IllegalArgumentException("Not a NeoForge Event: " + eventClassName);
        }

        subscribe(bus, raw.asSubclass(Event.class), priority, receiveCanceled);
    }
    private static <T extends Event> void subscribe(
            IEventBus bus,
            Class<T> eventClass,
            EventPriority priority,
            boolean receiveCanceled
    ) {
        bus.addListener(priority, receiveCanceled, eventClass, NativeLinker::onAnyEvent);
    }
}
