package net.fameless.forcebattle.game;

import net.fameless.forcebattle.ForceBattle;
import net.fameless.forcebattle.caption.Caption;
import net.fameless.forcebattle.configuration.SettingsManager;
import net.fameless.forcebattle.game.data.FBAdvancement;
import net.fameless.forcebattle.player.BattlePlayer;
import net.fameless.forcebattle.util.BattleType;
import net.fameless.forcebattle.util.BukkitUtil;
import net.fameless.forcebattle.util.StringUtility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

public class NametagManager {

    private static Scoreboard scoreboard = null;

    private static Scoreboard getCustomScoreboard() {
        if (scoreboard == null) {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        }
        return scoreboard;
    }

    public static void runTask() {
        Bukkit.getScheduler().runTaskTimer(ForceBattle.get(), () -> BattlePlayer.BATTLE_PLAYERS.forEach(NametagManager::updateNametag), 0, 3);
    }

    private static void updateNametag(@NotNull BattlePlayer battlePlayer) {
        if (battlePlayer.isOffline()) {
            Team team = getCustomScoreboard().getTeam(battlePlayer.getUuid().toString());
            if (team != null) {
                team.unregister();
            }
            for (Player target : Bukkit.getOnlinePlayers()) {
                Team targetTeam = target.getScoreboard().getTeam(battlePlayer.getUuid().toString());
                if (targetTeam != null && targetTeam.hasEntry(battlePlayer.getName())) {
                    targetTeam.removeEntry(battlePlayer.getName());
                }
            }
        }

        Team team = getCustomScoreboard().getTeam(battlePlayer.getUuid().toString());
        if (team == null) {
            team = getCustomScoreboard().registerNewTeam(battlePlayer.getUuid().toString());
        }
        if (!team.hasEntry(battlePlayer.getName())) {
            team.addEntry(battlePlayer.getName());
        }
        battlePlayer.setScoreboard(getCustomScoreboard());

        StringBuilder suffix = new StringBuilder(" ");

        if (battlePlayer.isExcluded()) {
            suffix.append(Caption.getAsLegacy("excluded"));
            team.setSuffix(suffix.toString());
            return;
        }

        if (!ForceBattle.getTimer().isRunning()) {
            suffix.append(Caption.getAsLegacy("waiting"));
            team.setSuffix(suffix.toString());
            return;
        }

        String objectiveString;
        Objective objective = battlePlayer.getObjective();
        if (BukkitUtil.convertObjective(BattleType.FORCE_ADVANCEMENT, objective.getObjectiveString()) instanceof FBAdvancement advancement) {
            objectiveString = advancement.name;
        } else {
            objectiveString = StringUtility.formatName(objective.getObjectiveString());
        }

        if (!SettingsManager.isEnabled(SettingsManager.Setting.HIDE_OBJECTIVES)) {
            suffix
                    .append(" ")
                    .append(ChatColor.GRAY)
                    .append(objective.getBattleType().getPrefix())
                    .append(ChatColor.DARK_GRAY)
                    .append(" » ")
                    .append(ChatColor.BLUE)
                    .append(
                            objectiveString);
        }

        if (!SettingsManager.isEnabled(SettingsManager.Setting.HIDE_OBJECTIVES) && !SettingsManager.isEnabled(SettingsManager.Setting.HIDE_POINTS)) {
            suffix.append(ChatColor.DARK_GRAY).append(" |");
        }

        if (!SettingsManager.isEnabled(SettingsManager.Setting.HIDE_POINTS)) {
            suffix.append(" ").append(ChatColor.GRAY).append("Points").append(ChatColor.DARK_GRAY).append(" » ")
                    .append(ChatColor.BLUE).append(battlePlayer.getPoints());
        }

        net.fameless.forcebattle.game.Team playerTeam = battlePlayer.getTeam();
        if (playerTeam != null) {
            suffix.append(ChatColor.DARK_GRAY).append(" | ").append(ChatColor.GRAY).append("Team").append(ChatColor.DARK_GRAY).append(" » ")
                    .append(ChatColor.BLUE).append(playerTeam.getId());
        }
        String formattedSuffix = suffix.toString();
        team.setSuffix(formattedSuffix);
    }

}
