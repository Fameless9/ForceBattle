package net.fameless.core.util;

import net.fameless.core.ForceBattle;
import net.fameless.core.caption.Caption;
import net.fameless.core.caption.Language;
import net.fameless.core.config.PluginConfig;
import net.fameless.core.player.BattlePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.stream.Stream;

public enum BattleType {

    FORCE_ITEM("Item", "Item", "modes.force-item", true),
    FORCE_MOB("Mob", "Mob", "modes.force-mob", false),
    FORCE_BIOME("Biome", "Biom", "modes.force-biome", false),
    FORCE_ADVANCEMENT("Advancement", "Advancement", "modes.force-advancement", false),
    FORCE_HEIGHT("Height", "Höhe", "modes.force-height", false),
    FORCE_COORDS("Coords", "Koordinaten", "modes.force-coords", false),
    FORCE_STRUCTURE("Structure", "Struktur", "modes.force-structure", false);

    private final String englishPrefix;
    private final String germanPrefix;
    private final String configPath;
    private final boolean defaultEnabled;

    BattleType(String englishPrefix, String germanPrefix, final String configPath, final boolean enabled) {
        this.englishPrefix = englishPrefix;
        this.germanPrefix = germanPrefix;
        this.configPath = configPath;
        defaultEnabled = enabled;
    }

    public String getPrefix() {
        return Caption.getCurrentLanguage() == Language.ENGLISH ? englishPrefix : germanPrefix;
    }

    public String getConfigPath() {
        return configPath;
    }

    public boolean isEnabled() {
        return PluginConfig.get().getBoolean(configPath + ".enabled", defaultEnabled);
    }

    public void setEnabled(boolean enabled) {
        boolean needsRefresh = !isAnyEnabled() && enabled;
        PluginConfig.get().set(configPath + ".enabled", enabled);
        if (needsRefresh) {
            BattlePlayer.getOnlinePlayers().forEach(battlePlayer -> battlePlayer.setCurrentObjective(
                    ForceBattle.getObjectiveManager().getNewObjective(battlePlayer),
                    false
            ));
        }
    }

    public static boolean isAnyEnabled() {
        return FORCE_ITEM.isEnabled() || FORCE_MOB.isEnabled() || FORCE_BIOME.isEnabled() ||
                FORCE_ADVANCEMENT.isEnabled() || FORCE_HEIGHT.isEnabled() || FORCE_STRUCTURE.isEnabled() || FORCE_COORDS.isEnabled();
    }

    public static @NotNull @Unmodifiable List<BattleType> getEnabledBattleTypes() {
        return Stream.of(FORCE_ITEM, FORCE_MOB, FORCE_BIOME, FORCE_ADVANCEMENT, FORCE_HEIGHT, FORCE_COORDS, FORCE_STRUCTURE)
                .filter(BattleType::isEnabled)
                .toList();
    }
}
