//defines an isolated copy of NativeLinker per mod and calls its native loader/init
//thanks hy4 flash free, very cool
package com.dbind.dbindmod;

import java.io.IOException;
import java.io.InputStream;

import net.neoforged.bus.api.IEventBus;

public class ModShimClassLoader extends ClassLoader {
    private static final String SHIM_NAME = "com.dbind.dbindmod.NativeLinker";
    private static final String SHIM_RESOURCE = "com/dbind/dbindmod/NativeLinker.class";

    public ModShimClassLoader(String modKey, ClassLoader parent) {
        super("dbind-shim-" + modKey, parent);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if (!SHIM_NAME.equals(name)) {
            return super.loadClass(name, resolve);
        }
        synchronized (getClassLoadingLock(name)) {
            Class<?> shim = findLoadedClass(name);
            if (shim == null) {
                byte[] bytes = shimBytes();
                shim = defineClass(name, bytes, 0, bytes.length);
            }
            if (resolve) {
                resolveClass(shim);
            }
            return shim;
        }
    }

    private byte[] shimBytes() throws ClassNotFoundException {
        try (InputStream in = getParent().getResourceAsStream(SHIM_RESOURCE)) {
            if (in == null) {
                throw new ClassNotFoundException(SHIM_RESOURCE);
            }
            return in.readAllBytes();
        } catch (IOException e) {
            throw new ClassNotFoundException(SHIM_RESOURCE, e);
        }
    }

    public void loadNative(String absolutePath) throws ReflectiveOperationException {
        Class<?> shim = loadClass(SHIM_NAME);
        shim.getMethod("loadNative", String.class).invoke(null, absolutePath);
    }

    public void init(IEventBus modEventBus, IEventBus gameEventBus) throws ReflectiveOperationException {
        Class<?> shim = loadClass(SHIM_NAME);
        shim.getMethod("onModInit", IEventBus.class, IEventBus.class).invoke(null, modEventBus, gameEventBus);
    }
}
