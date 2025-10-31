package net.fameless.forcebattle.game;

import net.fameless.forcebattle.ForceBattle;
import net.fameless.forcebattle.caption.Caption;
import net.fameless.forcebattle.configuration.SettingsManager;
import net.fameless.forcebattle.game.data.FBAdvancement;
import net.fameless.forcebattle.player.BattlePlayer;
import net.fameless.forcebattle.scoreboard.ScoreboardManager;
import net.fameless.forcebattle.util.BattleType;
import net.fameless.forcebattle.util.BukkitUtil;
import net.fameless.forcebattle.util.StringUtility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class NametagManager {

    private static final Map<UUID, Map<UUID, String>> lastSuffixes = new ConcurrentHashMap<>();

    public static void runTask() {
        Bukkit.getScheduler().runTaskTimer(ForceBattle.get(), () ->
                BattlePlayer.BATTLE_PLAYERS.forEach(NametagManager::updateNametag), 0, 3);
    }

    private static void updateNametag(@NotNull BattlePlayer battlePlayer) {
        Player player = battlePlayer.getPlayer();
        if (player == null) return;

        Scoreboard scoreboard = ScoreboardManager.getOrCreateScoreboard(battlePlayer);
        Map<UUID, String> cache = lastSuffixes.computeIfAbsent(battlePlayer.getUuid(), u -> new ConcurrentHashMap<>());

        for (BattlePlayer targetPlayer : BattlePlayer.BATTLE_PLAYERS) {
            Player target = targetPlayer.getPlayer();
            if (target == null) continue;

            Team team = scoreboard.getTeam(targetPlayer.getUuid().toString());
            if (team == null) {
                team = scoreboard.registerNewTeam(targetPlayer.getUuid().toString());
            }
            if (!team.hasEntry(targetPlayer.getName())) {
                team.addEntry(targetPlayer.getName());
            }

            String newSuffix = buildSuffix(targetPlayer);
            String oldSuffix = team.getSuffix();

            if (!newSuffix.equals(oldSuffix)) {
                team.setSuffix(newSuffix);
                cache.put(targetPlayer.getUuid(), newSuffix);
            }
        }

        player.setScoreboard(scoreboard);
    }

    private static String buildSuffix(@NotNull BattlePlayer targetPlayer) {
        StringBuilder suffix = new StringBuilder(" ");

        if (targetPlayer.isExcluded()) {
            suffix.append(Caption.getAsLegacy("excluded"));
        } else if (!ForceBattle.getTimer().isRunning()) {
            suffix.append(Caption.getAsLegacy("waiting"));
        }

        Objective objective = targetPlayer.getObjective();
        if (objective == null && targetPlayer.isInTeam()) {
            objective = targetPlayer.getTeam().getObjective();
        }

        if (objective != null) {
            String objectiveString;
            if (BukkitUtil.convertObjective(BattleType.FORCE_ADVANCEMENT, objective.getObjectiveString()) instanceof FBAdvancement advancement) {
                objectiveString = advancement.name;
            } else {
                objectiveString = StringUtility.formatName(objective.getObjectiveString());
            }

            boolean hideObjectives = SettingsManager.isEnabled(SettingsManager.Setting.HIDE_OBJECTIVES);
            boolean hidePoints = SettingsManager.isEnabled(SettingsManager.Setting.HIDE_POINTS);

            if (!hideObjectives) {
                suffix.append(" ")
                        .append(ChatColor.GRAY)
                        .append(objective.getBattleType().getPrefix())
                        .append(ChatColor.DARK_GRAY)
                        .append(" » ")
                        .append(ChatColor.BLUE)
                        .append(objectiveString);
            }

            if (!hideObjectives && !hidePoints) {
                suffix.append(ChatColor.DARK_GRAY).append(" |");
            }

            if (!hidePoints) {
                suffix.append(" ")
                        .append(ChatColor.GRAY).append("Points")
                        .append(ChatColor.DARK_GRAY).append(" » ")
                        .append(ChatColor.BLUE).append(targetPlayer.getPoints());
            }
        }

        var teamData = targetPlayer.getTeam();
        if (teamData != null) {
            suffix.append(ChatColor.DARK_GRAY).append(" | ")
                    .append(ChatColor.GRAY).append("Team")
                    .append(ChatColor.DARK_GRAY).append(" » ")
                    .append(ChatColor.BLUE).append(teamData.getId());
        }

        return suffix.toString();
    }
}
