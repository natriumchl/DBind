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
//run `dub add arsd-official:jni` in your dub project


//jni is hard

//dbindlib 0.1.0 for dbind pre1

module dbindlib;
import arsd.jni;
import std.conv : to;
import std.traits;
import std.format;

enum Priority { highest, high, normal, low, lowest }

package class JUtils {
    package static class ClassImports {
        package static class NeoForge {
            //NeoForge imports
            static final class Event : JavaClass!("net.neoforged.bus.api", Event) {}
            static final class EventPriority : JavaClass!("net.neoforged.bus.api", EventPriority) {
                @Import static @property EventPriority HIGHEST();
                @Import static @property EventPriority HIGH();
                @Import static @property EventPriority NORMAL();
                @Import static @property EventPriority LOW();
                @Import static @property EventPriority LOWEST();
            }
            static EventPriority javaPriorityTranslator(Priority priority) {
                final switch (priority) {
                    case Priority.highest:
                        return EventPriority.HIGHEST;

                    case Priority.high:
                        return EventPriority.HIGH;

                    case Priority.normal:
                        return EventPriority.NORMAL;

                    case Priority.low:
                        return EventPriority.LOW;

                    case Priority.lowest:
                        return EventPriority.LOWEST;
                }
            }
            static final class IEventBus : JavaClass!("net.neoforged.bus.api", IEventBus) {
                @Import bool post(Event event);
            }
        }

        private alias IEventBus     = NeoForge.IEventBus;
        private alias EventPriority = NeoForge.EventPriority;
        static final class NativeLinker : JavaClass!("com.dbind.dbindmod", NativeLinker) {
            @Import
            static void dLog(wstring message);
            
            @Import
            static void dLog(wstring message, int severity);

            @Import
            static IEventBus dEventBus();
            
            @Import
            static void dSubscribe(IEventBus bus, wstring eventClassName); //and its overload
            @Import
            static void dSubscribe(IEventBus bus, wstring eventClassName, EventPriority priority, bool receiveCanceled);

            @Export static void onModInit(IEventBus modEventBus, IEventBus gameEventBus) {
                .onModInit(modEventBus, gameEventBus);
            }
            @Export static void onAnyEvent(Event event) {
                .onAnyEvent(event);
            }
        }
    }
}

public alias IEventBus = JUtils.ClassImports.NeoForge.IEventBus;
public alias Event = JUtils.ClassImports.NeoForge.Event;

static wstring utf16(string input) {
    return input.to!wstring;
}
static string utf8(wstring input) {
    return input.to!string;
}

struct SubscribeEvent {
    Priority priority = Priority.normal;
    bool receiveCanceled = false;
}

enum LogSeverity : int {
    ERROR = 1,
    WARN  = 2,
    INFO  = 3,
    DEBUG = 4,
    TRACE = 5
}

alias Error = LogSeverity.ERROR;
alias Info = LogSeverity.INFO;
alias Warn = LogSeverity.WARN;
alias Debug = LogSeverity.DEBUG;
alias Trace = LogSeverity.TRACE;

class DBind {
public:

    //log("Hello World");
    static void log(string input) {
        JUtils.ClassImports.NativeLinker.dLog(input.utf16());
    }

    //log("Hello World", Info);
    static void log(string input, LogSeverity severity) {
        JUtils.ClassImports.NativeLinker.dLog(input.utf16(), cast(int) severity);
    }

    //log!Info("Hello World");
    static void log(LogSeverity severity)(string input) {
        log(input, severity);
    }

    static void subscribe(string eventClassName) {
        JUtils.ClassImports.NativeLinker.dSubscribe(
            JUtils.ClassImports.NativeLinker.dEventBus(), eventClassName.utf16()
        );
    }

    static void subscribe(string eventClassName, Priority p, bool receiveCanceled) {
        JUtils.ClassImports.NativeLinker.dSubscribe(
            JUtils.ClassImports.NativeLinker.dEventBus(), eventClassName.utf16(),
            JUtils.ClassImports.NeoForge.javaPriorityTranslator(p), receiveCanceled
        );
    }

}

extern(C) void onModInit(IEventBus modEventBus, IEventBus gameEventBus);
extern(C) void onAnyEvent(Event event);


/*
implementation:
planned V1
```
import dbindlib;
import std.conv : to;

extern(C) void onModInit(IEventBus modEventBus, IEventBus gameEventBus) {
    DBind.subscribe("net.neoforged.neoforge.event.tick.ServerTickEvent$Post");
}

extern(C) void onAnyEvent(Event event) {
    string eventName = event.getClass().getName().to!string;

    if (eventName == "net.neoforged.neoforge.event.tick.ServerTickEvent$Post") {
        //implementation
    }
}

```

planned in the future (won't implement until release)
```
import dbindlib;

//required boilerplate code
extern(C) void onModInit(IEventBus modEventBus, IEventBus gameEventBus) {
    //leave empty or not
}
extern(C) void onAnyEvent(Event event) {
    //leave empty or not (can be substituted)
}

@SubscribeEvent
void onLivingJump(
    LivingEvent.LivingJumpEvent event, 
    Priority priority, 
    bool receiveCanceled) 
{
    //maybe
}
```
or something like that

*/
