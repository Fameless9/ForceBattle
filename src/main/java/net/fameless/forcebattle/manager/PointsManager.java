package net.fameless.forcebattle.manager;

import net.fameless.forcebattle.team.Team;
import net.fameless.forcebattle.team.TeamManager;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class PointsManager {

    public static final HashMap<Player, Integer> pointsMap = new HashMap<>();

    public static int getPoints(Player player) {
        if (TeamManager.getTeam(player) != null) {
            return TeamManager.getTeam(player).getPoints();
        }
        Integer points = pointsMap.get(player);
        return points != null ? points.intValue() : 0;
    }
    public static void addPoint(Player player) {
        if (TeamManager.getTeam(player) != null) {
            Team team = TeamManager.getTeam(player);
            int points = team.getPoints();
            team.setPoints(points + 1);
            for (Player teamPlayer : team.getPlayers()) {
                teamPlayer.playSound(teamPlayer, Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.MASTER, 1, 20);
                NametagManager.updateNametag(teamPlayer);
                BossbarManager.updateBossbar(teamPlayer);
            }
            return;
        }
        int points = pointsMap.get(player);
        pointsMap.put(player, points + 1);
        player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.MASTER, 1, 20);
        NametagManager.updateNametag(player);
        BossbarManager.updateBossbar(player);
    }

    public static void setPoints(Player player, int points) {
        if (TeamManager.getTeam(player) != null) {
            Team team = TeamManager.getTeam(player);
            team.setPoints(points);
            return;
        }
        pointsMap.put(player, points);
    }
}
