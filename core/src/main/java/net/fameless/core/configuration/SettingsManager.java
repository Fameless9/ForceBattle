package net.fameless.core.configuration;

import net.fameless.core.ForceBattle;
import net.fameless.core.player.BattlePlayer;
import net.fameless.core.util.BattleType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public class SettingsManager {

    private static final EnumMap<Setting, Boolean> settingsMap = new EnumMap<>(Setting.class);

    private SettingsManager() {
    }

    public static @NotNull List<BattleType> getActiveChallenges() {
        List<BattleType> activeChallenges = new ArrayList<>();
        for (BattleType battleType : BattleType.values()) {
            Setting setting = Setting.valueOf(battleType.name());
            if (isEnabled(setting)) {
                activeChallenges.add(battleType);
            }
        }
        return activeChallenges;
    }

    public static boolean isEnabled(Setting setting) {
        return settingsMap.getOrDefault(setting, false);
    }

    public static void setEnabled(Setting setting, boolean enabled) {
        updateSetting(setting, enabled, false);
    }

    public static void applyMinimum() {
        if (getActiveChallenges().isEmpty()) {
            updateSetting(Setting.FORCE_ITEM, true, true);
        }
    }

    private static void updateSetting(Setting setting, boolean enabled, boolean skipObjectiveUpdate) {
        if (settingsMap.getOrDefault(setting, false) == enabled) {
            return;
        }
        settingsMap.put(setting, enabled);

        if (!skipObjectiveUpdate) {
            if (setting == Setting.CHAIN_MODE || setting.name().startsWith("FORCE")) {
                ForceBattle.getObjectiveManager().updateChainList();
            }
            BattlePlayer.BATTLE_PLAYERS.forEach(player -> player.updateObjective(false));
        }
    }

    // Challenge Settings
    public static void setForceItemEnabled(boolean enabled) {
        updateSetting(Setting.FORCE_ITEM, enabled, false);
    }

    public static void setForceMobEnabled(boolean enabled) {
        updateSetting(Setting.FORCE_MOB, enabled, false);
    }

    public static void setForceBiomeEnabled(boolean enabled) {
        updateSetting(Setting.FORCE_BIOME, enabled, false);
    }

    public static void setForceAdvancementEnabled(boolean enabled) {
        updateSetting(Setting.FORCE_ADVANCEMENT, enabled, false);
    }

    public static void setForceHeightEnabled(boolean enabled) {
        updateSetting(Setting.FORCE_HEIGHT, enabled, false);
    }

    // Miscellaneous Settings
    public static void setChainModeEnabled(boolean enabled) {
        updateSetting(Setting.CHAIN_MODE, enabled, false);
    }

    public static void setBackpackEnabled(boolean enabled) {
        updateSetting(Setting.BACKPACK, enabled, true);
    }

    public static void setHidePointsEnabled(boolean enabled) {
        updateSetting(Setting.HIDE_POINTS, enabled, true);
    }

    public static void setHideObjectivesEnabled(boolean enabled) {
        updateSetting(Setting.HIDE_OBJECTIVES, enabled, true);
    }

    public enum Setting {
        FORCE_ITEM,
        FORCE_MOB,
        FORCE_BIOME,
        FORCE_ADVANCEMENT,
        FORCE_HEIGHT,
        BACKPACK,
        CHAIN_MODE,
        HIDE_POINTS,
        HIDE_OBJECTIVES
    }

}
