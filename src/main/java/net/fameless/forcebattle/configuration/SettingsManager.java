package net.fameless.forcebattle.configuration;

import net.fameless.forcebattle.ForceBattle;
import net.fameless.forcebattle.player.BattlePlayer;
import net.fameless.forcebattle.util.BattleType;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public class SettingsManager {

    private static final EnumMap<Setting, SettingState> STATES = new EnumMap<>(Setting.class);

    private SettingsManager() {}

    static {
        for (Setting setting : Setting.values()) {
            STATES.put(setting, SettingState.OFF);
        }
    }

    public static @NotNull List<BattleType> getActiveChallenges() {
        List<BattleType> activeChallenges = new ArrayList<>();
        for (BattleType battleType : BattleType.values()) {
            if (isEnabled(battleType.getLinkedSetting())) {
                activeChallenges.add(battleType);
            }
        }
        return activeChallenges;
    }

    public static SettingState getState(Setting setting) {
        return STATES.getOrDefault(setting, SettingState.OFF);
    }

    public static void setState(Setting setting, SettingState state) {
        if (state == null) state = SettingState.OFF;
        STATES.put(setting, state);
        updateSetting(setting);
    }

    public static boolean isEnabled(Setting setting) {
        return getState(setting) != SettingState.OFF;
    }

    public static void applyMinimum() {
        boolean hasForceActive = STATES.entrySet().stream()
                .anyMatch(entry -> entry.getKey().name().startsWith("FORCE_") && entry.getValue() != SettingState.OFF);
        if (!hasForceActive) {
            setState(Setting.FORCE_ITEM, SettingState.PLAYER);
        }
    }

    private static void updateSetting(Setting setting) {
        if (setting == Setting.CHAIN_MODE || setting.name().startsWith("FORCE_")) {
            ForceBattle.getObjectiveManager().updateChainList();
        }
        BattlePlayer.BATTLE_PLAYERS.forEach(player -> player.updateObjective(false, false));
    }

    public static void setForceItemEnabled(boolean enabled) { setState(Setting.FORCE_ITEM, enabled ? SettingState.PLAYER : SettingState.OFF); }
    public static void setForceMobEnabled(boolean enabled) { setState(Setting.FORCE_MOB, enabled ? SettingState.PLAYER : SettingState.OFF); }
    public static void setForceBiomeEnabled(boolean enabled) { setState(Setting.FORCE_BIOME, enabled ? SettingState.PLAYER : SettingState.OFF); }
    public static void setForceAdvancementEnabled(boolean enabled) { setState(Setting.FORCE_ADVANCEMENT, enabled ? SettingState.PLAYER : SettingState.OFF); }
    public static void setForceHeightEnabled(boolean enabled) { setState(Setting.FORCE_HEIGHT, enabled ? SettingState.PLAYER : SettingState.OFF); }
    public static void setForceCoordsEnabled(boolean enabled) { setState(Setting.FORCE_COORDS, enabled ? SettingState.PLAYER : SettingState.OFF); }
    public static void setForceStructureEnabled(boolean enabled) { setState(Setting.FORCE_STRUCTURE, enabled ? SettingState.PLAYER : SettingState.OFF); }

    public static void setChainModeEnabled(boolean enabled) { setState(Setting.CHAIN_MODE, enabled ? SettingState.PLAYER : SettingState.OFF); }
    public static void setBackpackEnabled(boolean enabled) { setState(Setting.BACKPACK, enabled ? SettingState.PLAYER : SettingState.OFF); }
    public static void setHidePointsEnabled(boolean enabled) { setState(Setting.HIDE_POINTS, enabled ? SettingState.PLAYER : SettingState.OFF); }
    public static void setHideObjectivesEnabled(boolean enabled) { setState(Setting.HIDE_OBJECTIVES, enabled ? SettingState.PLAYER : SettingState.OFF); }
    public static void setNoDuplicateObjectivesEnabled(boolean enabled) { setState(Setting.NO_DUPLICATE_OBJECTIVES, enabled ? SettingState.PLAYER : SettingState.OFF); }
    public static void setSimplifiedObjectivesEnabled(boolean enabled) { setState(Setting.SIMPLIFIED_OBJECTIVES, enabled ? SettingState.PLAYER : SettingState.OFF); }
    public static void setExtraTeamObjectivesEnabled(boolean enabled) { setState(Setting.EXTRA_TEAM_OBJECTIVE, enabled ? SettingState.TEAM : SettingState.OFF); }

    public enum Setting {
        FORCE_ITEM,
        FORCE_MOB,
        FORCE_BIOME,
        FORCE_ADVANCEMENT,
        FORCE_HEIGHT,
        FORCE_COORDS,
        FORCE_STRUCTURE,
        BACKPACK,
        CHAIN_MODE,
        HIDE_POINTS,
        HIDE_OBJECTIVES,
        NO_DUPLICATE_OBJECTIVES,
        SIMPLIFIED_OBJECTIVES,
        EXTRA_TEAM_OBJECTIVE
    }

    public enum SettingState {
        OFF(ChatColor.RED + "Off"),
        PLAYER(ChatColor.GREEN + "Player"),
        TEAM(ChatColor.AQUA + "Team"),
        BOTH(ChatColor.GOLD + "Both");

        private final String displayName;

        SettingState(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public SettingState next() {
            return switch (this) {
                case OFF -> PLAYER;
                case PLAYER -> TEAM;
                case TEAM -> BOTH;
                case BOTH -> OFF;
            };
        }
    }
}
