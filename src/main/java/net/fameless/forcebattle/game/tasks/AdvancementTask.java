package net.fameless.forcebattle.game.tasks;

import net.fameless.forcebattle.ForceBattle;
import net.fameless.forcebattle.caption.Caption;
import net.fameless.forcebattle.configuration.SettingsManager;
import net.fameless.forcebattle.player.BattlePlayer;
import net.fameless.forcebattle.util.BattleType;
import net.fameless.forcebattle.util.BukkitUtil;
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

            String objectiveString = battlePlayer.getObjective().getObjectiveString();
            if (BukkitUtil.convertObjective(BattleType.FORCE_ADVANCEMENT, objectiveString) instanceof net.fameless.forcebattle.game.data.Advancement advancement) {
                if (hasAdvancement(player, advancement.getKey())) {
                    battlePlayer.sendMessage(Caption.of(
                            "notification.objective_finished",
                            TagResolver.resolver("objective", Tag.inserting(Component.text(advancement.getName())))
                    ));
                    if (ForceBattle.get().getConfig().getBoolean("reset-advancements-on-finish", true)) {resetAdvancements(player);}

                    Toast.display(
                            player,
                            Material.BREWING_STAND,
                            ChatColor.BLUE + advancement.getName(),
                            Toast.Style.GOAL,
                            ForceBattle.get()
                    );
                    battlePlayer.updateObjective(true, false);
                }
            }
        }
    }

    private boolean hasAdvancement(Player player, NamespacedKey key) {
        @NotNull Optional<Advancement> advancementOptional = getAdvancement(key);
        if (advancementOptional.isPresent()) {
            AdvancementProgress progress = player.getAdvancementProgress(advancementOptional.get());
            return progress.isDone();
        }
        return false;
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

    public void resetAdvancements(Player player) {
        Iterator<Advancement> iterator = Bukkit.getServer().advancementIterator();
        while (iterator.hasNext()) {
            AdvancementProgress progress = player.getAdvancementProgress(iterator.next());
            for (String criteria : progress.getAwardedCriteria()) {
                progress.revokeCriteria(criteria);
            }
        }
    }

}
