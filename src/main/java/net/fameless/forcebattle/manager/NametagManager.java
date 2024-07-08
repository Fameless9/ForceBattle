package net.fameless.forcebattle.manager;

import net.fameless.forcebattle.ForceBattlePlugin;
import net.fameless.forcebattle.command.ExcludeCommand;
import net.fameless.forcebattle.team.TeamManager;
import net.fameless.forcebattle.util.Advancement;
import net.fameless.forcebattle.util.Challenge;
import net.fameless.forcebattle.util.Format;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class NametagManager {

    private static final Scoreboard customScoreboard;

    static {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        customScoreboard = manager.getNewScoreboard();
    }

    public static void setupNametag(Player player) {
        getNametag(player);
    }

    public static void updateNametag(Player player) {
        getNametag(player);
    }

    private static void getNametag(Player player) {
        Team team = customScoreboard.getTeam(player.getUniqueId().toString());
        if (team == null) {
            team = customScoreboard.registerNewTeam(player.getUniqueId().toString());
        }

        Challenge type = ObjectiveManager.getChallengeType(player);
        Object challenge = ObjectiveManager.getObjective(player);

        StringBuilder suffix = new StringBuilder();

        suffix.append(" ");

        if (ExcludeCommand.excludedPlayers.contains(player)) {
            suffix.append(ChatColor.GRAY + "- excluded");
            team.setSuffix(suffix.toString());
            return;
        }

        if (!ForceBattlePlugin.getInstance().getTimer().isRunning()) {
            suffix.append(ChatColor.DARK_GRAY + "| " + ChatColor.GOLD + "Waiting...");
            team.setSuffix(suffix.toString());
            return;
        }
        if (type != null) {
            if (challenge instanceof Material item) {
                suffix.append(ChatColor.GRAY + "Item" + ChatColor.DARK_GRAY + " » " + ChatColor.GOLD + Format.formatName(item.name()));
            } else if (challenge instanceof EntityType mob) {
                suffix.append(ChatColor.GRAY + "Mob" + ChatColor.DARK_GRAY + " » " + ChatColor.GOLD + Format.formatName(mob.name()));
            } else if (challenge instanceof Biome biome) {
                suffix.append(ChatColor.GRAY + "Biome" + ChatColor.DARK_GRAY + " » " + ChatColor.GOLD + Format.formatName(biome.name()));
            } else if (challenge instanceof Advancement advancement) {
                suffix.append(ChatColor.GRAY + "Advancement" + ChatColor.DARK_GRAY + " » " + ChatColor.GOLD + Format.formatName(advancement.name()));
            } else if (challenge instanceof Integer height) {
                suffix.append(ChatColor.GRAY + "Height" + ChatColor.DARK_GRAY + " » " + ChatColor.GOLD + height);
            }
        } else {
            suffix.append(ChatColor.GRAY + "No objective");
        }

        suffix.append(ChatColor.DARK_GRAY + " | " + ChatColor.GRAY + "Points" + ChatColor.DARK_GRAY + " » " + ChatColor.GOLD + PointsManager.getPoints(player));

        net.fameless.forcebattle.team.Team playerTeam = TeamManager.getTeam(player);
        if (playerTeam != null) {
            suffix.append(ChatColor.DARK_GRAY + " | " + ChatColor.GRAY + "Team" + ChatColor.DARK_GRAY + " » " + ChatColor.GOLD + playerTeam.getId());
        }
        String formattedSuffix = String.valueOf(suffix);
        team.setSuffix(formattedSuffix);
    }

    public static void newTag(Player player) {
        Team team = customScoreboard.getTeam(player.getUniqueId().toString());
        if (team != null) {
            team.addEntry(player.getName());
        } else {
            team = customScoreboard.registerNewTeam(player.getUniqueId().toString());
            team.addEntry(player.getName());
        }

        player.setScoreboard(customScoreboard);
    }

    public static void removeTag(Player player) {
        Team team = customScoreboard.getTeam(player.getUniqueId().toString());
        if (team != null) {
            team.unregister();
        }
        for (Player target : Bukkit.getOnlinePlayers()) {
            Team targetTeam = target.getScoreboard().getTeam(player.getUniqueId().toString());
            if (targetTeam != null) {
                targetTeam.removeEntry(player.getDisplayName());
            }
        }
    }
}
