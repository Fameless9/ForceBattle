package net.fameless.forcebattle.game.tasks;

import net.fameless.forcebattle.ForceBattle;
import net.fameless.forcebattle.caption.Caption;
import net.fameless.forcebattle.configuration.SettingsManager;
import net.fameless.forcebattle.player.BattlePlayer;
import net.fameless.forcebattle.util.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class HeightTask implements ForceTask {

    @Override
    public void runTick() {
        if (!SettingsManager.isEnabled(SettingsManager.Setting.FORCE_HEIGHT)) return;
        if (!ForceBattle.getTimer().isRunning()) return;

        for (Player player : Bukkit.getOnlinePlayers()) {
            BattlePlayer battlePlayer = BattlePlayer.adapt(player);
            if (battlePlayer.isExcluded()) continue;

            checkPlayerObjective(battlePlayer, player);
            checkTeamObjective(battlePlayer, player);
        }
    }

    private void checkPlayerObjective(BattlePlayer battlePlayer, Player player) {
        if (battlePlayer.getObjective().getBattleType() != BattleType.FORCE_HEIGHT) return;

        String objective = battlePlayer.getObjective().getObjectiveString();
        if (isAtTargetHeight(player, objective)) {
            completeObjective(battlePlayer, objective);
            battlePlayer.updateObjective(true, false);
        }
    }

    private void checkTeamObjective(BattlePlayer battlePlayer, Player player) {
        if (!battlePlayer.isInTeam()) return;
        if (battlePlayer.getTeam().getObjective() == null) return;
        if (battlePlayer.getTeam().getObjective().getBattleType() != BattleType.FORCE_HEIGHT) return;

        String objective = battlePlayer.getTeam().getObjective().getObjectiveString();
        if (isAtTargetHeight(player, objective)) {
            completeObjective(battlePlayer, objective);
            battlePlayer.getTeam().updateObjective(battlePlayer, true, false);
        }
    }

    private boolean isAtTargetHeight(Player player, String objectiveString) {
        Object obj = BukkitUtil.convertObjective(BattleType.FORCE_HEIGHT, objectiveString);
        if (!(obj instanceof Integer height)) return false;

        return player.getLocation().getBlockY() == height;
    }

    private void completeObjective(BattlePlayer player, String objective) {
        Object obj = BukkitUtil.convertObjective(BattleType.FORCE_HEIGHT, objective);
        String heightDisplay = obj instanceof Integer height ? String.valueOf(height) : Format.formatName(objective);

        Material toastIcon = Material.SCAFFOLDING;
        ForceBattle plugin = ForceBattle.get();

        if (player.isInTeam() && SettingsManager.isEnabled(SettingsManager.Setting.EXTRA_TEAM_OBJECTIVE)) {
            Component teammateMsg = Caption.of(
                    "notification.objective_finished_by_teammate",
                    TagResolver.resolver("player", Tag.inserting(Component.text(player.getName()))),
                    TagResolver.resolver("objective", Tag.inserting(Component.text(heightDisplay)))
            );

            player.getTeam().getPlayers().stream()
                    .filter(teammate -> teammate != player)
                    .forEach(teammate -> {
                        teammate.sendMessage(teammateMsg);
                        Toast.display(teammate.getPlayer(), toastIcon, ChatColor.BLUE + heightDisplay, Toast.Style.GOAL, plugin);
                    });
        }

        player.sendMessage(Caption.of(
                "notification.objective_finished",
                TagResolver.resolver("objective", Tag.inserting(Component.text(heightDisplay)))
        ));
        Toast.display(player.getPlayer(), toastIcon, ChatColor.BLUE + heightDisplay, Toast.Style.GOAL, plugin);
    }
}
