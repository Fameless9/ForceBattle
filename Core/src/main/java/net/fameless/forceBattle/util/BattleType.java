package net.fameless.forceBattle.util;

import net.fameless.forceBattle.caption.Caption;
import net.fameless.forceBattle.caption.Language;

public enum BattleType {

    FORCE_ITEM("Item", "Item"),
    FORCE_MOB("Mob", "Mob"),
    FORCE_BIOME("Biome", "Biom"),
    FORCE_ADVANCEMENT("Advancement", "Advancement"),
    FORCE_HEIGHT("Height", "Höhe");

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
