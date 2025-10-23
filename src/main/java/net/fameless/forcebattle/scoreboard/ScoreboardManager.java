package net.fameless.forcebattle.scoreboard;

import net.fameless.forcebattle.ForceBattle;
import net.fameless.forcebattle.configuration.SettingsManager;
import net.fameless.forcebattle.game.Team;
import net.fameless.forcebattle.player.BattlePlayer;
import net.fameless.forcebattle.util.BattleType;
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
    private static final Map<BattlePlayer, Map<Integer, String>> LAST_LINES = new HashMap<>();
    private static final int maxStringLength = 30;

    public static void startUpdater() {
        Bukkit.getScheduler().runTaskTimer(ForceBattle.get(), ScoreboardManager::updateAll, 20L, 20L);
    }

    public static void updateAll() {
        for (BattlePlayer battlePlayer : BattlePlayer.getOnlinePlayers()) {
            boolean shouldHaveScoreboard = !battlePlayer.isExcluded() && ForceBattle.getTimer().isRunning();

            if (shouldHaveScoreboard && SettingsManager.isEnabled(SettingsManager.Setting.SHOW_SCOREBOARD)) {
                updateScoreboard(battlePlayer);
            } else if (SCOREBOARDS.containsKey(battlePlayer) && !SettingsManager.isEnabled(SettingsManager.Setting.SHOW_SCOREBOARD)) {
                removeScoreboard(battlePlayer);
            }
        }
    }

    public static Scoreboard getOrCreateScoreboard(BattlePlayer player) {
        return SCOREBOARDS.computeIfAbsent(player, p -> Bukkit.getScoreboardManager().getNewScoreboard());
    }

    public static void updateScoreboard(BattlePlayer player) {
        Scoreboard scoreboard = getOrCreateScoreboard(player);

        Objective obj = scoreboard.getObjective("fb_info");
        if (obj == null) {
            obj = scoreboard.registerNewObjective("fb_info", "dummy",
                    ChatColor.GOLD + "" + ChatColor.BOLD + "ForceBattle");
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        }

        Map<Integer, String> last = LAST_LINES.computeIfAbsent(player, p -> new HashMap<>());
        Map<Integer, String> newLines = new HashMap<>();

        int line = 15;

        var selfObjective = player.getObjective();
        if (selfObjective != null) {
            newLines.put(line--, ChatColor.YELLOW + "Your Objective:");
            String objName = getObjectiveDisplay(selfObjective);
            newLines.put(line--, ChatColor.WHITE + (objName.length() > maxStringLength ? objName.substring(0, 21) + "..." : objName));
        } else {
            newLines.put(line--, ChatColor.GRAY + "No current objective");
        }

        newLines.put(line--, " ");

        if (player.isInTeam()) {
            Team team = player.getTeam();

            var teamObjective = team.getObjective();
            if (teamObjective != null && SettingsManager.isEnabled(SettingsManager.Setting.EXTRA_TEAM_OBJECTIVE)) {
                newLines.put(line--, ChatColor.YELLOW + "Team Objective:");
                String objName = getObjectiveDisplay(teamObjective);
                newLines.put(line--, ChatColor.WHITE + (objName.length() > maxStringLength ? objName.substring(0, 21) + "..." : objName));
                newLines.put(line--, "  ");
            }

            if (team.getPlayers().size() > 1) {
                newLines.put(line--, ChatColor.YELLOW + "Teammates:");
            }

            for (BattlePlayer teammate : team.getPlayers()) {
                if (teammate.equals(player)) continue;

                newLines.put(line--, ChatColor.GRAY + "• " + ChatColor.AQUA + teammate.getName());

                var teammateObjective = teammate.getObjective();
                String objName = teammateObjective != null
                        ? getObjectiveDisplay(teammateObjective)
                        : "None";

                newLines.put(line--, ChatColor.WHITE + "  " + (objName.length() > maxStringLength ? objName.substring(0, 21) + "..." : objName));
            }
        }

        for (var entry : last.entrySet()) {
            if (!newLines.containsValue(entry.getValue())) {
                scoreboard.resetScores(entry.getValue());
            }
        }

        for (var entry : newLines.entrySet()) {
            if (!entry.getValue().equals(last.get(entry.getKey()))) {
                obj.getScore(entry.getValue()).setScore(entry.getKey());
            }
        }

        LAST_LINES.put(player, newLines);

        if (player.getPlayer().getScoreboard() != scoreboard) {
            player.getPlayer().setScoreboard(scoreboard);
        }
    }

    private static String getObjectiveDisplay(net.fameless.forcebattle.game.Objective objective) {
        String icon = switch (objective.getBattleType()) {
            case BattleType.FORCE_ITEM -> "✦";
            case BattleType.FORCE_MOB -> "☠";
            case BattleType.FORCE_ADVANCEMENT -> "★";
            case BattleType.FORCE_HEIGHT -> "⛰";
            case BattleType.FORCE_COORDS -> "⚑";
            case BattleType.FORCE_STRUCTURE -> "⌂";
            case BattleType.FORCE_BIOME -> "☀";
        };
        return icon + " " + StringUtility.formatName(objective.getObjectiveString());
    }

    public static void removeScoreboard(BattlePlayer player) {
        player.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        SCOREBOARDS.remove(player);
        LAST_LINES.remove(player);
    }
}
