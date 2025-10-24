package net.fameless.forcebattle.bossbar;

import net.fameless.forcebattle.ForceBattle;
import net.fameless.forcebattle.caption.Caption;
import net.fameless.forcebattle.configuration.SettingsManager;
import net.fameless.forcebattle.game.Objective;
import net.fameless.forcebattle.game.Team;
import net.fameless.forcebattle.player.BattlePlayer;
import net.fameless.forcebattle.util.StringUtility;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class BossbarManager {

    private static final HashMap<BattlePlayer, BossBar> lastPlayerBarMap = new HashMap<>();
    private static final HashMap<BattlePlayer, BossBar> lastTeamBarMap = new HashMap<>();

    public static void runTask() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                boolean timerRunning = ForceBattle.getTimer().isRunning();

                for (BattlePlayer battlePlayer : BattlePlayer.getOnlinePlayers()) {
                    if (battlePlayer.isExcluded()) {
                        hideBossBars(battlePlayer);
                        continue;
                    }

                    if (!timerRunning) {
                        showWaitingBossbar(battlePlayer);
                        hideBossBar(battlePlayer, lastTeamBarMap);
                        continue;
                    } else {
                        hideBossBar(battlePlayer, lastPlayerBarMap);
                    }

                    Objective playerObjective = battlePlayer.getObjective();
                    if (playerObjective != null) {
                        BossBar playerBar = BossBar.bossBar(
                                getPlayerObjectiveTitle(battlePlayer),
                                1,
                                BossBar.Color.BLUE,
                                BossBar.Overlay.PROGRESS
                        );
                        updateBossBar(battlePlayer, playerBar, lastPlayerBarMap);
                    } else {
                        hideBossBar(battlePlayer, lastPlayerBarMap);
                    }

                    if (battlePlayer.isInTeam()) {
                        Team team = battlePlayer.getTeam();
                        Objective teamObjective = team.getObjective();

                        if (teamObjective != null) {
                            BossBar teamBar = BossBar.bossBar(
                                    getTeamObjectiveTitle(team),
                                    1,
                                    BossBar.Color.PURPLE,
                                    BossBar.Overlay.PROGRESS
                            );
                            updateBossBar(battlePlayer, teamBar, lastTeamBarMap);
                        } else {
                            hideBossBar(battlePlayer, lastTeamBarMap);
                        }
                    } else {
                        hideBossBar(battlePlayer, lastTeamBarMap);
                    }
                }
            }
        };
        new Timer("forcebattle/bossbar").scheduleAtFixedRate(task, 150, 150);
    }

    private static void showWaitingBossbar(BattlePlayer battlePlayer) {
        BossBar waitingBar = BossBar.bossBar(
                Caption.of("waiting"),
                1,
                BossBar.Color.YELLOW,
                BossBar.Overlay.PROGRESS
        );
        updateBossBar(battlePlayer, waitingBar, lastPlayerBarMap);
    }

    private static void hideBossBars(BattlePlayer battlePlayer) {
        hideBossBar(battlePlayer, lastPlayerBarMap);
        hideBossBar(battlePlayer, lastTeamBarMap);
    }

    private static void hideBossBar(BattlePlayer battlePlayer, HashMap<BattlePlayer, BossBar> map) {
        BossBar existing = map.remove(battlePlayer);
        if (existing != null) {
            battlePlayer.getAudience().hideBossBar(existing);
        }
    }

    private static @NotNull Component getPlayerObjectiveTitle(BattlePlayer battlePlayer) {
        Objective playerObjective = battlePlayer.getObjective();
        if (playerObjective == null) return Component.empty();

        boolean isPlayerInTeam = battlePlayer.isInTeam();
        int points = isPlayerInTeam ? battlePlayer.getTeam().getPoints() : battlePlayer.getPoints();

        if (SettingsManager.isEnabled(SettingsManager.Setting.HIDE_POINTS)) {
            return Caption.of(
                    "bossbar.format_hide_points",
                    TagResolver.resolver("battletype", Tag.inserting(Component.text(playerObjective.getBattleType().getPrefix()))),
                    TagResolver.resolver("objective", Tag.inserting(Component.text(StringUtility.formatName(playerObjective.getObjectiveString()))))
            );
        }

        return Caption.of(
                "bossbar.format",
                TagResolver.resolver("battletype", Tag.inserting(Component.text(playerObjective.getBattleType().getPrefix()))),
                TagResolver.resolver("objective", Tag.inserting(Component.text(StringUtility.formatName(playerObjective.getObjectiveString())))),
                TagResolver.resolver("pointstype", Tag.inserting(Component.text(isPlayerInTeam ? "Team-Points" : "Points"))),
                TagResolver.resolver("points", Tag.inserting(Component.text(points)))
        );
    }

    private static @NotNull Component getTeamObjectiveTitle(Team team) {
        Objective teamObjective = team.getObjective();
        if (teamObjective == null) return Component.empty();

        if (SettingsManager.isEnabled(SettingsManager.Setting.HIDE_POINTS)) {
            return Caption.of(
                    "bossbar.team_format_hide_points",
                    TagResolver.resolver("battletype", Tag.inserting(Component.text(teamObjective.getBattleType().getPrefix()))),
                    TagResolver.resolver("objective", Tag.inserting(Component.text(StringUtility.formatName(teamObjective.getObjectiveString()))))
            );
        }

        return Caption.of(
                "bossbar.team_format",
                TagResolver.resolver("battletype", Tag.inserting(Component.text(teamObjective.getBattleType().getPrefix()))),
                TagResolver.resolver("objective", Tag.inserting(Component.text(StringUtility.formatName(teamObjective.getObjectiveString())))),
                TagResolver.resolver("pointstype", Tag.inserting(Component.text("Team-Points"))),
                TagResolver.resolver("points", Tag.inserting(Component.text(team.getPoints())))
        );
    }

    private static void updateBossBar(BattlePlayer battlePlayer, BossBar bossBar, HashMap<BattlePlayer, BossBar> map) {
        hideBossBar(battlePlayer, map);
        battlePlayer.getAudience().showBossBar(bossBar);
        map.put(battlePlayer, bossBar);
    }
}
