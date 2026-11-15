package com.dbind.dbindmod;

import java.nio.file.Path;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;


public class FileUnzipper {
    private static final Logger logs = LogUtils.getLogger();
    
    public static boolean unzip(Path sourceDest, Path endDest) {
        try (ZipFile zipFile = new ZipFile(sourceDest.toFile())) {
            zipFile.extractAll(endDest.toString());
            logs.info("Extracted {} to {}", sourceDest, endDest);
            return true;
        } catch (ZipException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}