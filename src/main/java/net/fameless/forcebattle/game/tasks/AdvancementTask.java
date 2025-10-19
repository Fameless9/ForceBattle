package net.fameless.forcebattle.game.tasks;

import net.fameless.forcebattle.ForceBattle;
import net.fameless.forcebattle.caption.Caption;
import net.fameless.forcebattle.configuration.SettingsManager;
import net.fameless.forcebattle.game.data.FBAdvancement;
import net.fameless.forcebattle.player.BattlePlayer;
import net.fameless.forcebattle.util.BattleType;
import net.fameless.forcebattle.util.BukkitUtil;
import net.fameless.forcebattle.util.StringUtility;
import net.fameless.forcebattle.util.Toast;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Optional;

public class AdvancementTask implements ForceTask {

    @Override
    public void runTick() {
        if (!SettingsManager.isEnabled(SettingsManager.Setting.FORCE_ADVANCEMENT)) return;
        if (!ForceBattle.getTimer().isRunning()) return;

        for (Player player : Bukkit.getOnlinePlayers()) {
            BattlePlayer battlePlayer = BattlePlayer.adapt(player);
            if (battlePlayer.isExcluded()) continue;

            checkPlayerObjective(battlePlayer, player);
            checkTeamObjective(battlePlayer, player);
        }
    }

    private void checkPlayerObjective(BattlePlayer battlePlayer, Player player) {
        if (battlePlayer.getObjective().getBattleType() != BattleType.FORCE_ADVANCEMENT) return;

        String objective = battlePlayer.getObjective().getObjectiveString();
        if (hasCompletedAdvancement(player, objective)) {
            completeObjective(battlePlayer, objective);
            if (ForceBattle.get().getConfig().getBoolean("reset-advancements-on-finish", true)) {
                resetAdvancements(player);
            }
            battlePlayer.updateObjective(true, false);
        }
    }

    private void checkTeamObjective(BattlePlayer battlePlayer, Player player) {
        if (!battlePlayer.isInTeam()) return;
        if (battlePlayer.getTeam().getObjective() == null) return;
        if (battlePlayer.getTeam().getObjective().getBattleType() != BattleType.FORCE_ADVANCEMENT) return;

        String objective = battlePlayer.getTeam().getObjective().getObjectiveString();
        if (hasCompletedAdvancement(player, objective)) {
            completeObjective(battlePlayer, objective);
            if (ForceBattle.get().getConfig().getBoolean("reset-advancements-on-finish", true)) {
                resetAdvancements(player);
            }
            battlePlayer.getTeam().updateObjective(battlePlayer, true, false);
        }
    }

    private boolean hasCompletedAdvancement(Player player, String objectiveString) {
        Object obj = BukkitUtil.convertObjective(BattleType.FORCE_ADVANCEMENT, objectiveString);
        if (!(obj instanceof FBAdvancement adv)) return false;

        Optional<Advancement> advancementOpt = getAdvancement(adv.getKey());
        if (advancementOpt.isEmpty()) return false;

        AdvancementProgress progress = player.getAdvancementProgress(advancementOpt.get());
        return progress.isDone();
    }

    private void completeObjective(BattlePlayer player, String objective) {
        String formattedObjective = StringUtility.formatName(objective);
        Material toastIcon = Material.BREWING_STAND;
        ForceBattle plugin = ForceBattle.get();

        if (player.isInTeam() && SettingsManager.isEnabled(SettingsManager.Setting.EXTRA_TEAM_OBJECTIVE)) {
            Component teammateMsg = Caption.of(
                    "notification.objective_finished_by_teammate",
                    TagResolver.resolver("player", Tag.inserting(Component.text(player.getName()))),
                    TagResolver.resolver("objective", Tag.inserting(Component.text(formattedObjective)))
            );

            player.getTeam().getPlayers().stream()
                    .filter(teammate -> teammate != player)
                    .forEach(teammate -> {
                        teammate.sendMessage(teammateMsg);
                        Toast.display(teammate.getPlayer(), toastIcon, ChatColor.BLUE + formattedObjective, Toast.Style.GOAL, plugin);
                    });
        }

        player.sendMessage(Caption.of(
                "notification.objective_finished",
                TagResolver.resolver("objective", Tag.inserting(Component.text(formattedObjective)))
        ));
        Toast.display(player.getPlayer(), toastIcon, ChatColor.BLUE + formattedObjective, Toast.Style.GOAL, plugin);
    }

    private @NotNull Optional<Advancement> getAdvancement(NamespacedKey key) {
        Iterator<Advancement> it = Bukkit.getServer().advancementIterator();
        while (it.hasNext()) {
            Advancement a = it.next();
            if (a.getKey().equals(key)) {
                return Optional.of(a);
            }
        }
        return Optional.empty();
    }

    private void resetAdvancements(Player player) {
        Iterator<Advancement> iterator = Bukkit.getServer().advancementIterator();
        while (iterator.hasNext()) {
            AdvancementProgress progress = player.getAdvancementProgress(iterator.next());
            for (String criteria : progress.getAwardedCriteria()) {
                progress.revokeCriteria(criteria);
            }
        }
    }
}
