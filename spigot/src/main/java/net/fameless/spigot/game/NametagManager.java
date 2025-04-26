package net.fameless.spigot.game;

import net.fameless.core.ForceBattle;
import net.fameless.core.caption.Caption;
import net.fameless.core.configuration.SettingsManager;
import net.fameless.core.game.Objective;
import net.fameless.core.util.BattleType;
import net.fameless.core.util.Format;
import net.fameless.spigot.BukkitPlatform;
import net.fameless.spigot.player.BukkitPlayer;
import net.fameless.spigot.util.Advancement;
import net.fameless.spigot.util.BukkitUtil;
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
        Bukkit.getScheduler().runTaskTimer(BukkitPlatform.get(), () -> BukkitPlayer.BUKKIT_PLAYERS.forEach(NametagManager::updateNametag), 0, 3);
    }

    private static void updateNametag(@NotNull BukkitPlayer bukkitPlayer) {
        if (bukkitPlayer.isOffline()) {
            Team team = getCustomScoreboard().getTeam(bukkitPlayer.getUniqueId().toString());
            if (team != null) {
                team.unregister();
            }
            for (Player target : Bukkit.getOnlinePlayers()) {
                Team targetTeam = target.getScoreboard().getTeam(bukkitPlayer.getUniqueId().toString());
                if (targetTeam != null && targetTeam.hasEntry(bukkitPlayer.getName())) {
                    targetTeam.removeEntry(bukkitPlayer.getName());
                }
            }
        }

        Team team = getCustomScoreboard().getTeam(bukkitPlayer.getUniqueId().toString());
        if (team == null) {
            team = getCustomScoreboard().registerNewTeam(bukkitPlayer.getUniqueId().toString());
        }
        if (!team.hasEntry(bukkitPlayer.getName())) {
            team.addEntry(bukkitPlayer.getName());
        }
        bukkitPlayer.setScoreboard(getCustomScoreboard());

        StringBuilder suffix = new StringBuilder(" ");

        if (bukkitPlayer.isExcluded()) {
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
        Objective objective = bukkitPlayer.getObjective();
        if (BukkitUtil.convertObjective(BattleType.FORCE_ADVANCEMENT, objective.getObjectiveString()) instanceof Advancement advancement) {
            objectiveString = advancement.name;
        } else {
            objectiveString = Format.formatName(objective.getObjectiveString());
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
                    .append(ChatColor.BLUE).append(bukkitPlayer.getPoints());
        }

        net.fameless.core.game.Team playerTeam = bukkitPlayer.getTeam();
        if (playerTeam != null) {
            suffix.append(ChatColor.DARK_GRAY).append(" | ").append(ChatColor.GRAY).append("Team").append(ChatColor.DARK_GRAY).append(" » ")
                    .append(ChatColor.BLUE).append(playerTeam.getId());
        }
        String formattedSuffix = String.valueOf(suffix).trim();
        team.setSuffix(formattedSuffix);
    }

}
