package net.fameless.forcebattle.bossbar;

import net.fameless.forcebattle.ForceBattle;
import net.fameless.forcebattle.caption.Caption;
import net.fameless.forcebattle.configuration.SettingsManager;
import net.fameless.forcebattle.player.BattlePlayer;
import net.fameless.forcebattle.util.Format;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class BossbarManager {

    private static final HashMap<BattlePlayer, BossBar> lastBossbarMap = new HashMap<>();

    public static void runTask() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                for (BattlePlayer battlePlayer : BattlePlayer.getOnlinePlayers()) {
                    if (battlePlayer.isExcluded()) {
                        hideBossBar(battlePlayer);
                        continue;
                    }

                    Component bossbarTitle = getBossbarTitle(battlePlayer);
                    BossBar bossBar = BossBar.bossBar(bossbarTitle, 1, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS);

                    updateBossBar(battlePlayer, bossBar);
                }
            }
        };
        new Timer("forcebattle/bossbar").scheduleAtFixedRate(task, 150, 150);
    }

    private static void hideBossBar(BattlePlayer battlePlayer) {
        if (lastBossbarMap.containsKey(battlePlayer)) {
            battlePlayer.getAudience().hideBossBar(lastBossbarMap.get(battlePlayer));
            lastBossbarMap.remove(battlePlayer);
        }
    }

    private static @NotNull Component getBossbarTitle(BattlePlayer battlePlayer) {
        if (ForceBattle.getTimer().isRunning()) {
            boolean isPlayerInTeam = battlePlayer.isInTeam();
            int points = isPlayerInTeam ? battlePlayer.getTeam().getPoints() : battlePlayer.getPoints();

            if (SettingsManager.isEnabled(SettingsManager.Setting.HIDE_POINTS)) {
                return Caption.of(
                        "bossbar.format_hide_points",
                        TagResolver.resolver("battletype", Tag.inserting(Component.text(battlePlayer.getObjective().getBattleType().getPrefix()))),
                        TagResolver.resolver("objective", Tag.inserting(Component.text(Format.formatName(battlePlayer.getObjective().getObjectiveString()))))
                );
            }

            return Caption.of(
                    "bossbar.format",
                    TagResolver.resolver("battletype", Tag.inserting(Component.text(battlePlayer.getObjective().getBattleType().getPrefix()))),
                    TagResolver.resolver("objective", Tag.inserting(Component.text(Format.formatName(battlePlayer.getObjective().getObjectiveString())))),
                    TagResolver.resolver("pointstype", Tag.inserting(Component.text(isPlayerInTeam ? "Team-Points" : "Points"))),
                    TagResolver.resolver("points", Tag.inserting(Component.text(points)))
            );
        } else {
            return Caption.of("waiting");
        }
    }

    private static void updateBossBar(BattlePlayer battlePlayer, BossBar bossBar) {
        if (lastBossbarMap.containsKey(battlePlayer)) {
            battlePlayer.getAudience().hideBossBar(lastBossbarMap.get(battlePlayer));
        }
        battlePlayer.getAudience().showBossBar(bossBar);
        lastBossbarMap.put(battlePlayer, bossBar);
    }

}
