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

    public static void loadNative(String absolutePath) {
        System.load(absolutePath);
    }

    public static void link(Path nativeLink, IEventBus modEventBus, String supposedName) {
        String linkedName = nativeLink.getFileName().toString().replaceFirst("[.][^.]+$", "");
        if (!linkedName.equals(supposedName)) {
            throw new RuntimeException("dbind: native " + nativeLink + " does not match mod " + supposedName);
        }

        ModShimClassLoader shimLoader = new ModShimClassLoader(supposedName, NativeLinker.class.getClassLoader());
        try {
            shimLoader.loadNative(nativeLink.toAbsolutePath().toString());
            log.info("Loaded {}", nativeLink);
            shimLoader.init(modEventBus, NeoForge.EVENT_BUS);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("dbind: failed to load mod " + supposedName, e);
        }
    }

    public static void dLog(String message) {
        log.info("{}", message);
    }

    //overload
    public static void dLog(String message, int severity /*could've used an enum, probably, but too lazy (it's on the d side)*/) { //or i could've used whatever the equivilent of templates in java is called
        switch (severity) {
            case 1:
                log.error("{}", message); 
                break;
            case 2:
                log.warn("{}", message); 
                break;
            case 3:
                log.info("{}", message); 
                break;
            case 4:
                log.debug("{}", message); 
                break;
            case 5:
                log.trace("{}", message); 
                break;
        }
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
