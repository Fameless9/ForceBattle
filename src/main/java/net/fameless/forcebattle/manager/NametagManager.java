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

        Challenge type = ItemManager.getChallengeType(player);
        Object challenge = ItemManager.getChallenge(player);

        StringBuilder suffix = new StringBuilder();

        if (ExcludeCommand.excludedPlayers.contains(player)) {
            suffix.append(ChatColor.GRAY + " - excluded");
            team.setSuffix(suffix.toString());
            return;
        }

        suffix.append(" " + ChatColor.GOLD + "Points" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + PointsManager.getPoints(player));

        if (!Timer.isRunning()) {
            suffix.append(ChatColor.DARK_GRAY + " | " + ChatColor.GOLD + "Waiting...");
            team.setSuffix(suffix.toString());
            return;
        }
        if (type != null) {
            if (type.equals(Challenge.FORCE_ITEM)) {
                Material item = (Material) challenge;
                suffix.append(ChatColor.DARK_GRAY + " | " + ChatColor.GOLD + "Item" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + BossbarManager.formatItemName(item.name().replace("_", " ")));
            } else if (type.equals(Challenge.FORCE_MOB)) {
                EntityType mob = (EntityType) challenge;
                suffix.append(ChatColor.DARK_GRAY + " | " + ChatColor.GOLD + "Mob" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + BossbarManager.formatItemName(mob.name().replace("_", " ")));
            } else if (type.equals(Challenge.FORCE_BIOME)) {
                Biome biome = (Biome) challenge;
                suffix.append(ChatColor.DARK_GRAY + " | " + ChatColor.GOLD + "Biome" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + BossbarManager.formatItemName(biome.name().replace("_", " ")));
            } else if (type.equals(Challenge.FORCE_ADVANCEMENT)) {
                Advancement advancement = (Advancement) challenge;
                suffix.append(ChatColor.DARK_GRAY + " | " + ChatColor.GOLD + "Biome" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + BossbarManager.formatItemName(advancement.name().replace("_", " ")));
            } else if (type.equals(Challenge.FORCE_HEIGHT)) {
                int height = (int) challenge;
                suffix.append(ChatColor.DARK_GRAY + " | " + ChatColor.GOLD + "Height" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + height);
            }
        }
        if (TeamManager.getTeam(player) != null) {
            suffix.append(ChatColor.DARK_GRAY + " | " + ChatColor.GOLD + "Team" + ChatColor.DARK_GRAY + ": " + ChatColor.GOLD + TeamManager.getTeam(player).getId());
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