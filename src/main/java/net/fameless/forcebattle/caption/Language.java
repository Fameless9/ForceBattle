package net.fameless.forcebattle.caption;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

@Getter
public enum Language {
    ENGLISH("en", "<prefix><!bold><green>Language has been updated to english."),
    CHINESE_SIMPLIFIED("zh_cn", "<prefix><!bold><green>语言已更新为简体中文."),
    CHINESE_TRADITIONAL("zh_tw", "<prefix><!bold><green>語言已更新爲緐體中文."),
    GERMAN("de", "<prefix><!bold><green>Die Sprache wurde auf deutsch gesetzt.");

    private final String identifier;
    private final String updateMessage;

    Language(String identifier, String updateMessage) {
        this.identifier = identifier;
        this.updateMessage = updateMessage;
    }

    public static @Nullable Language ofIdentifier(String identifier) {
        for (Language language : Language.values()) {
            if ((language.identifier.equals(identifier))) {
                return language;
            }
        }
        return null;
    }

}
