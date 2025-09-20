package net.fameless.core.caption;

import org.jetbrains.annotations.Nullable;

public enum Language {
    ENGLISH("en_US.json", "en", "<prefix><!bold><green>Language has been updated to english."),
    GERMAN("de_DE.json", "de", "<prefix><!bold><green>Die Sprache wurde auf deutsch gesetzt.");

    private final String filePath;
    private final String identifier;
    private final String updateMessage;

    Language(final String filePath, String identifier, String updateMessage) {
        this.filePath = filePath;
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

    public String getFilePath() {
        return filePath;
    }

    public String getUpdateMessage() {
        return updateMessage;
    }

    public String getIdentifier() {
        return identifier;
    }
}
