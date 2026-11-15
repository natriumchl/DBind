package com.dbind.dbindmod;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.StandardCopyOption;

import net.neoforged.fml.ModLoadingIssue;
import net.neoforged.fml.ModLoadingException;

public class CompilationProcess {
    private static final Path modFolder = ModFileScanner.dbindFolder;
    private static final Path winBinary = ModFileScanner.binWinDir;
    private static final Path linBinary = ModFileScanner.binLinDir;
    private static final Path macBinary = ModFileScanner.binMacDir;
    private static final Path bsdBinary = ModFileScanner.binBsdDir;
    
    public static boolean compile(Path pathToCompile) {
        try {
            Process compile = new ProcessBuilder("dub", "build").directory(pathToCompile.toFile()).start();
            int exitCode = compile.waitFor();
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }
    
    public static void move(Path pathToMove) {
        String fileExtension;
        Path targetFolder;
        if(DetectorOS.IS_WINDOWS) {
            fileExtension = ".dll"; 
            targetFolder = winBinary;
        } else if(DetectorOS.IS_LINUX) {
            fileExtension = ".so";
            targetFolder = linBinary;
        } else if(DetectorOS.IS_BSD) {
            fileExtension = ".so";
            targetFolder = bsdBinary;
        } else if(DetectorOS.IS_MAC) {
            fileExtension = ".dylib";
            targetFolder = macBinary;
        } else {
            throw new ModLoadingException(
                ModLoadingIssue.error("Your operating system is not supported.")
            );
        }
        
        Path sourcePath = pathToMove.resolve(JSONProperty.get(pathToMove.resolve("dbind.json"), "name") + fileExtension);
        try {
            Files.move(sourcePath, targetFolder, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static boolean compileAndMove(Path inputPath) {
        boolean IsCompileOK = compile(inputPath);
        if(IsCompileOK) {move(inputPath);} else {
            return false;
        }
        return true;
    }
}