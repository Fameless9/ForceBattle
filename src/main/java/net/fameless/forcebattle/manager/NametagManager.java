package net.fameless.forcebattle.manager;

import net.fameless.forcebattle.command.ExcludeCommand;
import net.fameless.forcebattle.team.TeamManager;
import net.fameless.forcebattle.timer.Timer;
import net.fameless.forcebattle.util.Advancement;
import net.fameless.forcebattle.util.Challenge;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.Team;

public class NametagManager implements Listener {
    public static void setupNametag(Player player) {
        getNametag(player);
    }

    public static void updateNametag(Player player) {
        getNametag(player);
    }

    private static void getNametag(Player player) {
        Team team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(player.getUniqueId().toString());
        if (team == null) {
            team = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam(player.getUniqueId().toString());
        }

        Challenge type = ObjectiveManager.getChallengeType(player);
        Object challenge = ObjectiveManager.getChallenge(player);

        StringBuilder suffix = new StringBuilder();

        if (ExcludeCommand.excludedPlayers.contains(player)) {
            suffix.append(ChatColor.GRAY).append(" - excluded");
            team.setSuffix(suffix.toString());
            return;
        }

        suffix.append(" ").append(ChatColor.GOLD).append("Points").append(ChatColor.DARK_GRAY).append(": ").append(ChatColor.GOLD)
                .append(PointsManager.getPoints(player));

        if (!Timer.isRunning()) {
            suffix.append(ChatColor.DARK_GRAY).append(" | ").append(ChatColor.GOLD).append("Waiting...");
            team.setSuffix(suffix.toString());
            return;
        }
        if (type != null) {
            if (challenge instanceof Material item) {
                suffix.append(ChatColor.DARK_GRAY).append(" | ").append(ChatColor.GOLD).append("Item").append(ChatColor.DARK_GRAY)
                        .append(": ").append(ChatColor.GOLD).append(BossbarManager.formatItemName(item.name().replace("_", " ")));
            } else if (challenge instanceof EntityType mob) {
                suffix.append(ChatColor.DARK_GRAY).append(" | ").append(ChatColor.GOLD).append("Mob").append(ChatColor.DARK_GRAY)
                        .append(": ").append(ChatColor.GOLD).append(BossbarManager.formatItemName(mob.name().replace("_", " ")));
            } else if (challenge instanceof Biome biome) {
                suffix.append(ChatColor.DARK_GRAY + " | " + ChatColor.GOLD + "Biome" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD).append(BossbarManager.formatItemName(biome.name().replace("_", " ")));
            } else if (challenge instanceof Advancement advancement) {
                suffix.append(ChatColor.DARK_GRAY + " | " + ChatColor.GOLD + "Advancement" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + BossbarManager.formatItemName(advancement.name().replace("_", " ")));
            } else if (challenge instanceof Integer height) {
                suffix.append(ChatColor.DARK_GRAY + " | " + ChatColor.GOLD + "Height" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + height);
            }
        }
        net.fameless.forcebattle.team.Team playerTeam = TeamManager.getTeam(player);
        if (playerTeam != null) {
            suffix.append(ChatColor.DARK_GRAY + " | " + ChatColor.GOLD + "Team" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + playerTeam.getId());
        }
        String formattedSuffix = String.valueOf(suffix).replace("_", " ");
        team.setSuffix(formattedSuffix);
    }

    public static void newTag(Player player) {
        Team team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(player.getUniqueId().toString());
        if (team != null) {
            team.addEntry(player.getName());
        }
    }

    public static void removeTag(Player player) {
        Bukkit.getScoreboardManager().getMainScoreboard().getTeam(player.getUniqueId().toString()).unregister();
        for (Player target : Bukkit.getOnlinePlayers()) {
            Team team = target.getScoreboard().getTeam(player.getUniqueId().toString());
            if (team != null) {
                team.removeEntry(player.getDisplayName());
            }
        }
    }

}