package com.dbind.dbindmod;


import net.neoforged.fml.loading.FMLPaths;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;


import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;
import java.util.Vector;


public class ModFileScanner {
    
    private static final Logger logs = LogUtils.getLogger();
    
    private static final Path modsDir = FMLPaths.MODSDIR.get(); 
    
    private static final Path minecraftRoot = FMLPaths.GAMEDIR.get();
    private static final Path dbindFolder = minecraftRoot.resolve("folder");
    
    public static void runFolderProcess() {
        try {
            if (Files.notExists(dbindFolder)) {
                Files.createDirectories(dbindFolder);
            }
        } catch (IOException e) {
            logs.warn("dbind: dbind folder exception; caught");
        }
    }
    
    private static Vector<String> shallowModSearch() {
        Vector<String> result = new Vector<>();

        try (var stream = Files.list(modsDir)) {
            stream.forEach(path -> result.add(path.toString()));
        } catch (IOException e) {
            logs.warn("dbind: shallow search exception; caught");
        }

        return result;
    }
    
    private static Vector<String> returnFilteredModsFile(Vector<String> input) {
        Vector<String> result = new Vector<>(); 
        for(String element : input) {
            if(element.endsWith("dbind-based.jar") || element.endsWith("dbb.jar")) { //check
                result.add(element); 
            }
        }
        return result;
    }
    
    public static void mfsMain() {
        runFolderProcess();
        Vector<String> modsToCompileStr = returnFilteredModsFile(shallowModSearch());
        Vector<Path> modsToCompile = new Vector<>(modsToCompileStr.size());
        for (String str : modsToCompileStr) {
            modsToCompile.add(Path.of(str));
        }
    }
}