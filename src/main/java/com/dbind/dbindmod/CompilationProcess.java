package com.dbind.dbindmod;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.StandardCopyOption;

import net.neoforged.fml.ModLoadingIssue;
import net.neoforged.fml.ModLoadingException;

import net.neoforged.bus.api.IEventBus;

public class CompilationProcess {
    private static final Path modFolder = ModFileScanner.dbindFolder;
    private static final Path winBinary = ModFileScanner.binWinDir;
    private static final Path linBinary = ModFileScanner.binLinDir;
    private static final Path macBinary = ModFileScanner.binMacDir;
    private static final Path bsdBinary = ModFileScanner.binBsdDir;
    
    public static boolean compile(Path pathToCompile) {
        try {
            Process compile = new ProcessBuilder("dub", "build").directory(pathToCompile.toFile()).inheritIO().start();
            int exitCode = compile.waitFor();
            return exitCode == 0;
        } catch (IOException e) {
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    public record MoveReturnType(Path path, String modName) {
        public MoveReturnType(Path path) {
            this(path, "DefaultMod");
        }
    }

    
    public static MoveReturnType move(Path pathToMove, String modName) {
        String fileExtension;
        Path targetFolder;
        if(DetectorOS.IS_WINDOWS) {
            modName = modName.replaceFirst("^lib", "");
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
        
        Path targetFile = targetFolder.resolve(modName + fileExtension);
        Path sourcePath = pathToMove.resolve(modName + fileExtension);
        try {
            Files.move(sourcePath, targetFile, StandardCopyOption.REPLACE_EXISTING);
            return new MoveReturnType(targetFile, modName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static boolean compileMoveAndLink(Path inputPath, IEventBus modEventBus, String modName) {
        boolean isCompileOK = compile(inputPath);
        if (!isCompileOK) {
            return false;
        }
        MoveReturnType moveResult = move(inputPath, modName);
        NativeLinker.link(moveResult.path(), modEventBus, moveResult.modName());
        return true;
    }
}