package net.fameless.forcebattle.game.tasks;

import net.fameless.forcebattle.ForceBattle;
import net.fameless.forcebattle.caption.Caption;
import net.fameless.forcebattle.configuration.SettingsManager;
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
import org.bukkit.entity.Player;

public class ItemTask implements ForceTask {

    @Override
    public void runTick() {
        if (!SettingsManager.isEnabled(SettingsManager.Setting.FORCE_ITEM)) return;
        if (!ForceBattle.getTimer().isRunning()) return;

        for (Player player : Bukkit.getOnlinePlayers()) {
            BattlePlayer battlePlayer = BattlePlayer.adapt(player);
            if (battlePlayer.isExcluded()) continue;

            checkPlayerObjective(battlePlayer, player);
            checkTeamObjective(battlePlayer, player);
        }
    }

    private void checkPlayerObjective(BattlePlayer battlePlayer, Player player) {
        if (battlePlayer.getObjective().getBattleType() != BattleType.FORCE_ITEM) return;

        String objective = battlePlayer.getObjective().getObjectiveString();
        if (hasRequiredItem(player, objective)) {
            completeObjective(battlePlayer, objective);
            battlePlayer.updateObjective(true, false);
        }
    }

    private void checkTeamObjective(BattlePlayer battlePlayer, Player player) {
        if (!battlePlayer.isInTeam()) return;
        if (battlePlayer.getTeam().getObjective() == null) return;
        if (battlePlayer.getTeam().getObjective().getBattleType() != BattleType.FORCE_ITEM) return;

        String objective = battlePlayer.getTeam().getObjective().getObjectiveString();
        if (hasRequiredItem(player, objective)) {
            completeObjective(battlePlayer, objective);
            battlePlayer.getTeam().updateObjective(battlePlayer, true, false);
        }
    }

    private boolean hasRequiredItem(Player player, String objectiveString) {
        Object obj = BukkitUtil.convertObjective(BattleType.FORCE_ITEM, objectiveString);
        if (!(obj instanceof Material material)) return false;
        return player.getInventory().contains(material);
    }

    private void completeObjective(BattlePlayer player, String objective) {
        String formattedObjective = StringUtility.formatName(objective);
        Object obj = BukkitUtil.convertObjective(BattleType.FORCE_ITEM, objective);
        Material toastIcon = obj instanceof Material mat ? mat : Material.BOOK;
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
}
