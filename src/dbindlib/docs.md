## src Implementation
for dbindlib v0.1.0, the basic implementation is this:
```d
import dbindlib;
import std.conv : to;

extern(C) void onModInit(IEventBus modEventBus, IEventBus gameEventBus) {
    //implementation
}

extern(C) void onAnyEvent(Event event) {
    //implementation
}
```
in the onAnyEvent function, you can check if the event matches your desired event (net.neoforged.neoforge.event.tick.ServerTickEvent$Post); "net.neoforged.neoforge.event.tick.ServerTickEvent.Post" with the inner class seperation (the last period) seperated by $ instead of .

```d
    if (event.name == "net.neoforged.neoforge.event.tick.ServerTickEvent$Post") {
        //implementation
    }
```

## dub.json
for the name section, name whatever you want with your mod;
```json
{
    "name": "dmod",
    "description": "mod, written in d",
    "version": "1.0.0",
    "targetType": "dynamicLibrary",
    "sourcePaths": ["source"],
    "dflags-dmd": ["-fPIC", "-defaultlib=libphobos2.a"],
    "dependencies": {
        "arsd-official:jni": "==12.1.3"
    }
}
```
-betterC is impossible

## dbind.json
your mod needs to have dbind.json for this to work;
```json
{
    "name": "libdmod"
}
```
it only requires the name field for now
### Name Field
the name field must be equal to dub.json's name field with "lib" prepended into it

## META-INF
since the mods are in mods/, you need this folder to not make NeoForge crash.
inside this folder, there should be `MANIFEST.MF` containing:
```
Manifest-Version: 1.0
FMLModType: GAMELIBRARY
Automatic-Module-Name: anything.you.want
```
## Putting it all together
run:
```
jar cMf yourMod.d.jar -C /path/to/your/mod/  .
```
and put your generated jar file inside the mods folder to verify if it works, or publish it somewhere
(DBind mods end with .d.jar)