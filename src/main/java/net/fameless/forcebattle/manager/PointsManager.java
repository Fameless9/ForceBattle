package net.fameless.forcebattle.manager;

import net.fameless.forcebattle.team.Team;
import net.fameless.forcebattle.team.TeamManager;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PointsManager {

    public static final HashMap<UUID, Integer> pointsMap = new HashMap<>();

    public static int getPoints(Player player) {
        return pointsMap.getOrDefault(player.getUniqueId(), 0);
    }

    public static void addPoint(Player player) {
        int points = pointsMap.getOrDefault(player.getUniqueId(), 0);
        pointsMap.put(player.getUniqueId(), points + 1);
        player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.MASTER, 1, 20);
        NametagManager.updateNametag(player);
        BossbarManager.updateBossbar(player);

        Team team = TeamManager.getTeam(player);
        if (team != null) {
            team.updatePoints();
            for (Player teamPlayer : team.getPlayers()) {
                NametagManager.updateNametag(teamPlayer);
                BossbarManager.updateBossbar(teamPlayer);
            }
        }
    }

    public static void setPoints(Player player, int points) {
        pointsMap.put(player.getUniqueId(), points);

        Team team = TeamManager.getTeam(player);
        if (team != null) {
            team.updatePoints();
        }
    }
}
