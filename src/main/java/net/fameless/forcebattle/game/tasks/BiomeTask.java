package net.fameless.forcebattle.game.tasks;

import net.fameless.forcebattle.ForceBattle;
import net.fameless.forcebattle.caption.Caption;
import net.fameless.forcebattle.configuration.SettingsManager;
import net.fameless.forcebattle.player.BattlePlayer;
import net.fameless.forcebattle.util.*;
import net.fameless.forcebattle.game.data.BiomeSimplified;
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
            if (battlePlayer.getObjective().getBattleType() != BattleType.FORCE_BIOME) continue;

            String objectiveString = battlePlayer.getObjective().getObjectiveString();
            Biome currentBiome = player.getWorld().getBiome(player.getLocation());

            boolean completed = false;

            if (SettingsManager.isEnabled(SettingsManager.Setting.SIMPLIFIED_OBJECTIVES)) {
                for (BiomeSimplified simplified : BiomeSimplified.values()) {
                    if (simplified.getName().equalsIgnoreCase(objectiveString)) {
                        if (simplified.getBiomes().contains(currentBiome)) {
                            completed = true;
                            break;
                        }
                    }
                }
            } else {
                if (BukkitUtil.convertObjective(BattleType.FORCE_BIOME, objectiveString) instanceof Biome biome) {
                    if (currentBiome.equals(biome)) {
                        completed = true;
                    }
                }
            }

            if (completed) {
                battlePlayer.sendMessage(Caption.of(
                        "notification.objective_finished",
                        TagResolver.resolver("objective", Tag.inserting(Component.text(Format.formatName(objectiveString))))
                ));
                Toast.display(
                        player,
                        Material.GRASS_BLOCK,
                        ChatColor.BLUE + Format.formatName(objectiveString),
                        Toast.Style.GOAL,
                        ForceBattle.get()
                );
                battlePlayer.updateObjective(true, false);
            }
        }
    }
}
