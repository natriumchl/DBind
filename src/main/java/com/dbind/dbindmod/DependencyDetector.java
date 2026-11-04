package com.dbind.dbindmod;

import java.io.IOException;


public class DependencyDetector {
    public static boolean dubAvailable() {
        try {
            Process process = new ProcessBuilder("dub", "--version").start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }
}