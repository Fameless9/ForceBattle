package net.fameless.core.caption;

import com.google.gson.JsonObject;
import net.fameless.core.ForceBattle;
import net.fameless.core.configuration.SettingsManager;
import net.fameless.core.event.EventDispatcher;
import net.fameless.core.event.LanguageChangeEvent;
import net.fameless.core.util.Format;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public final class Caption {

    private static final Logger logger = LoggerFactory.getLogger("ForceBattle/" + Caption.class.getSimpleName());
    private static final HashMap<Language, JsonObject> languageJsonObjectHashMap = new HashMap<>();
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
                .replace("<force-item-active>", getString(SettingsManager.isEnabled(SettingsManager.Setting.FORCE_ITEM) ? "active.true" : "active.false"))
                .replace("<force-mob-active>", getString(SettingsManager.isEnabled(SettingsManager.Setting.FORCE_MOB) ? "active.true" : "active.false"))
                .replace("<force-biome-active>", getString(SettingsManager.isEnabled(SettingsManager.Setting.FORCE_BIOME) ? "active.true" : "active.false"))
                .replace("<force-advancement-active>", getString(SettingsManager.isEnabled(SettingsManager.Setting.FORCE_ADVANCEMENT) ? "active.true" : "active.false"))
                .replace("<force-height-active>", getString(SettingsManager.isEnabled(SettingsManager.Setting.FORCE_HEIGHT) ? "active.true" : "active.false"))
                .replace("<chain-mode-active>", getString(SettingsManager.isEnabled(SettingsManager.Setting.CHAIN_MODE) ? "active.true" : "active.false"))
                .replace("<backpacks-active>", getString(SettingsManager.isEnabled(SettingsManager.Setting.BACKPACK) ? "active.true" : "active.false"))
                .replace("<hide-points-active>", getString(SettingsManager.isEnabled(SettingsManager.Setting.HIDE_POINTS) ? "active.true" : "active.false"))
                .replace("<hide-objectives-active>", getString(SettingsManager.isEnabled(SettingsManager.Setting.HIDE_OBJECTIVES) ? "active.true" : "active.false"))
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

    public static Language getCurrentLanguage() {
        return currentLanguage;
    }

    public static void setCurrentLanguage(Language newLanguage) {
        if (newLanguage != getCurrentLanguage()) {
            LanguageChangeEvent languageChangeEvent = new LanguageChangeEvent(newLanguage);
            EventDispatcher.post(languageChangeEvent);
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
