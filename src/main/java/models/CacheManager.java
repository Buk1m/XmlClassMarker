package models;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class CacheManager {
    private String cacheFilePath;
    private Map<String, String> classLabelsByWords;

    public CacheManager(String cacheFilePath) {
        this.cacheFilePath = cacheFilePath;
        classLabelsByWords = new HashMap<>();

        loadCache();
    }

    private void loadCache() {
        try (FileReader fileIn = new FileReader(cacheFilePath, StandardCharsets.UTF_8)) {
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, String>>(){}.getType();
            classLabelsByWords = gson.fromJson(fileIn, type);
        } catch (IOException e) {
            log.error("Loading cache failed!", e);
        }
    }

    public void addEntry(String classLabel, String markedText) {
        classLabelsByWords.put(markedText, classLabel);
    }

    public void saveCache() {
        try (FileWriter fileOut = new FileWriter(cacheFilePath, StandardCharsets.UTF_8)) {
            Gson gson = new GsonBuilder().setPrettyPrinting()
                                         .create();
            gson.toJson(classLabelsByWords, fileOut);
        } catch (IOException e) {
            log.error("Saving cache failed!", e);
        }
    }
}
