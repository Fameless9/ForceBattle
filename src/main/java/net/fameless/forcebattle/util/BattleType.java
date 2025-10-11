package net.fameless.forcebattle.util;

import lombok.Getter;
import net.fameless.forcebattle.caption.Caption;
import net.fameless.forcebattle.caption.Language;
import net.fameless.forcebattle.configuration.SettingsManager;

@Getter
public enum BattleType {

    FORCE_ITEM("Item", "Item", SettingsManager.Setting.FORCE_ITEM),
    FORCE_MOB("Mob", "Mob", SettingsManager.Setting.FORCE_MOB),
    FORCE_BIOME("Biome", "Biom", SettingsManager.Setting.FORCE_BIOME),
    FORCE_ADVANCEMENT("Advancement", "Advancement", SettingsManager.Setting.FORCE_ADVANCEMENT),
    FORCE_HEIGHT("Height", "Höhe", SettingsManager.Setting.FORCE_HEIGHT),
    FORCE_COORDS("Coords", "Koordinaten", SettingsManager.Setting.FORCE_COORDS),
    FORCE_STRUCTURE("Structure", "Struktur", SettingsManager.Setting.FORCE_STRUCTURE);

    private final String englishPrefix;
    private final String germanPrefix;
    private final SettingsManager.Setting linkedSetting;

    BattleType(String englishPrefix, String germanPrefix, SettingsManager.Setting linkedSetting) {
        this.englishPrefix = englishPrefix;
        this.germanPrefix = germanPrefix;
        this.linkedSetting = linkedSetting;
    }

    public String getPrefix() {
        return Caption.getCurrentLanguage() == Language.ENGLISH ? englishPrefix : germanPrefix;
    }

    public SettingsManager.SettingState getSettingState() {
        return SettingsManager.getState(linkedSetting);
    }
}
