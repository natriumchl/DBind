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

module dbindlib;
import arsd.jni;
import std.conv : to;

enum Priority { highest, high, normal, low, lowest }

private class JUtils {
    static class ClassImports {
        static class NeoForge {
            //NeoForge imports
            static final class Event : JavaClass!("net.neoforged.bus.api", Event) {}
            static final class EventPriority : JavaClass!("net.neoforged.bus.api", EventPriority) {
                @Import static EventPriority HIGHEST;
                @Import static EventPriority HIGH;
                @Import static EventPriority NORMAL;
                @Import static EventPriority LOW;
                @Import static EventPriority LOWEST;
            }
            static EventPriority javaPriorityTranslator(Priority priority) {
                final switch (priority) {
                    case Priority.highest:
                        return JUtils.ClassImports.EventPriority.HIGHEST;

                    case Priority.high:
                        return JUtils.ClassImports.EventPriority.HIGH;

                    case Priority.normal:
                        return JUtils.ClassImports.EventPriority.NORMAL;

                    case Priority.low:
                        return JUtils.ClassImports.EventPriority.LOW;

                    case Priority.lowest:
                        return JUtils.ClassImports.EventPriority.LOWEST;
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
            static IEventBus dEventBus();
            
            @Import
            static void dSubscribe(IEventBus bus, wstring eventClassName); //and its overload
            @Import
            static void dSubscribe(IEventBus bus, wstring eventClassName, EventPriority priority, bool receiveCanceled);
        }
    }
}

static wstring utf16(string input) {
    return input.to!wstring;
}
static string utf8(wstring input) {
    return input.to!string;
}

class DBind {
    static void log(string input) {
        JUtils.ClassImports.NativeLinker.dLog(input.utf16());
    }

    static void subscribe(string eventClassName) {
        JUtils.ClassImports.NativeLinker.dSubscribe(
            JUtils.ClassImports.NativeLinker.dEventBus(), eventClassName.utf16());
    }

    static void subscribe(string eventClassName, Priority p, bool receiveCanceled) {
        JUtils.ClassImports.NativeLinker.dSubscribe(
            JUtils.ClassImports.NativeLinker.dEventBus(), eventClassName.utf16(),
            JUtils.ClassImports.NeoForge.javaPriorityTranslator(p), receiveCanceled);
    }
}
int main(string[] args) {

}