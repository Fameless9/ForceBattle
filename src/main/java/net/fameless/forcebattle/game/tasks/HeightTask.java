package net.fameless.forcebattle.game.tasks;

import net.fameless.forcebattle.ForceBattle;
import net.fameless.forcebattle.caption.Caption;
import net.fameless.forcebattle.configuration.SettingsManager;
import net.fameless.forcebattle.player.BattlePlayer;
import net.fameless.forcebattle.util.BattleType;
import net.fameless.forcebattle.util.BukkitUtil;
import net.fameless.forcebattle.util.Format;
import net.fameless.forcebattle.util.Toast;
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
            if (battlePlayer.getObjective().getBattleType() != BattleType.FORCE_HEIGHT) continue;

            String objectiveString = battlePlayer.getObjective().getObjectiveString();
            if (BukkitUtil.convertObjective(BattleType.FORCE_HEIGHT, objectiveString) instanceof Integer height) {
                if (player.getLocation().getBlockY() == height) {
                    battlePlayer.sendMessage(Caption.of(
                            "notification.objective_finished",
                            TagResolver.resolver("objective", Tag.inserting(Component.text(height)))
                    ));
                    Toast.display(
                            player,
                            Material.SCAFFOLDING,
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
