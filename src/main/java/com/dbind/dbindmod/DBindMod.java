package com.dbind.dbindmod;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.ModLoadingIssue;
import net.neoforged.fml.ModLoadingException;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

@Mod(value = DBindMod.MODID)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = DBindMod.MODID)


public class DBindMod {
    public static final String MODID = "dbindmod";
    private static final Logger logs = LogUtils.getLogger();
    public static IEventBus MOD_EVENT_BUS;
    
    public DBindMod(IEventBus modEventBus) {
        MOD_EVENT_BUS = modEventBus;
    }
    
    @SubscribeEvent
    static void onLoadComplete(FMLLoadCompleteEvent event) {
        
        if((!(DependencyDetector.dubAvailable()))) {
            throw new ModLoadingException(
                ModLoadingIssue.error(
                    "DUB (D package manager) is missing from your environment variables. " + 
                            "To verify if it installs, run \"dub --version\" in your terminal (without the quotes). " + 
                                    "\nThis is essential for the mod to function."
                    
                    )
            );
        }
        
        ModFileScanner.Main(MOD_EVENT_BUS);
    }
}
