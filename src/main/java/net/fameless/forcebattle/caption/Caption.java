package net.fameless.forcebattle.caption;

import com.google.gson.JsonObject;
import lombok.Getter;
import net.fameless.forcebattle.ForceBattle;
import net.fameless.forcebattle.event.LanguageChangeEvent;
import net.fameless.forcebattle.util.Format;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public final class Caption {

    private static final Logger logger = LoggerFactory.getLogger("ForceBattle/" + Caption.class.getSimpleName());
    private static final HashMap<Language, JsonObject> languageJsonObjectHashMap = new HashMap<>();
    @Getter
    private static Language currentLanguage = Language.ENGLISH;

    private Caption() {
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
        ;
        return input;
    }

    public static void loadLanguage(Language language, JsonObject jsonObject) {
        languageJsonObjectHashMap.put(language, jsonObject);
    }

    private static String getString(String key) {
        JsonObject languageObject = languageJsonObjectHashMap.get(currentLanguage);
        if (!languageObject.has(key)) {
            return "";
        }
        return languageObject.get(key).getAsString();
    }

    public static void setCurrentLanguage(Language newLanguage) {
        if (newLanguage != getCurrentLanguage()) {
            LanguageChangeEvent languageChangeEvent = new LanguageChangeEvent(newLanguage);
            Bukkit.getPluginManager().callEvent(languageChangeEvent);
            if (languageChangeEvent.isCancelled()) {
                logger.info("LanguageChangeEvent has been denied by an external plugin.");
                return;
            }
            Caption.currentLanguage = newLanguage;
        }
    }

    @Contract(" -> new")
    public static @NotNull TagResolver prefixTagResolver() {
        return TagResolver.resolver("prefix", Tag.inserting(Caption.of("prefix")));
    }

}
