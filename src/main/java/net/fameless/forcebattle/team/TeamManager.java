package net.fameless.forcebattle.team;

import net.fameless.forcebattle.ForceBattlePlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TeamManager {

    private static final List<Team> teams = new ArrayList<>();

    public static void registerTeam(Team team) {
        for (Player player : team.getPlayers()) {
            player.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.GREEN + "New team created.");
        }
        teams.add(team);
    }

    public static void removeTeam(int teamId) {
        Team team = TeamManager.getTeam(teamId);
        if (team != null) {
            for (Player player : team.getPlayers()) {
                player.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.GOLD + "Your team has been deleted.");
            }

            team.getPlayers().clear();
            teams.remove(team);
        }
    }

    public static void removeTeam(Team team) {
        if (team != null) {

            for (Player player : team.getPlayers()) {
                player.sendMessage(ForceBattlePlugin.PREFIX + ChatColor.GOLD + "Your team has been deleted.");
            }

            team.getPlayers().clear();
            teams.remove(team);
        }
    }

    public static Team getTeam(Player player) {
        for (Team team : teams) {
            if (team.getPlayers().contains(player)) {
                return team;
            }
        }
        return null;
    }

    public static Team getTeam(int id) {
        for (Team team : teams) {
            if (team.getId() == id) {
                return team;
            }
        }
        return null;
    }

    public static List<Team> getTeams() {
        return teams;
    }
}