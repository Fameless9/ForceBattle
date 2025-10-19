package net.fameless.forcebattle.game.tasks;

import net.fameless.forcebattle.ForceBattle;
import net.fameless.forcebattle.caption.Caption;
import net.fameless.forcebattle.configuration.SettingsManager;
import net.fameless.forcebattle.game.data.BiomeSimplified;
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
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

public class BiomeTask implements ForceTask {

    @Override
    public void runTick() {
        if (!SettingsManager.isEnabled(SettingsManager.Setting.FORCE_BIOME)) return;
        if (!ForceBattle.getTimer().isRunning()) return;

        for (Player player : Bukkit.getOnlinePlayers()) {
            BattlePlayer battlePlayer = BattlePlayer.adapt(player);
            if (battlePlayer.isExcluded()) continue;

            Biome currentBiome = player.getWorld().getBiome(player.getLocation());
            checkPlayerObjective(battlePlayer, currentBiome);
            checkTeamObjective(battlePlayer, currentBiome);
        }
    }

    private void checkPlayerObjective(BattlePlayer battlePlayer, Biome currentBiome) {
        if (battlePlayer.getObjective().getBattleType() != BattleType.FORCE_BIOME) return;

        String objectiveString = battlePlayer.getObjective().getObjectiveString();
        if (isBiomeMatch(objectiveString, currentBiome)) {
            completeObjective(battlePlayer, objectiveString);
            battlePlayer.updateObjective(true, false);
        }
    }

    private void checkTeamObjective(BattlePlayer battlePlayer, Biome currentBiome) {
        if (!battlePlayer.isInTeam()) return;
        if (battlePlayer.getTeam().getObjective() == null) return;
        if (battlePlayer.getTeam().getObjective().getBattleType() != BattleType.FORCE_BIOME) return;

        String objectiveString = battlePlayer.getTeam().getObjective().getObjectiveString();
        if (isBiomeMatch(objectiveString, currentBiome)) {
            completeObjective(battlePlayer, objectiveString);
            battlePlayer.getTeam().updateObjective(battlePlayer, true, false);
        }
    }

    private boolean isBiomeMatch(String objectiveString, Biome currentBiome) {
        Object parsed = BukkitUtil.convertObjective(BattleType.FORCE_BIOME, objectiveString);

        if (parsed instanceof BiomeSimplified simplified) {
            return simplified.getBiomes().contains(currentBiome);
        }

        if (parsed instanceof Biome biome) {
            return biome == currentBiome;
        }

        return false;
    }

    private void completeObjective(BattlePlayer player, String objective) {
        String formattedObjective = StringUtility.formatName(objective);
        Material toastIcon = Material.GRASS_BLOCK;
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
