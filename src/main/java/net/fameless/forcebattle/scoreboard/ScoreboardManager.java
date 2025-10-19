package net.fameless.forcebattle.scoreboard;

import net.fameless.forcebattle.ForceBattle;
import net.fameless.forcebattle.game.Team;
import net.fameless.forcebattle.player.BattlePlayer;
import net.fameless.forcebattle.util.StringUtility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.Map;

public class ScoreboardManager {

    private static final Map<BattlePlayer, Scoreboard> SCOREBOARDS = new HashMap<>();

    public static void startUpdater() {
        Bukkit.getLogger().info("[ForceBattle] Scoreboard updater started.");

        Bukkit.getScheduler().runTaskTimer(ForceBattle.get(), ScoreboardManager::updateAll, 1L, 1L);
    }

    public static void updateAll() {
        for (BattlePlayer battlePlayer : BattlePlayer.getOnlinePlayers()) {
            if (battlePlayer.isExcluded() || !ForceBattle.getTimer().isRunning()) {
                removeScoreboard(battlePlayer);
                continue;
            }
            updateScoreboard(battlePlayer);
        }
    }

    public static void updateScoreboard(BattlePlayer player) {
        Scoreboard scoreboard = SCOREBOARDS.computeIfAbsent(player,
                p -> Bukkit.getScoreboardManager().getNewScoreboard());

        Objective obj = scoreboard.getObjective("fb_info");
        if (obj == null) {
            obj = scoreboard.registerNewObjective("fb_info", "dummy",
                    ChatColor.GOLD + "" + ChatColor.BOLD + "ForceBattle");
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        }

        scoreboard.getEntries().forEach(scoreboard::resetScores);

        int line = 15;

        var selfObjective = player.getObjective();
        if (selfObjective != null) {
            obj.getScore(ChatColor.YELLOW + "Your Objective:").setScore(line--);
            obj.getScore(ChatColor.WHITE + StringUtility.formatName(selfObjective.getObjectiveString())).setScore(line--);
        } else {
            obj.getScore(ChatColor.GRAY + "No current objective").setScore(line--);
        }

        obj.getScore(" ").setScore(line--);

        if (player.isInTeam()) {
            Team team = player.getTeam();
            obj.getScore(ChatColor.AQUA + "Your Team: " + ChatColor.WHITE + team.getId()).setScore(line--);

            for (BattlePlayer teammate : team.getPlayers()) {
                if (teammate.equals(player)) continue;

                var teammateObjective = teammate.getObjective();
                String objName = teammateObjective != null
                        ? StringUtility.formatName(teammateObjective.getObjectiveString())
                        : "None";

                String display = ChatColor.GRAY + "• " + ChatColor.YELLOW + teammate.getName() +
                        ChatColor.WHITE + ": " +
                        (objName.length() > 16 ? objName.substring(0, 13) + "..." : objName);

                obj.getScore(display).setScore(line--);
            }
        } else {
            obj.getScore(ChatColor.GRAY + "Not in a team").setScore(line--);
        }

        player.getPlayer().setScoreboard(scoreboard);
    }

    public static void removeScoreboard(BattlePlayer player) {
        player.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        SCOREBOARDS.remove(player);
        Bukkit.getLogger().info("[ForceBattle] Removed scoreboard for " + player.getName());
    }
}
