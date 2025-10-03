package net.fameless.forcebattle.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fameless.forcebattle.caption.Caption;
import net.fameless.forcebattle.caption.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SettingsFile {

    private static final Logger logger = LoggerFactory.getLogger("ForceBattle/" + SettingsFile.class.getSimpleName());
    private static final Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();

    public static void loadSettingsFromFile(File jsonFile) throws IOException {
        try (FileReader reader = new FileReader(jsonFile)) {
            JsonObject settingsObject = JsonParser.parseReader(reader).getAsJsonObject();

            // Load and enable settings
            for (SettingsManager.Setting setting : SettingsManager.Setting.values()) {
                if (settingsObject.has(setting.name())) {
                    boolean isEnabled = settingsObject.get(setting.name()).getAsBoolean();
                    SettingsManager.setEnabled(setting, isEnabled);
                }
            }
            SettingsManager.applyMinimum();

            if (settingsObject.has("language")) {
                try {
                    Language language = Language.valueOf(settingsObject.get("language").getAsString());
                    Caption.setCurrentLanguage(language);
                } catch (IllegalArgumentException e) {
                    logger.warn("Failed to load language from Settings file. No such language.");
                }
            }
        }
    }

    public static void saveToFile(File jsonFile) throws IOException {
        JsonObject settingsObject = new JsonObject();

        for (SettingsManager.Setting setting : SettingsManager.Setting.values()) {
            settingsObject.addProperty(setting.name(), SettingsManager.isEnabled(setting));
        }

        if (Caption.getCurrentLanguage() != null) {
            settingsObject.addProperty("language", Caption.getCurrentLanguage().name());
        }

        try (FileWriter writer = new FileWriter(jsonFile)) {
            GSON.toJson(settingsObject, writer);
        }
    }

}
