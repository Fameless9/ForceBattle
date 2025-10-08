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

public class ItemTask implements ForceTask {

    @Override
    public void runTick() {
        if (!SettingsManager.isEnabled(SettingsManager.Setting.FORCE_ITEM)) return;
        if (!ForceBattle.getTimer().isRunning()) return;

        for (Player player : Bukkit.getOnlinePlayers()) {
            BattlePlayer battlePlayer = BattlePlayer.adapt(player);
            if (battlePlayer.isExcluded()) continue;

            String objectiveString = battlePlayer.getObjective().getObjectiveString();
            if (BukkitUtil.convertObjective(BattleType.FORCE_ITEM, objectiveString) instanceof Material objective) {
                if (player.getInventory().contains(objective)) {
                    battlePlayer.sendMessage(Caption.of(
                            "notification.objective_finished",
                            TagResolver.resolver("objective", Tag.inserting(Component.text(Format.formatName(objectiveString))))
                    ));
                    Toast.display(
                            player,
                            objective,
                            ChatColor.BLUE + Format.formatName(objectiveString),
                            Toast.Style.GOAL,
                            ForceBattle.get()
                    );
                    battlePlayer.updateObjective(true, false);
                }
            }
        }
    }
}
