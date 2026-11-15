package com.dbind.dbindmod;

import java.io.IOException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.FileReader;

import java.nio.file.Path;


public class JSONProperty {
    public static String get(Path filePath, String getString) {
        try (FileReader reader = new FileReader(filePath.toFile())) {

            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            
            if (jsonObject.has(getString)) {
                return jsonObject.get(getString).getAsString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Property Not Found";
    }
}