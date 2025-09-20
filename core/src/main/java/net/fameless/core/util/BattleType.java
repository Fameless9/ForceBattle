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

    FORCE_ITEM("Item", "Item", "mode.force-item"),
    FORCE_MOB("Mob", "Mob", "mode.force-mob"),
    FORCE_BIOME("Biome", "Biom", "mode.force-biome"),
    FORCE_ADVANCEMENT("Advancement", "Advancement", "mode.force-advancement"),
    FORCE_HEIGHT("Height", "Höhe", "mode.force-height");

    private final String englishPrefix;
    private final String germanPrefix;
    private final String configPath;

    BattleType(String englishPrefix, String germanPrefix, final String configPath) {
        this.englishPrefix = englishPrefix;
        this.germanPrefix = germanPrefix;
        this.configPath = configPath;
    }

    public String getPrefix() {
        return Caption.getCurrentLanguage() == Language.ENGLISH ? englishPrefix : germanPrefix;
    }

    public String getConfigPath() {
        return configPath;
    }

    public boolean isEnabled() {
        return PluginConfig.get().getBoolean("modes." + name().toLowerCase().replace("_", "-") + ".enabled", true);
    }

    public void setEnabled(boolean enabled) {
        boolean needsRefresh = !isAnyEnabled() && enabled;
        PluginConfig.get().set("modes." + name().toLowerCase().replace("_", "-") + ".enabled", enabled);
        if (needsRefresh) {
            BattlePlayer.getOnlinePlayers().forEach(battlePlayer -> battlePlayer.setCurrentObjective(
                    ForceBattle.getObjectiveManager().getNewObjective(battlePlayer),
                    false
            ));
        }
    }

    public static boolean isAnyEnabled() {
        return FORCE_ITEM.isEnabled() || FORCE_MOB.isEnabled() || FORCE_BIOME.isEnabled() ||
                FORCE_ADVANCEMENT.isEnabled() || FORCE_HEIGHT.isEnabled();
    }

    public static @NotNull @Unmodifiable List<BattleType> getEnabledBattleTypes() {
        return Stream.of(FORCE_ITEM, FORCE_MOB, FORCE_BIOME, FORCE_ADVANCEMENT, FORCE_HEIGHT)
                .filter(BattleType::isEnabled)
                .toList();
    }
}
