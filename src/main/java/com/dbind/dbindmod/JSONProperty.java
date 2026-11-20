package com.dbind.dbindmod;

import java.io.IOException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import java.io.FileReader;

import java.nio.file.Path;


public class JSONProperty {
    private static final Logger logs = LogUtils.getLogger();

    public static String get(Path filePath, String getString) {
        try (FileReader reader = new FileReader(filePath.toFile())) {

            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            
            if (jsonObject.has(getString)) {
                return jsonObject.get(getString).getAsString();
            }
        } catch (IOException | RuntimeException e) {
            logs.error("dbind: failed to read property {} from {}", getString, filePath, e);
        }
        return "Property Not Found";
    }
}