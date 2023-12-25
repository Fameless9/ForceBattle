package net.fameless.forcebattle.manager;

import net.fameless.forcebattle.team.Team;
import net.fameless.forcebattle.team.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class LeaderboardManager {

    public static void displayLeaderboard() {
        List<Team> excluded = new ArrayList<>();
        List<Map.Entry<Player, Integer>> sortedEntries = new ArrayList<>(PointsManager.pointsMap.entrySet());
        sortedEntries.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        StringBuilder message = new StringBuilder(ChatColor.GOLD.toString() + ChatColor.BOLD + "LEADERBOARD:\n");
        int position = 1;
        for (Map.Entry<Player, Integer> entry : sortedEntries) {
            Player player = entry.getKey();
            int points = entry.getValue();

            if (player != null && TeamManager.getTeam(player) != null) {
                Team team = TeamManager.getTeam(player);
                if (excluded.contains(team)) {
                    continue;
                }
                for (Player teamPlayer : team.getPlayers()) {
                    teamPlayer.sendMessage(ChatColor.GOLD + "Your team placed " + position + " with " + team.getPoints() + " points.");
                }

                message.append(ChatColor.GRAY + String.valueOf(position)).append(". ").append(ChatColor.AQUA + "Team " + team.getId()).append(": ").append(ChatColor.GREEN + String.valueOf(team.getPoints())).append(" points\n");
                ++position;
                excluded.add(team);
            } else {
                if (player != null) {
                    player.sendMessage(ChatColor.GOLD + "You placed: " + position + " with " + points + " points.");
                }
                String playerName = (player != null) ? player.getName() : "Unknown";
                message.append(ChatColor.GRAY + String.valueOf(position)).append(". ").append(ChatColor.AQUA + playerName).append(": ").append(ChatColor.GREEN + String.valueOf(points)).append(" points\n");
                ++position;
            }
        }
        Bukkit.broadcastMessage(message.toString());
    }
}