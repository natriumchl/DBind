package com.dbind.dbindmod;

public class DetectorOS {
    private static final String OS = System.getProperty("os.name").toLowerCase();

    public static final boolean IS_WINDOWS = OS.contains("win");
    public static final boolean IS_MAC     = OS.contains("mac");
    public static final boolean IS_LINUX   = OS.contains("linux");
    public static final boolean IS_BSD     = OS.contains("bsd");
}