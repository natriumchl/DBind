package com.dbind.dbindmod;


import net.neoforged.fml.loading.FMLPaths;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;


import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;
import java.util.Vector;

import net.neoforged.bus.api.IEventBus;


public class ModFileScanner {
    
    private static final Logger logs = LogUtils.getLogger();
    
    public static final Path modsDir = FMLPaths.MODSDIR.get(); 
    
    public static final Path minecraftRoot = FMLPaths.GAMEDIR.get();
    public static final Path dbindFolder = minecraftRoot.resolve("dbind");
    public static final Path binFolder = dbindFolder.resolve("bin");
    public static final Path srcFolder = dbindFolder.resolve("src");
    public static final Path binWinDir = binFolder.resolve("win");
    public static final Path binLinDir = binFolder.resolve("linux");
    public static final Path binMacDir = binFolder.resolve("mac");
    public static final Path binBsdDir = binFolder.resolve("bsd");
    
    public static void runFolderProcess() {
        try {
            if (Files.notExists(binWinDir)) {
                Files.createDirectories(binWinDir);
            }
            if (Files.notExists(binLinDir)) {
                Files.createDirectories(binLinDir);
            }
            if (Files.notExists(binMacDir)) {
                Files.createDirectories(binMacDir);
            }
            if (Files.notExists(binBsdDir)) {
                Files.createDirectories(binBsdDir);
            }
            if (Files.notExists(srcFolder)) {
                Files.createDirectories(srcFolder);
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
            if(element.endsWith(".d.jar") || element.endsWith(".dbind.jar")) { //check
                result.add(element); 
            }
        }
        return result;
    }
    
    public static void unzipile(Vector<Path> modsToUnzip, IEventBus modEventBus) {
        Vector<String> usedNames = new Vector<>();
        for (Path path : modsToUnzip) {
            String sourceFolderName = path.getFileName().toString().replace('.', '_');
            Path destPath = srcFolder.resolve(sourceFolderName);
            boolean suc = FileUnzipper.unzip(path, destPath);
            if(suc) {
                String modName = sanitizeName(JSONProperty.get(destPath.resolve("dbind.json"), "name"));
                if(modName == null || usedNames.contains(modName)) {
                    logs.warn("dbind: skipping {} (missing or duplicate mod name: {})", path, modName);
                    continue;
                }
                usedNames.add(modName);
                CompilationProcess.compileMoveAndLink(destPath, modEventBus, modName);
            }

        }
    }

    private static String sanitizeName(String name) {
        if(name == null || name.equals("Property Not Found") || name.isBlank()) {
            return null;
        }
        return name.replaceAll("[^a-zA-Z0-9_]", "_");
    }
    
    public static void Main(IEventBus modEventBus) {
        runFolderProcess();
        Vector<String> modsToCompileStr = returnFilteredModsFile(shallowModSearch());
        Vector<Path> modsToCompile = new Vector<>(modsToCompileStr.size());
        for (String str : modsToCompileStr) {
            modsToCompile.add(Path.of(str));
        }
        
        unzipile(modsToCompile, modEventBus);
    }
}