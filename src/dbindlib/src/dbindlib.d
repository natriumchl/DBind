// SPDX-License-Identifier: Zlib
/*
 * Copyright (c) 2026 natriumchl
 *
 * This software is provided 'as-is', without any express or implied
 * warranty. In no event will the authors be held liable for any damages
 * arising from the use of this software.
 *
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 *
 * 1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software
 *    in a product, an acknowledgment in the product documentation would be
 *    appreciated but is not required.
 * 2. Altered source versions must be plainly marked as such, and must not be
 *    misrepresented as being the original software.
 * 3. This notice may not be removed or altered from any source distribution.
 */

//dependency: arsd-official:jni
//run `dub add arsd-official:jni` in yuor dub project

module dbindlib.src.dbindlib;
import arsd.jni;

extern(System):

private class JUtil {
    class Convert {
        class String {
            static void d_to_j() {
                //TODO
            }
            static void j_to_d() {
                //TODO
            }
        }
    }
    class ClassImports {
        final class NativeLinker : JavaClass!("com.dbind.dbindmod", Logs) {
            @Import
            static void dLog(wstring message);
            @Import
            static void dSubscribe(
                IEventBus bus,
                String eventClassName,
            );
            @Import
            static void dSubscribe(
                IEventBus bus,
                String eventClassName,
                EventPriority priority,
                bool receiveCanceled
            );
        }

        //neoforged classes
        final class Event : JavaClass!(
            "net.neoforged.bus.api",
            Event
        ) {
        }
        final class IEventBus : JavaClass!(
            "net.neoforged.bus.api",
            IEventBus
        ) {
        }
    }
}


public
{
    enum Dist {
        CLIENT,
        DEDICATED_SERVER,
    }
    // allow for @Mod("modid")
    struct Mod {
        string value;
        Dist[] dist = [Dist.CLIENT, Dist.DEDICATED_SERVER];
    }
    // allow for @EventBusSubscriber
    struct EventBusSubscriber {
        Dist[] dist = [Dist.CLIENT, Dist.DEDICATED_SERVER];
        string modid = "";
    }
}

class DBind {
public:
    static void log(string message) {
        JUtil.ClassImports.NativeLinker.dLog(message);
    }
}

void main(string[] args) {

}