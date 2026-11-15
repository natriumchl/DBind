package com.dbind.dbindmod;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.ModLoadingIssue;
import net.neoforged.fml.ModLoadingException;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = DBindMod.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = DBindMod.MODID, value = Dist.CLIENT)


public class DBindMod {
    public static final String MODID = "dbindmod";
    private static final Logger logs = LogUtils.getLogger();
    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        
        if((!(DependencyDetector.dubAvailable()))) {
            // TODO
            logs.warn("[DBIND]\nDUB (D package manager) is missing from your environment variables. " + 
                    "To verify if it installs, run \"dub --version\" in your terminal (without the quotes). " + 
                            "\nThis is essential for the mod to function."
                    );
            throw new ModLoadingException(
                ModLoadingIssue.error(
                    "DUB (D package manager) is missing from your environment variables. " + 
                            "To verify if it installs, run \"dub --version\" in your terminal (without the quotes). " + 
                                    "\nThis is essential for the mod to function."
                    
                    )
            );
        }
        
        ModFileScanner.Main();
    }
}
