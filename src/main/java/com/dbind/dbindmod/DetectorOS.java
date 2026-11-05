package com.dbind.dbindmod;

public class DetectorOS {
    private static final String OS = System.getProperty("os.name").toLowerCase();
    private static final String ARCH = System.getProperty("os.arch").toLowerCase();

    public static final boolean IS_WINDOWS = OS.contains("win");
    public static final boolean IS_MAC     = OS.contains("mac");
    public static final boolean IS_LINUX   = OS.contains("linux") || OS.contains("nux");
    public static final boolean IS_BSD     = OS.contains("bsd") || OS.contains("freebsd");
    
    public static final boolean IS_X64     = ARCH.contains("amd64") || ARCH.contains("x86_64");
    public static final boolean IS_ARM64   = ARCH.contains("aarch64") || ARCH.contains("arm64");
    
    public static final boolean IS_X86     = (ARCH.contains("x86") && !ARCH.contains("x86_64")) || ARCH.contains("i386") || ARCH.contains("i686");
    public static final boolean IS_ARM32   = ARCH.contains("arm") && !IS_ARM64;
}
