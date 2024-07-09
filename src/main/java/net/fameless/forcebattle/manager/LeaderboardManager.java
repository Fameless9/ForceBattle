package net.fameless.forcebattle.manager;

import net.fameless.forcebattle.ForceBattlePlugin;
import net.fameless.forcebattle.team.Team;
import net.fameless.forcebattle.team.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LeaderboardManager {

    public static void displayLeaderboard() {
        List<Team> excluded = new ArrayList<>();
        List<Map.Entry<UUID, Integer>> sortedEntries = new ArrayList<>(ForceBattlePlugin.get().getPointsManager().getPointsMap().entrySet());
        sortedEntries.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));
        StringBuilder message = new StringBuilder("\n" + ChatColor.GOLD + ChatColor.BOLD + "LEADERBOARD:\n");
        int position = 1;
        for (Map.Entry<UUID, Integer> entry : sortedEntries) {
            Player player = Bukkit.getPlayer(entry.getKey());
            int points = entry.getValue();
            if (player == null) continue;

            if (TeamManager.getTeam(player) != null) {
                Team team = TeamManager.getTeam(player);
                if (team == null || excluded.contains(team)) {
                    continue;
                }

                for (Player teamPlayer : team.getPlayers()) {
                    teamPlayer.sendMessage(ChatColor.GOLD + "Your team placed " + position + " with " + team.getPoints() + " points.");
                }

                message.append(ChatColor.GRAY).append(position).append(". ").append(ChatColor.AQUA).append("Team ")
                        .append(team.getId()).append(": ").append(ChatColor.GREEN).append(team.getPoints()).append(" points\n");
                ++position;
                excluded.add(team);
            } else {
                player.sendMessage(ChatColor.GOLD + "You placed: " + position + " with " + points + " points.");
                message.append(ChatColor.GRAY).append(position).append(". ").append(ChatColor.AQUA).append(player.getName()).append(": ")
                        .append(ChatColor.GREEN).append(points).append(" points\n");
                ++position;
            }
        }
        Bukkit.broadcastMessage(message.toString());
    }
}