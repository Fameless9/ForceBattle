package net.fameless.core.util;

import net.fameless.core.caption.Caption;
import net.fameless.core.caption.Language;

public enum BattleType {

    FORCE_ITEM("Item", "Item"),
    FORCE_MOB("Mob", "Mob"),
    FORCE_BIOME("Biome", "Biom"),
    FORCE_ADVANCEMENT("Advancement", "Advancement"),
    FORCE_HEIGHT("Height", "Höhe"),
    FORCE_COORDS("Coords", "Koordinaten"),
    FORCE_STRUCTURE("Structure", "Struktur");

    private final String englishPrefix;
    private final String germanPrefix;

    BattleType(String englishPrefix, String germanPrefix) {
        this.englishPrefix = englishPrefix;
        this.germanPrefix = germanPrefix;
    }

    public String getPrefix() {
        return Caption.getCurrentLanguage() == Language.ENGLISH ? englishPrefix : germanPrefix;
    }

}
