package net.fameless.forcebattle.configuration;

import net.fameless.forcebattle.ForceBattle;
import net.fameless.forcebattle.game.Team;
import net.fameless.forcebattle.gui.impl.SettingsGUI;
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
        if (setting == Setting.CHAIN_MODE) {
            ForceBattle.getObjectiveManager().updateChainList();
        }
        BattlePlayer.BATTLE_PLAYERS.forEach(player -> player.updateObjective(false, false));
        Team.teams.forEach(team -> team.updateObjective(null, false, false));
    }

    public static boolean isMultiState(Setting setting) {
        for (SettingsGUI.SettingButton button : SettingsGUI.SettingButton.values()) {
            if (button.getSetting() == setting) {
                return button.isMultiState();
            }
        }
        return false;
    }

    public enum Setting {
        FORCE_ITEM,
        FORCE_MOB,
        FORCE_BIOME,
        FORCE_ADVANCEMENT,
        FORCE_HEIGHT,
        FORCE_COORDS,
        FORCE_STRUCTURE,

        NO_HUNGER,
        BACKPACK,
        CHAIN_MODE,
        HIDE_POINTS,
        HIDE_OBJECTIVES,
        NO_DUPLICATE_OBJECTIVES,
        SIMPLIFIED_OBJECTIVES,
        EXTRA_TEAM_OBJECTIVE,
        SHOW_SCOREBOARD,

        EXCLUDE_MUSIC_DISCS,
        EXCLUDE_BANNER_PATTERNS,
        EXCLUDE_ARMOR_TEMPLATES,
        EXCLUDE_POTTERY_SHERDS,
        EXCLUDE_ORES,
        EXCLUDE_END,
        EXCLUDE_TRIAL_ITEMS,
    }


    public enum SettingState {
        OFF(ChatColor.RED + "Off"),
        PLAYER(ChatColor.GREEN + "Player"),
        TEAM(ChatColor.AQUA + "Team"),
        BOTH(ChatColor.GOLD + "Both");

        private final String defaultName;

        SettingState(String defaultName) {
            this.defaultName = defaultName;
        }

        public SettingState next(boolean multiState) {
            if (multiState) {
                return switch (this) {
                    case OFF -> PLAYER;
                    case PLAYER -> TEAM;
                    case TEAM -> BOTH;
                    case BOTH -> OFF;
                };
            } else {
                return this == OFF ? PLAYER : OFF;
            }
        }

        public String getDisplayName(boolean multiState) {
            if (multiState) return defaultName;

            return switch (this) {
                case OFF -> ChatColor.RED + "Off";
                default -> ChatColor.GREEN + "On";
            };
        }
    }
}
