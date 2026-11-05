package com.dbind.dbindmod;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = DBindMod.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = DBindMod.MODID, value = Dist.CLIENT)


public class DBindMod {
    public static final String MODID = "dbindmod";
    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        if(!(DependencyDetector.dubAvailable())) {
            // TODO
        }
    }
}
