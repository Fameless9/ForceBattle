package net.fameless.core.caption;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fameless.core.ForceBattle;
import net.fameless.core.config.PluginConfig;
import net.fameless.core.util.Format;
import net.fameless.core.util.PluginPaths;
import net.fameless.core.util.ResourceUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class Caption {

    private static final Logger logger = LoggerFactory.getLogger("ForceBattle/" + Caption.class.getSimpleName());
    private static final HashMap<Language, JsonObject> languageJsonObjectHashMap = new HashMap<>();
    private static Language currentLanguage = Language.ENGLISH;
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();

    private Caption() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }

    public static @NotNull Component of(String key, TagResolver... replacements) {
        String message = getString(key);
        if (message.isEmpty()) {
            message = "<prefix><red>No such key: " + key;
        }

        message = replaceDefaults(message);
        return MiniMessage.miniMessage().deserialize(message, replacements);
    }

    public static @NotNull String getAsLegacy(String key, TagResolver... replacements) {
        Component component = of(key, replacements);
        return LegacyComponentSerializer.legacySection().serialize(component);
    }

    private static @NotNull String replaceDefaults(String input) {
        input = input.replace("<prefix>", getString("prefix"))
                .replace("<time>", Format.formatTime(ForceBattle.getTimer().getTime()))
                .replace("<force-item-active>", getString(PluginConfig.get().getBoolean("modes.force-item.enabled", true) ? "active.true" : "active.false"))
                .replace("<force-mob-active>", getString(PluginConfig.get().getBoolean("modes.force-mob.enabled", false) ? "active.true" : "active.false"))
                .replace("<force-biome-active>", getString(PluginConfig.get().getBoolean("modes.force-biome.enabled", false) ? "active.true" : "active.false"))
                .replace("<force-advancement-active>", getString(PluginConfig.get().getBoolean("modes.force-biome.enabled", false) ? "active.true" : "active.false"))
                .replace("<force-height-active>", getString(PluginConfig.get().getBoolean("modes.force-height.enabled", false) ? "active.true" : "active.false"))
                .replace("<chain-mode-active>", getString(PluginConfig.get().getBoolean("settings.enable-chain-mode", false) ? "active.true" : "active.false"))
                .replace("<backpacks-active>", getString(PluginConfig.get().getBoolean("settings.enable-backpacks", false) ? "active.true" : "active.false"))
                .replace("<hide-points-active>", getString(PluginConfig.get().getBoolean("settings.hide-points", false) ? "active.true" : "active.false"))
                .replace("<hide-objectives-active>", getString(PluginConfig.get().getBoolean("settings.hide-objectives", false) ? "active.true" : "active.false"))
        ;
        return input;
    }

    public static void loadLanguage(Language language, JsonObject jsonObject) {
        languageJsonObjectHashMap.put(language, jsonObject);
    }

    public static void loadDefaultLanguages() {
        logger.info("Loading default languages...");
        for (Language language : Language.values()) {
            File langFile = PluginPaths.getLangFile(language);

            if (!langFile.exists()) {
                ResourceUtil.extractResourceIfMissing(language.getFilePath(), langFile);
            }

            JsonObject jsonObject;
            try (FileReader reader = new FileReader(langFile)) {
                jsonObject = GSON.fromJson(reader, JsonObject.class);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load language file: " + langFile.getPath(), e);
            }

            JsonObject defaultJsonObject = ResourceUtil.readJsonResource(language.getFilePath());
            for (Map.Entry<String, JsonElement> entry : defaultJsonObject.entrySet()) {
                String key = entry.getKey();
                if (!jsonObject.has(key)) {
                    jsonObject.add(key, entry.getValue());
                    logger.warn("Missing key '{}' in language '{}', adding default value.", key, language.getIdentifier());
                }
            }

            loadLanguage(language, jsonObject);
        }
    }

    private static String getString(String key) {
        JsonObject languageObject = languageJsonObjectHashMap.get(currentLanguage);
        if (!languageObject.has(key)) {
            return "";
        }
        return languageObject.get(key).getAsString();
    }

    public static Language getCurrentLanguage() {
        return currentLanguage;
    }

    public static void setCurrentLanguage(Language newLanguage) {
        if (newLanguage != getCurrentLanguage()) {
            Caption.currentLanguage = newLanguage;
        }
    }

    @Contract(" -> new")
    public static @NotNull TagResolver prefixTagResolver() {
        return TagResolver.resolver("prefix", Tag.inserting(Caption.of("prefix")));
    }

}
